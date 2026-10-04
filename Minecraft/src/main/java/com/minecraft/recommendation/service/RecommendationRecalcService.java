package com.minecraft.recommendation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.minecraft.entity.Attraction;
import com.minecraft.entity.Food;
import com.minecraft.entity.Hotel;
import com.minecraft.entity.Product;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.entity.recommendation.RecommendationItemSimilarity;
import com.minecraft.entity.recommendation.RecommendationJobLog;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.AttractionMapper;
import com.minecraft.mapper.FoodMapper;
import com.minecraft.mapper.HotelMapper;
import com.minecraft.mapper.ProductMapper;
import com.minecraft.mapper.RecommendationAggregationMapper;
import com.minecraft.mapper.RecommendationItemSimilarityMapper;
import com.minecraft.mapper.RecommendationJobLogMapper;
import com.minecraft.recommendation.algorithm.ContentProfileBuilder;
import com.minecraft.recommendation.algorithm.FeatureTokenizer;
import com.minecraft.recommendation.algorithm.ItemSimilarityCalculator;
import com.minecraft.recommendation.algorithm.Normalizers;
import com.minecraft.recommendation.algorithm.PopularityBuilder;
import com.minecraft.recommendation.algorithm.QualityScore;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.algorithm.SeasonalAnalyzer;
import com.minecraft.recommendation.algorithm.StrategyParameters;
import com.minecraft.recommendation.algorithm.model.ItemFeatures;
import com.minecraft.recommendation.algorithm.model.Neighbor;
import com.minecraft.recommendation.algorithm.model.ScoreComponent;
import com.minecraft.recommendation.algorithm.model.SimilarityEdge;
import com.minecraft.recommendation.algorithm.model.WeightedAction;
import com.minecraft.recommendation.config.RecommendationCacheKeys;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.ItemCountResult;
import com.minecraft.recommendation.dto.RecalcResult;
import com.minecraft.recommendation.dto.UserItemInteraction;
import com.minecraft.recommendation.enums.JobStatus;
import com.minecraft.recommendation.enums.JobTriggerType;
import com.minecraft.recommendation.enums.RecommendCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 推荐分离线重算编排。
 * <p>
 * 每个分类的处理流程：加载在架物品 → 集合式聚合行为（recency 窗口加权、全量计数、当月计数）
 * → 热度/品质/季节/内容分量 → Item-CF 相似度边整类替换 → 全局综合分 →
 * 批量 upsert（保留人工列）→ 清理匿名列表缓存。
 * <p>
 * 并发：分类锁 + ALL 锁，任务运行中重复触发快速失败（409）。
 * MANUAL 触发失败写 FAILED 日志后抛 BusinessException(500)；
 * SYSTEM 触发失败仅记录并返回，不影响调度器与后续分类。
 */
@Slf4j
@Service
public class RecommendationRecalcService {

    /** 全量重算标识与锁键 */
    public static final String ALL = "ALL";

    private static final int UPSERT_BATCH = 500;
    private static final int MAX_ERROR_LENGTH = 1000;

    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Autowired
    private RecommendationConfigService configService;
    @Autowired
    private RecommendationAggregationMapper aggregationMapper;
    @Autowired
    private RecommendationItemSimilarityMapper similarityMapper;
    @Autowired
    private RecommendationJobLogMapper jobLogMapper;
    @Autowired
    private RecommendationMapperRegistry registry;
    @Autowired
    private AttractionMapper attractionMapper;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private ProductMapper productMapper;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    // ---------------- 对外入口 ----------------

    /**
     * 重算单个分类（同步）。
     */
    public RecalcResult recalculate(RecommendCategory category,
                                    JobTriggerType triggerType,
                                    Long operatorId) {
        if (category == null) {
            return recalculateAll(triggerType, operatorId);
        }
        return runWithLock(category.code(), () -> recalculateCategory(category, triggerType, operatorId));
    }

