package com.minecraft.dto.response;

import com.minecraft.entity.RailwayBusinessAccount;
import lombok.Data;

/**
 * 商旅账户月度预算概览
 */
@Data
public class RailwayBusinessSummaryVO {

    private RailwayBusinessAccount account;

    /** 统计月份 yyyy-MM */
    private String month;

    /** 本月已审批通过的预估票款（分） */
    private Long usedCents;

    /** 本月剩余预算（分） */
    private Long remainingCents;

    /** 本月已通过申请数 */
    private Long approvedTrips;

    /** 预算使用率（0-100） */
    private Integer usagePercent;
}
