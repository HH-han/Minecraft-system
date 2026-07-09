package com.minecraft.service.impl;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.minecraft.dto.response.CaptchaResponse;
import com.minecraft.dto.response.VerifyResult;
import com.minecraft.entity.VerifyRecord;
import com.minecraft.mapper.VerifyRecordMapper;
import com.minecraft.service.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    private static final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);
    
    @Autowired
    private ImageCaptchaApplication imageCaptchaApplication;
    
    @Autowired
    private VerifyRecordMapper recordMapper;
    
    @Override
    public String generateAndCacheImage() {
        ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
        if (response.isSuccess()) {
            return response.getData().getId();
        }
        throw new RuntimeException("Failed to generate captcha");
    }
    
    @Override
    public CaptchaResponse getCaptcha(String traceId) {
        try {
            logger.info("Generating captcha for traceId: {}", traceId);
            
            ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
            
            if (!response.isSuccess()) {
                throw new RuntimeException("Failed to generate captcha: " + response.getMsg());
            }
            
            ImageCaptchaVO captchaVO = response.getData();
            
            CaptchaResponse captchaResponse = CaptchaResponse.builder()
                    .traceId(traceId)
                    .imageId(captchaVO.getId())
                    .imageData(captchaVO.getBackgroundImage())
                    .puzzleX(null)
                    .puzzleY(null)
                    .targetX(null)
                    .targetY(null)
                    .pieceWidth(null)
                    .pieceHeight(null)
                    .sliderPercent(null)
                    .build();
            
            captchaResponse.setSliderImage(captchaVO.getTemplateImage());
            captchaResponse.setType(captchaVO.getType());
            
            logger.info("Successfully generated captcha for traceId: {}, imageId: {}", traceId, captchaVO.getId());
            return captchaResponse;
            
        } catch (Exception e) {
            logger.error("Failed to generate captcha for traceId: {}", traceId, e);
            throw new RuntimeException("获取验证码失败", e);
        }
    }
    
    @Override
    public VerifyResult verify(String traceId, Integer sliderPosition, String userAgent, String clientIp) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Verifying captcha for traceId: {}", traceId);
            
            ImageCaptchaTrack track = new ImageCaptchaTrack();
            track.setLeft(sliderPosition);
            track.setTop(0);
            
            ApiResponse<?> response = imageCaptchaApplication.matching(traceId, track);
            
            boolean passed = response.isSuccess();
            
            VerifyRecord record = new VerifyRecord();
            record.setTraceId(traceId);
            record.setImageId(traceId);
            record.setClientIp(clientIp);
            record.setUserAgent(userAgent);
            record.setSliderOffset(sliderPosition);
            record.setTargetOffset(null);
            record.setIsPassed(passed ? 1 : 0);
            record.setCostTime((int)(System.currentTimeMillis() - startTime));
            
            new Thread(() -> {
                try {
                    recordMapper.insert(record);
                } catch (Exception e) {
                    logger.error("Failed to save verify record", e);
                }
            }).start();
            
            if (passed) {
                logger.info("Captcha verification passed for traceId: {}", traceId);
                return VerifyResult.builder().passed(true).message("验证成功").build();
            } else {
                logger.warn("Captcha verification failed for traceId: {}", traceId);
                return VerifyResult.builder().passed(false).message(response.getMsg() != null ? response.getMsg() : "验证失败，请重试").build();
            }
            
        } catch (Exception e) {
            logger.error("Failed to verify captcha for traceId: {}", traceId, e);
            return VerifyResult.fail("验证服务异常");
        }
    }
    
    @Override
    public String getImageFromPool() {
        return "";
    }
    
    @Override
    public void cacheSession(String traceId, com.minecraft.dto.request.CaptchaSession session) {
    }
    
    @Override
    public com.minecraft.dto.request.CaptchaSession getAndRemoveSession(String traceId) {
        return null;
    }
    
    @Override
    public com.minecraft.entity.CaptchaImage getImage(String imageId) {
        return null;
    }
    
    @Override
    public com.minecraft.entity.PuzzleConfig getPuzzleConfig(String imageId, Integer version) {
        return null;
    }
}