package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfficialwebsiteSettingsVO {
    private Integer id;
    private String settingKey;
    private String settingValue;
    private String settingGroup;
    private LocalDateTime updatedAt;
}