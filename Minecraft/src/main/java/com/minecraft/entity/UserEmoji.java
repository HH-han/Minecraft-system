package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_emoji")
public class UserEmoji {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String emojiUrl;
    
    private String emojiName;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}