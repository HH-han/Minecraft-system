package com.minecraft.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String avatar;
    private String account;
    private String phone;
}
