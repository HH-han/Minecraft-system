package com.minecraft.recommendation.algorithm;

import com.minecraft.recommendation.algorithm.model.ScoreComponent;
import com.minecraft.recommendation.enums.RecommendStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyParametersTest {

    private static final double EPS = 1e-12;

    @Test
    void hybrid_weightsNormalized_needNotSumToOne() {
        // 配置里给任意比例（和为 5），构造时归一化
        StrategyParameters p = StrategyParameters.of(RecommendStrategy.HYBRID,
                1.25, 1.25, 1.0, 0.5, 1.0, 20, 0.3, 0.15);
        double sum = p.weightCollaborative() + p.weightContent() + p.weightPopularity()
                + p.weightSeasonal() + p.weightQuality();
        assertEquals(1.0, sum, EPS);
        assertEquals(0.25, p.weight(ScoreComponent.COLLABORATIVE), EPS);
        assertEquals(0.10, p.weight(ScoreComponent.SEASONAL), EPS);
    }

    @Test
    void hybrid_allZeroWeights_usesDefaults() {
        StrategyParameters p = StrategyParameters.of(RecommendStrategy.HYBRID, 0, 0, 0, 0, 0, 20, 0, 0);
        assertEquals(StrategyParameters.DEFAULT_COLLABORATIVE, p.weightCollaborative(), EPS);
        assertEquals(StrategyParameters.DEFAULT_QUALITY, p.weightQuality(), EPS);
    }

    @Test
    void presets_areSingleFactor() {
        StrategyParameters popular = StrategyParameters.of(RecommendStrategy.POPULAR,
                1, 0, 0, 0, 0, 20, 0, 0);
        assertEquals(1.0, popular.weight(ScoreComponent.POPULARITY), EPS);
        assertEquals(0.0, popular.weight(ScoreComponent.COLLABORATIVE), EPS);

        StrategyParameters content = StrategyParameters.of(RecommendStrategy.CONTENT,
                0, 1, 0, 0, 0, 20, 0, 0);
        assertEquals(1.0, content.weight(ScoreComponent.CONTENT), EPS);

        StrategyParameters collab = StrategyParameters.of(RecommendStrategy.COLLABORATIVE,
                0, 0, 0, 0, 1, 20, 0, 0);
        assertEquals(1.0, collab.weight(ScoreComponent.COLLABORATIVE), EPS);
    }

    @Test
    void neighborKClamped_andBoostClamped() {
        StrategyParameters p = StrategyParameters.of(RecommendStrategy.HYBRID,
                1, 1, 1, 1, 1, 9999, 5.0, -1.0);
        assertEquals(StrategyParameters.MAX_NEIGHBOR_K, p.neighborK());
        assertEquals(1.0, p.diversityStrength(), EPS);
        assertEquals(0.0, p.featuredBoost(), EPS);

        StrategyParameters p2 = StrategyParameters.of(RecommendStrategy.HYBRID,
                1, 1, 1, 1, 1, -5, 0, 0);
        assertEquals(StrategyParameters.MIN_NEIGHBOR_K, p2.neighborK());
    }
}
