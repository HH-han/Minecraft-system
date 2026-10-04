package com.minecraft.recommendation.enums;

import com.minecraft.exception.BusinessException;

import java.util.Arrays;

/**
 * 业务级人工干预规则类型。
 * <ul>
 *   <li>RANK：排名筛选，条件形如"综合评分前 N"；</li>
 *   <li>TIME：时间范围，条件为规则生效时间窗口；</li>
 *   <li>SORT：排序；</li>
 *   <li>HIDE：隐藏；</li>
 *   <li>PIN：置顶；</li>
 *   <li>BOOST：加权。</li>
 * </ul>
 * 类型仅作语义归类，实际行为由 action_type + condition_json/action_params 决定。
 */
public enum InterventionRuleType {

    RANK("排名筛选"),
    TIME("时间范围"),
    SORT("排序"),
    HIDE("隐藏"),
    PIN("置顶"),
    BOOST("加权");

    private final String displayName;

    InterventionRuleType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static InterventionRuleType fromCode(String code) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "不支持的规则类型：" + code));
    }
}
