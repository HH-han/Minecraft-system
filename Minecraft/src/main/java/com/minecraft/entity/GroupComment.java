package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_comment")
public class GroupComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long postId;
    
    private Long userId;
    
    private Long parentId;
    
    private Long replyToUserId;
    
    private String content;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableLogic
    private Integer deleted;
}