package com.minecraft.dto.request;

import lombok.Data;

@Data
public class CaptchaSession {
    private String imageId;
    private Integer pieceX;
    private Integer pieceY;
    private Integer targetX;
    private Integer targetY;
    private Integer pieceWidth;
    private Integer pieceHeight;
    private Long createTime;
}