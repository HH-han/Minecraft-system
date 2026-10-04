package com.minecraft.recommendation.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 推荐参数变更事件：批量更新/重置后发布，
 * 调度管理器据此动态重排定时任务（AC-7）。
 */
@Getter
public class RecommendationConfigChangedEvent extends ApplicationEvent {

    /** 是否影响调度相关键（schedule.enabled / schedule.cron） */
    private final boolean scheduleAffected;

    /** 触发操作的管理员 ID（系统重置时为空） */
    private final Long operatorId;

    public RecommendationConfigChangedEvent(Object source, boolean scheduleAffected, Long operatorId) {
        super(source);
        this.scheduleAffected = scheduleAffected;
        this.operatorId = operatorId;
    }
}
