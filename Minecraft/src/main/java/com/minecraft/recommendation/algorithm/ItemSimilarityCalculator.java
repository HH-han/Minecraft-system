package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.Neighbor;
import com.minecraft.recommendation.algorithm.model.SimilarityEdge;
import com.minecraft.recommendation.algorithm.model.UserBehavior;
import com.minecraft.recommendation.algorithm.model.UserHistory;
import com.minecraft.recommendation.algorithm.model.WeightedAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Item-CF 物品相似度离线计算（纯逻辑、确定性）。
 * <p>
 * 以「用户-物品加权行为」构造隐式评分矩阵（同一用户对同一物品的多条行为权重相加），
 * 物品相似度 = 共现用户上加权向量的余弦：
 * <pre>
 * sim(i,j) = Σ_{u∈U(i)∩U(j)} w_ui·w_uj / (‖w_i‖·‖w_j‖)
 * </pre>
 * 每个物品只保留 Top-K 邻居，双向各输出一条边。
 * <p>
 * 复杂度：设 E=行为边数，m_u=用户 u 行为物品数，k'=物品实际共现邻居数，
 * 则 O(E + Σ_u m_u² + Σ_i k'_i log k'_i)；Top-K 截尾后持久化边数 ≤ 2·n·K。
 */
public final class ItemSimilarityCalculator {

    private ItemSimilarityCalculator() {
    }

    /**
     * 基于用户历史构建 Top-K 相似度边。
     */
    public static List<SimilarityEdge> buildEdges(String category,
                                                  List<UserHistory> histories,
                                                  int k) {
        List<WeightedAction> actions = new ArrayList<>();
        if (histories != null) {
            for (UserHistory history : histories) {
                if (history == null || history.behaviors() == null) {
                    continue;
                }
                for (UserBehavior b : history.behaviors()) {
                    actions.add(new WeightedAction(history.userId(), b.itemId(), b.weight(), b.time()));
                }
            }
        }
        return buildEdges(category, actions, k, null);
    }

    /**
     * 基于加权行为明细构建 Top-K 相似度边。
     *
     * @param cutoff 非空时仅保留 time 为空或 time &gt;= cutoff 的行为（recency 窗口）
     */
    public static List<SimilarityEdge> buildEdges(String category,
                                                  List<WeightedAction> actions,
                                                  int k,
                                                  LocalDateTime cutoff) {
        if (k <= 0 || actions == null || actions.isEmpty()) {
            return List.of();
        }

        // 1. 聚合成 user -> (item -> weight)
        Map<Long, Map<Long, Double>> userItems = new HashMap<>();
        for (WeightedAction action : actions) {
            if (action.weight() <= 0.0) {
                continue;
            }
            if (cutoff != null && action.time() != null && action.time().isBefore(cutoff)) {
                continue;
            }
            userItems
                    .computeIfAbsent(action.userId(), key -> new HashMap<>())
                    .merge(action.itemId(), action.weight(), Double::sum);
        }

        // 2. 共现点积 + 向量模长平方
        Map<Long, Map<Long, Double>> dot = new HashMap<>();
        Map<Long, Double> normSq = new HashMap<>();
        for (Map<Long, Double> items : userItems.values()) {
            for (Map.Entry<Long, Double> e : items.entrySet()) {
                normSq.merge(e.getKey(), e.getValue() * e.getValue(), Double::sum);
            }
            List<Map.Entry<Long, Double>> entries = new ArrayList<>(items.entrySet());
            for (int a = 0; a < entries.size(); a++) {
                for (int b = a + 1; b < entries.size(); b++) {
                    long i = entries.get(a).getKey();
                    long j = entries.get(b).getKey();
                    double contribution = entries.get(a).getValue() * entries.get(b).getValue();
                    dot.computeIfAbsent(i, key -> new HashMap<>()).merge(j, contribution, Double::sum);
                    dot.computeIfAbsent(j, key -> new HashMap<>()).merge(i, contribution, Double::sum);
                }
            }
        }

        // 3. 余弦相似度 + Top-K 截尾（确定性次序：相似度降序，邻居 ID 升序）
        List<SimilarityEdge> edges = new ArrayList<>();
        for (Map.Entry<Long, Map<Long, Double>> e : dot.entrySet()) {
            long itemId = e.getKey();
            double normI = Math.sqrt(normSq.getOrDefault(itemId, 0.0));
            if (normI <= 0.0) {
                continue;
            }
            List<Neighbor> neighbors = new ArrayList<>();
            for (Map.Entry<Long, Double> other : e.getValue().entrySet()) {
                double normJ = Math.sqrt(normSq.getOrDefault(other.getKey(), 0.0));
                if (normJ <= 0.0) {
                    continue;
                }
                double sim = other.getValue() / (normI * normJ);
                if (sim > 0.0) {
                    neighbors.add(new Neighbor(other.getKey(), normalize(sim)));
                }
            }
            neighbors.sort(Comparator.naturalOrder());
            List<Neighbor> topK = neighbors.size() > k ? neighbors.subList(0, k) : neighbors;
            for (Neighbor n : topK) {
                edges.add(new SimilarityEdge(category, itemId, n.itemId(), n.similarity()));
            }
        }
        return edges;
    }

