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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    
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
    private static final String POOL_KEY = "captcha:pool";
    private static final String PROCESSING_KEY = "captcha:processing";
    private static final int TOLERANCE = 8;
    
    @Override
    @Transactional
    public String generateAndCacheImage() {
        String imageId = java.util.UUID.randomUUID().toString().replace("-", "");
        String svgContent = generateSVG();
        byte[] imageBytes = svgContent.getBytes(StandardCharsets.UTF_8);
        
        String md5 = calculateMD5(imageBytes);
        
        Optional<CaptchaImage> existing = imageMapper.findByMd5Hash(md5);
        if (existing.isPresent()) {
            CaptchaImage image = existing.get();
            PuzzleConfig puzzle = generatePuzzleConfig(image);
            puzzleConfigMapper.insert(puzzle);
            cachePuzzleConfig(puzzle);
            return image.getId();
        }
        
        CaptchaImage image = new CaptchaImage();
        image.setId(imageId);
        image.setImageKey(imageId);
        image.setFileName(imageId + ".svg");
        image.setFileContent(svgContent);
        image.setFileSize(imageBytes.length);
        image.setWidth(width);
        image.setHeight(height);
        image.setMimeType("image/svg+xml");
        image.setMd5Hash(md5);
        image.setStatus(1);
        
        PuzzleConfig puzzle = generatePuzzleConfig(image);
        
        imageMapper.insert(image);
        puzzleConfigMapper.insert(puzzle);
        
        cacheImage(image);
        cachePuzzleConfig(puzzle);
        
        return imageId;
    }
    
    private String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
    
    private String generateSVG() {
        List<String> colors = Arrays.asList("#b0c4de", "#f7dc6f", "#a3d8a3", "#f5b7b1", "#d7bde2", "#aed6f1");
        String bgColor = colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
        String accentColor = colors.get(ThreadLocalRandom.current().nextInt(colors.size()));
        
        int cx = 80 + ThreadLocalRandom.current().nextInt(160);
        int cy = 40 + ThreadLocalRandom.current().nextInt(100);
        int r = 30 + ThreadLocalRandom.current().nextInt(50);
        
        return String.format(
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">" +
            "<rect width=\"%d\" height=\"%d\" fill=\"%s\"/>" +
            "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"%s\" opacity=\"0.6\"/>" +
            "<rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/>" +
            "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"#4a6cf7\" opacity=\"0.15\"/>" +
            "</svg>",
            width, height, width, height,
            width, height, bgColor,
            cx, cy, r, accentColor,
            160 + ThreadLocalRandom.current().nextInt(40),
            80 + ThreadLocalRandom.current().nextInt(40),
            10 + ThreadLocalRandom.current().nextInt(20)
        );
    }
    
    private PuzzleConfig generatePuzzleConfig(CaptchaImage image) {
        PuzzleConfig config = new PuzzleConfig();
        config.setImageId(image.getId());
        config.setVersion(1);
        
        int pieceWidth = 44;
        int pieceHeight = 44;
        
        int minPieceX = 60;
        int maxPieceX = 105;
        int pieceX = minPieceX + ThreadLocalRandom.current().nextInt(maxPieceX - minPieceX + 1);
        
        int minPieceY = 50;
        int maxPieceY = 95;
        int pieceY = minPieceY + ThreadLocalRandom.current().nextInt(maxPieceY - minPieceY + 1);
        
        config.setPieceX(pieceX);
        config.setPieceY(pieceY);
        config.setPieceWidth(pieceWidth);
        config.setPieceHeight(pieceHeight);
        
        int targetX = width - pieceWidth - 20;
        config.setTargetX(targetX);
        config.setTargetY(pieceY);
        
        double sliderPercent = (double) (targetX - pieceX) / (width - pieceWidth - pieceX);
        config.setSliderPercent(java.math.BigDecimal.valueOf(Math.min(Math.max(sliderPercent, 0.3), 0.9)));
        
        return config;
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
            String imageId = getImageFromPool();
            
            CaptchaImage image = getImage(imageId);
            PuzzleConfig puzzle = getPuzzleConfig(imageId, 1);
            
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
            
            return response;
            
        } catch (Exception e) {
            return CaptchaResponse.error("获取验证码失败");
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
            
            boolean passed = Math.abs(actualOffset - targetOffset) <= TOLERANCE;
            
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
        Object imageId = redisUtil.lpop(POOL_KEY);
        if (imageId != null) {
            return imageId.toString();
        }
        
        return generateAndCacheImage();
    }
}