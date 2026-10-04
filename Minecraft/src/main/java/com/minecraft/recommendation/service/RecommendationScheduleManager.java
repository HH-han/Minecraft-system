package com.minecraft.recommendation.service;

import com.minecraft.recommendation.algorithm.RecommendationDefaults;
import com.minecraft.recommendation.config.RecommendationMapperRegistry;
import com.minecraft.recommendation.enums.JobTriggerType;
import com.minecraft.recommendation.enums.RecommendCategory;
import com.minecraft.recommendation.event.RecommendationConfigChangedEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;

/**
 * 推荐定时任务管理器：
 * <ul>
 *   <li>启动时按 recommendation_config 的 schedule.enabled / schedule.cron 注册；</li>
 *   <li>参数变更事件（scheduleAffected）触发动态重排，无需重启；</li>
 *   <li>任务体 = 全量重算（SYSTEM）+ 过期曝光清理，任何异常只记录日志；</li>
 *   <li>关闭调度时取消已有排程。</li>
 * </ul>
 */
@Slf4j
@Component
public class RecommendationScheduleManager {

    private final TaskScheduler taskScheduler;
    private final RecommendationConfigService configService;
    private final RecommendationRecalcService recalcService;
    private final RecommendationAnalyticsService analyticsService;
    private final RecommendationMapperRegistry registry;

    private volatile ScheduledFuture<?> scheduledFuture;
    private volatile CronTrigger cronTrigger;
    private volatile String activeCron;

    public RecommendationScheduleManager(
            @Qualifier("recommendationTaskScheduler") TaskScheduler taskScheduler,
            RecommendationConfigService configService,
            RecommendationRecalcService recalcService,
            RecommendationAnalyticsService analyticsService,
            RecommendationMapperRegistry registry) {
        this.taskScheduler = taskScheduler;
        this.configService = configService;
        this.recalcService = recalcService;
        this.analyticsService = analyticsService;
        this.registry = registry;
    }

    @PostConstruct
    public void init() {
        reschedule();
        checkColdStart();
    }

    /**
     * 冷启动检测：四张推荐结果表全部为空时给出明确运维提示。
     * 推荐结果仅由重算任务写入，全新部署后若从未触发过重算，管理端/用户端列表都会为空。
     */
    private void checkColdStart() {
        try {
            boolean allEmpty = true;
            for (RecommendCategory category : RecommendCategory.values()) {
                Long count = registry.mapper(category).selectCount(null);
                if (count != null && count > 0) {
                    allEmpty = false;
                    break;
                }
            }
            if (allEmpty) {
                log.warn("推荐结果表全部为空：请在管理端「智能推荐 - 推荐物品」点击全量重算，"
                        + "或调用 POST /api/admin/recommendations/recalc 生成初始推荐数据");
            }
        } catch (Exception e) {
            log.debug("推荐冷启动检测跳过：{}", e.getMessage());
        }
    }

    /**
     * 参数变更事件：仅调度相关键变化时重排。
     */
    @EventListener
    public void onConfigChanged(RecommendationConfigChangedEvent event) {
        if (event.isScheduleAffected()) {
            reschedule();
        }
    }

    /**
     * 按当前配置重新注册定时任务。
     */
    public synchronized void reschedule() {
        cancelExisting();
        boolean enabled;
        try {
            enabled = configService.getBoolean(RecommendationDefaults.SCHEDULE_ENABLED);
        } catch (Exception e) {
            log.warn("读取推荐调度开关失败，保持不调度：{}", e.getMessage());
            return;
        }
        if (!enabled) {
            activeCron = null;
            cronTrigger = null;
            log.info("推荐定时任务未启用");
            return;
        }
        String cron = configService.getGlobalString(RecommendationDefaults.SCHEDULE_CRON);
        if (!isValidCron(cron)) {
            log.error("推荐定时 Cron 非法，放弃注册：{}", cron);
            return;
        }
        CronTrigger trigger = new CronTrigger(cron.replace("?", "*"));
        this.scheduledFuture = taskScheduler.schedule(this::runScheduledTask, trigger);
        this.cronTrigger = trigger;
        this.activeCron = cron;
        log.info("推荐定时任务已注册 cron={}", cron);
    }

    /**
     * 调度任务体：全量重算 + 曝光清理（互不影响，异常均不外抛）。
     */
    void runScheduledTask() {
        try {
            recalcService.recalculate(null, JobTriggerType.SYSTEM, null);
        } catch (Exception e) {
            log.error("定时全量推荐重算异常", e);
        }
        try {
            int purged = analyticsService.purgeExpired();
            if (purged > 0) {
                log.info("定时清理过期曝光日志 {} 行", purged);
            }
        } catch (Exception e) {
            log.error("定时曝光日志清理异常", e);
        }
    }

    /**
     * 当前是否存在有效排程。
     */
    public boolean isScheduled() {
        ScheduledFuture<?> future = scheduledFuture;
        return future != null && !future.isCancelled() && !future.isDone();
    }

    /**
     * 当前生效的 Cron 表达式；未调度返回 null。
     */
    public String getActiveCron() {
        return isScheduled() ? activeCron : null;
    }

    /**
     * 下次计划执行时间；未调度返回 null。
     */
    public LocalDateTime nextRunTime() {
        CronTrigger trigger = cronTrigger;
        if (!isScheduled() || trigger == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                trigger.nextExecution(new SimpleTriggerContext()), ZoneId.systemDefault());
    }

    private void cancelExisting() {
        ScheduledFuture<?> existing = scheduledFuture;
        if (existing != null) {
            existing.cancel(false);
            scheduledFuture = null;
        }
        cronTrigger = null;
        activeCron = null;
    }

    private static boolean isValidCron(String cron) {
        if (cron == null || cron.isBlank()) {
            return false;
        }
        try {
            CronExpression.parse(cron.trim().replace("?", "*"));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
