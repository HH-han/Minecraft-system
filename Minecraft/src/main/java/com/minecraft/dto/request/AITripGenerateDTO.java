package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AITripGenerateDTO {

    /** 旅行团ID，可为空 */
    private Long groupId;

    /** 目的地 */
    @NotBlank(message = "目的地不能为空")
    @Size(max = 100, message = "目的地不能超过100个字符")
    private String destination;

    /** 行程天数 */
    @Min(value = 1, message = "行程天数不能小于1天")
    @Max(value = 30, message = "行程天数不能超过30天")
    private Integer days;

    /** 出行偏好 */
    @Size(max = 500, message = "出行偏好不能超过500个字符")
    private String preferences;
}
