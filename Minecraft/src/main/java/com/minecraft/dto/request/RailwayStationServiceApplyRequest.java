package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 站车服务预约申请
 */
@Data
public class RailwayStationServiceApplyRequest {

    @NotNull(message = "请选择服务项目")
    private Long serviceId;

    @NotBlank(message = "请选择车站")
    private String stationName;

    @NotNull(message = "请选择服务日期")
    private LocalDate serviceDate;

    @NotNull(message = "请填写服务人数")
    @Min(value = 1, message = "服务人数至少 1 人")
    @Max(value = 20, message = "单次预约最多 20 人")
    private Integer passengerCount;

    @NotBlank(message = "请填写联系人")
    private String contactName;

    @NotBlank(message = "请填写联系电话")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    private String remark;
}
