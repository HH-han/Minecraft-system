package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ItemFeatures;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PopularityBuilderTest {

    private static final double EPS = 1e-12;

    private final Map<String, Double> weights = Map.of(
            PopularityBuilder.ACTION_LIKE, 1.0,
            PopularityBuilder.ACTION_COLLECT, 3.0,
            PopularityBuilder.ACTION_COMMENT, 2.0,
            PopularityBuilder.ACTION_CART, 4.0,
            PopularityBuilder.ACTION_ORDER, 6.0);

    private ItemFeatures item(long id, double likes, double orders) {
        Map<String, Double> counts = new HashMap<>();
        counts.put(PopularityBuilder.ACTION_LIKE, likes);
        counts.put(PopularityBuilder.ACTION_ORDER, orders);
        return ItemFeatures.builder()
                .itemId(id).category("attraction")
                .counts(counts)
                .monthlyCounts(new HashMap<>())
                .active(true)
                .build();
    }

    @Test
    void build_empty_returnsEmpty() {
        assertTrue(PopularityBuilder.build(List.of(), weights, 10.0).isEmpty());
    }

    @Test
    void build_minMaxRange_dominantItemCompressedButStillTop() {
        // 头部物品行为量是尾部的 1000 倍，对数压缩后仍排第一
        List<ItemFeatures> items = List.of(
                item(1L, 1000.0, 100.0),
                item(2L, 10.0, 1.0),
                item(3L, 1.0, 0.0));
        Map<Long, Double> pop = PopularityBuilder.build(items, weights, 10.0);

        assertEquals(0.0, pop.get(3L), EPS);
        assertEquals(1.0, pop.get(1L), EPS);
        assertTrue(pop.get(2L) > 0.0 && pop.get(2L) < 1.0);
        // 压缩后腰部物品不至于被头部碾压到接近 0（与线性 min-max 对比）
        // 线性口径下腰部仅 16/1600≈0.01，对数压缩后约 0.32，显著缓解头部垄断
        assertTrue(pop.get(2L) > 0.15, "log compression should keep mid item meaningful: " + pop.get(2L));
        pop.values().forEach(v -> assertTrue(v >= 0.0 && v <= 1.0));
    }

    @Test
    void build_allZero_returnsZeros() {
        Map<Long, Double> pop = PopularityBuilder.build(
                List.of(item(1L, 0, 0), item(2L, 0, 0)), weights, 10.0);
        assertEquals(0.0, pop.get(1L), EPS);
        assertEquals(0.0, pop.get(2L), EPS);
    }

    @Test
    void weightedCount_respectsActionWeights() {
        ItemFeatures f = item(7L, 2.0, 3.0);
        assertEquals(2.0 * 1.0 + 3.0 * 6.0, PopularityBuilder.weightedCount(f, weights), EPS);
        assertEquals(0.0, PopularityBuilder.weightedCount(null, weights), EPS);
    }
}
