package com.minecraft.recommendation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.recommendation.RecommendationConfig;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationConfigMapper;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.algorithm.StrategyParameters;
import com.minecraft.recommendation.config.RecommendationCacheKeys;
import com.minecraft.recommendation.dto.ConfigUpdateParam;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.enums.RecommendStrategy;
import com.minecraft.recommendation.event.RecommendationConfigChangedEvent;
import com.minecraft.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 推荐参数服务：两级（GLOBAL/CATEGORY）参数的读取、校验、批量更新与恢复默认。
 * <p>
 * 读取链路：进程内短 TTL 快照 -> Redis（key={@value #CACHE_KEY}）-> 数据库；
 * 任何写入都会失效两级缓存并发布 {@link RecommendationConfigChangedEvent}。
 * Redis 不可用时自动降级到直读数据库，不影响业务。
 */
@Slf4j
@Service
public class RecommendationConfigService
        extends ServiceImpl<RecommendationConfigMapper, RecommendationConfig> {

    public static final String CACHE_KEY = RecommendationCacheKeys.CONFIG;
    private static final long REDIS_TTL_SECONDS = 300L;
    private static final long LOCAL_TTL_MILLIS = 60_000L;

    @Autowired(required = false)
    private RedisUtil redisUtil;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /** 进程内 L1 快照 */
    private volatile ConfigSnapshot localSnapshot;
    private volatile long localExpireAt;

    // ---------------- 读取 ----------------

    /**
     * 获取当前参数快照（带缓存）。
     */
    public ConfigSnapshot snapshot() {
        long now = System.currentTimeMillis();
        ConfigSnapshot local = localSnapshot;
        if (local != null && now < localExpireAt) {
            return local;
        }
        ConfigSnapshot fromRedis = readFromRedis();
        if (fromRedis != null) {
            localSnapshot = fromRedis;
            localExpireAt = now + LOCAL_TTL_MILLIS;
            return fromRedis;
        }
        ConfigSnapshot loaded = loadFromDb();
        cacheSnapshot(loaded);
        return loaded;
    }

    /**
     * 取分类参数值：分类行 -> 全局行 -> 代码默认值。
     */
    public String get(RecommendCategory category, String key) {
        ConfigSnapshot snapshot = snapshot();
        String value;
        if (category != null) {
            value = snapshot.categoryValue(category.code(), key);
            if (value != null) {
                return value;
            }
        }
        value = snapshot.globalValue(key);
        if (value != null) {
            return value;
        }
        return defaultOf(key);
    }

    public double getDouble(RecommendCategory category, String key) {
        try {
            return Double.parseDouble(get(category, key));
        } catch (NumberFormatException e) {
            return Double.parseDouble(defaultOf(key));
        }
    }

    public int getInt(RecommendCategory category, String key) {
        return (int) Math.round(getDouble(category, key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(null, key));
    }

    public String getGlobalString(String key) {
        return get(null, key);
    }

    /**
     * 构建某分类的策略参数（权重自动归一化）。
     */
    public StrategyParameters strategyParameters(RecommendCategory category) {
        RecommendStrategy strategy = RecommendStrategy.fromCode(
                get(category, RecommendationDefaults.STRATEGY));
        return StrategyParameters.of(strategy,
                getDouble(category, RecommendationDefaults.WEIGHT_COLLABORATIVE),
                getDouble(category, RecommendationDefaults.WEIGHT_CONTENT),
                getDouble(category, RecommendationDefaults.WEIGHT_POPULARITY),
                getDouble(category, RecommendationDefaults.WEIGHT_SEASONAL),
                getDouble(category, RecommendationDefaults.WEIGHT_QUALITY),
                getInt(null, RecommendationDefaults.CF_NEIGHBOR_K),
                getDouble(null, RecommendationDefaults.DIVERSITY_STRENGTH),
                getDouble(null, RecommendationDefaults.FEATURED_BOOST));
    }

    /**
     * 后台视图：按 scope/category 列出全部参数（含元数据描述）。
     */
    public List<RecommendationConfig> listAll() {
        return list(new LambdaQueryWrapper<RecommendationConfig>()
                .orderByAsc(RecommendationConfig::getScope)
                .orderByAsc(RecommendationConfig::getCategory)
                .orderByAsc(RecommendationConfig::getParamKey));
    }

    // ---------------- 写入 ----------------

    /**
     * 批量更新参数：先整体校验（任何一项非法都不落库），再逐项持久化。
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<ConfigUpdateParam> updates, Long operatorId) {
        if (updates == null || updates.isEmpty()) {
            throw new BusinessException(400, "更新参数列表不能为空");
        }
        // 1. 全量校验（含作用域/键存在性/类型/取值范围）
        List<RecommendationConfig> toPersist = new ArrayList<>(updates.size());
        for (ConfigUpdateParam param : updates) {
            toPersist.add(validateAndResolve(param));
        }
        // 2. 全部合法后才写库（逐项 upsert，唯一键 scope+category+key）
        boolean scheduleAffected = false;
        for (RecommendationConfig row : toPersist) {
            RecommendationConfig existing = findRow(row.getScope(), row.getCategory(), row.getParamKey());
            if (existing == null) {
                row.setUpdatedBy(operatorId);
                save(row);
            } else {
                existing.setParamValue(row.getParamValue());
                existing.setValueType(row.getValueType());
                existing.setDescription(row.getDescription());
                existing.setUpdatedBy(operatorId);
                updateById(existing);
            }
            scheduleAffected |= isScheduleKey(row.getParamKey());
        }
        invalidateCache();
        eventPublisher.publishEvent(new RecommendationConfigChangedEvent(this, scheduleAffected, operatorId));
    }

    /**
     * 恢复默认：category 为空时重置全局参数，否则重置指定分类参数。
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetToDefaults(RecommendCategory category, Long operatorId) {
        if (category == null) {
            remove(new LambdaQueryWrapper<RecommendationConfig>()
                    .eq(RecommendationConfig::getScope, RecommendationConfig.SCOPE_GLOBAL));
            RecommendationDefaults.seedConfigs().stream()
                    .filter(c -> RecommendationConfig.SCOPE_GLOBAL.equals(c.getScope()))
                    .forEach(c -> {
                        c.setUpdatedBy(operatorId);
                        save(c);
                    });
        } else {
            remove(new LambdaQueryWrapper<RecommendationConfig>()
                    .eq(RecommendationConfig::getScope, RecommendationConfig.SCOPE_CATEGORY)
                    .eq(RecommendationConfig::getCategory, category.code()));
            RecommendationDefaults.seedConfigs().stream()
                    .filter(c -> RecommendationConfig.SCOPE_CATEGORY.equals(c.getScope())
                            && category.code().equals(c.getCategory()))
                    .forEach(c -> {
                        c.setUpdatedBy(operatorId);
                        save(c);
                    });
        }
        invalidateCache();
        // 恢复默认也可能影响调度开关/cron
        eventPublisher.publishEvent(new RecommendationConfigChangedEvent(this, true, operatorId));
    }

    // ---------------- 校验 ----------------

    private RecommendationConfig validateAndResolve(ConfigUpdateParam param) {
        if (param == null) {
            throw new BusinessException(400, "参数项不能为空");
        }
        String scope = param.getScope();
        String category = param.getCategory() == null ? "" : param.getCategory().trim();
        String key = param.getParamKey() == null ? "" : param.getParamKey().trim();
        String value = param.getParamValue() == null ? "" : param.getParamValue().trim();

        if (!RecommendationConfig.SCOPE_GLOBAL.equals(scope)
                && !RecommendationConfig.SCOPE_CATEGORY.equals(scope)) {
            throw new BusinessException(400, "参数作用域非法（仅支持 GLOBAL/CATEGORY）：" + scope);
        }
        if (RecommendationConfig.SCOPE_CATEGORY.equals(scope)) {
            RecommendCategory.fromCode(category);
            if (!RecommendationDefaults.CATEGORY_SPECS.stream()
                    .anyMatch(s -> s.key().equals(key))) {
                throw new BusinessException(400, "分类参数键不存在：" + key);
            }
        } else if (RecommendationDefaults.GLOBAL_SPECS.stream().noneMatch(s -> s.key().equals(key))) {
            throw new BusinessException(400, "全局参数键不存在：" + key);
        }
        if (value.isEmpty()) {
            throw new BusinessException(400, "参数值不能为空：" + key);
        }

        RecommendationDefaults.ParamSpec spec = RecommendationDefaults.registry().get(key);
        validateTypedValue(key, value, spec.valueType());

        RecommendationConfig row = new RecommendationConfig();
        row.setScope(scope);
        row.setCategory(RecommendationConfig.SCOPE_GLOBAL.equals(scope) ? "" : category);
        row.setParamKey(key);
        row.setParamValue(value);
        row.setValueType(spec.valueType());
        row.setDescription(spec.description());
        return row;
    }

    private void validateTypedValue(String key, String value, String valueType) {
        switch (valueType) {
            case RecommendationConfig.TYPE_BOOLEAN -> {
                if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                    throw new BusinessException(400, "参数 " + key + " 必须是布尔值（true/false）");
                }
            }
            case RecommendationConfig.TYPE_NUMBER -> {
                double number;
                try {
                    number = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    throw new BusinessException(400, "参数 " + key + " 必须是数字：" + value);
                }
                validateNumberRange(key, number);
            }
            case RecommendationConfig.TYPE_STRING -> validateStringValue(key, value);
            default -> throw new BusinessException(400, "参数类型未定义：" + valueType);
        }
    }

    private void validateNumberRange(String key, double number) {
        switch (key) {
            case RecommendationDefaults.WEIGHT_COLLABORATIVE,
                    RecommendationDefaults.WEIGHT_CONTENT,
                    RecommendationDefaults.WEIGHT_POPULARITY,
                    RecommendationDefaults.WEIGHT_SEASONAL,
                    RecommendationDefaults.WEIGHT_QUALITY,
                    RecommendationDefaults.DIVERSITY_STRENGTH,
                    RecommendationDefaults.FEATURED_BOOST,
                    RecommendationDefaults.EXPOSURE_SAMPLE_RATE ->
                    requireRange(key, number, 0.0, 1.0);
            case RecommendationDefaults.CF_NEIGHBOR_K -> {
                requireRange(key, number, 1.0, 200.0);
                if (number != Math.rint(number)) {
                    throw new BusinessException(400, "参数 " + key + " 必须是整数");
                }
            }
            case RecommendationDefaults.BEHAVIOR_RECENCY_DAYS -> requireRange(key, number, 1.0, 730.0);
            case RecommendationDefaults.RETENTION_DAYS -> requireRange(key, number, 1.0, 3650.0);
            case RecommendationDefaults.EXPOSURE_ATTRIBUTION_HOURS -> requireRange(key, number, 1.0, 8760.0);
            case RecommendationDefaults.POPULARITY_LOG_BASE -> requireRange(key, number, 1.0001, 100.0);
            case RecommendationDefaults.BEHAVIOR_WEIGHT_LIKE,
                    RecommendationDefaults.BEHAVIOR_WEIGHT_COLLECT,
                    RecommendationDefaults.BEHAVIOR_WEIGHT_COMMENT,
                    RecommendationDefaults.BEHAVIOR_WEIGHT_CART,
                    RecommendationDefaults.BEHAVIOR_WEIGHT_ORDER -> requireRange(key, number, 0.0, 100.0);
            default -> requireRange(key, number, 0.0, 1_000_000.0);
        }
    }

    private void requireRange(String key, double number, double min, double max) {
        if (number < min || number > max) {
            throw new BusinessException(400, "参数 " + key + " 取值必须在 [" + min + ", " + max + "] 区间内：" + number);
        }
    }

    private void validateStringValue(String key, String value) {
        switch (key) {
            case RecommendationDefaults.STRATEGY -> {
                try {
                    RecommendStrategy.fromCode(value);
                } catch (BusinessException e) {
                    throw new BusinessException(400, "参数 " + key + " 非法：" + value);
                }
            }
            case RecommendationDefaults.COLD_START -> {
                if (!"POPULAR".equalsIgnoreCase(value)) {
                    throw new BusinessException(400, "参数 " + key + " 目前仅支持 POPULAR");
                }
            }
            case RecommendationDefaults.SCHEDULE_CRON -> validateCron(value);
            default -> {
                // 其它字符串参数仅限制长度
                if (value.length() > 128) {
                    throw new BusinessException(400, "参数 " + key + " 长度不能超过 128");
                }
            }
        }
    }

    /**
     * 校验 6 段 Cron（秒 分 时 日 月 周），兼容 Quartz 的 '?'（等价于 '*'）。
     */
    public static void validateCron(String cron) {
        String normalized = cron.trim();
        String[] fields = normalized.split("\\s+");
        if (fields.length != 6) {
            throw new BusinessException(400, "Cron 必须是 6 段（秒 分 时 日 月 周）：" + cron);
        }
        try {
            CronExpression.parse(normalized.replace("?", "*"));
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "Cron 表达式非法：" + cron);
        }
    }

    public static boolean isScheduleKey(String key) {
        return RecommendationDefaults.SCHEDULE_ENABLED.equals(key)
                || RecommendationDefaults.SCHEDULE_CRON.equals(key);
    }

    // ---------------- 装配 / 缓存 ----------------

    private ConfigSnapshot loadFromDb() {
        List<RecommendationConfig> rows = list();
        ConfigSnapshot snapshot = new ConfigSnapshot();
        Map<String, String> global = new LinkedHashMap<>();
        Map<String, Map<String, String>> categories = new LinkedHashMap<>();
        for (RecommendationConfig row : rows) {
            if (RecommendationConfig.SCOPE_GLOBAL.equals(row.getScope())) {
                global.put(row.getParamKey(), row.getParamValue());
            } else if (row.getCategory() != null) {
                categories.computeIfAbsent(row.getCategory(), k -> new LinkedHashMap<>())
                        .put(row.getParamKey(), row.getParamValue());
            }
        }
        snapshot.setGlobal(global);
        snapshot.setCategories(categories);
        return snapshot;
    }

    private RecommendationConfig findRow(String scope, String category, String key) {
        return baseMapper.selectByKey(scope, category, key);
    }

    private String defaultOf(String key) {
        RecommendationDefaults.ParamSpec spec = RecommendationDefaults.registry().get(key);
        return spec == null ? null : spec.defaultValue();
    }

    private ConfigSnapshot readFromRedis() {
        if (redisUtil == null) {
            return null;
        }
        try {
            Object cached = redisUtil.get(CACHE_KEY);
            if (cached instanceof ConfigSnapshot snapshot) {
                return snapshot;
            }
        } catch (Exception e) {
            log.warn("读取推荐参数 Redis 缓存失败，降级直读数据库：{}", e.getMessage());
        }
        return null;
    }

    private void cacheSnapshot(ConfigSnapshot snapshot) {
        localSnapshot = snapshot;
        localExpireAt = System.currentTimeMillis() + LOCAL_TTL_MILLIS;
        if (redisUtil != null) {
            try {
                redisUtil.set(CACHE_KEY, snapshot, REDIS_TTL_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("写入推荐参数 Redis 缓存失败：{}", e.getMessage());
            }
        }
    }

    /**
     * 失效两级缓存（写操作后调用，也供测试使用）。
     */
    public void invalidateCache() {
        localSnapshot = null;
        localExpireAt = 0L;
        if (redisUtil != null) {
            try {
                redisUtil.delete(CACHE_KEY);
            } catch (Exception e) {
                log.warn("删除推荐参数 Redis 缓存失败：{}", e.getMessage());
            }
        }
    }

    /** 当前时间（包内可见，便于测试时间相关逻辑） */
    LocalDateTime now() {
        return LocalDateTime.now();
    }
}
