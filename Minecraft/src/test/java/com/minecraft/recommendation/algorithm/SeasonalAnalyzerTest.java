package com.minecraft.recommendation.algorithm;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeasonalAnalyzerTest {

    private static final double EPS = 1e-12;

    @Test
    void parseSeasons_allYearTokens() {
        assertEquals(Set.of(SeasonalAnalyzer.ALL), SeasonalAnalyzer.parseSeasons("四季皆宜"));
        assertEquals(Set.of(SeasonalAnalyzer.ALL), SeasonalAnalyzer.parseSeasons("全年开放"));
        assertEquals(Set.of(SeasonalAnalyzer.ALL), SeasonalAnalyzer.parseSeasons("all year round"));
    }

    @Test
    void parseSeasons_specificSeasons_chineseAndEnglish() {
        assertEquals(Set.of(SeasonalAnalyzer.SPRING, SeasonalAnalyzer.SUMMER),
                SeasonalAnalyzer.parseSeasons("春夏"));
        assertEquals(Set.of(SeasonalAnalyzer.AUTUMN), SeasonalAnalyzer.parseSeasons("autumn"));
        assertEquals(Set.of(SeasonalAnalyzer.AUTUMN, SeasonalAnalyzer.WINTER),
                SeasonalAnalyzer.parseSeasons("Fall & Winter"));
    }

    @Test
    void parseSeasons_blankOrNull_empty() {
        assertTrue(SeasonalAnalyzer.parseSeasons(null).isEmpty());
        assertTrue(SeasonalAnalyzer.parseSeasons("  ").isEmpty());
    }

    @Test
    void seasonOfMonth_bands() {
        assertEquals(SeasonalAnalyzer.SPRING, SeasonalAnalyzer.seasonOfMonth(3));
        assertEquals(SeasonalAnalyzer.SUMMER, SeasonalAnalyzer.seasonOfMonth(8));
        assertEquals(SeasonalAnalyzer.AUTUMN, SeasonalAnalyzer.seasonOfMonth(11));
        assertEquals(SeasonalAnalyzer.WINTER, SeasonalAnalyzer.seasonOfMonth(12));
        assertEquals(SeasonalAnalyzer.WINTER, SeasonalAnalyzer.seasonOfMonth(1));
    }

    @Test
    void score_allYear_isOne() {
        assertEquals(1.0, SeasonalAnalyzer.score(Set.of(SeasonalAnalyzer.ALL), 11, 0.0), EPS);
    }

    @Test
    void score_matchingSeason_isOne_nonMatching_isZero() {
        Set<String> summer = Set.of(SeasonalAnalyzer.SUMMER);
        assertEquals(1.0, SeasonalAnalyzer.score(summer, 7, 0.0), EPS);
        assertEquals(0.0, SeasonalAnalyzer.score(summer, 1, 0.0), EPS);
    }

    @Test
    void score_noSeasonText_behaviorDrivenNeutralFloor() {
        // 无任何行为 → 中性 0.5
        assertEquals(0.5, SeasonalAnalyzer.score(Set.of(), 7, 0.0), EPS);
        // 当月达到季度均值 → 1.0
        assertEquals(1.0, SeasonalAnalyzer.score(Set.of(), 7, 1.0), EPS);
        // 比例超 1 截断
        assertEquals(1.0, SeasonalAnalyzer.score(Set.of(), 7, 5.0), EPS);
        // 半比例 → 0.75
        assertEquals(0.75, SeasonalAnalyzer.score(Set.of(), 7, 0.5), EPS);
    }

    @Test
    void monthlyRatio_mathAndEdges() {
        // 总量 100，季度均 25，当月 50 → 2
        assertEquals(2.0, SeasonalAnalyzer.monthlyRatio(50, 100), EPS);
        assertEquals(0.0, SeasonalAnalyzer.monthlyRatio(5, 0), EPS);
    }
}
