package com.minecraft.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupTripPlanDTO {

    private Long groupId;

    /** 第几天，从0开始 */
    @Min(value = 0, message = "行程天数索引不能为负数")
    private Integer dayIndex;

    private LocalDate date;

    @Size(max = 50, message = "时间段不能超过50个字符")
    private String timeSlot;

    @Size(max = 200, message = "活动内容不能超过200个字符")
    private String activity;

    @Size(max = 200, message = "地点不能超过200个字符")
    private String location;

    @Size(max = 50, message = "交通方式不能超过50个字符")
    private String transport;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String notes;
}
