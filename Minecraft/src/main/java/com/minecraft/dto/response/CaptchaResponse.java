package com.minecraft.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CaptchaResponse {
    private String traceId;
    private String imageId;
    private String imageData;
    private Integer puzzleX;
    private Integer puzzleY;
    private Integer pieceWidth;
    private Integer pieceHeight;
    private BigDecimal sliderPercent;
    
    public static CaptchaResponse error(String message) {
        return CaptchaResponse.builder().build();
    }
}