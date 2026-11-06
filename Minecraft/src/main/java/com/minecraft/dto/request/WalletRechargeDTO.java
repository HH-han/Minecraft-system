package com.minecraft.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包充值请求
 */
@Data
@Schema(description = "钱包充值请求")
public class WalletRechargeDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额最小0.01元")
    @DecimalMax(value = "50000", message = "单笔充值不能超过50000元")
    @Schema(description = "充值金额（元）")
    private BigDecimal amount;
}
