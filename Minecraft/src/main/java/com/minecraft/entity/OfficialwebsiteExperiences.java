package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_experiences")
public class OfficialwebsiteExperiences {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String title;
    
    private String iconClass;
    
    private String description;
    
    private Integer isActive;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}