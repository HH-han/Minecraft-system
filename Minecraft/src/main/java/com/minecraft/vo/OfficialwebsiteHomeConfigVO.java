package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteHomeConfigVO {
    private Integer id;
    private String sectionKey;
    private String sectionTitle;
    private String sectionSubtitle;
    private String sectionLabel;
    private Integer isVisible;
    private String configData;
    private LocalDateTime updatedAt;
}