package com.minecraft.dto.request;

import lombok.Data;

@Data
public class AITripGenerateDTO {
    private Long groupId;
    private String destination;
    private Integer days;
    private String preferences;
}