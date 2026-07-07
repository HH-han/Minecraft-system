package com.minecraft.dto.request;

import lombok.Data;

@Data
public class VerifyRequest {
    private String traceId;
    private Integer sliderPosition;
    private String userAgent;
}