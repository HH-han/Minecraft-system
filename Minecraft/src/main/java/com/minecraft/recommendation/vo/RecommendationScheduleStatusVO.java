package com.minecraft.recommendation.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐定时任务当前状态。
 */
@Data
public class RecommendationScheduleStatusVO {

    private boolean scheduled;

    private String cron;

    private LocalDateTime nextRunTime;
}
