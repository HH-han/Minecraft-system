package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteNavigationVO {
    private Integer id;
    private String title;
    private String url;
    private Integer sortOrder;
    private Integer isActive;
    private Integer parentId;
    private LocalDateTime createdAt;
}