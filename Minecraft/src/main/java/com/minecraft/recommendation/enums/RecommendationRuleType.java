package com.minecraft.recommendation.enums;

import com.minecraft.exception.BusinessException;

import java.util.Arrays;

/**
 * 推荐人工干预规则类型。
 * <ul>
 *   <li>PIN：置顶，按 sortOrder 升序排在列表最前；</li>
 *   <li>BOOST：加权，保持综合分排序，但对命中物品的分数做向 1 拉近的加成；</li>
 *   <li>HIDE：隐藏，命中物品不出现在客户端推荐列表。</li>
 * </ul>
 */
public enum RecommendationRuleType {

    PIN("置顶"),
    BOOST("加权"),
    HIDE("隐藏");

    private final String displayName;

    RecommendationRuleType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static RecommendationRuleType fromCode(String code) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "不支持的规则类型：" + code));
    }
}
