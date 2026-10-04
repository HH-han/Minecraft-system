package com.minecraft.recommendation.algorithm;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureTokenizerTest {

    @Test
    void tokenize_mixedDelimiters_lowercaseTrimDedup() {
        Set<String> tokens = FeatureTokenizer.tokenize("亲子, 自然风光 / 网红、打卡;打卡");
        assertEquals(Set.of("亲子", "自然风光", "网红", "打卡"), tokens);
    }

    @Test
    void tokenize_nullOrBlank_empty() {
        assertTrue(FeatureTokenizer.tokenize(null).isEmpty());
        assertTrue(FeatureTokenizer.tokenize("  ").isEmpty());
    }

    @Test
    void tokenizeAll_mergesFields() {
        Set<String> tokens = FeatureTokenizer.tokenizeAll("a,b", "b，c", null);
        assertEquals(Set.of("a", "b", "c"), tokens);
    }
}
