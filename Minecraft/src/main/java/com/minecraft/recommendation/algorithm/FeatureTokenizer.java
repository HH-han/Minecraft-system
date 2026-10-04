package com.minecraft.recommendation.algorithm;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 标签/类型文本分词。源数据中 tags 形如 "亲子,自然风光 / 网红打卡"，
 * 统一按中英文逗号、顿号、分号、斜杠、空白切分，小写并去空。
 */
public final class FeatureTokenizer {

    private FeatureTokenizer() {
    }

    private static final String DELIMITERS = "[,，、;；/|\\s]+";

    public static Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        Set<String> tokens = new LinkedHashSet<>();
        Arrays.stream(text.split(DELIMITERS))
                .map(String::trim)
                .filter(t -> !t.isEmpty())
                .map(String::toLowerCase)
                .forEach(tokens::add);
        return tokens;
    }

    /**
     * 合并多个文本字段的分词结果。
     */
    public static Set<String> tokenizeAll(String... texts) {
        if (texts == null) {
            return Set.of();
        }
        Set<String> all = new LinkedHashSet<>();
        for (String text : texts) {
            all.addAll(tokenize(text));
        }
        return all;
    }
}
