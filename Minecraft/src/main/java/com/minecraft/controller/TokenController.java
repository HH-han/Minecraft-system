package com.minecraft.controller;

import com.minecraft.common.Result;
import com.minecraft.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Token管理控制器
 */
@RestController
@RequestMapping("/api/token")
@Tag(name = "Token管理", description = "Token状态查询和刷新")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    /**
     * 获取Token状态信息
     */
    @Operation(summary = "获取Token状态")
    @GetMapping("/status")
    public Result getTokenStatus() {
        try {
            Map<String, Object> tokenData = tokenService.getTokenStatus();
            return Result.success(tokenData);
        } catch (Exception e) {
            return Result.error("获取Token状态失败: " + e.getMessage());
        }
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result refreshToken() {
        try {
            Map<String, Object> responseData = tokenService.refreshToken();
            return Result.success(responseData);
        } catch (Exception e) {
            return Result.error("刷新Token失败: " + e.getMessage());
        }
    }
}
