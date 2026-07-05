package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteFooterVO {
    private Integer id;
    private String brandName;
    private String brandDescription;
    private String socialLinks;
    private String copyrightText;
    private LocalDateTime updatedAt;
}