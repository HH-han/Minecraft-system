package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐曝光日志（公共接口异步记录，用于归因分析）。
 */
@Data
@TableName("recommendation_exposure_log")
public class RecommendationExposureLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID，匿名曝光为空 */
    private Long userId;

    private String category;

    private Long itemId;

    private BigDecimal score;

    /** 展示排名（从 1 开始） */
    private Integer rankPosition;

    /** PERSONALIZED / POPULAR / FEATURED / COLD_START */
    private String source;

    private String requestId;

    private LocalDateTime createdAt;
}
