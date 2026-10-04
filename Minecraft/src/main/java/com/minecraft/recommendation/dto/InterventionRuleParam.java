package com.minecraft.recommendation.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务级人工干预规则新增/更新参数。
 */
@Data
public class InterventionRuleParam {

    /** 规则编码（全局唯一） */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 规则类型：RANK/TIME/SORT/HIDE/PIN/BOOST */
    private String ruleType;

    /** 规则描述 */
    private String description;

    /** 作用范围：attraction/hotel/food/product/ALL */
    private String scopeCategory;

    /** 干预条件（JSON 字符串） */
    private String conditionJson;

    /** 干预动作：FILTER/SORT/HIDE/PIN/BOOST */
    private String actionType;

    /** 干预动作参数（JSON 字符串，可空） */
    private String actionParams;

    /** 执行优先级，越小越先执行 */
    private Integer priority;

    /** 生效状态：1-启用 0-停用 */
    private Integer status;

    /** 生效开始时间，空=立即生效 */
    private LocalDateTime effectiveStart;

    /** 生效结束时间，空=长期有效 */
    private LocalDateTime effectiveEnd;

    /** 备注 */
    private String remark;
}
