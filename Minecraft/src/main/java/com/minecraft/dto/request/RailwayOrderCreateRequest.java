package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RailwayOrderCreateRequest {

    @NotNull(message = "运行车次不能为空")
    private Long serviceId;

    @NotNull(message = "上车站序不能为空")
    private Integer fromStopSeq;

    @NotNull(message = "下车站序不能为空")
    private Integer toStopSeq;

    @NotBlank(message = "席别不能为空")
    private String seatType;

    @NotEmpty(message = "请选择乘车人")
    private List<Long> passengerIds;

    private String contactPhone;
}
