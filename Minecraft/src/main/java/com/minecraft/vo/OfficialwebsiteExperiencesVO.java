package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteExperiencesVO {
    private Integer id;
    private String title;
    private String iconClass;
    private String description;
    private Integer isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}