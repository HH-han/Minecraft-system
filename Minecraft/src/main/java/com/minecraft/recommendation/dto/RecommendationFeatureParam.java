package com.minecraft.recommendation.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 人工干预（置顶/加权/上/下架推荐行）请求。
 */
@Data
public class RecommendationFeatureParam {

    /** 是否置顶；null 表示不修改 */
    private Boolean featured;

    /** 人工加权系数 [0,1]；null 表示不修改 */
    private BigDecimal featureWeight;

    /** 推荐行状态：1 在架 0 下线；null 表示不修改 */
    private Integer status;
}
