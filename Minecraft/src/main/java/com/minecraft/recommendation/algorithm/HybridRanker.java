package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ScoreComponent;
import com.minecraft.recommendation.algorithm.model.ScoredCandidate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 混合推荐排序器（纯逻辑、确定性）。
 * <p>
 * 两条入口：
 * <ul>
 *   <li>{@link #rankGlobal}：匿名/冷启动，使用离线写入的全局基线分
 *       （热度+季节+品质+群体内容亲和的混合结果）；</li>
 *   <li>{@link #rankPersonalized}：登录用户，五因子加权；
 *       若候选全部没有个性化分量（用户无行为），自动回退全局口径。</li>
 * </ul>
 * 公共处理：剔除排除项 → 计算最终分 → 人工置顶/加权 → 排序 O(n log n)
 * → 标签多样性贪心重排（有界池）→ 截断 limit。
 * 相同输入必定产生相同输出（全部比较使用 itemId 作为最终次序）。
 */
public final class HybridRanker {

    /** 多样性重排的候选池上限：只在头部候选内重排，保证开销有界 */
    public static final int DIVERSITY_POOL_CAP = 150;

    /** 单次返回条数上限（服务层也会再次约束） */
    public static final int MAX_LIMIT = 100;

    private HybridRanker() {
    }

    /**
     * 全局（匿名/冷启动）排序。
     */
    public static List<ScoredCandidate> rankGlobal(List<ScoredCandidate> candidates,
                                                   StrategyParameters params,
                                                   int limit) {
        List<ScoredCandidate> active = filterActive(candidates);
        for (ScoredCandidate c : active) {
            double score = c.getBaseScore() > 0.0
                    ? c.getBaseScore()
                    : globalComposite(c, params);
            c.setFinalScore(score);
        }
        return finalize(active, params, limit);
    }

    /**
     * 个性化排序。没有任何个性化信号（协同/内容分量全为 0）时回退全局口径。
     */
    public static List<ScoredCandidate> rankPersonalized(List<ScoredCandidate> candidates,
                                                         StrategyParameters params,
                                                         int limit) {
        List<ScoredCandidate> active = filterActive(candidates);
        boolean personalized = active.stream().anyMatch(c ->
                c.getComponent(ScoreComponent.COLLABORATIVE) > 0.0
                        || c.getComponent(ScoreComponent.CONTENT) > 0.0);
        if (!personalized) {
            return rankGlobal(active, params, limit);
        }
        for (ScoredCandidate c : active) {
            double score = params.weight(ScoreComponent.COLLABORATIVE) * c.getComponent(ScoreComponent.COLLABORATIVE)
                    + params.weight(ScoreComponent.CONTENT) * c.getComponent(ScoreComponent.CONTENT)
                    + params.weight(ScoreComponent.POPULARITY) * c.getComponent(ScoreComponent.POPULARITY)
                    + params.weight(ScoreComponent.SEASONAL) * c.getComponent(ScoreComponent.SEASONAL)
                    + params.weight(ScoreComponent.QUALITY) * c.getComponent(ScoreComponent.QUALITY);
            c.setFinalScore(score);
        }
        return finalize(active, params, limit);
    }

    // ---------------- 内部步骤 ----------------

    private static List<ScoredCandidate> filterActive(List<ScoredCandidate> candidates) {
        List<ScoredCandidate> active = new ArrayList<>();
        if (candidates != null) {
            for (ScoredCandidate c : candidates) {
                if (c != null && !c.isExcluded()) {
                    active.add(c);
                }
            }
        }
        return active;
    }

    /**
     * 无离线基线分时的全局兜底：热度/季节/品质按三者权重重新归一化。
     */
    private static double globalComposite(ScoredCandidate c, StrategyParameters params) {
        double wPop = params.weight(ScoreComponent.POPULARITY);
        double wSeason = params.weight(ScoreComponent.SEASONAL);
        double wQuality = params.weight(ScoreComponent.QUALITY);
        double sum = wPop + wSeason + wQuality;
        if (sum <= 0.0) {
            return 0.0;
        }
        return (wPop * c.getComponent(ScoreComponent.POPULARITY)
                + wSeason * c.getComponent(ScoreComponent.SEASONAL)
                + wQuality * c.getComponent(ScoreComponent.QUALITY)) / sum;
    }

    /**
     * 人工干预 + 排序 + 多样性 + 截断。
     */
    private static List<ScoredCandidate> finalize(List<ScoredCandidate> active,
                                                  StrategyParameters params,
                                                  int requestedLimit) {
        if (active.isEmpty()) {
            return List.of();
        }
        int limit = Math.max(1, Math.min(MAX_LIMIT, requestedLimit));

        // 非置顶物品的人工加权（加法，随后截断）
        if (params.featuredBoost() > 0.0) {
            for (ScoredCandidate c : active) {
                if (!c.isFeatured() && c.getFeatureWeight() > 0.0) {
                    c.setFinalScore(c.getFinalScore() + params.featuredBoost() * c.getFeatureWeight());
                }
            }
        }

        List<ScoredCandidate> pinned = new ArrayList<>();
        List<ScoredCandidate> rest = new ArrayList<>();
        for (ScoredCandidate c : active) {
            (c.isFeatured() ? pinned : rest).add(c);
        }
        // 置顶：加权系数降序 → 分数降序 → ID 升序
        pinned.sort(Comparator
                .comparingDouble(ScoredCandidate::getFeatureWeight).reversed()
                .thenComparing(Comparator.comparingDouble(ScoredCandidate::getFinalScore).reversed())
                .thenComparingLong(ScoredCandidate::getItemId));
        // 普通：分数降序 → ID 升序（确定性）
        rest.sort(Comparator
                .comparingDouble(ScoredCandidate::getFinalScore).reversed()
                .thenComparingLong(ScoredCandidate::getItemId));

        int restQuota = Math.max(0, limit - pinned.size());
        List<ScoredCandidate> rankedRest = diversify(rest, params.diversityStrength(),
                restQuota, DIVERSITY_POOL_CAP);

        List<ScoredCandidate> result = new ArrayList<>(Math.min(limit, active.size()));
        result.addAll(pinned.size() > limit ? pinned.subList(0, limit) : pinned);
        result.addAll(rankedRest);
        return result;
    }

    /**
     * 标签多样性贪心重排（MMR 简化版）：
     * 每轮选择 (1-λ)·相关度 - λ·与已选最大标签 Jaccard 最大的物品。
     * 只在按相关度排序后的有界头部池内重排，复杂度 O(L²)，L ≤ poolCap。
     */
    static List<ScoredCandidate> diversify(List<ScoredCandidate> relevanceSorted,
                                           double lambda,
                                           int limit,
                                           int poolCap) {
        int need = Math.min(limit, relevanceSorted.size());
        if (need == 0) {
            return List.of();
        }
        if (lambda <= 0.0 || relevanceSorted.size() == 1) {
            return new ArrayList<>(relevanceSorted.subList(0, need));
        }
        int cap = Math.max(need, Math.min(poolCap, relevanceSorted.size()));
        List<ScoredCandidate> pool = new ArrayList<>(relevanceSorted.subList(0, cap));

        // 目标分归一化到 [0,1]（池内 min-max 退化为全 0 时按 0 处理）
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (ScoredCandidate c : pool) {
            min = Math.min(min, c.getFinalScore());
            max = Math.max(max, c.getFinalScore());
        }
        double range = max - min;

        List<ScoredCandidate> selected = new ArrayList<>(need);
        selected.add(pool.remove(0));
        while (selected.size() < need && !pool.isEmpty()) {
            ScoredCandidate best = null;
            double bestObjective = Double.NEGATIVE_INFINITY;
            for (ScoredCandidate candidate : pool) {
                double relevance = range <= 0.0 ? 0.0 : (candidate.getFinalScore() - min) / range;
                double maxSim = 0.0;
                for (ScoredCandidate chosen : selected) {
                    maxSim = Math.max(maxSim, Normalizers.jaccard(candidate.getTags(), chosen.getTags()));
                }
                double objective = (1.0 - lambda) * relevance - lambda * maxSim;
                if (objective > bestObjective
                        || (objective == bestObjective && best != null
                        && (candidate.getFinalScore() > best.getFinalScore()
                        || (candidate.getFinalScore() == best.getFinalScore()
                        && candidate.getItemId() < best.getItemId())))) {
                    bestObjective = objective;
                    best = candidate;
                }
            }
            selected.add(best);
            pool.remove(best);
        }
        // 需求超过头部池时，按原相关度顺序补足
        if (selected.size() < need) {
            for (ScoredCandidate c : relevanceSorted.subList(cap, relevanceSorted.size())) {
                if (selected.size() >= need) {
                    break;
                }
                selected.add(c);
            }
        }
        return selected;
    }
}
