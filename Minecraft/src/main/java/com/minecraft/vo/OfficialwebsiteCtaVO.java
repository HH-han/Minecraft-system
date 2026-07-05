package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteCtaVO {
    private Integer id;
    private String title;
    private String description;
    private String btnText;
    private String btnUrl;
    private String bgColor;
    private Integer isActive;
    private LocalDateTime createdAt;
}