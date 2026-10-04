package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务级人工干预规则（条件-动作模式）。
 * <p>
 * 与 {@link RecommendationRule}（面向单个物品的置顶/加权/隐藏）互补：
 * 本表面向一类业务规则，如"只展示综合评分前五的记录"。
 * 干预条件（condition_json）与干预动作参数（action_params）以 JSON 存储，
 * 便于未来扩展新的规则类型。
 */
@Data
@TableName("intervention_rule")
public class InterventionRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 规则编码（全局唯一，供程序引用） */
    private String ruleCode;

    /** 规则名称 */
    private String ruleName;

    /** 规则类型：RANK/TIME/SORT/HIDE/PIN/BOOST（可扩展） */
    private String ruleType;

    /** 规则描述 */
    private String description;

    /** 作用范围：attraction/hotel/food/product，ALL=全部分类 */
    private String scopeCategory;

    /** 干预条件（JSON） */
    private String conditionJson;

    /** 干预动作：FILTER/SORT/HIDE/PIN/BOOST（可扩展） */
    private String actionType;

    /** 干预动作参数（JSON，可空） */
    private String actionParams;

    /** 执行优先级，数值越小越先执行 */
    private Integer priority;

    /** 生效状态：1-启用 0-停用 */
    private Integer status;

    /** 规则生效开始时间，空=立即生效 */
    private LocalDateTime effectiveStart;

    /** 规则生效结束时间，空=长期有效 */
    private LocalDateTime effectiveEnd;

    /** 创建人（管理员用户 ID） */
    private Long createdBy;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
