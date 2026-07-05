package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_hero")
public class OfficialwebsiteHero {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String badgeText;
    
    private String title;
    
    private String subtitle;
    
    private String description;
    
    private String bgImage;
    
    private String bgVideo;
    
    private String btnText;
    
    private Integer isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}