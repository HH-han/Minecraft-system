package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.RecommendationItemSimilarity;
import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.RecommendationAggregationMapper;
import com.minecraft.mapper.RecommendationItemSimilarityMapper;
import com.minecraft.recommendation.algorithm.ContentProfileBuilder;
import com.minecraft.recommendation.algorithm.HybridRanker;
import com.minecraft.recommendation.algorithm.ItemSimilarityCalculator;
import com.minecraft.recommendation.algorithm.Normalizers;
import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.algorithm.SeasonalAnalyzer;
import com.minecraft.recommendation.algorithm.StrategyParameters;
import com.minecraft.recommendation.algorithm.model.ItemFeatures;
import com.minecraft.recommendation.algorithm.model.Neighbor;
import com.minecraft.recommendation.algorithm.model.ScoreComponent;
import com.minecraft.recommendation.algorithm.model.ScoredCandidate;
import com.minecraft.recommendation.algorithm.model.UserBehavior;
import com.minecraft.recommendation.config.RecommendationCacheKeys;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.dto.UserItemInteraction;
import com.minecraft.recommendation.enums.ExposureSource;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.vo.RecommendationItemVO;
import com.minecraft.recommendation.vo.RecommendationResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 推荐查询服务：
 * <ul>
 *   <li>匿名/冷启动：读离线写入的全局推荐行，置顶 + 基线分排序，结果短 TTL 缓存 Redis；</li>
 *   <li>登录用户：实时叠加 Item-CF 预测分与内容画像余弦，五因子个性化排序；</li>
 *   <li>相关推荐：物品共现相似度边优先，不足时标签 Jaccard 兜底；</li>
 *   <li>命中曝光配置时异步记录曝光日志（仅列表接口）。</li>
 * </ul>
 * 单次查询候选池有界（≤200），排序 O(n log n)。
 */
@Slf4j
@Service
public class RecommendationQueryService {

    /** 个性化/匿名排序候选池上限 */
    private static final int CANDIDATE_POOL = 200;

    /** 相关推荐兜底扫描池 */
    private static final int RELATED_POOL = 100;

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;
    private static final long GLOBAL_CACHE_TTL_SECONDS = 120L;

    private final RecommendationMapperRegistry registry;
    private final RecommendationItemCatalog catalog;
    private final RecommendationAggregationMapper aggregationMapper;
    private final RecommendationItemSimilarityMapper similarityMapper;
    private final RecommendationConfigService configService;
    private final RecommendationExposureService exposureService;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public RecommendationQueryService(RecommendationMapperRegistry registry,
                                      RecommendationItemCatalog catalog,
                                      RecommendationAggregationMapper aggregationMapper,
                                      RecommendationItemSimilarityMapper similarityMapper,
                                      RecommendationConfigService configService,
                                      RecommendationExposureService exposureService) {
        this.registry = registry;
        this.catalog = catalog;
        this.aggregationMapper = aggregationMapper;
        this.similarityMapper = similarityMapper;
        this.configService = configService;
        this.exposureService = exposureService;
    }

    // ---------------- 列表推荐 ----------------

    public RecommendationResultVO list(RecommendCategory category, Long userId,
                                       Integer limit, String city, String season) {
        int safeLimit = normalizeLimit(limit);
        String normalizedCity = city == null || city.isBlank() ? null : city.trim();
        StrategyParameters params = configService.strategyParameters(category);

        if (userId == null) {
            // 匿名：同分类+城市+条数共享缓存
            String cacheKey = RecommendationCacheKeys.globalList(category.code(), normalizedCity, safeLimit);
            RecommendationResultVO cached = readCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            RecommendationResultVO result = rankGlobal(category, params, safeLimit, normalizedCity, season);
            writeCache(cacheKey, result);
            exposureService.captureExposure(category, null, ExposureSource.POPULAR,
                    result.getRequestId(), result.getItems());
            return result;
        }

        // 登录用户：尝试个性化，无行为自动走冷启动全局口径
        RecommendationResultVO result = rankForUser(category, userId, params, safeLimit, normalizedCity, season);
        ExposureSource source = ExposureSource.PERSONALIZED.name().equals(result.getSource())
                ? ExposureSource.PERSONALIZED : ExposureSource.COLD_START;
        exposureService.captureExposure(category, userId, source,
                result.getRequestId(), result.getItems());
        return result;
    }

