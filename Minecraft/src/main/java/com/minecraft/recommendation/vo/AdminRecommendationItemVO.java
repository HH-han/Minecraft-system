package com.minecraft.recommendation.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理后台推荐物品行（含全部分量与人工干预字段）。
 */
@Data
public class AdminRecommendationItemVO {

    private Long itemId;

    private String name;

    private String city;

    private BigDecimal recommendationScore;

    private BigDecimal userPreferenceMatching;

    private BigDecimal popularityIndex;

    private BigDecimal collaborativeScore;

    private BigDecimal contentScore;

    private BigDecimal seasonalScore;

    private BigDecimal qualityScore;

    private Boolean isFeatured;

    private BigDecimal featureWeight;

    /** 推荐行状态 1 在架 / 0 下线 */
    private Integer status;

    private LocalDateTime updateTimestamp;
}
