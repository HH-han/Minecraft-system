package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理端会员订单列表项
 */
@Data
public class MemberOrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private String account;

    private String nickname;

    private Long packageId;

    private String packageName;

    private BigDecimal amount;

    private Integer payType;

    private Integer status;

    private LocalDateTime payTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createTime;
}
