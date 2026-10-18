package com.minecraft.pay.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一创建支付响应
 */
@Data
public class PayCreateResponse {

    private String payNo;

    private String flowNo;

    private String channelCode;

    /** 前端调起支付所需参数（微信 JSAPI / 支付宝表单 / Mock） */
    private Map<String, Object> payParams;

    private LocalDateTime expireTime;

    /** 支付主单状态 */
    private String status;

    private BigDecimal amount;
}
