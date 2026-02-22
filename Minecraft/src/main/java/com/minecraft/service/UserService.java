package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.request.RegisterRequest;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.entity.User;

public interface UserService extends IService<User> {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
    User getUserInfo(Long userId);
    void updateUserInfo(User user);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    void updateOnlineStatus(Long userId, boolean online);
}
