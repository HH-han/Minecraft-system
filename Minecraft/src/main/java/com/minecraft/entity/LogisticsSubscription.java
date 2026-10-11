package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流订阅（用于推送）
 */
@Data
@TableName("logistics_subscription")
public class LogisticsSubscription {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shippingId;

    private Long userId;

    private String carrierCode;

    private String trackingNo;

    private LocalDateTime subscribeAt;

    private LocalDateTime lastPushAt;

    /** 1-订阅中 0-已取消 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
