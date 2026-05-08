package com.minecraft.dto.request;

import lombok.Data;

@Data
public class GroupChatMessage {
    private Long groupId;
    private Long senderId;
    private String content;
    private String messageType = "text";
}