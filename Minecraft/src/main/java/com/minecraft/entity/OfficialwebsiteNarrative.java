package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_narrative")
public class OfficialwebsiteNarrative {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String title;
    
    private String subtitle;
    
    private String content;
    
    private String quoteText;
    
    private String imageUrl;
    
    private Integer isActive;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}