package com.minecraft.service.impl;

import com.minecraft.entity.User;
import com.minecraft.service.TokenService;
import com.minecraft.service.UserService;
import com.minecraft.utils.JwtUtil;
import com.minecraft.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Token服务实现
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;

    @Override
    public Map<String, Object> getTokenStatus() {
        // 从SecurityContext中获取当前用户信息
        Long userId = SecurityUtils.getCurrentUserId();
        String token = SecurityUtils.getCurrentToken();

        if (token == null) {
            throw new RuntimeException("未找到Token");
        }

        // 解析Token
        Claims claims = jwtUtil.getClaimsFromToken(token);
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        boolean expired = jwtUtil.isTokenExpired(token);
        boolean valid = jwtUtil.validateToken(token);

        // 计算剩余时间
        long now = System.currentTimeMillis();
        long exp = expiration.getTime();
        long remainingTime = exp - now;

        // 从数据库中获取真实的用户名
        String username = "未知用户";
        if (userId != null) {
            User user = userService.getById(userId);
            if (user != null) {
                username = user.getUsername();
                if (username == null || username.trim().isEmpty()) {
                    username = user.getAccount();
                }
            }
        }

        // 构建响应数据
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("valid", valid);
        tokenData.put("expired", expired);
        tokenData.put("username", username);
        tokenData.put("issuedAt", issuedAt);
        tokenData.put("expiration", expiration);
        tokenData.put("message", expired ? "Token已过期" : "Token有效");
        
        // 可以添加更多自定义信息
        Map<String, Object> permissions = new HashMap<>();
        // 这里可以从用户权限系统中获取实际的权限信息
        permissions.put("admin", true);
        permissions.put("user", true);
        tokenData.put("permissions", permissions);

        return tokenData;
    }

    @Override
    public Map<String, Object> refreshToken() {
        // 从SecurityContext中获取当前用户信息
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();

        if (userId == null || username == null) {
            throw new RuntimeException("未找到用户信息");
        }

        // 生成新的Token
        String newToken = jwtUtil.generateToken(userId, username);

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", newToken);
        responseData.put("message", "Token刷新成功");

        return responseData;
    }
}
