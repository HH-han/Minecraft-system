package com.minecraft.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 邮件发送工具类
 */
@Component
public class EmailUtil {

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    @Autowired
    public EmailUtil(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
    }

    /**
     * 生成6位随机验证码
     * @return 6位数字验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 发送验证码邮件
     * @param toEmail 收件人邮箱
     * @param code 验证码
     */
    public void sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("【即时通讯】注册验证码");
            message.setText("您的注册验证码是：" + code + "，有效期5分钟，请及时使用。");
            
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("发送邮件失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("发送验证码邮件失败: " + e.getMessage());
        }
    }
}