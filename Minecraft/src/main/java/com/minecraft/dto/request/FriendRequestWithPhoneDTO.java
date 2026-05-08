package com.minecraft.dto.request;

import lombok.Data;

@Data
public class FriendRequestWithPhoneDTO {
    private Long senderId;
    private Long receiverId;
    private String receiverPhone;
    private String message;
}