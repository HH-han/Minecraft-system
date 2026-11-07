package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 团体购票申请
 */
@Data
public class RailwayGroupBookingRequest {

    @NotBlank(message = "请填写单位或团体名称")
    private String companyName;

    @NotBlank(message = "请填写联系人")
    private String contactName;

    @NotBlank(message = "请填写联系电话")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    @NotBlank(message = "请填写出发站")
    private String fromStation;

    @NotBlank(message = "请填写到达站")
    private String toStation;

    @NotNull(message = "请选择乘车日期")
    private LocalDate travelDate;

    private LocalDate returnDate;

    @NotNull(message = "请填写团体人数")
    @Min(value = 20, message = "团体购票至少 20 人")
    @Max(value = 500, message = "单笔团体购票最多 500 人")
    private Integer passengerCount;

    @NotBlank(message = "请选择席别")
    private String seatType;

    private String trainNo;

    private String remark;
}
