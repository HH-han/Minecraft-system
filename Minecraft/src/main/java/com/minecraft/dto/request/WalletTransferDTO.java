package com.minecraft.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包转账请求（转给好友）
 */
@Data
@Schema(description = "钱包转账请求")
public class WalletTransferDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "转出方用户ID")
    private Long userId;

    @NotNull(message = "收款人ID不能为空")
    @Schema(description = "收款人用户ID")
    private Long toUserId;

    @NotNull(message = "转账金额不能为空")
    @DecimalMin(value = "0.01", message = "转账金额最小0.01元")
    @DecimalMax(value = "50000", message = "单笔转账不能超过50000元")
    @Schema(description = "转账金额（元）")
    private BigDecimal amount;

    @Size(max = 100, message = "备注不能超过100字")
    @Schema(description = "转账备注")
    private String remark;

    @NotBlank(message = "支付密码不能为空")
    @Size(min = 6, max = 6, message = "支付密码为6位数字")
    @Pattern(regexp = "\\d{6}", message = "支付密码为6位数字")
    @Schema(description = "支付密码（6位数字）")
    private String payPassword;
}