    /**
     * 将有向边组织为 itemId -> 邻居列表（按相似度降序）。
     */
    public static Map<Long, List<Neighbor>> neighborMap(List<SimilarityEdge> edges) {
        Map<Long, List<Neighbor>> map = new HashMap<>();
        if (edges == null) {
            return map;
        }
        for (SimilarityEdge edge : edges) {
            map.computeIfAbsent(edge.itemId(), key -> new ArrayList<>())
                    .add(new Neighbor(edge.neighborId(), edge.similarity()));
        }
        map.values().forEach(list -> list.sort(Comparator.naturalOrder()));
        return map;
    }

    /**
     * Item-CF 预测分：pred(u,i) = Σ sim(i,j)·r_uj / Σ sim(i,j)，
     * 用户行为权重需先归一化到 [0,1]（按该用户最大权重）。无共同邻居返回 0。
     *
     * @return itemId -> 协同过滤分 [0,1]，仅包含得分 &gt; 0 的物品
     */
    public static Map<Long, Double> predict(Map<Long, Double> normalizedUserWeights,
                                            Map<Long, List<Neighbor>> neighborsByItem) {
        Map<Long, Double> scores = new HashMap<>();
        if (normalizedUserWeights == null || normalizedUserWeights.isEmpty()
                || neighborsByItem == null || neighborsByItem.isEmpty()) {
            return scores;
        }
        for (Map.Entry<Long, List<Neighbor>> e : neighborsByItem.entrySet()) {
            long itemId = e.getKey();
            double weightedSum = 0.0;
            double simSum = 0.0;
            for (Neighbor n : e.getValue()) {
                Double r = normalizedUserWeights.get(n.itemId());
                if (r != null && r > 0.0) {
                    weightedSum += n.similarity() * r;
                    simSum += n.similarity();
                }
            }
            if (simSum > 0.0) {
                scores.put(itemId, Normalizers.clamp01(weightedSum / simSum));
            }
        }
        return scores;
    }

    /**
     * 将用户物品权重按最大值归一化到 [0,1]；空/全 0 输入返回空 Map。
     */
    public static Map<Long, Double> normalizeUserWeights(Map<Long, Double> rawWeights) {
        if (rawWeights == null || rawWeights.isEmpty()) {
            return Map.of();
        }
        double max = rawWeights.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        if (max <= 0.0) {
            return Collections.emptyMap();
        }
        Map<Long, Double> normalized = new HashMap<>();
        rawWeights.forEach((item, w) -> {
            if (w != null && w > 0.0) {
                normalized.put(item, Math.min(1.0, w / max));
            }
        });
        return normalized;
    }

    private static double normalize(double sim) {
        if (Double.isNaN(sim) || sim < 0.0) {
            return 0.0;
        }
        return Math.min(sim, 1.0);
    }
}
