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
 * 支付主单
 */
@Data
@TableName("pay_order")
public class PayOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 支付主单号 */
    private String payNo;

    /** 业务订单号 */
    private String bizOrderNo;

    /** 业务类型 */
    private String bizType;

    /** 付款用户ID */
    private Long userId;

    /** 订单总金额（元） */
    private BigDecimal totalAmount;

    /** 实付金额（元） */
    private BigDecimal payAmount;

    /** 积分抵扣金额（元） */
    private BigDecimal pointsDeduct;

    /** 支付渠道 */
    private String channelCode;

    /** 支付状态 */
    private String status;

    /** 渠道交易号 */
    private String channelTradeNo;

    /** 支付标题 */
    private String subject;

    /** 支付过期时间 */
    private LocalDateTime expireTime;

    /** 支付成功时间 */
    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
