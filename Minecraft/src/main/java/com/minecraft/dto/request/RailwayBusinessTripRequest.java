package com.minecraft.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商旅出差申请
 */
@Data
public class RailwayBusinessTripRequest {

    @NotBlank(message = "请填写出发站")
    private String fromStation;

    @NotBlank(message = "请填写到达站")
    private String toStation;

    @NotNull(message = "请选择出发日期")
    private LocalDate departDate;

    private LocalDate returnDate;

    @NotBlank(message = "请填写出差事由")
    private String purpose;

    @NotNull(message = "请填写出行人数")
    @Min(value = 1, message = "出行人数至少 1 人")
    @Max(value = 50, message = "单次申请最多 50 人")
    private Integer passengerCount;

    @NotNull(message = "请填写预估票款")
    @DecimalMin(value = "1", message = "预估票款不能低于 1 元")
    private BigDecimal estimatedAmount;
}
