package com.minecraft.utils;

import jakarta.mail.internet.InternetAddress;
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
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            InternetAddress from = new InternetAddress(fromEmail, "博览科技", "UTF-8");
            mimeMessage.setFrom(from);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, toEmail);
            mimeMessage.setSubject("【博览科技】注册验证码", "UTF-8");

            String htmlContent = buildVerificationCodeEmail(code);
            mimeMessage.setContent(htmlContent, "text/html; charset=UTF-8");

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("发送邮件失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("发送验证码邮件失败: " + e.getMessage());
        }
    }

    /**
     * 构建验证码邮件HTML内容 - 深空极光风格（移动端优先、邮件客户端安全）
     *
     * <p>设计要点：</p>
     * <ul>
     *     <li>深色背景由外层容器独立承载，不依赖 body 样式（Gmail 等会剥离 body 背景）</li>
     *     <li>不使用 backdrop-filter 毛玻璃与 background-clip 渐变文字，避免部分客户端渲染为空白/透明</li>
     *     <li>验证码使用高对比度纯色 + 柔和光晕，确保在任何客户端都清晰可读</li>
     *     <li>不使用 flex 布局，改用 inline-block / 文本居中，兼容性更好</li>
     *     <li>针对移动端做字号、间距、圆角与验证码字距的自适应</li>
     * </ul>
     *
     * @param code 验证码
     * @return HTML格式的邮件内容
     */
    private String buildVerificationCodeEmail(String code) {
        return """
                <!DOCTYPE html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="x-apple-disable-message-reformatting">
                    <meta name="format-detection" content="telephone=no,email=no,address=no">
                    <meta name="color-scheme" content="dark">
                    <meta name="supported-color-schemes" content="dark">
                    <title>博览科技 · 邮箱验证码</title>
                    <style>
                        :root { color-scheme: dark; }
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        body {
                            margin: 0;
                            padding: 0;
                            width: 100%;
                            background-color: #0a0818;
                            background-image: linear-gradient(165deg, #0a0818 0%, #1a1140 52%, #2a1a4d 100%);
                            font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Microsoft YaHei',
                                'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
                            -webkit-font-smoothing: antialiased;
                            -webkit-text-size-adjust: 100%;
                        }

                        /* 外层容器：独立承载整屏深色背景，不依赖 body */
                        .wrap {
                            width: 100%;
                            padding: 40px 16px;
                            background-color: #0a0818;
                            background-image: linear-gradient(165deg, #0a0818 0%, #1a1140 52%, #2a1a4d 100%);
                        }

                        /* 卡片：实色表面 + 细描边，替代不兼容的毛玻璃 */
                        .email-card {
                            width: 100%;
                            max-width: 520px;
                            margin: 0 auto;
                            border-radius: 22px;
                            overflow: hidden;
                            background-color: #17132f;
                            background-image: linear-gradient(165deg, #1d1740 0%, #17132f 58%, #1b1234 100%);
                            border: 1px solid rgba(124, 107, 255, 0.22);
                            box-shadow: 0 24px 64px rgba(0, 0, 0, 0.55);
                        }

                        /* 顶部流光渐变条（不支持动画时降级为静态渐变） */
                        .accent-bar {
                            height: 4px;
                            background-color: #7c6bff;
                            background-image: linear-gradient(90deg, #7c6bff, #b26bff, #ff6bb5, #b26bff, #7c6bff);
                            background-size: 220% 100%;
                            animation: shimmer 4s linear infinite;
                        }
                        @keyframes shimmer {
                            0% { background-position: -110% 0; }
                            100% { background-position: 210% 0; }
                        }

                        .inner { padding: 40px 38px 34px; }

                        /* ===== 品牌 ===== */
                        .brand {
                            text-align: center;
                            margin-bottom: 26px;
                        }
                        .logo {
                            width: 64px;
                            height: 64px;
                            line-height: 64px;
                            margin: 0 auto 16px;
                            border-radius: 18px;
                            font-size: 30px;
                            text-align: center;
                            color: #ffffff;
                            background-color: #6d5cff;
                            background-image: linear-gradient(135deg, #7c6bff, #b26bff 55%, #ff6bb5);
                            box-shadow: 0 14px 34px rgba(124, 107, 255, 0.45);
                        }
                        .brand-name {
                            font-size: 13px;
                            font-weight: 600;
                            letter-spacing: 0.34em;
                            text-indent: 0.34em;
                            color: #a9a3c9;
                        }

                        /* ===== 标题与说明 ===== */
                        .title {
                            margin: 0 0 10px;
                            text-align: center;
                            font-size: 24px;
                            font-weight: 700;
                            letter-spacing: 0.01em;
                            color: #f3f0ff;
                        }
                        .subtitle {
                            margin: 0 0 26px;
                            text-align: center;
                            font-size: 14px;
                            line-height: 1.8;
                            color: #a9a3c9;
                        }

                        /* ===== 验证码卡片 ===== */
                        .code-box {
                            margin: 0 0 22px;
                            padding: 24px 16px;
                            text-align: center;
                            border-radius: 16px;
                            background-color: rgba(124, 107, 255, 0.10);
                            border: 1px solid rgba(124, 107, 255, 0.30);
                        }
                        .code-label {
                            margin-bottom: 14px;
                            font-size: 11px;
                            font-weight: 600;
                            letter-spacing: 0.34em;
                            text-indent: 0.34em;
                            color: #8e86b8;
                        }
                        .code-value {
                            display: inline-block;
                            font-family: 'SFMono-Regular', 'Roboto Mono', 'Courier New', monospace;
                            font-size: 44px;
                            line-height: 1.1;
                            font-weight: 700;
                            letter-spacing: 12px;
                            color: #ffffff;
                            text-shadow: 0 0 28px rgba(178, 107, 255, 0.55);
                        }

                        /* ===== 有效期 ===== */
                        .expiry {
                            margin-bottom: 26px;
                            text-align: center;
                            font-size: 13px;
                            color: #a9a3c9;
                        }
                        .expiry-badge {
                            display: inline-block;
                            margin: 0 4px;
                            padding: 4px 14px;
                            border-radius: 999px;
                            font-weight: 700;
                            color: #ffd79a;
                            background-color: rgba(255, 199, 115, 0.14);
                            border: 1px solid rgba(255, 199, 115, 0.30);
                        }

                        .divider {
                            height: 1px;
                            border: 0;
                            margin: 0 0 22px;
                            background-color: rgba(255, 255, 255, 0.09);
                            background-image: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.16), transparent);
                        }

                        /* ===== 安全提示 ===== */
                        .tips {
                            text-align: center;
                            font-size: 13px;
                            line-height: 1.9;
                            color: #8e86b8;
                        }
                        .tips .warn { color: #d98aa6; }

                        /* 装饰点（纯文本实现，兼容性优于 flex） */
                        .dots {
                            margin-top: 18px;
                            text-align: center;
                            font-size: 8px;
                            letter-spacing: 0.35em;
                            text-indent: 0.35em;
                            color: rgba(124, 107, 255, 0.6);
                        }

                        .copyright {
                            margin-top: 14px;
                            text-align: center;
                            font-size: 11px;
                            letter-spacing: 0.04em;
                            color: #5e5880;
                        }

                        /* ===== 移动端优化 ===== */
                        @media (max-width: 480px) {
                            .wrap { padding: 20px 12px; }
                            .email-card { border-radius: 18px; }
                            .inner { padding: 30px 20px 26px; }
                            .logo {
                                width: 56px;
                                height: 56px;
                                line-height: 56px;
                                font-size: 26px;
                                border-radius: 16px;
                            }
                            .brand { margin-bottom: 22px; }
                            .brand-name { letter-spacing: 0.26em; text-indent: 0.26em; }
                            .title { font-size: 21px; }
                            .subtitle { margin-bottom: 22px; font-size: 13px; }
                            .code-box { padding: 20px 10px; border-radius: 14px; }
                            .code-value { font-size: 38px; letter-spacing: 9px; }
                            .expiry { margin-bottom: 22px; font-size: 12px; }
                            .tips { font-size: 12px; }
                        }

                        @media (max-width: 360px) {
                            .inner { padding: 26px 16px 22px; }
                            .code-value { font-size: 32px; letter-spacing: 6px; }
                        }
                    </style>
                </head>
                <body>
                    <div class="wrap">
                        <div class="email-card">
                            <div class="accent-bar"></div>
                            <div class="inner">
                                <!-- 品牌 -->
                                <div class="brand">
                                    <div class="logo">✦</div>
                                    <div class="brand-name">博览科技</div>
                                </div>

                                <!-- 标题与说明 -->
                                <h1 class="title">邮箱验证码</h1>
                                <p class="subtitle">
                                    感谢您注册博览科技服务<br>
                                    请输入以下验证码完成账号激活
                                </p>

                                <!-- 验证码 -->
                                <div class="code-box">
                                    <div class="code-label">安全验证码</div>
                                    <div class="code-value">%s</div>
                                </div>

                                <!-- 有效期 -->
                                <div class="expiry">
                                    有效期 <span class="expiry-badge">5 分钟</span> · 请及时使用
                                </div>

                                <hr class="divider">

                                <!-- 安全提示 -->
                                <div class="tips">
                                    <p>如非本人操作，请忽略此邮件</p>
                                    <p class="warn">请勿将验证码告知他人，保障账户安全</p>
                                </div>

                                <!-- 装饰点 -->
                                <div class="dots">●&nbsp;●&nbsp;●&nbsp;●&nbsp;●</div>

                                <div class="copyright">© 2026 博览科技 · 系统自动发送，请勿回复</div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """
        .replace("%s", code);
    }
}
