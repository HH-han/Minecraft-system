package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.Neighbor;
import com.minecraft.recommendation.algorithm.model.SimilarityEdge;
import com.minecraft.recommendation.algorithm.model.UserBehavior;
import com.minecraft.recommendation.algorithm.model.UserHistory;
import com.minecraft.recommendation.algorithm.model.WeightedAction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemSimilarityCalculatorTest {

    private final LocalDateTime now = LocalDateTime.now();

    private WeightedAction action(long user, long item, double weight, LocalDateTime time) {
        return new WeightedAction(user, item, weight, time);
    }

    @Test
    void buildEdges_emptyAndInvalidK() {
        assertTrue(ItemSimilarityCalculator.buildEdges("attraction", List.of(), 5, null).isEmpty());
        assertTrue(ItemSimilarityCalculator.buildEdges("attraction",
                List.of(action(1, 1, 1.0, now)), 0, null).isEmpty());
    }

    @Test
    void buildEdges_coInteraction_symmetricAndWithinUnitRange() {
        // u1 行为 1,2,3；u2 行为 1,2 → 1-2 共现两次；u3 只行为 4（孤立，无任何边）
        List<WeightedAction> actions = List.of(
                action(1, 1, 1, now), action(1, 2, 1, now), action(1, 3, 1, now),
                action(2, 1, 1, now), action(2, 2, 1, now),
                action(3, 4, 1, now));
        List<SimilarityEdge> edges = ItemSimilarityCalculator.buildEdges("attraction", actions, 10, null);

        Map<Long, Map<Long, Double>> byItem = new HashMap<>();
        for (SimilarityEdge e : edges) {
            byItem.computeIfAbsent(e.itemId(), k -> new HashMap<>()).put(e.neighborId(), e.similarity());
            assertTrue(e.similarity() > 0.0 && e.similarity() <= 1.0);
        }
        // 对称：每个共现对两个方向都有边
        for (SimilarityEdge e : new ArrayList<>(edges)) {
            assertEquals(e.similarity(), byItem.get(e.neighborId()).get(e.itemId()), 1e-12);
        }
        // 1 与 2 被两个用户共现，1 与 3 只被一个用户共现
        assertTrue(byItem.get(1L).get(2L) > byItem.get(1L).get(3L));
        // 孤立物品 4 不出现在任何边中
        assertFalse(byItem.containsKey(4L));
    }

    @Test
    void buildEdges_respectsTopK() {
        // hub 物品 1 与 6 个物品共现，K=2 时只保留最相似的 2 个邻居
        List<WeightedAction> actions = new ArrayList<>();
        long[] neighbors = {2, 3, 4, 5, 6, 7};
        for (int u = 0; u < neighbors.length; u++) {
            actions.add(action(u + 10, 1, 1, now));
            actions.add(action(u + 10, neighbors[u], 1, now));
        }
        // 让 2、3 与 1 多一次共现 → 相似度更高
        actions.add(action(99, 1, 1, now));
        actions.add(action(99, 2, 1, now));
        actions.add(action(99, 3, 1, now));

        List<SimilarityEdge> edges = ItemSimilarityCalculator.buildEdges("attraction", actions, 2, null);
        long outgoingFrom1 = edges.stream().filter(e -> e.itemId() == 1L).count();
        assertEquals(2L, outgoingFrom1);
        List<SimilarityEdge> top = edges.stream().filter(e -> e.itemId() == 1L).toList();
        assertEquals(2L, top.get(0).neighborId());
        assertEquals(3L, top.get(1).neighborId());
    }

    @Test
    void buildEdges_cutoff_ignoresOldInteractions() {
        LocalDateTime cutoff = now.minusDays(90);
        List<WeightedAction> actions = List.of(
                action(1, 1, 1, now),
                action(1, 2, 1, now),
                action(2, 1, 1, now.minusDays(200)),
                action(2, 2, 1, now.minusDays(200)));
        // 窗口内只有 u1 的边，1、2 仍共现（一个用户），但 u2 被整体丢弃
        List<SimilarityEdge> edges = ItemSimilarityCalculator.buildEdges("attraction", actions, 10, cutoff);
        assertEquals(2, edges.size());
        edges.forEach(e -> assertTrue(e.similarity() > 0.0));
    }

    @Test
    void buildEdges_zeroAndNegativeWeights_ignored() {
        List<WeightedAction> actions = List.of(
                action(1, 1, 0.0, now),
                action(1, 2, -1.0, now));
        assertTrue(ItemSimilarityCalculator.buildEdges("attraction", actions, 10, null).isEmpty());
    }

    @Test
    void buildEdges_fromHistories_aggregatesWeights() {
        UserHistory history = new UserHistory(1L, List.of(
                new UserBehavior(1L, 2.0, now),
                new UserBehavior(2L, 3.0, now)));
        List<SimilarityEdge> edges = ItemSimilarityCalculator.buildEdges("hotel", List.of(history), 5);
        // 单用户两物品 → 双向边，余弦恒为 1
        assertEquals(2, edges.size());
        assertEquals(1.0, edges.get(0).similarity(), 1e-12);
        assertEquals("hotel", edges.get(0).category());
    }

    @Test
    void predict_weightedNeighborAverage() {
        Map<Long, List<Neighbor>> neighbors = Map.of(
                10L, List.of(new Neighbor(1L, 1.0), new Neighbor(2L, 0.5)),
                20L, List.of(new Neighbor(3L, 0.8)));
        Map<Long, Double> userWeights = ItemSimilarityCalculator.normalizeUserWeights(
                Map.of(1L, 6.0, 2L, 3.0));
        // 归一化后 1→1.0, 2→0.5
        Map<Long, Double> scores = ItemSimilarityCalculator.predict(userWeights, neighbors);
        // pred(10) = (1*1 + 0.5*0.5)/(1+0.5) = 1.25/1.5
        assertEquals(1.25 / 1.5, scores.get(10L), 1e-12);
        // 20 的邻居 3 用户未交互 → 无预测
        assertFalse(scores.containsKey(20L));
    }

    @Test
    void normalizeUserWeights_zeroAndEmptySafe() {
        assertTrue(ItemSimilarityCalculator.normalizeUserWeights(Map.of()).isEmpty());
        assertTrue(ItemSimilarityCalculator.normalizeUserWeights(Map.of(1L, 0.0)).isEmpty());
        Map<Long, Double> n = ItemSimilarityCalculator.normalizeUserWeights(Map.of(1L, 2.0, 2L, 4.0));
        assertEquals(0.5, n.get(1L), 1e-12);
        assertEquals(1.0, n.get(2L), 1e-12);
    }
}
