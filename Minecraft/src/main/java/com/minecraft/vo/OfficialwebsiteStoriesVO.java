package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteStoriesVO {
    private Integer id;
    private String authorName;
    private String authorRole;
    private String authorAvatar;
    private String content;
    private Integer rating;
    private String destination;
    private Integer isFeatured;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}