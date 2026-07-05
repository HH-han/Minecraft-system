package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_cta")
public class OfficialwebsiteCta {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String title;
    
    private String description;
    
    private String btnText;
    
    private String btnUrl;
    
    private String bgColor;
    
    private Integer isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}