package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.request.RegisterRequest;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService extends IService<User> {
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);
    String register(RegisterRequest request);
    User getUserInfo(Long userId);
    void updateUserInfo(User user);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    void updateOnlineStatus(Long userId, boolean online);
    String uploadAvatar(Long userId, MultipartFile file);
    void deleteUser(Long userId);
    List<User> getAllUsers();
    User getUserByAccount(String account);
}
