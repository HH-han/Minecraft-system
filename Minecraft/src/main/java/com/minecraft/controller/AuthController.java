package com.minecraft.controller;

import com.minecraft.dto.request.EmailCaptchaRequest;
import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.request.RegisterRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.entity.User;
import com.minecraft.service.UserService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response = userService.login(request, httpRequest);
        return ApiResponse.success("登录成功", response);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String account = userService.register(request);
            return ApiResponse.success("注册成功", account);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/email-captcha")
    public ApiResponse<String> sendEmailCaptcha(@Valid @RequestBody EmailCaptchaRequest request) {
        try {
            userService.sendEmailCaptcha(request.getEmail());
            return ApiResponse.success("验证码发送成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/codes")
    public ApiResponse<User> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "邮箱验证码登录")
    @PostMapping("/email-login")
    public ApiResponse<LoginResponse> loginByEmail(@Valid @RequestBody EmailCaptchaRequest request, HttpServletRequest httpRequest) {
        try {
            LoginResponse response = userService.loginByEmail(request.getEmail(), request.getCaptcha(), httpRequest);
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
