package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_notification")
public class GroupNotification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long receiverId;
    
    private Long groupId;
    
    private String type;
    
    private String title;
    
    private String content;
    
    private Long targetId;
    
    private Boolean isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}