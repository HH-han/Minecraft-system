<template>
    <div class="website-intro">
        <div class="intro-hero">
            <h1 class="hero-title">Minecraft 旅游平台</h1>
            <p class="hero-subtitle">一站式旅游资讯与旅行规划服务</p>
        </div>

        <section class="intro-section">
            <div class="section-card">
                <h2 class="card-title">关于本站</h2>
                <p class="card-text">
                    本网站是本人学习使用Spring Boot和Vue 3独立开发的旅游平台。
                    后端采用Spring Boot + MyBatis架构，前端使用Vue 3 + Element Plus构建。
                </p>
                <p class="card-text">
                    平台提供景点推荐、酒店预订、美食探索和旅行计划管理等核心功能，
                    旨在为用户提供便捷、高效的旅游服务体验。
                </p>
            </div>
        </section>

        <section class="intro-section features-section">
            <h2 class="section-title">主要功能</h2>
            <div class="features-grid">
                <div class="feature-card">
                    <h3>资讯中心</h3>
                    <p>提供实时的旅游资讯和攻略，帮助用户规划完美旅程</p>
                </div>
                <div class="feature-card">
                    <h3>娱乐天地</h3>
                    <p>汇聚各地美食、景点和旅游资源，发现旅行的乐趣</p>
                </div>
                <div class="feature-card">
                    <h3>用户互动</h3>
                    <p>支持论坛、评论和私信功能，方便用户之间的交流</p>
                </div>
            </div>
        </section>

        <section class="intro-section tech-section">
            <h2 class="section-title">技术栈</h2>
            <div class="tech-grid">
                <div class="tech-card">
                    <div class="tech-header">
                        <span class="tech-badge backend">后端</span>
                    </div>
                    <ul class="tech-list">
                        <li>Spring Boot</li>
                        <li>Spring Security</li>
                        <li>MyBatis</li>
                        <li>JWT</li>
                        <li>Druid</li>
                    </ul>
                </div>
                <div class="tech-card">
                    <div class="tech-header">
                        <span class="tech-badge frontend">前端</span>
                    </div>
                    <ul class="tech-list">
                        <li>Vue 3</li>
                        <li>Element Plus</li>
                        <li>Vite</li>
                        <li>Pinia</li>
                        <li>Vue Router</li>
                    </ul>
                </div>
                <div class="tech-card">
                    <div class="tech-header">
                        <span class="tech-badge database">数据库</span>
                    </div>
                    <ul class="tech-list">
                        <li>MySQL</li>
                        <li>Druid连接池</li>
                        <li>Redis缓存</li>
                    </ul>
                </div>
            </div>
        </section>

        <section class="intro-section contact-section">
            <div class="section-card contact-card">
                <h2 class="card-title">联系我们</h2>
                <p class="card-text">
                    如果您有任何问题或建议，请随时与我们联系。
                </p>
                <a :href="contactEmail" target="_blank" class="contact-link">
                    <span>发送邮件</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M7 17L17 7M17 7H7M17 7V17"/>
                    </svg>
                </a>
            </div>
        </section>

        <footer class="intro-footer">
            <p class="copyright">© {{ currentYear }} Minecraft 旅游平台. 保留所有权利。</p>
            <div class="legal-links">
                <a href="#">隐私政策</a>
                <a href="#">服务条款</a>
                <a href="#">Cookie政策</a>
            </div>
        </footer>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const contactEmail = ref('https://wx.mail.qq.com/login/loginpage?auth_type=3&login_exception=true');
const currentYear = new Date().getFullYear();
</script>

<style scoped>
.website-intro {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 22px;
}

.intro-hero {
    text-align: center;
    padding: 60px 20px;
    background: linear-gradient(180deg, #f5f5f7 0%, #ffffff 100%);
    border-radius: 28px;
    margin-bottom: 60px;
}

.hero-title {
    font-size: 56px;
    font-weight: 700;
    color: #1d1d1f;
    letter-spacing: -0.02em;
    margin-bottom: 12px;
}

.hero-subtitle {
    font-size: 28px;
    color: #6e6e73;
    font-weight: 400;
}

.intro-section {
    margin-bottom: 60px;
}

.section-title {
    font-size: 32px;
    font-weight: 700;
    color: #1d1d1f;
    text-align: center;
    margin-bottom: 40px;
}

.section-card {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 48px 40px;
}

.card-title {
    font-size: 28px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 20px;
}

.card-text {
    font-size: 17px;
    line-height: 1.6;
    color: #6e6e73;
    margin-bottom: 16px;
}

.card-text:last-child {
    margin-bottom: 0;
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 24px;
}

.feature-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 32px 28px;
    transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

.feature-card h3 {
    font-size: 20px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 12px;
}

.feature-card p {
    font-size: 15px;
    color: #6e6e73;
    line-height: 1.5;
}

.tech-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
}

.tech-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 28px 24px;
}

.tech-header {
    margin-bottom: 20px;
}

.tech-badge {
    display: inline-block;
    padding: 6px 16px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
}

.tech-badge.backend {
    background: #dbeafe;
    color: #1e40af;
}

.tech-badge.frontend {
    background: #d1fae5;
    color: #065f46;
}

.tech-badge.database {
    background: #fef3c7;
    color: #92400e;
}

.tech-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.tech-list li {
    padding: 10px 0;
    font-size: 15px;
    color: #6e6e73;
    border-bottom: 1px solid #e5e5e7;
}

.tech-list li:last-child {
    border-bottom: none;
}

.contact-card {
    text-align: center;
}

.contact-link {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    margin-top: 24px;
    padding: 14px 32px;
    background: #2997ff;
    color: white;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
    text-decoration: none;
    transition: background 0.2s, transform 0.2s;
}

.contact-link:hover {
    background: #0066cc;
    transform: translateY(-2px);
}

.contact-link svg {
    width: 18px;
    height: 18px;
}

.intro-footer {
    text-align: center;
    padding: 40px 20px;
    border-top: 1px solid #f5f5f7;
}

.copyright {
    font-size: 13px;
    color: #6e6e73;
    margin-bottom: 16px;
}

.legal-links {
    display: flex;
    justify-content: center;
    gap: 32px;
}

.legal-links a {
    font-size: 13px;
    color: #2997ff;
    text-decoration: none;
}

.legal-links a:hover {
    text-decoration: underline;
}

@media (max-width: 768px) {
    .hero-title {
        font-size: 36px;
    }

    .hero-subtitle {
        font-size: 20px;
    }

    .section-card {
        padding: 32px 24px;
    }

    .legal-links {
        flex-direction: column;
        gap: 12px;
    }
}
</style>
