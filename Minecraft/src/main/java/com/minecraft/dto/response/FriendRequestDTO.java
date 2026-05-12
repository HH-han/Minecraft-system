package com.minecraft.dto.response;

import lombok.Data;

@Data
public class FriendRequestDTO {
    private Long id;
    private Long userId;
    private Long friendId;
    private String username;
    private String avatar;
    private String message;
    private String createTime;
    private String phone;
    private String email;
}