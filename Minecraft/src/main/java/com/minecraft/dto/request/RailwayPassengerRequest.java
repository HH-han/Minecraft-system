package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RailwayPassengerRequest {

    @NotBlank(message = "乘车人姓名不能为空")
    private String name;

    @NotBlank(message = "证件号码不能为空")
    private String idCardNo;

    /** ADULT / CHILD / STUDENT */
    private String passengerType = "ADULT";

    private String phone;
}
