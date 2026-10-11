package com.minecraft.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款单展示对象
 */
@Data
public class RefundOrderVO {

    private Long id;

    private String refundNo;

    private String orderNo;

    private String channel;

    private BigDecimal amount;

    private Integer status;

    private String statusDesc;

    private String channelRefundNo;

    private String failReason;

    private LocalDateTime refundTime;

    private LocalDateTime createTime;
}
