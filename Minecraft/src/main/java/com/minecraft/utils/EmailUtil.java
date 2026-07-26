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
     * 
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
     * 
     * @param toEmail 收件人邮箱
     * @param code    验证码
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
     * 构建验证码邮件HTML内容 - 炫彩渐变风格
     * 
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
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        body {
                            margin: 0;
                            padding: 0;
                            font-family: -apple-system, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
                            background: linear-gradient(135deg, #0f0c29, #302b63, #24243e);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        }
                        .email-container {
                            max-width: 540px;
                            width: 100%;
                            margin: 20px auto;
                            padding: 20px;
                            animation: fadeInUp 0.8s ease-out;
                        }
                        @keyframes fadeInUp {
                            from {
                                opacity: 0;
                                transform: translateY(30px);
                            }
                            to {
                                opacity: 1;
                                transform: translateY(0);
                            }
                        }
                        @keyframes shimmer {
                            0% { background-position: -200% center; }
                            100% { background-position: 200% center; }
                        }
                        @keyframes pulse {
                            0%, 100% { transform: scale(1); }
                            50% { transform: scale(1.03); }
                        }
                        @keyframes float {
                            0%, 100% { transform: translateY(0px); }
                            50% { transform: translateY(-6px); }
                        }
                        .email-card {
                            background: rgba(255, 255, 255, 0.05);
                            backdrop-filter: blur(20px);
                            -webkit-backdrop-filter: blur(20px);
                            border-radius: 24px;
                            padding: 48px 40px 40px;
                            border: 1px solid rgba(255, 255, 255, 0.1);
                            box-shadow:
                                0 25px 60px rgba(0, 0, 0, 0.5),
                                inset 0 1px 0 rgba(255, 255, 255, 0.1);
                            position: relative;
                            overflow: hidden;
                        }
                        /* 背景光晕 */
                        .email-card::before {
                            content: '';
                            position: absolute;
                            top: -50%;
                            right: -50%;
                            width: 100%;
                            height: 100%;
                            background: radial-gradient(circle, rgba(99, 102, 241, 0.15), transparent 70%);
                            pointer-events: none;
                        }
                        .email-card::after {
                            content: '';
                            position: absolute;
                            bottom: -30%;
                            left: -30%;
                            width: 80%;
                            height: 80%;
                            background: radial-gradient(circle, rgba(236, 72, 153, 0.1), transparent 70%);
                            pointer-events: none;
                        }

                        /* ===== 顶部装饰条 ===== */
                        .header-accent {
                            position: absolute;
                            top: 0;
                            left: 0;
                            right: 0;
                            height: 4px;
                            background: linear-gradient(90deg, #6366f1, #8b5cf6, #ec4899, #8b5cf6, #6366f1);
                            background-size: 200% 100%;
                            animation: shimmer 3s linear infinite;
                            border-radius: 24px 24px 0 0;
                        }

                        /* ===== Logo区域 ===== */
                        .logo-area {
                            text-align: center;
                            margin-bottom: 28px;
                            position: relative;
                            z-index: 1;
                        }
                        .logo-icon {
                            display: inline-flex;
                            align-items: center;
                            justify-content: center;
                            width: 72px;
                            height: 72px;
                            background: linear-gradient(135deg, #6366f1, #8b5cf6, #a855f7);
                            border-radius: 20px;
                            font-size: 34px;
                            box-shadow:
                                0 8px 32px rgba(99, 102, 241, 0.35),
                                inset 0 1px 0 rgba(255, 255, 255, 0.3);
                            animation: float 3s ease-in-out infinite;
                            position: relative;
                        }
                        .logo-icon::after {
                            content: '';
                            position: absolute;
                            inset: -2px;
                            border-radius: 22px;
                            background: linear-gradient(135deg, #6366f1, #ec4899);
                            z-index: -1;
                            opacity: 0.4;
                            filter: blur(12px);
                        }
                        .title {
                            font-size: 26px;
                            font-weight: 700;
                            color: #ffffff;
                            margin: 16px 0 0 0;
                            letter-spacing: 1px;
                            text-shadow: 0 2px 20px rgba(99, 102, 241, 0.3);
                        }
                        .title .gradient-text {
                            background: linear-gradient(135deg, #a78bfa, #f472b6);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                            background-clip: text;
                        }

                        /* ===== 正文 ===== */
                        .subtitle {
                            color: rgba(255, 255, 255, 0.7);
                            font-size: 15px;
                            text-align: center;
                            margin: 0 0 32px 0;
                            line-height: 1.8;
                            position: relative;
                            z-index: 1;
                        }
                        .subtitle strong {
                            color: #c4b5fd;
                            font-weight: 600;
                        }

                        /* ===== 验证码卡片 ===== */
                        .code-wrapper {
                            background: rgba(255, 255, 255, 0.06);
                            border-radius: 16px;
                            padding: 28px 24px 24px;
                            text-align: center;
                            margin-bottom: 28px;
                            border: 1px solid rgba(255, 255, 255, 0.08);
                            position: relative;
                            z-index: 1;
                            backdrop-filter: blur(10px);
                            transition: all 0.3s ease;
                        }
                        .code-wrapper:hover {
                            border-color: rgba(139, 92, 246, 0.3);
                            box-shadow: 0 0 40px rgba(99, 102, 241, 0.05);
                        }
                        .code-label {
                            font-size: 12px;
                            color: rgba(255, 255, 255, 0.4);
                            letter-spacing: 3px;
                            text-transform: uppercase;
                            margin-bottom: 12px;
                            font-weight: 500;
                        }
                        .code-value {
                            font-size: 48px;
                            font-weight: 800;
                            letter-spacing: 12px;
                            color: #ffffff;
                            font-family: 'Courier New', 'SF Mono', monospace;
                            display: inline-block;
                            padding: 4px 24px;
                            border-radius: 12px;
                            background: linear-gradient(135deg, #6366f1, #a855f7, #ec4899);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                            background-clip: text;
                            text-shadow: none;
                            animation: pulse 2s ease-in-out infinite;
                            position: relative;
                        }
                        .code-value::after {
                            content: '';
                            position: absolute;
                            inset: -4px;
                            border-radius: 16px;
                            background: linear-gradient(135deg, rgba(99, 102, 241, 0.2), rgba(236, 72, 153, 0.2));
                            filter: blur(20px);
                            z-index: -1;
                        }

                        /* ===== 有效期 ===== */
                        .expiry-info {
                            text-align: center;
                            font-size: 14px;
                            color: rgba(255, 255, 255, 0.5);
                            margin-bottom: 28px;
                            position: relative;
                            z-index: 1;
                        }
                        .expiry-info .time-badge {
                            display: inline-block;
                            background: linear-gradient(135deg, rgba(251, 191, 36, 0.2), rgba(245, 158, 11, 0.15));
                            color: #fbbf24;
                            padding: 4px 16px;
                            border-radius: 20px;
                            font-weight: 600;
                            border: 1px solid rgba(251, 191, 36, 0.15);
                            backdrop-filter: blur(10px);
                        }

                        /* ===== 分隔线 ===== */
                        .divider {
                            border: none;
                            height: 1px;
                            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.08), transparent);
                            margin: 24px 0;
                            position: relative;
                            z-index: 1;
                        }

                        /* ===== 底部 ===== */
                        .footer-text {
                            font-size: 13px;
                            color: rgba(255, 255, 255, 0.3);
                            text-align: center;
                            line-height: 1.9;
                            position: relative;
                            z-index: 1;
                        }
                        .footer-text .highlight {
                            color: rgba(167, 139, 250, 0.6);
                        }
                        .footer-text .warning {
                            color: rgba(244, 63, 94, 0.5);
                        }
                        .footer-text .copyright {
                            font-size: 11px;
                            color: rgba(255, 255, 255, 0.15);
                            margin-top: 10px;
                            letter-spacing: 0.5px;
                        }

                        /* ===== 底部装饰点 ===== */
                        .dots {
                            display: flex;
                            justify-content: center;
                            gap: 8px;
                            margin-top: 20px;
                            position: relative;
                            z-index: 1;
                        }
                        .dots span {
                            width: 6px;
                            height: 6px;
                            border-radius: 50%;
                            background: linear-gradient(135deg, #6366f1, #ec4899);
                            opacity: 0.3;
                        }
                        .dots span:nth-child(2) { opacity: 0.5; }
                        .dots span:nth-child(3) { opacity: 0.7; }
                        .dots span:nth-child(4) { opacity: 0.5; }
                        .dots span:nth-child(5) { opacity: 0.3; }

                        /* ===== 移动端适配 ===== */
                        @media (max-width: 480px) {
                            .email-container {
                                padding: 12px;
                                margin: 10px auto;
                            }
                            .email-card {
                                padding: 32px 20px 28px;
                                border-radius: 18px;
                            }
                            .logo-icon {
                                width: 60px;
                                height: 60px;
                                font-size: 28px;
                            }
                            .title {
                                font-size: 22px;
                            }
                            .code-value {
                                font-size: 36px;
                                letter-spacing: 8px;
                                padding: 0 12px;
                            }
                            .code-wrapper {
                                padding: 20px 16px 18px;
                            }
                            .subtitle {
                                font-size: 14px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-card">
                            <!-- 顶部装饰渐变条 -->
                            <div class="header-accent"></div>

                            <!-- Logo -->
                            <div class="logo-area">
                                <div class="logo-icon">✦</div>
                                <h1 class="title">
                                    <span class="gradient-text">即时通讯</span>
                                </h1>
                            </div>

                            <!-- 正文 -->
                            <p class="subtitle">
                                感谢您注册 <strong>即时通讯</strong> 服务<br>
                                请输入以下验证码完成账号激活
                            </p>

                            <!-- 验证码 -->
                            <div class="code-wrapper">
                                <div class="code-label">✦ 安全验证码 ✦</div>
                                <div class="code-value">%s</div>
                            </div>

                            <!-- 有效期 -->
                            <div class="expiry-info">
                                ⏱ 有效期 <span class="time-badge">5 分钟</span> · 请及时使用
                            </div>

                            <hr class="divider">

                            <!-- 底部 -->
                            <div class="footer-text">
                                <p style="margin: 0;">
                                    <span class="highlight">◆</span> 如果非本人操作，请忽略此邮件 <span class="highlight">◆</span>
                                </p>
                                <p style="margin: 4px 0 0 0; font-size: 12px;">
                                    <span class="warning">⚠</span> 请勿将验证码告知他人，保障账户安全
                                </p>
                                <p class="copyright">
                                    © 2026 即时通讯 · 系统自动发送，请勿回复
                                </p>
                            </div>

                            <!-- 装饰点 -->
                            <div class="dots">
                                <span></span>
                                <span></span>
                                <span></span>
                                <span></span>
                                <span></span>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(code);
    }
}