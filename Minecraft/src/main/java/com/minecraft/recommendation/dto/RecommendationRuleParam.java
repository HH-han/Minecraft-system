package com.minecraft.recommendation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐规则创建/更新入参。
 */
@Data
public class RecommendationRuleParam {

    /** 分类编码 attraction/hotel/food/product */
    private String category;

    /** 物品 ID（更新时以路径 id 为准，该字段可空） */
    private Long itemId;

    /** PIN / BOOST / HIDE */
    private String ruleType;

    /** 置顶位序 */
    private Integer sortOrder;

    /** 加权系数 [0,1] */
    private BigDecimal boostWeight;

    /** 生效开始时间 */
    private LocalDateTime startTime;

    /** 生效结束时间 */
    private LocalDateTime endTime;

    /** 1-启用 0-停用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
