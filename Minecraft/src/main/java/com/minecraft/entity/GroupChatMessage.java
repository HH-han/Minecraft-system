package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("group_chat_message")
public class GroupChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long groupId;
    
    private Long senderId;
    
    private String senderName;
    
    private String senderAvatar;
    
    private String content;
    
    private String messageType;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
