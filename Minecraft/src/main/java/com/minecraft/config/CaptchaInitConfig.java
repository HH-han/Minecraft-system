package com.minecraft.config;

import com.minecraft.service.CaptchaService;
import com.minecraft.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CaptchaInitConfig implements CommandLineRunner {

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${captcha.pool.min-size:10}")
    private int minPoolSize;

    @Value("${captcha.pool.max-size:100}")
    private int maxPoolSize;

    private static final String POOL_KEY = "captcha:pool";

    @Override
    public void run(String... args) throws Exception {
        Long poolSize = redisUtil.lsize(POOL_KEY);
        if (poolSize == null || poolSize < minPoolSize) {
            int toGenerate = minPoolSize - (poolSize != null ? poolSize.intValue() : 0);
            System.out.println("初始化图片池，需要生成 " + toGenerate + " 张验证码图片");
            
            for (int i = 0; i < toGenerate; i++) {
                String imageId = captchaService.generateAndCacheImage();
                redisUtil.rpush(POOL_KEY, imageId);
                
                if (i % 10 == 0) {
                    System.out.println("已生成 " + (i + 1) + " 张验证码图片");
                }
            }
            
            System.out.println("图片池初始化完成");
        } else {
            System.out.println("图片池已有 " + poolSize + " 张图片，无需初始化");
        }
    }
}