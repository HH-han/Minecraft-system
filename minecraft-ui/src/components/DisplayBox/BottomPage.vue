<template>
    <!-- 页脚 (蓝色主题 · 无动画) -->
    <footer class="footer-wrapper">
        <div class="footer-container">
            <!-- 品牌区 -->
            <div class="brand-col">
                <div class="logo">
                    <i class="fas fa-globe-asia"></i>
                    <span>博览旅行</span>
                </div>
                <div class="brand-desc">
                    <i class="fas fa-compass"></i> 跨越山海，博览世界。每一程都是独特的风景。
                </div>
                <div style="display: flex; gap: 0.8rem; margin-top: 0.3rem; flex-wrap: wrap;">
                    <span
                        style="background: rgba(30,144,255,0.06); padding: 0.2rem 1rem; border-radius: 20px; border:1px solid rgba(64,164,255,0.1); font-size:0.75rem; color:#aac6ed;"><i
                            class="fas fa-check-circle" style="color: #4a9eff;"></i> 品质保证</span>
                    <span
                        style="background: rgba(30,144,255,0.06); padding: 0.2rem 1rem; border-radius: 20px; border:1px solid rgba(64,164,255,0.1); font-size:0.75rem; color:#aac6ed;"><i
                            class="fas fa-shield-alt" style="color: #4a9eff;"></i> 安心出行</span>
                </div>
            </div>

            <!-- 快速链接 -->
            <div class="quick-col">
                <h4 class="footer-title"><i class="fas fa-rocket"></i> 探索</h4>
                <ul class="footer-links">
                    <li><a href="#"><i class="fas fa-map-marked-alt"></i> 目的地</a></li>
                    <li><a href="#"><i class="fas fa-umbrella-beach"></i> 度假套餐</a></li>
                    <li><a href="#"><i class="fas fa-hiking"></i> 户外探险</a></li>
                    <li><a href="#"><i class="fas fa-ship"></i> 邮轮之旅</a></li>
                </ul>
            </div>

            <!-- 联系方式 -->
            <div class="contact-col">
                <h4 class="footer-title"><i class="fas fa-headset"></i> 联络</h4>
                <ul class="footer-links">
                    <li><a href="#"><i class="fas fa-phone-alt"></i> +86 400 8888 666</a></li>
                    <li><a href="#"><i class="fas fa-envelope"></i> travel@bltravel.com</a></li>
                    <li><a href="#"><i class="fas fa-clock"></i> 09:00 – 21:00 (UTC+8)</a></li>
                    <li><a href="#"><i class="fas fa-comment-dots"></i> 在线客服</a></li>
                </ul>
                <div class="social-icons">
                    <a href="#" aria-label="微信"><i class="fab fa-weixin"></i></a>
                    <a href="#" aria-label="微博"><i class="fab fa-weibo"></i></a>
                    <a href="#" aria-label="抖音"><i class="fab fa-tiktok"></i></a>
                    <a href="#" aria-label="YouTube"><i class="fab fa-youtube"></i></a>
                </div>
            </div>

            <!-- 订阅 -->
            <div class="newsletter-col">
                <h4 class="footer-title"><i class="fas fa-paper-plane"></i> 特惠订阅</h4>
                <p><i class="fas fa-gem" style="color: #4a9eff; margin-right: 6px;"></i> 获取独家旅行灵感 & 早鸟折扣</p>
                <div class="newsletter-input-group">
                    <input type="email" placeholder="你的邮箱地址" aria-label="邮箱订阅">
                    <button><i class="fas fa-arrow-right"></i> 订阅</button>
                </div>
                <p style="font-size:0.7rem; color:#6d8fb0; margin-top: 0.2rem;">
                    <i class="fas fa-lock" style="margin-right: 4px;"></i> 你的信息受保护，可随时退订
                </p>
            </div>
        </div>

        <!-- 版权 -->
        <div class="footer-bottom">
            <span>© 2026 <strong style="color: #b8d6ff;">博览旅行</strong> · 探索无限</span>
            <div class="legal">
                <a href="#">隐私政策</a>
                <a href="#">使用条款</a>
                <a href="#">Cookie 设置</a>
                <span style="margin-left: 0.8rem;"> <span class="heart">❤</span> 匠心旅行</span>
            </div>
        </div>
    </footer>
