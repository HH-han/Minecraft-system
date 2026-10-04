package com.minecraft.recommendation.dto;

import lombok.Data;

/**
 * 推荐重算结果摘要。全量重算时 category 为 ALL，itemCount/edgeCount 为四分类合计。
 */
@Data
public class RecalcResult {

    /** 分类编码；全量为 ALL */
    private String category;

    /** RUNNING / SUCCESS / FAILED */
    private String status;

    private int itemCount;

    private int edgeCount;

    private long durationMs;

    /** 任务日志 ID（全量时为 ALL 汇总日志 ID） */
    private Long jobLogId;

    private String errorMessage;
}
