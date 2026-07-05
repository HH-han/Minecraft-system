package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("officialwebsite_stories")
public class OfficialwebsiteStories {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String authorName;
    
    private String authorRole;
    
    private String authorAvatar;
    
    private String content;
    
    private Integer rating;
    
    private String destination;
    
    private Integer isFeatured;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}