package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 售后审核请求
 */
@Data
public class AfterSaleAuditRequest {

    @NotNull(message = "审核结果不能为空")
    private Boolean approved;

    private String auditRemark;

    /** 审核通过时的实际退款金额 */
    private BigDecimal actualAmount;

    /** 退货地址（退货退款/换货时告知买家） */
    private String returnAddress;
}
