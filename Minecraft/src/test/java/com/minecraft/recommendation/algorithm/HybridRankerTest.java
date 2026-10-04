package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ScoreComponent;
import com.minecraft.recommendation.algorithm.model.ScoredCandidate;
import com.minecraft.recommendation.enums.RecommendStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HybridRankerTest {

    private static final double EPS = 1e-12;

    private StrategyParameters hybrid(double diversity) {
        return StrategyParameters.of(RecommendStrategy.HYBRID,
                0.25, 0.25, 0.20, 0.10, 0.20, 20, diversity, 0.15);
    }

    private ScoredCandidate candidate(long id, double pop, double season, double quality, String... tags) {
        return new ScoredCandidate(id, tags.length == 0 ? Set.of() : Set.of(tags))
                .component(ScoreComponent.POPULARITY, pop)
                .component(ScoreComponent.SEASONAL, season)
                .component(ScoreComponent.QUALITY, quality);
    }

    @Test
    void rankGlobal_empty_returnsEmpty() {
        assertTrue(HybridRanker.rankGlobal(List.of(), hybrid(0.0), 10).isEmpty());
    }

    @Test
    void rankGlobal_coldStart_neverEmptyWhileActiveItemsExist() {
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(
                List.of(candidate(1L, 0, 0, 0), candidate(2L, 0, 0, 0)), hybrid(0.0), 10);
        assertEquals(2, ranked.size());
    }

    @Test
    void rankGlobal_usesStoredBaseScore() {
        ScoredCandidate a = candidate(1L, 0.9, 1, 1).baseScore(0.1);
        ScoredCandidate b = candidate(2L, 0.1, 0, 0).baseScore(0.9);
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(List.of(a, b), hybrid(0.0), 10);
        assertEquals(2L, ranked.get(0).getItemId());
        assertEquals(0.9, ranked.get(0).getFinalScore(), EPS);
    }

    @Test
    void rankGlobal_fallsBackToCompositeWhenNoBaseScore() {
        // 无基线分时按热度/季节/品质（0.2/0.1/0.2 → 归一 0.4/0.2/0.4）
        ScoredCandidate hot = candidate(1L, 1.0, 0, 0);
        ScoredCandidate seasonal = candidate(2L, 0, 1.0, 0);
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(List.of(hot, seasonal), hybrid(0.0), 10);
        assertEquals(1L, ranked.get(0).getItemId());
        assertEquals(0.4, ranked.get(0).getFinalScore(), EPS);
        assertEquals(0.2, ranked.get(1).getFinalScore(), EPS);
    }

    @Test
    void excludedItems_removed() {
        ScoredCandidate a = candidate(1L, 1, 1, 1).excluded(true);
        ScoredCandidate b = candidate(2L, 0.1, 0.1, 0.1);
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(List.of(a, b), hybrid(0.0), 10);
        assertEquals(1, ranked.size());
        assertEquals(2L, ranked.get(0).getItemId());
    }

    @Test
    void featuredItems_pinnedFirst_sortedByWeightThenScore() {
        ScoredCandidate normal = candidate(1L, 1.0, 1, 1);
        ScoredCandidate pinnedWeak = candidate(2L, 0.1, 0, 0).featured(true).featureWeight(0.2);
        ScoredCandidate pinnedStrong = candidate(3L, 0.1, 0, 0).featured(true).featureWeight(0.9);
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(
                List.of(normal, pinnedWeak, pinnedStrong), hybrid(0.0), 10);
        assertEquals(List.of(3L, 2L, 1L), ranked.stream().map(ScoredCandidate::getItemId).toList());
    }

    @Test
    void nonPinnedFeatureWeight_getsBoost() {
        ScoredCandidate boosted = candidate(1L, 0.5, 0.5, 0.5).featureWeight(1.0);
        ScoredCandidate plain = candidate(2L, 0.9, 0.9, 0.9);
        // base：plain=0.9 > boosted 兜底 0.5；boosted 获得 +0.15 → 0.65 仍小于 0.9 → plain 在前
        List<ScoredCandidate> rankedNoBoost = HybridRanker.rankGlobal(
                List.of(boosted, plain),
                StrategyParameters.of(RecommendStrategy.HYBRID, 1, 1, 1, 1, 1, 20, 0, 0.0), 10);
        assertEquals(2L, rankedNoBoost.get(0).getItemId());

        ScoredCandidate boosted2 = candidate(3L, 0.5, 0.5, 0.5).featureWeight(1.0);
        ScoredCandidate plain2 = candidate(4L, 0.55, 0.55, 0.55);
        List<ScoredCandidate> rankedBoost = HybridRanker.rankGlobal(
                List.of(boosted2, plain2), hybrid(0.0), 10);
        // 0.5 + 0.15 = 0.65 > 0.55
        assertEquals(3L, rankedBoost.get(0).getItemId());
        assertEquals(0.65, rankedBoost.get(0).getFinalScore(), EPS);
    }

    @Test
    void personalized_usesFiveFactors_andWeightSensitivity() {
        ScoredCandidate strongCollab = candidate(1L, 0.2, 0.5, 0.5)
                .component(ScoreComponent.COLLABORATIVE, 1.0).component(ScoreComponent.CONTENT, 0.0);
        ScoredCandidate strongContent = candidate(2L, 0.2, 0.5, 0.5)
                .component(ScoreComponent.COLLABORATIVE, 0.0).component(ScoreComponent.CONTENT, 1.0);

        StrategyParameters favorCollab = StrategyParameters.of(RecommendStrategy.HYBRID,
                0.8, 0.05, 0.05, 0.05, 0.05, 20, 0, 0);
        List<ScoredCandidate> byCollab = HybridRanker.rankPersonalized(
                List.of(strongCollab, strongContent), favorCollab, 10);
        assertEquals(1L, byCollab.get(0).getItemId());

        StrategyParameters favorContent = StrategyParameters.of(RecommendStrategy.HYBRID,
                0.05, 0.8, 0.05, 0.05, 0.05, 20, 0, 0);
        List<ScoredCandidate> byContent = HybridRanker.rankPersonalized(
                List.of(strongCollab, strongContent), favorContent, 10);
        assertEquals(2L, byContent.get(0).getItemId());
    }

    @Test
    void strategyPresets_collaborativeVsPopular() {
        ScoredCandidate collabItem = candidate(1L, 0.1, 0, 0)
                .component(ScoreComponent.COLLABORATIVE, 1.0);
        ScoredCandidate popularItem = candidate(2L, 1.0, 0, 0)
                .component(ScoreComponent.COLLABORATIVE, 0.1);

        StrategyParameters collab = StrategyParameters.of(RecommendStrategy.COLLABORATIVE,
                0, 0, 1, 0, 0, 20, 0, 0);
        List<ScoredCandidate> collabRanked = HybridRanker.rankPersonalized(
                List.of(collabItem, popularItem), collab, 10);
        assertEquals(1L, collabRanked.get(0).getItemId());

        StrategyParameters popular = StrategyParameters.of(RecommendStrategy.POPULAR,
                1, 0, 0, 0, 0, 20, 0, 0);
        List<ScoredCandidate> popularRanked = HybridRanker.rankPersonalized(
                List.of(collabItem, popularItem), popular, 10);
        assertEquals(2L, popularRanked.get(0).getItemId());
    }

    @Test
    void personalized_withoutSignals_fallsBackToGlobal() {
        ScoredCandidate a = candidate(1L, 0, 0, 0).baseScore(0.3);
        ScoredCandidate b = candidate(2L, 0, 0, 0).baseScore(0.8);
        List<ScoredCandidate> ranked = HybridRanker.rankPersonalized(List.of(a, b), hybrid(0.0), 10);
        assertEquals(2L, ranked.get(0).getItemId());
        assertEquals(0.8, ranked.get(0).getFinalScore(), EPS);
    }

    @Test
    void limit_isClampedAndHonored() {
        List<ScoredCandidate> many = java.util.stream.IntStream.rangeClosed(1, 10)
                .mapToObj(i -> candidate(i, i / 10.0, 0, 0))
                .toList();
        List<ScoredCandidate> top3 = HybridRanker.rankGlobal(many, hybrid(0.0), 3);
        assertEquals(3, top3.size());
        // 超过 MAX_LIMIT 被截断
        List<ScoredCandidate> capped = HybridRanker.rankGlobal(many, hybrid(0.0), 500);
        assertEquals(10, capped.size());
    }

    @Test
    void determinism_repeatedRunsIdentical() {
        List<ScoredCandidate> pool = List.of(
                candidate(1L, 0.5, 0.5, 0.5, "亲子", "海滨"),
                candidate(2L, 0.5, 0.5, 0.5, "亲子", "美食"),
                candidate(3L, 0.5, 0.5, 0.5, "历史", "古镇"),
                candidate(4L, 0.5, 0.5, 0.5, "海滨", "潜水"),
                candidate(5L, 0.5, 0.5, 0.5, "亲子", "游乐园"));
        List<Long> first = null;
        for (int run = 0; run < 5; run++) {
            List<Long> order = HybridRanker.rankGlobal(pool, hybrid(0.3), 10)
                    .stream().map(ScoredCandidate::getItemId).toList();
            if (first == null) {
                first = order;
            } else {
                assertEquals(first, order);
            }
        }
    }

    @Test
    void diversity_reordersSameScoreItems_apart() {
        // 同分候选：A/B 标签高度重叠，C 标签独立；λ 大时 C 应被提前以分散标签
        ScoredCandidate a = candidate(1L, 1, 1, 1, "亲子");
        ScoredCandidate b = candidate(2L, 1, 1, 1, "亲子", "海滨");
        ScoredCandidate c = candidate(3L, 1, 1, 1, "历史");
        StrategyParameters diverse = StrategyParameters.of(RecommendStrategy.HYBRID,
                0.25, 0.25, 0.2, 0.1, 0.2, 20, 0.9, 0);

        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(List.of(a, b, c), diverse, 3);
        List<Long> ids = ranked.stream().map(ScoredCandidate::getItemId).toList();
        // 同分时确定性 ID 次序 1,2,3；多样性应把独立标签的 3 提到 2 之前
        assertNotEquals(List.of(1L, 2L, 3L), ids);
        assertEquals(1L, ids.get(0));
        assertEquals(3L, ids.get(1));
    }

    @Test
    void diversityDisabled_keepsRelevanceOrder() {
        ScoredCandidate a = candidate(1L, 0.9, 1, 1, "亲子");
        ScoredCandidate b = candidate(2L, 0.8, 1, 1, "历史");
        ScoredCandidate c = candidate(3L, 0.7, 1, 1, "美食");
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(List.of(a, b, c), hybrid(0.0), 3);
        assertEquals(List.of(1L, 2L, 3L), ranked.stream().map(ScoredCandidate::getItemId).toList());
    }

    @Test
    void scores_alwaysWithinUnitRange() {
        ScoredCandidate overshoot = candidate(1L, 5.0, 5.0, 5.0).featureWeight(2.0);
        ScoredCandidate negative = candidate(2L, -1, 0, 0);
        List<ScoredCandidate> ranked = HybridRanker.rankGlobal(
                List.of(overshoot, negative), hybrid(0.0), 10);
        for (ScoredCandidate c : ranked) {
            assertTrue(c.getFinalScore() >= 0.0 && c.getFinalScore() <= 1.0);
        }
    }
}
