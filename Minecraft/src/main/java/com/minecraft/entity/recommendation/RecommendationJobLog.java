package com.minecraft.entity.recommendation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐重算任务日志。
 */
@Data
@TableName("recommendation_job_log")
public class RecommendationJobLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类编码；全量重算为 ALL */
    private String category;

    /** SYSTEM / MANUAL */
    private String triggerType;

    private Long operatorId;

    /** RUNNING / SUCCESS / FAILED */
    private String status;

    private Integer itemCount;

    private Long durationMs;

    private String errorMessage;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
