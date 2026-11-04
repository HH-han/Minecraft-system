package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyRequest {

    /** 验证会话ID */
    @NotBlank(message = "验证会话不能为空")
    private String traceId;

    /** 滑块位置 */
    @NotNull(message = "滑块位置不能为空")
    private Integer sliderPosition;

    @Size(max = 500, message = "User-Agent 不能超过500个字符")
    private String userAgent;
}
