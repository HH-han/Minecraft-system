package com.minecraft.pay.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 渠道回调解析结果
 */
@Data
public class NotifyResult {

    /** 验签是否通过 */
    private boolean verified;

    /** 支付是否成功 */
    private boolean success;

    private String payNo;

    private BigDecimal amount;

    private String channelTradeNo;

    private String rawBody;
}
