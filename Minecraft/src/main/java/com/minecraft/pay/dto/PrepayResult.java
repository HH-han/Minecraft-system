package com.minecraft.pay.dto;

import lombok.Data;

import java.util.Map;

/**
 * 渠道预支付结果
 */
@Data
public class PrepayResult {

    private boolean success;

    /** 渠道预支付标识 */
    private String prepayId;

    /** 前端调起支付参数 */
    private Map<String, Object> payParams;

    /** 渠道原始响应 */
    private String rawResponse;

    private String errorMsg;
}
