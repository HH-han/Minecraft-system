package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员开通订单
 */
@Data
@TableName("member_order")
public class MemberOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long packageId;

    private String packageName;

    /** 实付金额 */
    private BigDecimal amount;

    /** 1 支付宝 2 微信 3 余额 */
    private Integer payType;

    /** 0 待支付 1 已支付 2 已取消 3 已退款 */
    private Integer status;

    private LocalDateTime payTime;

    /** 会员生效时间 */
    private LocalDateTime startTime;

    /** 会员到期时间 */
    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
