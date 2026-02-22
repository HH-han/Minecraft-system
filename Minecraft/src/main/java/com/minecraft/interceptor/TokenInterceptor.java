package com.minecraft.interceptor;

import com.minecraft.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            return true;
        }

        token = token.substring(7);
        
        try {
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserId(token);
                request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            return true;
        }

        return true;
    }
}
