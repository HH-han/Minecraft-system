package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 买家填写退货物流请求
 */
@Data
public class AfterSaleReturnRequest {

    @NotBlank(message = "退货承运商不能为空")
    private String returnCarrier;

    @NotBlank(message = "退货单号不能为空")
    private String returnTracking;
}
