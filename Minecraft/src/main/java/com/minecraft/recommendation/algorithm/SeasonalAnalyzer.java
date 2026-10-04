package com.minecraft.recommendation.algorithm;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 季节趋势分析（确定性纯逻辑）。
 * <p>
 * 季节文本口径：
 * <ul>
 *   <li>含「四季皆宜/全年/常年/all/year-round」→ 全年适游，得分恒 1；</li>
 *   <li>含春/夏/秋/冬（或英文 spring/summer/autumn/fall/winter）→ 按月份带匹配；</li>
 *   <li>无季节文本 → 行为侧信号：当月行为占比相对「季度均值（总量/4）」的比例，
 *       映射到 [0.5,1]；完全无行为时中性 0.5。</li>
 * </ul>
 */
public final class SeasonalAnalyzer {

    public static final String ALL = "all";
    public static final String SPRING = "spring";
    public static final String SUMMER = "summer";
    public static final String AUTUMN = "autumn";
    public static final String WINTER = "winter";

    private SeasonalAnalyzer() {
    }

    /**
     * 解析季节文本为标准 token 集合。
     */
    public static Set<String> parseSeasons(String seasonText) {
        if (seasonText == null || seasonText.isBlank()) {
            return Set.of();
        }
        String text = seasonText.toLowerCase();
        Set<String> seasons = new LinkedHashSet<>();
        // 英文 all 需词边界匹配，避免 "fall" 等单词误命中
        if (text.contains("四季") || text.contains("全年") || text.contains("常年")
                || text.matches(".*\\b(all|year-round|year round)\\b.*")) {
            seasons.add(ALL);
            return seasons;
        }
        if (text.contains("春") || text.contains("spring")) {
            seasons.add(SPRING);
        }
        if (text.contains("夏") || text.contains("summer")) {
            seasons.add(SUMMER);
        }
        if (text.contains("秋") || text.contains("autumn") || text.contains("fall")) {
            seasons.add(AUTUMN);
        }
        if (text.contains("冬") || text.contains("winter")) {
            seasons.add(WINTER);
        }
        return seasons;
    }

    /**
     * 月份 -> 季节 token。3-5 春，6-8 夏，9-11 秋，12/1/2 冬。
     */
    public static String seasonOfMonth(int month) {
        if (month >= 3 && month <= 5) {
            return SPRING;
        }
        if (month >= 6 && month <= 8) {
            return SUMMER;
        }
        if (month >= 9 && month <= 11) {
            return AUTUMN;
        }
        return WINTER;
    }

    /**
     * 计算季节分。
     *
     * @param seasonTokens 解析后的季节 token（可为空）
     * @param month        当前月份 1-12
     * @param monthRatio   当月行为 / 季度均行为（无季节文本时使用，负数按 0、大于 1 截断）
     */
    public static double score(Set<String> seasonTokens, int month, double monthRatio) {
        if (seasonTokens == null || seasonTokens.isEmpty()) {
            // 无文本：行为侧信号，映射到 [0.5,1]
            return 0.5 + 0.5 * Normalizers.clamp01(monthRatio);
        }
        if (seasonTokens.contains(ALL)) {
            return 1.0;
        }
        return seasonTokens.contains(seasonOfMonth(month)) ? 1.0 : 0.0;
    }

    /**
     * 计算当月行为相对季度均值的比例：monthCount / (totalCount / 4)。
     * 总量为 0 时返回 0（score 将给中性 0.5）。
     */
    public static double monthlyRatio(double monthCount, double totalCount) {
        if (totalCount <= 0.0) {
            return 0.0;
        }
        double quarterlyAverage = totalCount / 4.0;
        if (quarterlyAverage <= 0.0) {
            return 0.0;
        }
        return monthCount / quarterlyAverage;
    }
}
