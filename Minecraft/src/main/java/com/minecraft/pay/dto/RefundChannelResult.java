package com.minecraft.pay.dto;

import lombok.Data;

/**
 * 渠道退款结果
 */
@Data
public class RefundChannelResult {

    private boolean success;

    private String channelRefundNo;

    private String rawResponse;

    private String errorMsg;
}
