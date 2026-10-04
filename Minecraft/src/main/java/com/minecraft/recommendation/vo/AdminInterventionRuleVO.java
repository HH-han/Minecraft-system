package com.minecraft.recommendation.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务级人工干预规则管理端视图。
 */
@Data
public class AdminInterventionRuleVO {

    private Long id;

    /** 规则编码 */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 规则类型 */
    private String ruleType;

    /** 规则描述 */
    private String description;

    /** 作用范围 */
    private String scopeCategory;

    /** 干预条件（JSON 字符串） */
    private String conditionJson;

    /** 干预动作 */
    private String actionType;

    /** 干预动作参数（JSON 字符串） */
    private String actionParams;

    /** 执行优先级 */
    private Integer priority;

    /** 生效状态 1-启用 0-停用 */
    private Integer status;

    /** 生效开始时间 */
    private LocalDateTime effectiveStart;

    /** 生效结束时间 */
    private LocalDateTime effectiveEnd;

    /** 创建人 */
    private Long createdBy;

    /** 备注 */
    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