    private RecommendationResultVO rankGlobal(RecommendCategory category, StrategyParameters params,
                                              int limit, String city, String season) {
        List<ScoredCandidate> candidates = loadCandidates(category, city, season);
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(candidates, params, limit);
        Map<Long, ItemDetail> details = detailsOf(category, ranked);
        return buildResult(category, ExposureSource.POPULAR, params, ranked, details);
    }

    private RecommendationResultVO rankForUser(RecommendCategory category, Long userId,
                                               StrategyParameters params, int limit,
                                               String city, String season) {
        List<ScoredCandidate> candidates = loadCandidates(category, city, season);
        if (candidates.isEmpty()) {
            return buildResult(category, ExposureSource.COLD_START, params, List.of(), Map.of());
        }

        int recencyDays = configService.getInt(null, RecommendationDefaults.BEHAVIOR_RECENCY_DAYS);
        LocalDateTime since = LocalDateTime.now().minusDays(recencyDays);
        List<UserItemInteraction> interactions =
                aggregationMapper.selectUserInteractions(category.itemTable(), userId, since);

        if (interactions == null || interactions.isEmpty()) {
            List<ScoredCandidate> ranked = HybridRanker.rankGlobal(candidates, params, limit);
            return buildResult(category, ExposureSource.COLD_START, params, ranked, detailsOf(category, ranked));
        }

        LocalDateTime now = LocalDateTime.now();
        double halfLife = Math.max(1.0, recencyDays / 2.0);
        Map<Long, Double> rawWeights = new HashMap<>();
        List<UserBehavior> behaviors = new ArrayList<>(interactions.size());
        Set<Long> interactedItemIds = new HashSet<>();
        for (UserItemInteraction interaction : interactions) {
            double weight = actionWeight(interaction.getActionType())
                    * Normalizers.exponentialDecay(Duration.between(
                    interaction.getActionTime(), now).toDays(), halfLife);
            if (weight <= 0.0) {
                continue;
            }
            rawWeights.merge(interaction.getItemId(), weight, Double::sum);
            behaviors.add(new UserBehavior(interaction.getItemId(), weight, interaction.getActionTime()));
            interactedItemIds.add(interaction.getItemId());
        }

        // 用户行为涉及但不在候选池内的物品也需要画像特征
        Set<Long> featureIds = new HashSet<>(interactedItemIds);
        candidates.forEach(c -> featureIds.add(c.getItemId()));
        Map<Long, ItemDetail> allDetails = catalog.load(category, featureIds);
        Map<Long, ItemFeatures> itemIndex = toFeatureIndex(category, allDetails);

        Map<String, Double> userVector = ContentProfileBuilder.userVector(behaviors, itemIndex);

        // Item-CF：一次 IN 查询取回候选物品的全部邻居边
        List<Long> candidateIds = candidates.stream().map(ScoredCandidate::getItemId).toList();
        List<RecommendationItemSimilarity> edges =
                similarityMapper.selectNeighborsByItems(category.code(), candidateIds);
        Map<Long, List<Neighbor>> neighborsByItem = toNeighborMap(edges);
        Map<Long, Double> cfScores = ItemSimilarityCalculator.predict(
                ItemSimilarityCalculator.normalizeUserWeights(rawWeights), neighborsByItem);

        boolean hasSignal = false;
        for (ScoredCandidate candidate : candidates) {
            long id = candidate.getItemId();
            Double cf = cfScores.get(id);
            if (cf != null && cf > 0.0) {
                candidate.component(ScoreComponent.COLLABORATIVE, cf);
                hasSignal = true;
            }
            ItemFeatures features = itemIndex.get(id);
            if (features != null && !userVector.isEmpty()) {
                double content = ContentProfileBuilder.contentScore(userVector, features);
                if (content > 0.0) {
                    candidate.component(ScoreComponent.CONTENT, content);
                    hasSignal = true;
                }
            }
        }

        ExposureSource source = hasSignal ? ExposureSource.PERSONALIZED : ExposureSource.COLD_START;
        List<ScoredCandidate> ranked = hasSignal
                ? HybridRanker.rankPersonalized(candidates, params, limit)
                : HybridRanker.rankGlobal(candidates, params, limit);
        // 展示详情只需最终结果集，但特征阶段已批量加载，直接复用
        Map<Long, ItemDetail> rankedDetails = new LinkedHashMap<>();
        for (ScoredCandidate c : ranked) {
            ItemDetail detail = allDetails.get(c.getItemId());
            if (detail != null) {
                rankedDetails.put(c.getItemId(), detail);
            }
        }
        return buildResult(category, source, params, ranked, rankedDetails);
    }

