package com.minecraft.recommendation.enums;

import com.minecraft.exception.BusinessException;

import java.util.Arrays;

/**
 * 业务级人工干预动作类型。
 * <ul>
 *   <li>FILTER：筛选，仅保留条件命中的物品；</li>
 *   <li>SORT：按 action_params 指定字段对列表排序；</li>
 *   <li>HIDE：剔除条件命中的物品；</li>
 *   <li>PIN：将条件命中的物品移到列表最前；</li>
 *   <li>BOOST：对条件命中的物品加权后重新排序。</li>
 * </ul>
 */
public enum InterventionActionType {

    FILTER("筛选"),
    SORT("排序"),
    HIDE("隐藏"),
    PIN("置顶"),
    BOOST("加权");

    private final String displayName;

    InterventionActionType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static InterventionActionType fromCode(String code) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(400, "不支持的干预动作：" + code));
    }
}
