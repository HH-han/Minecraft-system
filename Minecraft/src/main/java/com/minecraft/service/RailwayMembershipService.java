package com.minecraft.service;

import com.minecraft.dto.response.PageResponse;
import com.minecraft.dto.response.RailwayMemberOverviewVO;
import com.minecraft.entity.RailwayMemberGift;
import com.minecraft.entity.RailwayMemberRedemption;
import com.minecraft.entity.RailwayPointLog;

import java.util.List;

/**
 * 铁路会员积分服务
 */
public interface RailwayMembershipService {

    RailwayMemberOverviewVO overview(Long userId);

    PageResponse<RailwayPointLog> pageLogs(Long userId, Integer pageNum, Integer pageSize);

    List<RailwayMemberGift> listGifts();

    RailwayMemberRedemption redeem(Long userId, Long giftId);

    /**
     * 车票支付成功后发放铁路积分与成长值（幂等）
     */
    void awardOrderPoints(String orderNo);
}
