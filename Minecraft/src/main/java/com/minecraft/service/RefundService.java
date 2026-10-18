package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.pay.entity.PayRefund;

/**
 * 退款服务（统一走聚合支付网关）
 */
public interface RefundService extends IService<PayRefund> {

    /** 发起售后退款 */
    PayRefund createRefund(Long afterSaleId, Long adminId);

    /** 渠道回调/人工确认退款结果 */
    void notifyResult(Long refundId, boolean success, String channelRefundNo, String failReason);

    /** 退款单列表 */
    PageResponse<?> page(PageRequest request);
}
