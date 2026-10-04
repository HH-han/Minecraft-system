package com.minecraft.recommendation.algorithm;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NormalizersTest {

    private static final double EPS = 1e-12;

    @Test
    void minMax_empty_returnsEmpty() {
        assertTrue(Normalizers.minMax(List.of()).isEmpty());
    }

    @Test
    void minMax_allEqual_returnsZeros() {
        List<Double> result = Normalizers.minMax(List.of(3.0, 3.0, 3.0));
        assertEquals(List.of(0.0, 0.0, 0.0), result);
    }

    @Test
    void minMax_allZero_returnsZeros() {
        List<Double> result = Normalizers.minMax(List.of(0.0, 0.0));
        assertEquals(List.of(0.0, 0.0), result);
    }

    @Test
    void minMax_spansRange() {
        List<Double> result = Normalizers.minMax(List.of(2.0, 4.0, 6.0));
        assertEquals(0.0, result.get(0), EPS);
        assertEquals(0.5, result.get(1), EPS);
        assertEquals(1.0, result.get(2), EPS);
    }

    @Test
    void logCompress_zero_isZero_andMonotonic() {
        assertEquals(0.0, Normalizers.logCompress(0.0, 10.0), EPS);
        assertEquals(1.0, Normalizers.logCompress(9.0, 10.0), EPS);
        assertTrue(Normalizers.logCompress(1000.0, 10.0) > Normalizers.logCompress(99.0, 10.0));
    }

    @Test
    void logCompress_negativeTreatedAsZero_invalidBaseFallsBackToNaturalLog() {
        assertEquals(0.0, Normalizers.logCompress(-5.0, 10.0), EPS);
        assertEquals(Math.log1p(8.0), Normalizers.logCompress(8.0, 1.0), EPS);
    }

    @Test
    void cosine_identicalVectors_isOne() {
        Map<String, Double> v = Map.of("a", 1.0, "b", 2.0);
        assertEquals(1.0, Normalizers.cosine(v, Map.of("a", 1.0, "b", 2.0)), EPS);
    }

    @Test
    void cosine_orthogonalVectors_isZero() {
        assertEquals(0.0, Normalizers.cosine(Map.of("a", 1.0), Map.of("b", 1.0)), EPS);
    }

    @Test
    void cosine_zeroOrNullVector_isZero() {
        assertEquals(0.0, Normalizers.cosine(Map.of(), Map.of("a", 1.0)), EPS);
        assertEquals(0.0, Normalizers.cosine(null, Map.of("a", 1.0)), EPS);
    }

    @Test
    void cosine_overlappingPartial_withinUnitRange() {
        double sim = Normalizers.cosine(Map.of("a", 1.0, "b", 1.0), Map.of("a", 1.0, "c", 1.0));
        assertEquals(0.5, sim, EPS);
    }

    @Test
    void jaccard_knownCases() {
        assertEquals(0.0, Normalizers.jaccard(List.of(), List.of("x")), EPS);
        assertEquals(1.0, Normalizers.jaccard(List.of("a", "b"), List.of("b", "a")), EPS);
        assertEquals(1.0 / 3.0, Normalizers.jaccard(List.of("a", "b"), List.of("b", "c")), EPS);
    }

    @Test
    void exponentialDecay_halfLifeAndEdges() {
        assertEquals(1.0, Normalizers.exponentialDecay(0, 45.0), EPS);
        assertEquals(0.5, Normalizers.exponentialDecay(45, 45.0), EPS);
        assertEquals(0.25, Normalizers.exponentialDecay(90, 45.0), EPS);
        // 非法半衰天数/未来时间 → 不衰减
        assertEquals(1.0, Normalizers.exponentialDecay(-3, 45.0), EPS);
        assertEquals(1.0, Normalizers.exponentialDecay(10, 0.0), EPS);
    }

    @Test
    void clamp01_boundsAndNaN() {
        assertEquals(0.0, Normalizers.clamp01(-0.1), EPS);
        assertEquals(1.0, Normalizers.clamp01(2.0), EPS);
        assertEquals(0.3, Normalizers.clamp01(0.3), EPS);
        assertEquals(0.0, Normalizers.clamp01(Double.NaN), EPS);
    }
}
