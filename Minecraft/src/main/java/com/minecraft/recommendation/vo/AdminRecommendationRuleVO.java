package com.minecraft.recommendation.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端推荐规则列表项（附带物品名称/城市，便于页面展示）。
 */
@Data
public class AdminRecommendationRuleVO {

    private Long id;

    private String category;

    private Long itemId;

    /** 物品名称（来自源物品表，可能为空） */
    private String itemName;

    /** 物品所在城市 */
    private String city;

    /** 物品子类型：酒店星级 / 美食品类 / 文创类型 */
    private String subType;

    private String ruleType;

    private Integer sortOrder;

    private BigDecimal boostWeight;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private String remark;

    private Long operatorId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
