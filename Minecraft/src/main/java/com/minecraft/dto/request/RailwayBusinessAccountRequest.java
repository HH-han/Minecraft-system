package com.minecraft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商旅企业账户开通 / 修改
 */
@Data
public class RailwayBusinessAccountRequest {

    @NotBlank(message = "请填写企业名称")
    private String companyName;

    private String taxNo;

    @NotBlank(message = "请填写企业联系人")
    private String contactName;

    @NotBlank(message = "请填写联系电话")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    @NotNull(message = "请填写月度差旅预算")
    @DecimalMin(value = "1000", message = "月度差旅预算不能低于 1000 元")
    private BigDecimal monthlyBudget;
}
