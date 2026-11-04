package com.minecraft.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationUpdateDTO {

    private Long groupId;

    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度范围应在-90到90之间")
    @DecimalMax(value = "90", message = "纬度范围应在-90到90之间")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180", message = "经度范围应在-180到180之间")
    @DecimalMax(value = "180", message = "经度范围应在-180到180之间")
    private BigDecimal longitude;

    /** 定位精度（米） */
    @Min(value = 0, message = "定位精度不能为负数")
    private Integer accuracy;
}
