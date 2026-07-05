package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteNarrativeVO {
    private Integer id;
    private String title;
    private String subtitle;
    private String content;
    private String quoteText;
    private String imageUrl;
    private Integer isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}