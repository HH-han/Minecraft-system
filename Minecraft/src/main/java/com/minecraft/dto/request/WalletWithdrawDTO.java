package com.minecraft.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包提现请求
 */
@Data
@Schema(description = "钱包提现请求")
public class WalletWithdrawDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额最小0.01元")
    @Schema(description = "提现金额（元）")
    private BigDecimal amount;

    @NotBlank(message = "支付密码不能为空")
    @Size(min = 6, max = 6, message = "支付密码为6位数字")
    @Pattern(regexp = "\\d{6}", message = "支付密码为6位数字")
    @Schema(description = "支付密码（6位数字）")
    private String payPassword;
}
