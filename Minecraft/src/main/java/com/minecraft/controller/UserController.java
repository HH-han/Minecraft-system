package com.minecraft.controller;

import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.entity.User;
import com.minecraft.service.LoginLogService;
import com.minecraft.service.UserService;
import com.minecraft.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private LoginLogService loginLogService;

    @Operation(summary ="获取用户信息")
    @GetMapping("/info")
    public ApiResponse<User> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(userService.getUserInfo(userId));
    }

    @Operation(summary ="更新用户信息")
    @PutMapping("/update")
    public ApiResponse<Void> updateUserInfo(@RequestBody User user) {
        userService.updateUserInfo(user);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="修改密码")
    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updatePassword(userId, oldPassword, newPassword);
        return ApiResponse.success("修改成功", null);
    }

    @Operation(summary ="上传头像")
    @PostMapping("/avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();
        String avatarUrl = userService.uploadAvatar(userId, file);
        return ApiResponse.success(avatarUrl);
    }

    @Operation(summary ="删除用户")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary ="获取所有用户")
    @GetMapping("/list")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ApiResponse.success(users);
    }

    @Operation(summary ="根据账号获取用户信息")
    @GetMapping("/by-account")
    public ApiResponse<User> getUserByAccount(@RequestParam String account) {
        User user = userService.getUserByAccount(account);
        return ApiResponse.success(user);
    }

    @Operation(summary ="退出登录")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateOnlineStatus(userId, false);
        // 清除用户的登录日志
        loginLogService.clearLoginLogs(userId);
        return ApiResponse.success("退出成功", null);
    }

    @Operation(summary ="管理员登录")
    @PostMapping("/admin/login")
    public ApiResponse<LoginResponse> adminLogin(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response = userService.adminLogin(request, httpRequest);
        return ApiResponse.success(response);
    }

    @Operation(summary ="获取用户总数")
    @GetMapping("/count")
    public ApiResponse<Long> getUserCount() {
        long count = userService.getUserCount();
        return ApiResponse.success(count);
    }
}
