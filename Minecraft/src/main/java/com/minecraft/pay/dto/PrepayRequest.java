package com.minecraft.pay.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 渠道预支付请求
 */
@Data
public class PrepayRequest {

    private String payNo;

    private String flowNo;

    private Long userId;

    private BigDecimal amount;

    private String subject;

    private LocalDateTime expireTime;

    private String notifyUrl;
}
