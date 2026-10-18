package com.minecraft.pay.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 退款成功事件：由业务模块监听并同步订单/售后状态
 */
@Getter
@RequiredArgsConstructor
public class PayRefundSuccessEvent {

    private final Long refundId;
}
