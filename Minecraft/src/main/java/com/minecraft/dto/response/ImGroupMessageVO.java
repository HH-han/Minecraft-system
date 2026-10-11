package com.minecraft.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 群聊消息（管理端）
 */
@Data
public class ImGroupMessageVO {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private String messageType;
    private LocalDateTime createTime;
}
