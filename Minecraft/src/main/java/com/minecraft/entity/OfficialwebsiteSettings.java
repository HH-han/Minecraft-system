package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_settings")
public class OfficialwebsiteSettings {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String settingKey;
    
    private String settingValue;
    
    private String settingGroup;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}