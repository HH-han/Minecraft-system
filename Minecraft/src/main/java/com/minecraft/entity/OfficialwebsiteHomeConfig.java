package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_home_config")
public class OfficialwebsiteHomeConfig {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String sectionKey;
    
    private String sectionTitle;
    
    private String sectionSubtitle;
    
    private String sectionLabel;
    
    private Integer isVisible;
    
    private String configData;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}