package com.minecraft.service.impl;

import com.minecraft.dto.request.CaptchaSession;
import com.minecraft.dto.response.CaptchaResponse;
import com.minecraft.dto.response.VerifyResult;
import com.minecraft.entity.CaptchaImage;
import com.minecraft.entity.PuzzleConfig;
import com.minecraft.entity.VerifyRecord;
import com.minecraft.mapper.CaptchaImageMapper;
import com.minecraft.mapper.PuzzleConfigMapper;
import com.minecraft.mapper.VerifyRecordMapper;
import com.minecraft.service.CaptchaService;
import com.minecraft.utils.RedisUtil;
import com.minecraft.utils.SliderCaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minecraft.exception.BusinessException;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    private static final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);
    
    @Autowired
    private CaptchaImageMapper imageMapper;
    
    @Autowired
    private PuzzleConfigMapper puzzleConfigMapper;
    
    @Autowired
    private VerifyRecordMapper recordMapper;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Value("${captcha.image.width:320}")
    private int width;
    
    @Value("${captcha.image.height:180}")
    private int height;
    
    private static final String IMAGE_KEY_PREFIX = "captcha:img:";
    private static final String PUZZLE_KEY_PREFIX = "captcha:puzzle:";
    private static final String SESSION_KEY_PREFIX = "captcha:session:";
    private static final int MAX_DB_RECORDS = 1000;
    
    @Override
    @Transactional
    public String generateAndCacheImage() {
        return generateNewCaptcha();
    }
    
    @Transactional
    public String generateNewCaptcha() {
        CaptchaImage image = SliderCaptchaUtil.generateCaptchaImage(width, height);
        PuzzleConfig puzzle = SliderCaptchaUtil.generatePuzzleConfig(image, width);
        
        String fileContent = image.getFileContent();
        image.setFileContent(null);
        image.setFileSize(0);
        
        imageMapper.insert(image);
        puzzleConfigMapper.insert(puzzle);
        
        image.setFileContent(fileContent);
        image.setFileSize(fileContent.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        
        cacheImage(image);
        cachePuzzleConfig(puzzle);
        
        return image.getId();
    }
    
    private void cleanupOldRecords() {
        try {
            long imageCount = imageMapper.selectCount(null);
            if (imageCount > MAX_DB_RECORDS) {
                int excess = (int) (imageCount - MAX_DB_RECORDS);
                imageMapper.deleteOldRecords(excess);
                puzzleConfigMapper.deleteOldRecords(excess);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public CaptchaImage getImage(String imageId) {
        String redisKey = IMAGE_KEY_PREFIX + imageId;
        Object cached = redisUtil.get(redisKey);
        if (cached != null) {
            return (CaptchaImage) cached;
        }
        
        CaptchaImage image = imageMapper.selectById(imageId);
        if (image != null) {
            image.setFileContent(null);
            redisUtil.set(redisKey, image, 10, TimeUnit.MINUTES);
        }
        
        return image;
    }
    
    @Override
    public PuzzleConfig getPuzzleConfig(String imageId, Integer version) {
        String cacheKey = PUZZLE_KEY_PREFIX + imageId + ":v" + version;
        
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (PuzzleConfig) cached;
        }
        
        Optional<PuzzleConfig> config = puzzleConfigMapper.findByImageIdAndVersion(imageId, version);
        
        if (config.isPresent()) {
            redisUtil.set(cacheKey, config.get(), 30, TimeUnit.MINUTES);
            return config.get();
        }
        
        return null;
    }
    
    @Override
    public void cacheSession(String traceId, CaptchaSession session) {
        String key = SESSION_KEY_PREFIX + traceId;
        redisUtil.set(key, session, 3, TimeUnit.MINUTES);
    }
    
    @Override
    public CaptchaSession getAndRemoveSession(String traceId) {
        String key = SESSION_KEY_PREFIX + traceId;
        Object session = redisUtil.get(key);
        if (session != null) {
            redisUtil.delete(key);
            return (CaptchaSession) session;
        }
        return null;
    }
    
    private void cacheImage(CaptchaImage image) {
        String key = IMAGE_KEY_PREFIX + image.getId();
        redisUtil.set(key, image, 10, TimeUnit.MINUTES);
    }
    
    private void cachePuzzleConfig(PuzzleConfig puzzle) {
        String key = PUZZLE_KEY_PREFIX + puzzle.getImageId() + ":v" + puzzle.getVersion();
        redisUtil.set(key, puzzle, 30, TimeUnit.MINUTES);
    }
    
    @Override
    public CaptchaResponse getCaptcha(String traceId) {
        try {
            logger.info("Generating captcha for traceId: {}", traceId);
            
            String imageId = generateNewCaptcha();
            logger.debug("Generated captcha imageId: {}", imageId);
            
            CaptchaImage image = getImage(imageId);
            if (image == null) {
                throw new RuntimeException("Failed to retrieve captcha image with id: " + imageId);
            }
            
            PuzzleConfig puzzle = getPuzzleConfig(imageId, 1);
            if (puzzle == null) {
                throw new RuntimeException("Failed to retrieve puzzle config for image: " + imageId);
            }
            
            CaptchaResponse response = CaptchaResponse.builder()
                    .traceId(traceId)
                    .imageId(imageId)
                    .imageData(image.getFileContent())
                    .puzzleX(puzzle.getPieceX())
                    .puzzleY(puzzle.getPieceY())
                    .targetX(puzzle.getTargetX())
                    .targetY(puzzle.getTargetY())
                    .pieceWidth(puzzle.getPieceWidth())
                    .pieceHeight(puzzle.getPieceHeight())
                    .sliderPercent(puzzle.getSliderPercent())
                    .build();
            
            CaptchaSession session = new CaptchaSession();
            session.setImageId(imageId);
            session.setPieceX(puzzle.getPieceX());
            session.setPieceY(puzzle.getPieceY());
            session.setTargetX(puzzle.getTargetX());
            session.setTargetY(puzzle.getTargetY());
            session.setPieceWidth(puzzle.getPieceWidth());
            session.setPieceHeight(puzzle.getPieceHeight());
            session.setCreateTime(System.currentTimeMillis());
            cacheSession(traceId, session);
            
            cleanupOldRecords();
            
            logger.info("Successfully generated captcha for traceId: {}", traceId);
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to generate captcha for traceId: {}", traceId, e);
            throw new BusinessException("获取验证码失败", e);
        }
    }
    
    @Override
    public VerifyResult verify(String traceId, Integer sliderPosition, String userAgent, String clientIp) {
        long startTime = System.currentTimeMillis();
        
        try {
            CaptchaSession session = getAndRemoveSession(traceId);
            if (session == null) {
                return VerifyResult.fail("会话已过期或无效");
            }
            
            int pieceX = session.getPieceX();
            int targetX = session.getTargetX();
            
            int actualOffset = sliderPosition;
            int targetOffset = targetX - pieceX;
            
            boolean passed = SliderCaptchaUtil.verifySlider(actualOffset, targetOffset);
            
            VerifyRecord record = new VerifyRecord();
            record.setTraceId(traceId);
            record.setImageId(session.getImageId());
            record.setClientIp(clientIp);
            record.setUserAgent(userAgent);
            record.setSliderOffset(actualOffset);
            record.setTargetOffset(targetOffset);
            record.setIsPassed(passed ? 1 : 0);
            record.setCostTime((int)(System.currentTimeMillis() - startTime));
            
            new Thread(() -> {
                try {
                    recordMapper.insert(record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            return VerifyResult.builder()
                    .passed(passed)
                    .message(passed ? "验证成功" : "验证失败，请重试")
                    .build();
            
        } catch (Exception e) {
            return VerifyResult.fail("验证服务异常");
        }
    }

    @Override
    public String getImageFromPool() {
        return "";
    }
}