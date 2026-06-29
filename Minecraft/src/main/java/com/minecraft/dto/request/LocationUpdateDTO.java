package com.minecraft.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationUpdateDTO {
    private Long groupId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer accuracy;
}