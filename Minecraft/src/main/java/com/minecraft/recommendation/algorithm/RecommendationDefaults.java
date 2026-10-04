package com.minecraft.recommendation.algorithm;

import com.minecraft.entity.recommendation.RecommendationConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐算法默认参数注册表。
 * <p>
 * 单一事实来源（SQL 种子数据保持同值）：既是参数校验的键注册表，
 * 也是首次启动种子写入与「恢复默认」的依据。
 */
public final class RecommendationDefaults {

    private RecommendationDefaults() {
    }

    /**
     * 参数元数据：键 / 默认值 / 值类型 / 说明。
     */
    public record ParamSpec(String key, String defaultValue, String valueType, String description) {
    }

    // ---------------- 参数键常量 ----------------

    public static final String BEHAVIOR_WEIGHT_LIKE = "behavior.weight.like";
    public static final String BEHAVIOR_WEIGHT_COLLECT = "behavior.weight.collect";
    public static final String BEHAVIOR_WEIGHT_COMMENT = "behavior.weight.comment";
    public static final String BEHAVIOR_WEIGHT_CART = "behavior.weight.cart";
    public static final String BEHAVIOR_WEIGHT_ORDER = "behavior.weight.order";
    public static final String CF_NEIGHBOR_K = "cf.neighbor.k";
    public static final String BEHAVIOR_RECENCY_DAYS = "behavior.recency.days";
    public static final String POPULARITY_LOG_BASE = "popularity.log.base";
    public static final String DIVERSITY_STRENGTH = "diversity.strength";
    public static final String FEATURED_BOOST = "featured.boost";
    public static final String COLD_START = "cold.start";
    public static final String EXPOSURE_ENABLED = "exposure.enabled";
    public static final String EXPOSURE_SAMPLE_RATE = "exposure.sample.rate";
    public static final String EXPOSURE_ATTRIBUTION_HOURS = "exposure.attribution.hours";
    public static final String RETENTION_DAYS = "retention.days";
    public static final String SCHEDULE_ENABLED = "schedule.enabled";
    public static final String SCHEDULE_CRON = "schedule.cron";

    public static final String WEIGHT_COLLABORATIVE = "weight.collaborative";
    public static final String WEIGHT_CONTENT = "weight.content";
    public static final String WEIGHT_POPULARITY = "weight.popularity";
    public static final String WEIGHT_SEASONAL = "weight.seasonal";
    public static final String WEIGHT_QUALITY = "weight.quality";
    public static final String STRATEGY = "strategy";

    // ---------------- 默认值定义 ----------------

    public static final List<ParamSpec> GLOBAL_SPECS = List.of(
            new ParamSpec(BEHAVIOR_WEIGHT_LIKE, "1", RecommendationConfig.TYPE_NUMBER, "行为权重：点赞"),
            new ParamSpec(BEHAVIOR_WEIGHT_COLLECT, "3", RecommendationConfig.TYPE_NUMBER, "行为权重：收藏"),
            new ParamSpec(BEHAVIOR_WEIGHT_COMMENT, "2", RecommendationConfig.TYPE_NUMBER, "行为权重：评论"),
            new ParamSpec(BEHAVIOR_WEIGHT_CART, "4", RecommendationConfig.TYPE_NUMBER, "行为权重：加购物车"),
            new ParamSpec(BEHAVIOR_WEIGHT_ORDER, "6", RecommendationConfig.TYPE_NUMBER, "行为权重：下单（已支付/已完成）"),
            new ParamSpec(CF_NEIGHBOR_K, "20", RecommendationConfig.TYPE_NUMBER, "Item-CF 每物品保留的相似邻居数量"),
            new ParamSpec(BEHAVIOR_RECENCY_DAYS, "90", RecommendationConfig.TYPE_NUMBER, "用户行为时间衰减窗口（天）"),
            new ParamSpec(POPULARITY_LOG_BASE, "10", RecommendationConfig.TYPE_NUMBER, "热度对数压缩底数"),
            new ParamSpec(DIVERSITY_STRENGTH, "0.3", RecommendationConfig.TYPE_NUMBER, "多样性重排强度（0-1，0关闭）"),
            new ParamSpec(FEATURED_BOOST, "0.15", RecommendationConfig.TYPE_NUMBER, "人工推荐加权（置顶项除外）"),
            new ParamSpec(COLD_START, "POPULAR", RecommendationConfig.TYPE_STRING, "冷启动/匿名策略"),
            new ParamSpec(EXPOSURE_ENABLED, "true", RecommendationConfig.TYPE_BOOLEAN, "是否记录推荐曝光日志"),
            new ParamSpec(EXPOSURE_SAMPLE_RATE, "1.0", RecommendationConfig.TYPE_NUMBER, "曝光采样率（0-1）"),
            new ParamSpec(EXPOSURE_ATTRIBUTION_HOURS, "72", RecommendationConfig.TYPE_NUMBER, "曝光转化归因窗口（小时）"),
            new ParamSpec(RETENTION_DAYS, "30", RecommendationConfig.TYPE_NUMBER, "曝光日志保留天数"),
            new ParamSpec(SCHEDULE_ENABLED, "false", RecommendationConfig.TYPE_BOOLEAN, "是否启用定时自动重算"),
            new ParamSpec(SCHEDULE_CRON, "0 0 3 * * ?", RecommendationConfig.TYPE_STRING, "定时重算 Cron（6位：秒 分 时 日 月 周）")
    );

