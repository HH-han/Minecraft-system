package com.minecraft.dto.response;

import com.minecraft.vo.MemberInfoVO;
import lombok.Data;

import java.util.List;

/**
 * 铁路会员中心概览
 */
@Data
public class RailwayMemberOverviewVO {

    /** 全站会员信息（等级 / 成长值 / 折扣 / 权益） */
    private MemberInfoVO member;

    /** 铁路积分余额 */
    private Integer points;

    /** 累计获得积分 */
    private Integer totalEarned;

    /** 已支付车票订单数 */
    private Long paidOrders;

    /** 累计购票金额（分） */
    private Long totalSpentCents;

    /** 最近积分流水 */
    private List<com.minecraft.entity.RailwayPointLog> recentLogs;
}
