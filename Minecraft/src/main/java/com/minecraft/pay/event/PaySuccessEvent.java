package com.minecraft.pay.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 支付成功事件：由业务模块监听并推进自身订单状态
 */
@Getter
@RequiredArgsConstructor
public class PaySuccessEvent {

    private final String payNo;
}
