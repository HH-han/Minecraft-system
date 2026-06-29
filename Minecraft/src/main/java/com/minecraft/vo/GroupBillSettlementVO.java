package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class GroupBillSettlementVO {
    private Map<Long, BigDecimal> userDebts;
    private Map<Long, BigDecimal> userCredits;
    private BigDecimal totalAmount;
    private Integer memberCount;
}