package com.minecraft.recommendation.enums;

import com.minecraft.exception.BusinessException;

import java.util.Arrays;

/**
 * 推荐策略。HYBRID 为默认混合策略，其余为单一口径（便于运营对比效果）。
 */
public enum RecommendStrategy {

    /** 混合：协同过滤 + 内容 + 热度 + 季节 + 品质 */
    HYBRID,
    /** 热门优先 */
    POPULAR,
    /** 内容匹配优先 */
    CONTENT,
    /** 协同过滤优先 */
    COLLABORATIVE;

    public static RecommendStrategy fromCode(String code) {
        if (code == null || code.isBlank()) {
            return HYBRID;
        }
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "不支持的推荐策略：" + code));
    }
}
