package com.minecraft.pay.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付退款单
 */
@Data
@TableName("pay_refund")
public class PayRefund {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 退款单号 */
    private String refundNo;

    /** 原支付主单号 */
    private String payNo;

    /** 业务订单号（冗余） */
    private String bizOrderNo;

    /** 关联售后单ID */
    private Long afterSaleId;

    /** 退款用户ID */
    private Long userId;

    /** 原支付渠道 */
    private String channelCode;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款原因 */
    private String reason;

    /** RefundStatus: 0-待退款 1-退款中 2-成功 3-失败 */
    private Integer status;

    /** 渠道退款单号 */
    private String channelRefundNo;

    /** 失败原因 */
    private String failReason;

    /** 退款完成时间 */
    private LocalDateTime refundTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