    // ---------------- 相关推荐 ----------------

    public RecommendationResultVO related(RecommendCategory category, Long itemId, Integer limit) {
        int safeLimit = normalizeLimit(limit);
        if (!catalog.exists(category, itemId)) {
            throw new BusinessException(404, "物品不存在：" + category.code() + "/" + itemId);
        }

        // 1. 相似度边优先
        List<RecommendationItemSimilarity> edges =
                similarityMapper.selectNeighbors(category.code(), itemId, Math.max(safeLimit * 2, 20));
        List<RecommendationItemVO> items = new ArrayList<>(safeLimit);
        Set<Long> usedIds = new HashSet<>();
        usedIds.add(itemId);
        if (edges != null) {
            for (RecommendationItemSimilarity edge : edges) {
                if (items.size() >= safeLimit) {
                    break;
                }
                if (usedIds.add(edge.getNeighborId())) {
                    double sim = edge.getSimilarity() == null ? 0.0 : edge.getSimilarity().doubleValue();
                    items.add(toVO(edge.getNeighborId(), sim, items.size() + 1, null));
                }
            }
        }
        // 邻居可能含已下架物品，批量加载后剔除并补齐
        Map<Long, ItemDetail> neighborDetails = catalog.load(category,
                items.stream().map(RecommendationItemVO::getItemId).toList());
        items.removeIf(vo -> {
            ItemDetail d = neighborDetails.get(vo.getItemId());
            return d == null || !Integer.valueOf(1).equals(d.getStatus());
        });
        reindex(items);
        fillDetailFields(items, neighborDetails);

        // 2. 不足时标签 Jaccard 兜底
        if (items.size() < safeLimit) {
            fillRelatedByTags(category, itemId, safeLimit, items, usedIds);
        }

        RecommendationResultVO result = new RecommendationResultVO();
        result.setCategory(category.code());
        result.setSource("RELATED");
        result.setRequestId(newRequestId());
        result.setGeneratedAt(LocalDateTime.now());
        result.setItems(items);
        return result;
    }

    private void fillRelatedByTags(RecommendCategory category, Long seedId, int limit,
                                   List<RecommendationItemVO> items, Set<Long> usedIds) {
        ItemDetail seed = catalog.load(category, List.of(seedId)).get(seedId);
        Set<String> seedTags = seed == null ? Set.of() : new HashSet<>(seed.getTags());
        List<BaseRecommendationItem> rows = registry.selectTopScored(category, RELATED_POOL);
        List<Long> candidateIds = rows.stream()
                .map(BaseRecommendationItem::getItemId)
                .filter(id -> !usedIds.contains(id))
                .toList();
        Map<Long, ItemDetail> details = catalog.load(category, candidateIds);

        record TagCandidate(long itemId, double score) {
        }
        List<TagCandidate> tagCandidates = new ArrayList<>();
        for (BaseRecommendationItem row : rows) {
            long id = row.getItemId();
            if (usedIds.contains(id)) {
                continue;
            }
            ItemDetail detail = details.get(id);
            if (detail == null || !Integer.valueOf(1).equals(detail.getStatus())) {
                continue;
            }
            double overlap = seedTags.isEmpty() ? 0.0 : Normalizers.jaccard(seedTags, new HashSet<>(detail.getTags()));
            double base = row.getRecommendationScore() == null ? 0.0 : row.getRecommendationScore().doubleValue();
            // 有标签命中时相似度主导，无命中时回退全局基线（统一降权，排在相似度边之后）
            double score = overlap > 0.0 ? 0.5 + 0.5 * overlap : 0.25 * base;
            tagCandidates.add(new TagCandidate(id, score));
        }
        tagCandidates.sort(Comparator.comparingDouble(TagCandidate::score).reversed()
                .thenComparingLong(TagCandidate::itemId));
        for (TagCandidate tc : tagCandidates) {
            if (items.size() >= limit) {
                break;
            }
            if (usedIds.add(tc.itemId())) {
                items.add(toVO(tc.itemId(), tc.score(), items.size() + 1, details.get(tc.itemId())));
            }
        }
    }

