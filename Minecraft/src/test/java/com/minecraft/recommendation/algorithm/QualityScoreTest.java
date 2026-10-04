package com.minecraft.recommendation.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QualityScoreTest {

    private static final double EPS = 1e-12;

    @Test
    void of_ratingScaled_unknownIsNeutral() {
        assertEquals(1.0, QualityScore.of(5), EPS);
        assertEquals(0.8, QualityScore.of(4), EPS);
        assertEquals(0.5, QualityScore.of(0), EPS);
        assertEquals(0.5, QualityScore.of(-1), EPS);
        assertEquals(1.0, QualityScore.of(99), EPS);
    }
}
