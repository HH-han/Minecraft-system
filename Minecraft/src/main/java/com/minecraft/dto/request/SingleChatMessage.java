package com.minecraft.dto.request;

import lombok.Data;

@Data
public class SingleChatMessage {
    private Long senderId;
    private Long receiverId;
    private String content;
    private String messageType = "text";
}