    // ---------------- 装配辅助 ----------------

    private List<ScoredCandidate> loadCandidates(RecommendCategory category, String city, String season) {
        List<BaseRecommendationItem> rows = registry.selectTopScored(category, CANDIDATE_POOL);
        if (rows.isEmpty()) {
            return List.of();
        }
        Map<Long, ItemDetail> details = catalog.load(category,
                rows.stream().map(BaseRecommendationItem::getItemId).toList());
        List<ScoredCandidate> candidates = new ArrayList<>(rows.size());
        for (BaseRecommendationItem row : rows) {
            ItemDetail detail = details.get(row.getItemId());
            if (detail == null || !Integer.valueOf(1).equals(detail.getStatus())) {
                continue;
            }
            if (city != null && !city.equals(detail.getCity())) {
                continue;
            }
            ScoredCandidate candidate = new ScoredCandidate(row.getItemId(), new HashSet<>(detail.getTags()));
            candidate.baseScore(decimal(row.getRecommendationScore()))
                    .component(ScoreComponent.POPULARITY, decimal(row.getPopularityIndex()))
                    .component(ScoreComponent.QUALITY, decimal(row.getQualityScore()))
                    .component(ScoreComponent.COLLABORATIVE, decimal(row.getCollaborativeScore()))
                    .component(ScoreComponent.CONTENT, decimal(row.getContentScore()));
            double seasonal = decimal(row.getSeasonalScore());
            // 客户端显式指定季节时，以物品季节文本实时判定（仅景点有季节字段）：
            // 物品全年适宜或含目标季节 -> 1，否则 0
            if (season != null && !season.isBlank() && detail.getSeason() != null) {
                Set<String> wanted = SeasonalAnalyzer.parseSeasons(season);
                Set<String> itemSeasons = SeasonalAnalyzer.parseSeasons(detail.getSeason());
                seasonal = itemSeasons.contains("all")
                        || wanted.stream().anyMatch(itemSeasons::contains) ? 1.0 : 0.0;
            }
            candidate.component(ScoreComponent.SEASONAL, seasonal);
            if (Boolean.TRUE.equals(row.getIsFeatured())) {
                candidate.featured(true)
                        .featureWeight(row.getFeatureWeight() == null ? 1.0 : row.getFeatureWeight().doubleValue());
            } else if (row.getFeatureWeight() != null && row.getFeatureWeight().doubleValue() > 0.0) {
                candidate.featureWeight(row.getFeatureWeight().doubleValue());
            }
            candidates.add(candidate);
        }
        return candidates;
    }

    private Map<Long, ItemFeatures> toFeatureIndex(RecommendCategory category,
                                                    Map<Long, ItemDetail> details) {
        Map<Long, ItemFeatures> index = new HashMap<>(details.size());
        for (ItemDetail d : details.values()) {
            index.put(d.getItemId(), ItemFeatures.builder()
                    .itemId(d.getItemId())
                    .category(category.code())
                    .tags(new HashSet<>(d.getTags() == null ? List.of() : d.getTags()))
                    .city(d.getCity())
                    .subType(d.getSubType())
                    .rating(d.getRating() == null ? 0 : d.getRating())
                    .price(d.getPrice() == null ? 0.0 : d.getPrice().doubleValue())
                    .seasons(d.getSeason() == null ? Set.of() : SeasonalAnalyzer.parseSeasons(d.getSeason()))
                    .counts(new HashMap<>())
                    .monthlyCounts(new HashMap<>())
                    .active(true)
                    .build());
        }
        return index;
    }