    public static final List<ParamSpec> CATEGORY_SPECS = List.of(
            new ParamSpec(WEIGHT_COLLABORATIVE, "0.25", RecommendationConfig.TYPE_NUMBER, "协同过滤权重"),
            new ParamSpec(WEIGHT_CONTENT, "0.25", RecommendationConfig.TYPE_NUMBER, "内容匹配权重"),
            new ParamSpec(WEIGHT_POPULARITY, "0.20", RecommendationConfig.TYPE_NUMBER, "热度权重"),
            new ParamSpec(WEIGHT_SEASONAL, "0.10", RecommendationConfig.TYPE_NUMBER, "季节趋势权重"),
            new ParamSpec(WEIGHT_QUALITY, "0.20", RecommendationConfig.TYPE_NUMBER, "品质权重"),
            new ParamSpec(STRATEGY, "HYBRID", RecommendationConfig.TYPE_STRING, "推荐策略 HYBRID/POPULAR/CONTENT/COLLABORATIVE")
    );

    /** 四个分类编码（与 RecommendCategory 对齐，此处保持字面量避免反向依赖） */
    public static final List<String> CATEGORIES = List.of("attraction", "hotel", "food", "product");

    /**
     * 参数键注册表：key -> spec（全局键与分类键同名但作用域不同，注册表用于键存在性校验）。
     */
    public static Map<String, ParamSpec> registry() {
        Map<String, ParamSpec> map = new LinkedHashMap<>();
        GLOBAL_SPECS.forEach(s -> map.put(s.key(), s));
        CATEGORY_SPECS.forEach(s -> map.putIfAbsent(s.key(), s));
        return map;
    }

    /**
     * 生成与 SQL 种子完全一致的配置实体集合（供种子初始化/恢复默认使用）。
     */
    public static List<RecommendationConfig> seedConfigs() {
        List<RecommendationConfig> list = new ArrayList<>(GLOBAL_SPECS.size() + CATEGORY_SPECS.size() * 4);
        for (ParamSpec spec : GLOBAL_SPECS) {
            list.add(build(RecommendationConfig.SCOPE_GLOBAL, RecommendationConfig.GLOBAL_CATEGORY, spec));
        }
        for (String category : CATEGORIES) {
            for (ParamSpec spec : CATEGORY_SPECS) {
                list.add(build(RecommendationConfig.SCOPE_CATEGORY, category, spec));
            }
        }
        return list;
    }

    private static RecommendationConfig build(String scope, String category, ParamSpec spec) {
        RecommendationConfig config = new RecommendationConfig();
        config.setScope(scope);
        config.setCategory(category);
        config.setParamKey(spec.key());
        config.setParamValue(spec.defaultValue());
        config.setValueType(spec.valueType());
        config.setDescription(spec.description());
        return config;
    }
}
