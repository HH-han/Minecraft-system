package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("emoji")
public class Emoji {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String emojiCode;
    
    private String emojiName;
    
    private String category;
    
    private String unicode;
    
    private String description;
    
    private Integer sortOrder;
    
    private Integer isActive;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}