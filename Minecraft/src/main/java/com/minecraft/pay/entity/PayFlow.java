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
 * 支付流水（每次支付尝试一条）
 */
@Data
@TableName("pay_flow")
public class PayFlow {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 流水号 */
    private String flowNo;

    /** 关联支付主单号 */
    private String payNo;

    /** 本次尝试的渠道 */
    private String channelCode;

    /** 渠道预支付标识 */
    private String channelPrepayId;

    /** 本次支付金额 */
    private BigDecimal amount;

    /** 流水状态 */
    private String status;

    /** 渠道原始响应 */
    private String channelResponse;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
