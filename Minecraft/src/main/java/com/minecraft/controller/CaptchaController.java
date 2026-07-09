package com.minecraft.controller;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.minecraft.dto.request.VerifyRequest;
import com.minecraft.dto.response.CaptchaResponse;
import com.minecraft.dto.response.VerifyResult;
import com.minecraft.entity.VerifyRecord;
import com.minecraft.mapper.VerifyRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Tag(name = "验证码管理")
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @Autowired
    private ImageCaptchaApplication imageCaptchaApplication;

    @Autowired
    private VerifyRecordMapper recordMapper;

    @Operation(summary = "获取滑块验证码")
    @GetMapping("/get")
    public com.minecraft.dto.response.ApiResponse<CaptchaResponse> getCaptcha(@RequestParam String traceId) {
        try {
            ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);

            if (!response.isSuccess()) {
                return com.minecraft.dto.response.ApiResponse.error(response.getMsg());
            }

            ImageCaptchaVO captchaVO = response.getData();

            CaptchaResponse captchaResponse = CaptchaResponse.builder()
                    .traceId(traceId)
                    .imageId(captchaVO.getId())
                    .imageData(captchaVO.getBackgroundImage())
                    .sliderImage(captchaVO.getTemplateImage())
                    .type(captchaVO.getType())
                    .build();

            return com.minecraft.dto.response.ApiResponse.success("获取成功", captchaResponse);

        } catch (Exception e) {
            logger.error("Failed to generate captcha for traceId: {}", traceId, e);
            return com.minecraft.dto.response.ApiResponse.error("获取验证码失败");
        }
    }

    @Operation(summary = "验证滑块位置")
    @PostMapping("/verify")
    public com.minecraft.dto.response.ApiResponse<VerifyResult> verify(@RequestBody VerifyRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIP(httpRequest);
        long startTime = System.currentTimeMillis();

        try {
            ImageCaptchaTrack track = new ImageCaptchaTrack();
            track.setLeft(request.getSliderPosition());
            track.setTop(0);

            ApiResponse<?> response = imageCaptchaApplication.matching(request.getTraceId(), track);

            boolean passed = response.isSuccess();

            VerifyRecord record = new VerifyRecord();
            record.setTraceId(request.getTraceId());
            record.setImageId(request.getTraceId());
            record.setClientIp(clientIp);
            record.setUserAgent(request.getUserAgent());
            record.setSliderOffset(request.getSliderPosition());
            record.setTargetOffset(null);
            record.setIsPassed(passed ? 1 : 0);
            record.setCostTime((int) (System.currentTimeMillis() - startTime));

            new Thread(() -> {
                try {
                    recordMapper.insert(record);
                } catch (Exception e) {
                    logger.error("Failed to save verify record", e);
                }
            }).start();

            if (passed) {
                return com.minecraft.dto.response.ApiResponse.success("验证成功", VerifyResult.builder().passed(true).message("验证成功").build());
            } else {
                return com.minecraft.dto.response.ApiResponse.success("验证失败", VerifyResult.builder().passed(false).message(response.getMsg() != null ? response.getMsg() : "验证失败，请重试").build());
            }

        } catch (Exception e) {
            logger.error("Failed to verify captcha for traceId: {}", request.getTraceId(), e);
            return com.minecraft.dto.response.ApiResponse.error("验证服务异常");
        }
    }

    @Operation(summary = "TIANAI-CAPTCHA标准生成接口")
    @PostMapping("/gen")
    public ApiResponse<ImageCaptchaVO> genCaptcha() {
        return imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
    }

    @Operation(summary = "TIANAI-CAPTCHA标准验证接口")
    @PostMapping("/check")
    public ApiResponse<?> checkCaptcha(@RequestBody VerifyData data) {
        ApiResponse<?> response = imageCaptchaApplication.matching(data.getId(), data.getData());
        if (response.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
        }
        return response;
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

    @lombok.Data
    public static class VerifyData {
        private String id;
        private ImageCaptchaTrack data;
    }
}