</template>
<script>
export default {
    name: 'BottomPage'
}
</script>
<style scoped>
/* ===== 页脚主容器：深邃蓝色调，无动画 ===== */
.footer-wrapper {
    position: relative;
    background: linear-gradient(160deg, #0b1a2f 0%, #0f2847 45%, #0b1f3b 100%);
    color: #d4e6ff;
    padding: 3rem 2rem 1.8rem;
    border-top: 2px solid rgba(64, 164, 255, 0.25);
    backdrop-filter: blur(2px);
    overflow: hidden;
}

/* 内容容器 置于上层 */
.footer-container {
    position: relative;
    z-index: 2;
    max-width: 1400px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1.2fr;
    gap: 2rem;
    align-items: start;
    justify-items: start;
}

/* ----- 品牌区域 (博览旅行) ----- */
.brand-col {
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
}

.logo {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 2.4rem;
    font-weight: 700;
    letter-spacing: 2px;
    color: #c6e2ff;
    text-shadow: 0 0 12px rgba(30, 144, 255, 0.3);
    transition: transform 0.2s ease;
}

.logo i {
    font-size: 2.8rem;
    color: #4a9eff;
    filter: drop-shadow(0 0 8px rgba(30, 144, 255, 0.4));
}

.logo:hover {
    transform: scale(1.01);
}

.brand-desc {
    font-size: 0.95rem;
    line-height: 1.6;
    color: #9bbce0;
    max-width: 260px;
    letter-spacing: 0.3px;
    background: rgba(255, 255, 255, 0.02);
    padding: 0.5rem 0.8rem;
    border-left: 3px solid #3b82f6;
    border-radius: 0 8px 8px 0;
    backdrop-filter: blur(2px);
    box-shadow: 0 0 12px rgba(30, 144, 255, 0.05);
}

.brand-desc i {
    color: #4a9eff;
    margin-right: 6px;
}

/* 通用标题 ———— 蓝色调 */
.footer-title {
    font-size: 1.1rem;
    font-weight: 600;
    letter-spacing: 1.5px;
    margin-bottom: 1.4rem;
    color: #d4eaff;
    position: relative;
    display: inline-block;
    text-transform: uppercase;
    border-bottom: 2px solid rgba(64, 164, 255, 0.25);
    padding-bottom: 6px;
}

.footer-title i {
    color: #4a9eff;
    margin-right: 8px;
    filter: drop-shadow(0 0 6px rgba(30, 144, 255, 0.2));
}

/* 链接列表 */
.footer-links {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 0.9rem;
    padding: 0;
}

.footer-links li a {
    color: #b8d2f2;
    text-decoration: none;
    font-size: 0.95rem;
    font-weight: 400;
    display: flex;
    align-items: center;
    gap: 10px;
    transition: all 0.2s ease;
    padding: 0.2rem 0;
    border-bottom: 1px solid transparent;
}

.footer-links li a i {
    width: 20px;
    color: #4a9eff;
    transition: transform 0.2s ease, color 0.2s ease;
    font-size: 0.9rem;
}

.footer-links li a:hover {
    color: #ffffff;
    transform: translateX(5px);
    border-bottom-color: rgba(64, 164, 255, 0.2);
}

.footer-links li a:hover i {
    transform: translateX(3px) scale(1.05);
    color: #6aafff;
}

/* 联系列 图标微调 */
.contact-col .footer-links li a i {
    width: 24px;
    color: #5b9eff;
}

.contact-col .footer-links li a:hover i {
    color: #7fc8ff;
}

/* 社交图标 ———— 蓝色玻璃态 */
.social-icons {
    display: flex;
    gap: 1.2rem;
    margin-top: 1.2rem;
    flex-wrap: nowrap;
}

.social-icons a {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: rgba(30, 144, 255, 0.06);
    backdrop-filter: blur(4px);
    border: 1px solid rgba(64, 164, 255, 0.2);
    color: #b0d2ff;
    font-size: 1.6rem;
    transition: all 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.2);
    box-shadow: 0 4px 12px rgba(0, 20, 50, 0.3);
    text-decoration: none;
}

