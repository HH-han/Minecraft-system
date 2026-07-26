package com.minecraft.utils;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            // 使用MimeMessage支持HTML格式
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "即时通讯团队");
            helper.setTo(toEmail);
            helper.setSubject("【即时通讯】注册验证码");

            // HTML邮件内容
            String htmlContent = buildVerificationCodeEmail(code);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("发送邮件失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("发送验证码邮件失败: " + e.getMessage());
        }
    }

    /**
     * 构建验证码邮件HTML内容
     * @param code 验证码
     * @return HTML格式的邮件内容
     */
    private String buildVerificationCodeEmail(String code) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    font-family: -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                    background-color: #f5f7fa;
                }
                .email-container {
                    max-width: 520px;
                    margin: 0 auto;
                    padding: 20px;
                }
                .email-card {
                    background: #ffffff;
                    border-radius: 16px;
                    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
                    padding: 40px 36px 36px;
                }
                .logo-area {
                    text-align: center;
                    margin-bottom: 32px;
                }
                .logo-icon {
                    display: inline-block;
                    width: 56px;
                    height: 56px;
                    background: linear-gradient(135deg, #4F46E5, #7C3AED);
                    border-radius: 14px;
                    color: #ffffff;
                    font-size: 28px;
                    line-height: 56px;
                    text-align: center;
                    font-weight: 700;
                }
                .title {
                    font-size: 22px;
                    font-weight: 600;
                    color: #1a1a2e;
                    margin: 0 0 8px 0;
                    text-align: center;
                }
                .subtitle {
                    color: #6b7280;
                    font-size: 15px;
                    text-align: center;
                    margin: 0 0 32px 0;
                    line-height: 1.6;
                }
                .code-wrapper {
                    background: #f8f9fc;
                    border-radius: 12px;
                    padding: 24px;
                    text-align: center;
                    margin-bottom: 28px;
                    border: 1px dashed #e5e7eb;
                }
                .code-label {
                    font-size: 13px;
                    color: #6b7280;
                    letter-spacing: 1px;
                    text-transform: uppercase;
                    margin-bottom: 8px;
                }
                .code-value {
                    font-size: 40px;
                    font-weight: 700;
                    letter-spacing: 8px;
                    color: #1a1a2e;
                    font-family: 'Courier New', monospace;
                    background: white;
                    padding: 8px 20px;
                    border-radius: 8px;
                    display: inline-block;
                    border: 1px solid #e5e7eb;
                }
                .expiry-info {
                    text-align: center;
                    font-size: 14px;
                    color: #6b7280;
                    margin-bottom: 28px;
                }
                .expiry-info span {
                    background: #fef3c7;
                    color: #d97706;
                    padding: 2px 12px;
                    border-radius: 20px;
                    font-weight: 500;
                }
                .divider {
                    border: none;
                    border-top: 1px solid #f0f0f0;
                    margin: 24px 0;
                }
                .footer-text {
                    font-size: 13px;
                    color: #9ca3af;
                    text-align: center;
                    line-height: 1.8;
                }
                .footer-text .highlight {
                    color: #4F46E5;
                    font-weight: 500;
                }
                .footer-text .warning {
                    color: #ef4444;
                }
                @media (max-width: 480px) {
                    .email-card {
                        padding: 28px 20px 24px;
                    }
                    .code-value {
                        font-size: 32px;
                        letter-spacing: 6px;
                    }
                    .title {
                        font-size: 19px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <div class="email-card">
                    <!-- Logo -->
                    <div class="logo-area">
                        <div class="logo-icon">💬</div>
                        <h1 class="title">即时通讯</h1>
                    </div>
                    
                    <!-- 正文 -->
                    <p class="subtitle">
                        感谢您注册即时通讯服务，<br>
                        请输入以下验证码完成注册：
                    </p>
                    
                    <!-- 验证码 -->
                    <div class="code-wrapper">
                        <div class="code-label">🔐 验证码</div>
                        <div class="code-value">%s</div>
                    </div>
                    
                    <!-- 有效期 -->
                    <div class="expiry-info">
                        ⏱️ 有效期 <span>5 分钟</span>，请尽快使用
                    </div>
                    
                    <hr class="divider">
                    
                    <!-- 底部 -->
                    <div class="footer-text">
                        <p style="margin: 0;">
                            如果这不是您本人的操作，请忽略此邮件。<br>
                            为保障账户安全，请勿将验证码泄露给他人。
                        </p>
                        <p style="margin: 8px 0 0 0; font-size: 12px; color: #d1d5db;">
                            © 2026 即时通讯 · 系统自动发送，请勿回复
                        </p>
                    </div>
                </div>
            </div>
        </body>
        </html>
        """.formatted(code);
    }
}