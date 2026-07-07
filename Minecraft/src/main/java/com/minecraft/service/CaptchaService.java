package com.minecraft.service;

import com.minecraft.dto.request.CaptchaSession;
import com.minecraft.dto.response.CaptchaResponse;
import com.minecraft.dto.response.VerifyResult;
import com.minecraft.entity.CaptchaImage;
import com.minecraft.entity.PuzzleConfig;

public interface CaptchaService {
    
    String generateAndCacheImage();
    
    CaptchaImage getImage(String imageId);
    
    PuzzleConfig getPuzzleConfig(String imageId, Integer version);
    
    void cacheSession(String traceId, CaptchaSession session);
    
    CaptchaSession getAndRemoveSession(String traceId);
    
    CaptchaResponse getCaptcha(String traceId);
    
    VerifyResult verify(String traceId, Integer sliderPosition, String userAgent, String clientIp);
    
    String getImageFromPool();
}