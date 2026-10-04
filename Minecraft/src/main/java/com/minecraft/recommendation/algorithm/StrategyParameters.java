package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ScoreComponent;
import com.minecraft.recommendation.enums.RecommendStrategy;

/**
 * 推荐策略参数（不可变）。
 * <p>
 * HYBRID 下五因子权重在构造时归一化（和为 1），因此管理后台配置的权重无需手动凑 1；
 * POPULAR/CONTENT/COLLABORATIVE 预设为单因子 1.0，便于运营对比口径。
 */
public record StrategyParameters(RecommendStrategy strategy,
                                 double weightCollaborative,
                                 double weightContent,
                                 double weightPopularity,
                                 double weightSeasonal,
                                 double weightQuality,
                                 int neighborK,
                                 double diversityStrength,
                                 double featuredBoost) {

    /** HYBRID 默认权重（与 RecommendationDefaults 种子一致） */
    public static final double DEFAULT_COLLABORATIVE = 0.25;
    public static final double DEFAULT_CONTENT = 0.25;
    public static final double DEFAULT_POPULARITY = 0.20;
    public static final double DEFAULT_SEASONAL = 0.10;
    public static final double DEFAULT_QUALITY = 0.20;

    public static final int MIN_NEIGHBOR_K = 1;
    public static final int MAX_NEIGHBOR_K = 200;

    public StrategyParameters {
        neighborK = Math.max(MIN_NEIGHBOR_K, Math.min(MAX_NEIGHBOR_K, neighborK));
        diversityStrength = Normalizers.clamp01(diversityStrength);
        featuredBoost = Normalizers.clamp01(featuredBoost);
    }

    /**
     * 按策略构造；原始权重仅在 HYBRID 下使用并自动归一化。
     */
    public static StrategyParameters of(RecommendStrategy strategy,
                                        double rawCollaborative,
                                        double rawContent,
                                        double rawPopularity,
                                        double rawSeasonal,
                                        double rawQuality,
                                        int neighborK,
                                        double diversityStrength,
                                        double featuredBoost) {
        RecommendStrategy s = strategy == null ? RecommendStrategy.HYBRID : strategy;
        double wCollab = 0.0;
        double wContent = 0.0;
        double wPopularity = 0.0;
        double wSeasonal = 0.0;
        double wQuality = 0.0;
        switch (s) {
            case POPULAR -> wPopularity = 1.0;
            case CONTENT -> wContent = 1.0;
            case COLLABORATIVE -> wCollab = 1.0;
            case HYBRID -> {
                double sum = rawCollaborative + rawContent + rawPopularity + rawSeasonal + rawQuality;
                if (sum <= 0.0) {
                    wCollab = DEFAULT_COLLABORATIVE;
                    wContent = DEFAULT_CONTENT;
                    wPopularity = DEFAULT_POPULARITY;
                    wSeasonal = DEFAULT_SEASONAL;
                    wQuality = DEFAULT_QUALITY;
                } else {
                    wCollab = rawCollaborative / sum;
                    wContent = rawContent / sum;
                    wPopularity = rawPopularity / sum;
                    wSeasonal = rawSeasonal / sum;
                    wQuality = rawQuality / sum;
                }
            }
        }
        return new StrategyParameters(s, wCollab, wContent, wPopularity, wSeasonal, wQuality,
                neighborK, diversityStrength, featuredBoost);
    }

    /**
     * 因子权重查询。
     */
    public double weight(ScoreComponent component) {
        return switch (component) {
            case COLLABORATIVE -> weightCollaborative;
            case CONTENT -> weightContent;
            case POPULARITY -> weightPopularity;
            case SEASONAL -> weightSeasonal;
            case QUALITY -> weightQuality;
        };
    }
}
