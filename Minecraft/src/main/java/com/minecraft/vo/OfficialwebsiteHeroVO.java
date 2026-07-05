package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteHeroVO {
    private Integer id;
    private String badgeText;
    private String title;
    private String subtitle;
    private String description;
    private String bgImage;
    private String bgVideo;
    private String btnText;
    private Integer isActive;
    private LocalDateTime createdAt;
}