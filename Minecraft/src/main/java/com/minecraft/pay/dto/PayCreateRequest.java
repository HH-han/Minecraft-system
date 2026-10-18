package com.minecraft.pay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 统一创建支付请求
 */
@Data
public class PayCreateRequest {

    /** 业务订单号 */
    @NotBlank(message = "业务订单号不能为空")
    private String bizOrderNo;

    /** 业务类型 MALL/HOTEL/TICKET/POINTS/GROUP/MEMBER/RAILWAY */
    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    /** 付款用户ID（服务端以登录用户为准） */
    private Long userId;

    /** 订单总金额（元） */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于 0")
    private BigDecimal totalAmount;

    /** 积分抵扣金额（元） */
    private BigDecimal pointsDeduct = BigDecimal.ZERO;

    /** 支付渠道 MOCK/WECHAT/ALIPAY/UNIONPAY，默认 MOCK */
    private String channelCode = "MOCK";

    /** 支付标题 */
    private String subject;

    /** 支付过期时间（分钟），默认 30 */
    private Integer expireMinutes = 30;
}
