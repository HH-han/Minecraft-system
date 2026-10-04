package com.minecraft.recommendation.service;

import com.minecraft.entity.recommendation.BaseRecommendationItem;
import com.minecraft.entity.recommendation.RecommendationRule;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.dto.ItemDetail;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.enums.RecommendationRuleType;
import com.minecraft.recommendation.vo.ClientRecommendationItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 客户端规则推荐服务。
 * <p>
 * 排序规则：
 * <ol>
 *   <li>HIDE 规则命中的物品直接剔除；</li>
 *   <li>PIN 规则命中的物品按 sortOrder 升序置顶（即使综合分较低也会被补捞）；</li>
 *   <li>其余物品按综合分降序；BOOST 规则命中时最终分 = 综合分 + boost*(1-综合分)；</li>
 *   <li>没有任何人工规则时，结果等价于纯综合分推荐；</li>
 *   <li>最后应用业务级干预规则（intervention_rule，条件-动作模式）：
 *       FILTER 筛选 / SORT 排序 / HIDE 隐藏 / PIN 置顶 / BOOST 加权重排。</li>
 * </ol>
 * 城市过滤：参数非空时与物品 city 精确匹配。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientRecommendationService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;
    private static final int POOL_BASE = 100;
    private static final int POOL_FACTOR = 5;
    private static final int POOL_MAX = 200;

    private final RecommendationMapperRegistry registry;
    private final RecommendationItemCatalog catalog;
    private final RecommendationRuleService ruleService;
    private final InterventionRuleService interventionRuleService;

    /**
     * 获取客户端推荐列表。
     *
     * @param category 推荐分类
     * @param limit    返回条数（1-50，默认 10）
     * @param city     城市精确过滤，空=不过滤
     */
    public List<ClientRecommendationItemVO> list(RecommendCategory category, Integer limit, String city) {
        int size = normalizeLimit(limit);
        LocalDateTime now = LocalDateTime.now();
        String cityFilter = city == null || city.isBlank() ? null : city.trim();

        // 1. 解析生效规则
        List<RecommendationRule> rules = ruleService.activeRules(category, now);
        Set<Long> hidden = new HashSet<>();
        Map<Long, BigDecimal> boosts = new HashMap<>();
        // 置顶顺序即规则返回顺序（sortOrder 升序）
        List<Long> pinOrder = new ArrayList<>();
        for (RecommendationRule rule : rules) {
            RecommendationRuleType type = RecommendationRuleType.valueOf(rule.getRuleType());
            switch (type) {
                case HIDE -> hidden.add(rule.getItemId());
                case PIN -> {
                    if (!hidden.contains(rule.getItemId())) {
                        pinOrder.add(rule.getItemId());
                    }
                }
                case BOOST -> {
                    if (!hidden.contains(rule.getItemId())) {
                        boosts.put(rule.getItemId(), rule.getBoostWeight());
                    }
                }
            }
        }
        // HIDE 优先：若先出现 PIN 后出现 HIDE，需移除已加入的置顶项
        pinOrder.removeIf(hidden::contains);

        // 2. 综合分候选池（在架）
        int poolSize = Math.min(POOL_MAX, Math.max(size * POOL_FACTOR, POOL_BASE));
        List<BaseRecommendationItem> rows = new ArrayList<>(
                registry.selectActiveScored(category, poolSize));

        // 3. 置顶物品可能不在高分池内，按规则单独补捞
        Set<Long> pooledIds = new HashSet<>();
        for (BaseRecommendationItem row : rows) {
            pooledIds.add(row.getItemId());
        }
        List<Long> missingPins = pinOrder.stream()
                .filter(id -> !pooledIds.contains(id) && !hidden.contains(id))
                .toList();
        if (!missingPins.isEmpty()) {
            rows.addAll(registry.selectScoredByItemIds(category, missingPins));
        }

        // 4. 物品详情 + 城市/隐藏过滤
        Map<Long, ItemDetail> details = catalog.load(category,
                rows.stream().map(BaseRecommendationItem::getItemId).toList());
        Map<Long, BaseRecommendationItem> rowById = new LinkedHashMap<>();
        for (BaseRecommendationItem row : rows) {
            ItemDetail detail = details.get(row.getItemId());
            if (detail == null || detail.getStatus() == null || detail.getStatus() != 1) {
                continue;
            }
            if (hidden.contains(row.getItemId())) {
                continue;
            }
            if (cityFilter != null && !cityFilter.equals(detail.getCity())) {
                continue;
            }
            rowById.put(row.getItemId(), row);
        }

        // 5. 置顶段（按规则位序，去重）
        List<Long> orderedIds = new ArrayList<>();
        Set<Long> pinned = new HashSet<>();
        for (Long itemId : pinOrder) {
            if (rowById.containsKey(itemId) && pinned.add(itemId)) {
                orderedIds.add(itemId);
            }
        }

        // 6. 自然段：综合分（含 BOOST 加成）降序
        List<BaseRecommendationItem> natural = new ArrayList<>();
        for (BaseRecommendationItem row : rowById.values()) {
            if (!pinned.contains(row.getItemId())) {
                natural.add(row);
            }
        }
        natural.sort(Comparator
                .comparingDouble((BaseRecommendationItem r) ->
                        effectiveScore(r.getRecommendationScore(), boosts.get(r.getItemId())))
                .reversed()
                .thenComparingLong(BaseRecommendationItem::getItemId));
        for (BaseRecommendationItem row : natural) {
            orderedIds.add(row.getItemId());
        }

        // 7. 业务级干预规则（intervention_rule）：条件-动作模式，支持筛选/排序/隐藏/置顶/加权
        List<InterventionRuleService.Candidate> candidates = new ArrayList<>(orderedIds.size());
        for (Long itemId : orderedIds) {
            BaseRecommendationItem row = rowById.get(itemId);
            ItemDetail detail = details.get(itemId);
            candidates.add(new InterventionRuleService.Candidate(
                    itemId,
                    effectiveScore(row.getRecommendationScore(), boosts.get(itemId)),
                    detail == null ? null : detail.getPrice(),
                    detail == null ? null : detail.getRating()));
        }
        InterventionRuleService.AppliedOrder applied =
                interventionRuleService.apply(category.code(), candidates, now);
        List<Long> finalOrder = applied.orderedIds();
        Set<Long> enginePinned = applied.pinnedIds();

        // 8. 裁剪 + 组装 VO
        List<ClientRecommendationItemVO> result = new ArrayList<>(Math.min(size, finalOrder.size()));
        int rank = 1;
        for (Long itemId : finalOrder) {
            if (result.size() >= size) {
                break;
            }
            BaseRecommendationItem row = rowById.get(itemId);
            ItemDetail detail = details.get(itemId);
            boolean isPinned = pinned.contains(itemId) || enginePinned.contains(itemId);
            result.add(toVO(row, detail, rank, isPinned, boosts.get(itemId)));
            rank++;
        }
        return result;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    /**
     * BOOST 加权：向 1 线性拉近；无加权规则时返回原综合分。
     */
    private double effectiveScore(BigDecimal base, BigDecimal boost) {
        double s = base == null ? 0.0 : base.doubleValue();
        s = Math.max(0.0, Math.min(1.0, s));
        if (boost == null) {
            return s;
        }
        double b = boost.doubleValue();
        return Math.max(0.0, Math.min(1.0, s + b * (1.0 - s)));
    }

    private ClientRecommendationItemVO toVO(BaseRecommendationItem row, ItemDetail detail,
                                            int rank, boolean isPinned, BigDecimal boost) {
        ClientRecommendationItemVO vo = new ClientRecommendationItemVO();
        vo.setItemId(row.getItemId());
        if (detail != null) {
            vo.setName(detail.getName());
            vo.setImage(detail.getCoverImage());
            vo.setCity(detail.getCity());
            vo.setProvince(detail.getProvince());
            vo.setPrice(detail.getPrice());
            vo.setRating(detail.getRating());
            vo.setTags(detail.getTags());
            vo.setSubType(detail.getSubType());
        }
        BigDecimal finalScore = BigDecimal.valueOf(
                effectiveScore(row.getRecommendationScore(), boost))
                .setScale(4, RoundingMode.HALF_UP);
        vo.setScore(finalScore);
        if (isPinned) {
            vo.setRuleType(RecommendationRuleType.PIN.name());
        } else if (boost != null) {
            vo.setRuleType(RecommendationRuleType.BOOST.name());
        }
        vo.setFeatured(isPinned);
        vo.setRankPosition(rank);
        return vo;
    }
}
