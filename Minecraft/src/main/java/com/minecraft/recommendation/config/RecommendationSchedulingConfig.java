package com.minecraft.recommendation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 推荐系统调度配置。
 * <p>
 * 独立单线程调度器承载「定时重算 + 曝光清理」任务，避免与业务线程池互相干扰；
 * Cron 表达式来自数据库 recommendation_config，由 RecommendationScheduleManager 动态注册。
 */
@Configuration
@EnableScheduling
public class RecommendationSchedulingConfig {

    @Bean(name = "recommendationTaskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler recommendationTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("rec-schedule-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.initialize();
        return scheduler;
    }
}
