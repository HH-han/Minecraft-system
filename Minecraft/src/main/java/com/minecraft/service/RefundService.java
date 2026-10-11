package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.RefundOrder;

/**
 * 退款服务
 */
public interface RefundService extends IService<RefundOrder> {

    /** 发起退款（生成退款单，当前为模拟/人工渠道） */
    RefundOrder createRefund(Long afterSaleId, Long adminId);

    /** 渠道回调/人工确认结果 */
    void notifyResult(Long refundId, boolean success, String channelRefundNo, String failReason);

    /** 退款单列表 */
    PageResponse<?> page(PageRequest request);
}
