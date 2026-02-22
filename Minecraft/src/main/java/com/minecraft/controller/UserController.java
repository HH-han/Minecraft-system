package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.User;
import com.minecraft.service.UserService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

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
}
