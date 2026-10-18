package com.minecraft.pay.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付主单展示对象
 */
@Data
public class PayOrderVO {

    private Long id;

    private String payNo;

    private String bizOrderNo;

    private String bizType;

    private Long userId;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private BigDecimal pointsDeduct;

    private String channelCode;

    private String status;

    private String statusDesc;

    private String channelTradeNo;

    private String subject;

    private LocalDateTime expireTime;

    private LocalDateTime payTime;

    private LocalDateTime createdAt;

    /** 已成功退款金额 */
    private BigDecimal refundedAmount;
}
