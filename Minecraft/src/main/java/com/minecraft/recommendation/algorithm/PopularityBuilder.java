package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ItemFeatures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 热度分构建：加权行为计数 -> 对数压缩 -> min-max 归一化。
 * <p>
 * 加权计数 = Σ 行为次数 × 行为权重（like/collect/comment/cart/order），
 * 对数压缩抑制头部垄断，最终跨物品 min-max 得到 [0,1] 的热度指数。
 * 复杂度 O(n)（n=物品数）。
 */
public final class PopularityBuilder {

    public static final String ACTION_LIKE = "like";
    public static final String ACTION_COLLECT = "collect";
    public static final String ACTION_COMMENT = "comment";
    public static final String ACTION_CART = "cart";
    public static final String ACTION_ORDER = "order";

    private PopularityBuilder() {
    }

    /**
     * @param items          全部物品（含行为计数）
     * @param actionWeights  行为类型 -> 权重（非负）
     * @param logBase        对数压缩底数（>1）
     * @return itemId -> 热度指数 [0,1]，保持入参顺序
     */
    public static Map<Long, Double> build(List<ItemFeatures> items,
                                          Map<String, Double> actionWeights,
                                          double logBase) {
        Map<Long, Double> result = new LinkedHashMap<>();
        if (items == null || items.isEmpty()) {
            return result;
        }
        List<Long> ids = new ArrayList<>(items.size());
        List<Double> compressed = new ArrayList<>(items.size());
        for (ItemFeatures item : items) {
            double raw = weightedCount(item, actionWeights);
            ids.add(item.getItemId());
            compressed.add(Normalizers.logCompress(raw, logBase));
        }
        List<Double> normalized = Normalizers.minMax(compressed);
        for (int i = 0; i < ids.size(); i++) {
            result.put(ids.get(i), normalized.get(i));
        }
        return result;
    }

    /**
     * 单物品加权原始计数（不压缩、不归一化），供按月统计复用。
     */
    public static double weightedCount(ItemFeatures item, Map<String, Double> actionWeights) {
        if (item == null || item.getCounts() == null || actionWeights == null) {
            return 0.0;
        }
        double sum = 0.0;
        for (Map.Entry<String, Double> e : item.getCounts().entrySet()) {
            Double w = actionWeights.get(e.getKey());
            if (w != null && e.getValue() != null) {
                sum += Math.max(0.0, e.getValue()) * Math.max(0.0, w);
            }
        }
        return sum;
    }
}
