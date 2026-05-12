package com.minecraft.dto.response;

import lombok.Data;

@Data
public class FriendInfoDTO {
    private Long friendId;
    private String remark;
    private String username;
    private String avatar;
    private Boolean online;
    private String lastMessage;
    private String lastMessageTime;
    private Integer unreadCount;
    private String phone;
    private String email;
}