    /**
     * 按固定顺序重算全部分类。
     */
    public RecalcResult recalculateAll(JobTriggerType triggerType, Long operatorId) {
        JobTriggerType trigger = triggerType == null ? JobTriggerType.MANUAL : triggerType;
        return runWithLock(ALL, () -> {
            long start = System.currentTimeMillis();
            LocalDateTime startTime = LocalDateTime.now();
            Long allLogId = insertJobLog(ALL, trigger, operatorId, startTime);
            log.info("推荐全量重算开始 trigger={} operator={}", trigger, operatorId);

            int totalItems = 0;
            int totalEdges = 0;
            String firstError = null;
            for (RecommendCategory category : RecommendCategory.values()) {
                try {
                    RecalcResult part = recalculateCategory(category, trigger, operatorId);
                    totalItems += part.getItemCount();
                    totalEdges += part.getEdgeCount();
                    if (!JobStatus.SUCCESS.name().equals(part.getStatus()) && firstError == null) {
                        firstError = part.getErrorMessage();
                    }
                } catch (BusinessException e) {
                    // SYSTEM 继续后续分类；MANUAL 立即失败
                    firstError = e.getMessage();
                    if (trigger == JobTriggerType.MANUAL) {
                        finishJobLog(allLogId, JobStatus.FAILED, totalItems,
                                System.currentTimeMillis() - start, firstError, LocalDateTime.now());
                        throw e;
                    }
                }
            }

            long duration = System.currentTimeMillis() - start;
            boolean anyFailure = firstError != null;
            JobStatus status = anyFailure ? JobStatus.FAILED : JobStatus.SUCCESS;
            finishJobLog(allLogId, status, totalItems, duration, firstError, LocalDateTime.now());
            log.info("推荐全量重算结束 status={} items={} edges={} durationMs={}",
                    status, totalItems, totalEdges, duration);

            RecalcResult result = new RecalcResult();
            result.setCategory(ALL);
            result.setStatus(status.name());
            result.setItemCount(totalItems);
            result.setEdgeCount(totalEdges);
            result.setDurationMs(duration);
            result.setJobLogId(allLogId);
            result.setErrorMessage(firstError);
            return result;
        });
    }

    // ---------------- 单分类流水线 ----------------

