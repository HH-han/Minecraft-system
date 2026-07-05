package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteImmersionVO {
    private Integer id;
    private String quoteText;
    private String author;
    private Integer isActive;
    private LocalDateTime createdAt;
}