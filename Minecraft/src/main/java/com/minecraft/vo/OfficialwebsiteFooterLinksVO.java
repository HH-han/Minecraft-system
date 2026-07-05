package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteFooterLinksVO {
    private Integer id;
    private String category;
    private String title;
    private String url;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}