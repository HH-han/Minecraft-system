package com.minecraft.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 设置/修改支付密码请求
 */
@Data
@Schema(description = "设置支付密码请求")
public class WalletPasswordDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "支付密码不能为空")
    @Size(min = 6, max = 6, message = "支付密码为6位数字")
    @Pattern(regexp = "\\d{6}", message = "支付密码为6位数字")
    @Schema(description = "新支付密码（6位数字）")
    private String payPassword;

    @Schema(description = "旧支付密码（修改时必填）")
    private String oldPayPassword;
}