.social-icons a:hover {
    transform: translateY(-4px) scale(1.05);
    background: rgba(30, 144, 255, 0.15);
    border-color: #4a9eff;
    color: #d4ecff;
    box-shadow: 0 0 20px rgba(30, 144, 255, 0.2);
}

/* 订阅区域 */
.newsletter-col {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.newsletter-col p {
    color: #9bbce0;
    font-size: 0.9rem;
    line-height: 1.5;
    margin-bottom: 0.5rem;
}

.newsletter-input-group {
    display: flex;
    background: rgba(255, 255, 255, 0.03);
    border-radius: 40px;
    border: 1px solid rgba(64, 164, 255, 0.15);
    backdrop-filter: blur(4px);
    overflow: hidden;
    transition: all 0.25s ease;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
}

.newsletter-input-group:focus-within {
    border-color: #4a9eff;
    box-shadow: 0 0 20px rgba(30, 144, 255, 0.1);
}

.newsletter-input-group input {
    flex: 1;
    background: transparent;
    border: none;
    padding: 0.8rem 1.2rem;
    font-size: 0.9rem;
    color: #e6f0ff;
    outline: none;
    min-width: 80px;
}

.newsletter-input-group input::placeholder {
    color: #7b9fc9;
    font-weight: 300;
    letter-spacing: 0.3px;
}

.newsletter-input-group button {
    background: linear-gradient(145deg, #1a5fb0, #0e3f7a);
    border: none;
    padding: 0.8rem 1.6rem;
    color: #f0f7ff;
    font-weight: 600;
    font-size: 0.9rem;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    gap: 8px;
    letter-spacing: 0.5px;
    border-left: 1px solid rgba(255, 255, 255, 0.05);
}

.newsletter-input-group button i {
    font-size: 1rem;
    color: #8fcbff;
}

.newsletter-input-group button:hover {
    background: linear-gradient(145deg, #2a70c0, #1a4f8a);
    box-shadow: 0 0 18px rgba(30, 144, 255, 0.15);
}

/* 底部版权 */
.footer-bottom {
    position: relative;
    z-index: 2;
    margin-top: 2.5rem;
    padding-top: 1.8rem;
    border-top: 1px solid rgba(100, 180, 255, 0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
    font-size: 0.8rem;
    color: #7a9fc9;
}

.footer-bottom .legal a {
    color: #7a9fc9;
    text-decoration: none;
    margin-left: 1.5rem;
    transition: color 0.2s;
}

.footer-bottom .legal a:hover {
    color: #8fcbff;
    text-decoration: underline;
}

.footer-bottom .heart {
    color: #4a9eff;
    margin: 0 4px;
    display: inline-block;
}

/* 响应式 */
@media (max-width: 992px) {
    .footer-container {
        grid-template-columns: 1fr 1fr;
        gap: 2rem 1.5rem;
    }

    .brand-col {
        grid-column: 1 / -1;
        align-items: center;
        text-align: center;
    }

    .brand-desc {
        max-width: 100%;
        text-align: center;
        border-left: none;
        border-bottom: 2px solid #3b82f6;
        border-radius: 0;
        padding: 0.5rem 1rem;
    }

    .social-icons {
        justify-content: center;
    }
}

@media (max-width: 600px) {
    .footer-container {
        grid-template-columns: 1fr;
        gap: 2rem;
        text-align: center;
    }

    .footer-links li a {
        justify-content: center;
    }

    .newsletter-input-group {
        flex-wrap: wrap;
        border-radius: 30px;
    }

    .newsletter-input-group input {
        width: 100%;
        text-align: center;
        padding: 0.8rem 1rem;
    }

    .newsletter-input-group button {
        width: 100%;
        justify-content: center;
        border-left: none;
        border-top: 1px solid rgba(255, 255, 255, 0.05);
        border-radius: 0 0 30px 30px;
    }

    .footer-bottom {
        flex-direction: column;
        text-align: center;
    }

    .footer-bottom .legal a {
        margin: 0 0.6rem;
    }

    .logo {
        font-size: 2rem;
        justify-content: center;
    }
}
</style>