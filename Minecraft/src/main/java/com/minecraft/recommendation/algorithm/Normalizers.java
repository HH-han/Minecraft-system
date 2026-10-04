package com.minecraft.recommendation.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 无状态数学工具：min-max 归一化、对数压缩、稀疏向量余弦、时间衰减。
 * 全部纯函数，便于确定性单测。
 */
public final class Normalizers {

    private Normalizers() {
    }

    /**
     * min-max 归一化到 [0,1]。
     * 空输入返回空列表；最大值等于最小值（含全 0）时全部映射为 0，避免除零。
     */
    public static List<Double> minMax(Collection<Double> values) {
        List<Double> result = new ArrayList<>(values.size());
        if (values.isEmpty()) {
            return result;
        }
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (Double v : values) {
            double x = v == null ? 0.0 : v;
            min = Math.min(min, x);
            max = Math.max(max, x);
        }
        double range = max - min;
        for (Double v : values) {
            double x = v == null ? 0.0 : v;
            result.add(range <= 0.0 ? 0.0 : (x - min) / range);
        }
        return result;
    }

    /**
     * 对数压缩：log_base(1 + max(0,x))。缓解头部物品热度垄断。
     * base 必须大于 1，非法时退化为自然对数。
     */
    public static double logCompress(double x, double base) {
        double v = Math.max(0.0, x);
        if (base <= 1.0) {
            return Math.log1p(v);
        }
        return Math.log1p(v) / Math.log(base);
    }

    /**
     * 非负稀疏向量余弦，结果 [0,1]；任一向量为零向量返回 0。
     * 遍历较小的向量以降低稀疏场景开销。
     */
    public static double cosine(Map<String, Double> a, Map<String, Double> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Map<String, Double> small = a.size() <= b.size() ? a : b;
        Map<String, Double> large = small == a ? b : a;
        double dot = 0.0;
        for (Map.Entry<String, Double> e : small.entrySet()) {
            Double other = large.get(e.getKey());
            if (other != null) {
                dot += e.getValue() * other;
            }
        }
        double normA = norm(a);
        double normB = norm(b);
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        double sim = dot / (normA * normB);
        if (sim < 0.0) {
            return 0.0;
        }
        return Math.min(sim, 1.0);
    }

    /**
     * 集合 Jaccard 相似度 [0,1]，双空集返回 0。
     */
    public static double jaccard(Iterable<String> a, Iterable<String> b) {
        if (a == null || b == null) {
            return 0.0;
        }
        java.util.Set<String> setB = new java.util.HashSet<>();
        b.forEach(setB::add);
        if (setB.isEmpty()) {
            return 0.0;
        }
        java.util.Set<String> setA = new java.util.HashSet<>();
        a.forEach(setA::add);
        if (setA.isEmpty()) {
            return 0.0;
        }
        int intersection = 0;
        for (String t : setA) {
            if (setB.contains(t)) {
                intersection++;
            }
        }
        int unionSize = setA.size() + setB.size() - intersection;
        return unionSize == 0 ? 0.0 : (double) intersection / unionSize;
    }

    /**
     * 指数时间衰减：0.5^(ageDays / halfLifeDays)。
     * 半衰天数非法时不衰减（返回 1）；未来时间截断为不衰减。
     */
    public static double exponentialDecay(long ageDays, double halfLifeDays) {
        if (halfLifeDays <= 0.0 || ageDays <= 0L) {
            return 1.0;
        }
        return Math.pow(0.5, ageDays / halfLifeDays);
    }

    /** 截断到 [0,1]。 */
    public static double clamp01(double v) {
        if (Double.isNaN(v) || v < 0.0) {
            return 0.0;
        }
        return Math.min(v, 1.0);
    }

    private static double norm(Map<String, Double> vector) {
        double sum = 0.0;
        for (double v : vector.values()) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }
}