    private RecalcResult recalculateCategory(RecommendCategory category,
                                             JobTriggerType trigger,
                                             Long operatorId) {
        long start = System.currentTimeMillis();
        LocalDateTime startTime = LocalDateTime.now();
        Long logId = insertJobLog(category.code(), trigger, operatorId, startTime);
        log.info("推荐重算开始 category={} trigger={}", category.code(), trigger);
        try {
            LocalDateTime now = LocalDateTime.now();
            String code = category.code();

            // 1. 参数
            Map<String, Double> actionWeights = actionWeights();
            int k = configService.getInt(null, RecommendationDefaults.CF_NEIGHBOR_K);
            int recencyDays = configService.getInt(null, RecommendationDefaults.BEHAVIOR_RECENCY_DAYS);
            double logBase = configService.getDouble(null, RecommendationDefaults.POPULARITY_LOG_BASE);
            StrategyParameters params = configService.strategyParameters(category);
            LocalDateTime since = now.minusDays(recencyDays);
            LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();

            // 2. 在架物品 + 静态特征
            List<ItemFeatures> items = loadActiveItems(category);
            Map<Long, ItemFeatures> featureIndex = new HashMap<>();
            for (ItemFeatures item : items) {
                featureIndex.put(item.getItemId(), item);
            }

            // 3. 行为聚合：全量购物车/支付订单计数（冗余列未覆盖的两张表）
            mergeCountResults(featureIndex, aggregationMapper.countCartAll(code), PopularityBuilder.ACTION_CART);
            mergeCountResults(featureIndex, aggregationMapper.countPaidOrdersAll(code), PopularityBuilder.ACTION_ORDER);

            // 4. recency 窗口加权行为（协同过滤）+ 当月行为计数（季节信号）
            List<WeightedAction> actions = new ArrayList<>();
            Map<Long, Map<String, Double>> monthly = new HashMap<>();
            collectActions(code, PopularityBuilder.ACTION_LIKE,
                    aggregationMapper.selectLikes(code, since), actionWeights, now, recencyDays, actions, monthly);
            collectActions(code, PopularityBuilder.ACTION_COLLECT,
                    aggregationMapper.selectCollections(code, since), actionWeights, now, recencyDays, actions, monthly);
            collectActions(code, PopularityBuilder.ACTION_COMMENT,
                    aggregationMapper.selectComments(code, since), actionWeights, now, recencyDays, actions, monthly);
            collectActions(code, PopularityBuilder.ACTION_CART,
                    aggregationMapper.selectCartItems(code, since), actionWeights, now, recencyDays, actions, monthly);
            collectActions(code, PopularityBuilder.ACTION_ORDER,
                    aggregationMapper.selectPaidOrders(code, since), actionWeights, now, recencyDays, actions, monthly);
            // 当月计数补齐（recency 查询只覆盖窗口；月初时窗口起点早于月初，无需额外查询；
            // 当 recency 窗口短于当月跨度时月度计数可能偏小，这里以窗口内数据为准）
            applyMonthlyCounts(featureIndex, monthly);

            // 5. 相似度边（整类替换）
            List<SimilarityEdge> edges = ItemSimilarityCalculator.buildEdges(code, actions, k, null);
            similarityMapper.deleteByCategory(code);
            persistEdges(category, edges);
            Map<Long, List<Neighbor>> neighborMap = ItemSimilarityCalculator.neighborMap(edges);

            // 6. 热度（对数压缩 + min-max）
            Map<Long, Double> popularity = PopularityBuilder.build(items, actionWeights, logBase);

            // 7. token 全局流行度（群体亲和口径）
            Map<String, Double> tokenPopularity = new HashMap<>();
            for (ItemFeatures item : items) {
                for (String token : ContentProfileBuilder.itemVector(item).keySet()) {
                    tokenPopularity.merge(token, 1.0, Double::sum);
                }
            }

            // 8. 组装分量 + 综合分
            int month = now.getMonthValue();
            List<BaseRecommendationItem> rows = new ArrayList<>(items.size());
            for (ItemFeatures item : items) {
                long id = item.getItemId();
                double pop = popularity.getOrDefault(id, 0.0);
                double quality = QualityScore.of(item.getRating());
                double monthRatio = SeasonalAnalyzer.monthlyRatio(item.monthlyTotalCount(), item.totalCount());
                double seasonal = SeasonalAnalyzer.score(item.getSeasons(), month, monthRatio);
                double preference = ContentProfileBuilder.populationAffinity(item, tokenPopularity);
                double content = contentCompleteness(item);
                double collab = meanNeighborSimilarity(neighborMap.get(id));

                double finalScore = params.weight(ScoreComponent.COLLABORATIVE) * collab
                        + params.weight(ScoreComponent.CONTENT) * preference
                        + params.weight(ScoreComponent.POPULARITY) * pop
                        + params.weight(ScoreComponent.SEASONAL) * seasonal
                        + params.weight(ScoreComponent.QUALITY) * quality;

                BaseRecommendationItem row = registry.newInstance(category);
                row.setItemId(id);
                row.setRecommendationScore(scale(finalScore));
                row.setUserPreferenceMatching(scale(preference));
                row.setPopularityIndex(scale(pop));
                row.setCollaborativeScore(scale(collab));
                row.setContentScore(scale(content));
                row.setSeasonalScore(scale(seasonal));
                row.setQualityScore(scale(quality));
                rows.add(row);
            }

            // 9. 批量 upsert（保留人工列）
            for (int i = 0; i < rows.size(); i += UPSERT_BATCH) {
                registry.upsert(category, rows.subList(i, Math.min(i + UPSERT_BATCH, rows.size())));
            }

            // 10. 清理匿名列表缓存
            evictGlobalListCache(category);

            long duration = System.currentTimeMillis() - start;
            finishJobLog(logId, JobStatus.SUCCESS, rows.size(), duration, null, LocalDateTime.now());
            log.info("推荐重算成功 category={} items={} edges={} durationMs={}",
                    code, rows.size(), edges.size(), duration);

            RecalcResult result = new RecalcResult();
            result.setCategory(code);
            result.setStatus(JobStatus.SUCCESS.name());
            result.setItemCount(rows.size());
            result.setEdgeCount(edges.size());
            result.setDurationMs(duration);
            result.setJobLogId(logId);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            String message = truncate(e.getMessage());
            finishJobLog(logId, JobStatus.FAILED, 0, duration, message, LocalDateTime.now());
            log.error("推荐重算失败 category=" + category.code(), e);
            if (trigger == JobTriggerType.MANUAL) {
                throw new BusinessException(500, "推荐重算失败：" + message);
            }
            RecalcResult failed = new RecalcResult();
            failed.setCategory(category.code());
            failed.setStatus(JobStatus.FAILED.name());
            failed.setDurationMs(duration);
            failed.setJobLogId(logId);
            failed.setErrorMessage(message);
            return failed;
        }
    }