    private Map<Long, List<Neighbor>> toNeighborMap(List<RecommendationItemSimilarity> edges) {
        Map<Long, List<Neighbor>> map = new HashMap<>();
        if (edges == null) {
            return map;
        }
        for (RecommendationItemSimilarity edge : edges) {
            map.computeIfAbsent(edge.getItemId(), k -> new ArrayList<>())
                    .add(new Neighbor(edge.getNeighborId(),
                            edge.getSimilarity() == null ? 0.0 : edge.getSimilarity().doubleValue()));
        }
        return map;
    }

    private double actionWeight(String actionType) {
        if (actionType == null) {
            return 1.0;
        }
        return switch (actionType) {
            case "LIKE" -> configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_LIKE);
            case "COLLECT" -> configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_COLLECT);
            case "COMMENT" -> configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_COMMENT);
            case "CART" -> configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_CART);
            case "ORDER" -> configService.getDouble(null, RecommendationDefaults.BEHAVIOR_WEIGHT_ORDER);
            default -> 1.0;
        };
    }

    private Map<Long, ItemDetail> detailsOf(RecommendCategory category, List<ScoredCandidate> ranked) {
        return catalog.load(category, ranked.stream().map(ScoredCandidate::getItemId).toList());
    }

    private RecommendationResultVO buildResult(RecommendCategory category, ExposureSource source,
                                               StrategyParameters params,
                                               List<ScoredCandidate> ranked,
                                               Map<Long, ItemDetail> details) {
        List<RecommendationItemVO> items = new ArrayList<>(ranked.size());
        for (ScoredCandidate c : ranked) {
            RecommendationItemVO vo = toVO(c.getItemId(), c.getFinalScore(),
                    items.size() + 1, details.get(c.getItemId()));
            vo.setFeatured(c.isFeatured());
            items.add(vo);
        }
        RecommendationResultVO result = new RecommendationResultVO();
        result.setCategory(category.code());
        result.setSource(source.name());
        result.setStrategy(params.strategy().name());
        result.setRequestId(newRequestId());
        result.setGeneratedAt(LocalDateTime.now());
        result.setItems(items);
        return result;
    }

    private void fillDetailFields(List<RecommendationItemVO> items, Map<Long, ItemDetail> details) {
        for (RecommendationItemVO vo : items) {
            ItemDetail d = details.get(vo.getItemId());
            if (d != null) {
                vo.setName(d.getName());
                vo.setCoverImage(d.getCoverImage());
                vo.setCity(d.getCity());
                vo.setProvince(d.getProvince());
                vo.setPrice(d.getPrice());
                vo.setRating(d.getRating());
                vo.setTags(d.getTags());
            }
        }
    }

    private RecommendationItemVO toVO(long itemId, double score, int rank, ItemDetail detail) {
        RecommendationItemVO vo = new RecommendationItemVO();
        vo.setItemId(itemId);
        vo.setScore(BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP));
        vo.setRankPosition(rank);
        vo.setFeatured(false);
        if (detail != null) {
            vo.setName(detail.getName());
            vo.setCoverImage(detail.getCoverImage());
            vo.setCity(detail.getCity());
            vo.setProvince(detail.getProvince());
            vo.setPrice(detail.getPrice());
            vo.setRating(detail.getRating());
            vo.setTags(detail.getTags());
        }
        return vo;
    }

    private void reindex(List<RecommendationItemVO> items) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setRankPosition(i + 1);
        }
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private static double decimal(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private static String newRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // ---------------- 缓存 ----------------

    private RecommendationResultVO readCache(String key) {
        if (redisTemplate == null) {
            return null;
        }
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof RecommendationResultVO vo) {
                return vo;
            }
        } catch (Exception e) {
            log.warn("读取推荐列表缓存失败 key={}: {}", key, e.getMessage());
        }
        return null;
    }

    private void writeCache(String key, RecommendationResultVO result) {
        if (redisTemplate == null) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, result, GLOBAL_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("写入推荐列表缓存失败 key={}: {}", key, e.getMessage());
        }
    }
}
