package com.minecraft.config;

import com.minecraft.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CaptchaInitConfig implements CommandLineRunner {

    @Autowired
    private CaptchaService captchaService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("滑块验证码服务初始化完成，每次请求将动态生成随机拼图位置");
    }
}