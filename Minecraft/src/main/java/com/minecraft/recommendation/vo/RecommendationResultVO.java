package com.minecraft.recommendation.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐结果包装：口径元数据 + 物品列表。
 */
@Data
public class RecommendationResultVO {

    private String category;

    /** PERSONALIZED / POPULAR / FEATURED / COLD_START / RELATED */
    private String source;

    /** 策略名 HYBRID/POPULAR/CONTENT/COLLABORATIVE */
    private String strategy;

    /** 单次请求 ID（曝光归因键） */
    private String requestId;

    private LocalDateTime generatedAt;

    private List<RecommendationItemVO> items;
}
