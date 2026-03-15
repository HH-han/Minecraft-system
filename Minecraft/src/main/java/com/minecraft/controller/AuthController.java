package com.minecraft.controller;

import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.request.RegisterRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.service.UserService;
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
}