    // ---------------- 特征装配 ----------------

    private List<ItemFeatures> loadActiveItems(RecommendCategory category) {
        return switch (category) {
            case ATTRACTION -> attractionMapper.selectList(
                            new LambdaQueryWrapper<Attraction>().eq(Attraction::getStatus, 1))
                    .stream().map(this::toFeatures).toList();
            case HOTEL -> hotelMapper.selectList(
                            new LambdaQueryWrapper<Hotel>().eq(Hotel::getStatus, 1))
                    .stream().map(this::toFeatures).toList();
            case RESTAURANT -> foodMapper.selectList(
                            new LambdaQueryWrapper<Food>().eq(Food::getStatus, 1))
                    .stream().map(this::toFeatures).toList();
            case SOUVENIR -> productMapper.selectList(
                            new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1))
                    .stream().map(this::toFeatures).toList();
        };
    }

    private ItemFeatures toFeatures(Attraction a) {
        return ItemFeatures.builder()
                .itemId(a.getId()).category(RecommendCategory.ATTRACTION.code())
                .tags(FeatureTokenizer.tokenize(a.getTags()))
                .city(a.getCity())
                .rating(a.getRating() == null ? 0 : a.getRating())
                .price(a.getPrice() == null ? 0.0 : a.getPrice().doubleValue())
                .seasons(SeasonalAnalyzer.parseSeasons(a.getSeason()))
                .counts(baseCounts(nz(a.getLikeCount()), nz(a.getCollectCount()), nz(a.getCommentCount())))
                .monthlyCounts(new HashMap<>())
                .active(true).build();
    }

    private ItemFeatures toFeatures(Hotel h) {
        return ItemFeatures.builder()
                .itemId(h.getId()).category(RecommendCategory.HOTEL.code())
                .tags(FeatureTokenizer.tokenize(h.getFacilities()))
                .city(h.getCity())
                .subType(h.getStarLevel() == null ? null : String.valueOf(h.getStarLevel()))
                .rating(h.getRating() == null ? 0 : h.getRating())
                .price(h.getPrice() == null ? 0.0 : h.getPrice().doubleValue())
                .seasons(Set.of())
                .counts(baseCounts(nz(h.getLikeCount()), nz(h.getCollectCount()), nz(h.getCommentCount())))
                .monthlyCounts(new HashMap<>())
                .active(true).build();
    }

    private ItemFeatures toFeatures(Food f) {
        return ItemFeatures.builder()
                .itemId(f.getId()).category(RecommendCategory.RESTAURANT.code())
                .tags(FeatureTokenizer.tokenize(f.getTags()))
                .city(f.getCity()).subType(f.getCuisineType())
                .rating(f.getRating() == null ? 0 : f.getRating())
                .price(f.getPrice() == null ? 0.0 : f.getPrice().doubleValue())
                .seasons(Set.of())
                .counts(baseCounts(nz(f.getLikeCount()), nz(f.getCollectCount()), nz(f.getCommentCount())))
                .monthlyCounts(new HashMap<>())
                .active(true).build();
    }

    private ItemFeatures toFeatures(Product p) {
        return ItemFeatures.builder()
                .itemId(p.getId()).category(RecommendCategory.SOUVENIR.code())
                .tags(FeatureTokenizer.tokenize(p.getTags()))
                .city(p.getCity()).subType(p.getType())
                .rating(p.getRating() == null ? 0 : p.getRating())
                .price(p.getPrice() == null ? 0.0 : p.getPrice().doubleValue())
                .seasons(Set.of())
                .counts(baseCounts(nz(p.getLikeCount()), nz(p.getCollectCount()), nz(p.getCommentCount())))
                .monthlyCounts(new HashMap<>())
                .active(true).build();
    }

    private void mergeCountResults(Map<Long, ItemFeatures> index,
                                   List<ItemCountResult> results, String action) {
        if (results == null) {
            return;
        }
        for (ItemCountResult r : results) {
            ItemFeatures item = index.get(r.getItemId());
            if (item != null && r.getCnt() != null) {
                item.getCounts().put(action, r.getCnt().doubleValue());
            }
        }
    }

    private void collectActions(String code, String action,
                                List<UserItemInteraction> interactions,
                                Map<String, Double> actionWeights,
                                LocalDateTime now, int recencyDays,
                                List<WeightedAction> target,
                                Map<Long, Map<String, Double>> monthly) {
        if (interactions == null) {
            return;
        }
        double baseWeight = actionWeights.getOrDefault(action, 0.0);
        double halfLife = Math.max(1.0, recencyDays / 2.0);
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        for (UserItemInteraction interaction : interactions) {
            if (interaction.getUserId() == null || interaction.getItemId() == null) {
                continue;
            }
            LocalDateTime time = interaction.getActionTime() == null ? now : interaction.getActionTime();
            long ageDays = Math.max(0L, ChronoUnit.DAYS.between(time, now));
            double weight = baseWeight * Normalizers.exponentialDecay(ageDays, halfLife);
            if (weight > 0.0) {
                target.add(new WeightedAction(interaction.getUserId(), interaction.getItemId(), weight, time));
            }
            if (!time.isBefore(monthStart)) {
                monthly.computeIfAbsent(interaction.getItemId(), k -> new HashMap<>())
                        .merge(action, 1.0, Double::sum);
            }
        }
    }

    private void applyMonthlyCounts(Map<Long, ItemFeatures> index,
                                    Map<Long, Map<String, Double>> monthly) {
        monthly.forEach((itemId, counts) -> {
            ItemFeatures item = index.get(itemId);
            if (item != null) {
                item.getMonthlyCounts().putAll(counts);
            }
        });
    }

    private Map<String, Double> actionWeights() {
        Map<String, Double> weights = new HashMap<>();
        weights.put(PopularityBuilder.ACTION_LIKE,
                configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_LIKE));
        weights.put(PopularityBuilder.ACTION_COLLECT,
                configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_COLLECT));
        weights.put(PopularityBuilder.ACTION_COMMENT,
                configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_COMMENT));
        weights.put(PopularityBuilder.ACTION_CART,
                configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_CART));
        weights.put(PopularityBuilder.ACTION_ORDER,
                configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_ORDER));
        return weights;
    }

    /** 内容完整度：标签（70%，5 个封顶）+ 城市/子类型各 15% */
    private double contentCompleteness(ItemFeatures item) {
        double tagPart = Math.min(5, item.getTags() == null ? 0 : item.getTags().size()) / 5.0 * 0.7;
        double cityPart = item.getCity() != null && !item.getCity().isBlank() ? 0.15 : 0.0;
        double typePart = item.getSubType() != null && !item.getSubType().isBlank() ? 0.15 : 0.0;
        return Normalizers.clamp01(tagPart + cityPart + typePart);
    }

    private double meanNeighborSimilarity(List<Neighbor> neighbors) {
        if (neighbors == null || neighbors.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Neighbor n : neighbors) {
            sum += n.similarity();
        }
        return sum / neighbors.size();
    }

    private void persistEdges(RecommendCategory category, List<SimilarityEdge> edges) {
        if (edges.isEmpty()) {
            return;
        }
        List<RecommendationItemSimilarity> batch = new ArrayList<>(UPSERT_BATCH);
        for (SimilarityEdge edge : edges) {
            RecommendationItemSimilarity entity = new RecommendationItemSimilarity();
            entity.setCategory(category.code());
            entity.setItemId(edge.itemId());
            entity.setNeighborId(edge.neighborId());
            entity.setSimilarity(BigDecimal.valueOf(edge.similarity()));
            batch.add(entity);
            if (batch.size() >= UPSERT_BATCH) {
                similarityMapper.upsertSimilarities(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            similarityMapper.upsertSimilarities(batch);
        }
    }

    private void evictGlobalListCache(RecommendCategory category) {
        if (redisTemplate == null) {
            return;
        }
        try {
            Set<String> keys = redisTemplate.keys(RecommendationCacheKeys.GLOBAL_LIST_PREFIX
                    + category.code() + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("清理推荐匿名列表缓存失败 category={}: {}", category.code(), e.getMessage());
        }
    }

    // ---------------- 任务日志 / 锁 ----------------

    private Long insertJobLog(String category, JobTriggerType trigger,
                              Long operatorId, LocalDateTime startTime) {
        RecommendationJobLog jobLog = new RecommendationJobLog();
        jobLog.setCategory(category);
        jobLog.setTriggerType(trigger.name());
        jobLog.setOperatorId(operatorId);
        jobLog.setStatus(JobStatus.RUNNING.name());
        jobLog.setItemCount(0);
        jobLog.setStartTime(startTime);
        jobLogMapper.insert(jobLog);
        return jobLog.getId();
    }

    private void finishJobLog(Long logId, JobStatus status, int itemCount,
                              long durationMs, String error, LocalDateTime endTime) {
        if (logId == null) {
            return;
        }
        RecommendationJobLog update = new RecommendationJobLog();
        update.setId(logId);
        update.setStatus(status.name());
        update.setItemCount(itemCount);
        update.setDurationMs(durationMs);
        update.setErrorMessage(error);
        update.setEndTime(endTime);
        jobLogMapper.updateById(update);
    }

    private RecalcResult runWithLock(String key, java.util.function.Supplier<RecalcResult> action) {
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        if (!lock.tryLock()) {
            throw new BusinessException(409, "推荐任务正在运行，请勿重复触发：" + key);
        }
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    private static Map<String, Double> baseCounts(double likes, double collects, double comments) {
        Map<String, Double> counts = new HashMap<>();
        counts.put(PopularityBuilder.ACTION_LIKE, likes);
        counts.put(PopularityBuilder.ACTION_COLLECT, collects);
        counts.put(PopularityBuilder.ACTION_COMMENT, comments);
        counts.put(PopularityBuilder.ACTION_CART, 0.0);
        counts.put(PopularityBuilder.ACTION_ORDER, 0.0);
        return counts;
    }

    private static double nz(Integer value) {
        return value == null ? 0.0 : value;
    }

    private static BigDecimal scale(double v) {
        return BigDecimal.valueOf(Normalizers.clamp01(v));
    }

    private static String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() > MAX_ERROR_LENGTH ? message.substring(0, MAX_ERROR_LENGTH) : message;
    }
}
