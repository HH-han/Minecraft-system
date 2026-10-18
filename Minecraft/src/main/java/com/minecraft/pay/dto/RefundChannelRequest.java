package com.minecraft.pay.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 渠道退款请求
 */
@Data
public class RefundChannelRequest {

    private String payNo;

    private String refundNo;

    private BigDecimal amount;

    private String reason;

    private String channelTradeNo;
}
