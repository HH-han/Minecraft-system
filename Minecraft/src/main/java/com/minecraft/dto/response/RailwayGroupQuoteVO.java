package com.minecraft.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 团体购票预估报价
 */
@Data
public class RailwayGroupQuoteVO {

    /** 是否匹配到固定车次 */
    private Boolean matched;

    private String trainNo;

    /** 预估单人票价（分，折扣前） */
    private Integer unitPriceCents;

    /** 团体折扣率 */
    private BigDecimal discountRate;

    private Integer passengerCount;

    /** 预估总额（分，折扣后） */
    private Integer totalAmountCents;

    private String message;
}
