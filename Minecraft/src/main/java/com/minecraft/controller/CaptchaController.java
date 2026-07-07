package com.minecraft.controller;

import com.minecraft.dto.request.VerifyRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.CaptchaResponse;
import com.minecraft.dto.response.VerifyResult;
import com.minecraft.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "验证码管理")
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @Operation(summary = "获取滑块验证码")
    @GetMapping("/get")
    public ApiResponse<CaptchaResponse> getCaptcha(@RequestParam String traceId) {
        CaptchaResponse response = captchaService.getCaptcha(traceId);
        return ApiResponse.success("获取成功", response);
    }

    @Operation(summary = "验证滑块位置")
    @PostMapping("/verify")
    public ApiResponse<VerifyResult> verify(@RequestBody VerifyRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIP(httpRequest);
        VerifyResult result = captchaService.verify(
                request.getTraceId(),
                request.getSliderPosition(),
                request.getUserAgent(),
                clientIp
        );
        return ApiResponse.success(result.getMessage(), result);
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}