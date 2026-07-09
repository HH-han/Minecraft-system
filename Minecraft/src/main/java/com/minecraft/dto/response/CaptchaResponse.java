package com.minecraft.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaResponse {
    private String traceId;
    private String imageId;
    private String imageData;
    private String sliderImage;
    private String type;
    
    private Integer puzzleX;
    private Integer puzzleY;
    private Integer targetX;
    private Integer targetY;
    private Integer pieceWidth;
    private Integer pieceHeight;
    private Float sliderPercent;
    
    public static CaptchaResponse error(String message) {
        return CaptchaResponse.builder().build();
    }
}