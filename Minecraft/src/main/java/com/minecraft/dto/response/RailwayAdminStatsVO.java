package com.minecraft.dto.response;

import lombok.Data;

@Data
public class RailwayAdminStatsVO {

    private Long todayOrders;

    private Long todayRevenue;

    private Long pendingOrders;

    private Long paidOrders;

    private Long totalOrders;

    private Long totalRevenue;
}
