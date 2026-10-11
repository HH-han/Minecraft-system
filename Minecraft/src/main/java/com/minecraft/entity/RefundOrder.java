package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款单（当前支付为模拟网关，退款为模拟/人工入账）
 */
@Data
@TableName("refund_order")
public class RefundOrder {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String refundNo;

    private Long afterSaleId;

    private Long orderId;

    private String orderNo;

    private Long userId;

    /** 原支付单ID(payment.id) */
    private Long payId;

    private String paymentNo;

    /** 渠道 支付宝/微信/银联/银行卡 */
    private String channel;

    private BigDecimal amount;

    /** RefundStatus: 0-待退款 1-退款中 2-成功 3-失败 */
    private Integer status;

    private String channelRefundNo;

    private String failReason;

    private LocalDateTime refundTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
