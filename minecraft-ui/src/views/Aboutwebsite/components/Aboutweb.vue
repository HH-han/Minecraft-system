<template>
    <div class="about-page">
        <div class="about-hero">
            <h1 class="hero-title">关于 Minecraft 旅游平台</h1>
            <p class="hero-subtitle">探索世界，从这里开始</p>
        </div>

        <section class="about-section">
            <div class="section-card">
                <h2 class="card-title">关于我们</h2>
                <p class="card-text">
                    Minecraft 旅游平台是一个专注于旅游资讯和旅行规划的现代化网站。
                    我们致力于为用户提供全面的旅游景点信息、酒店预订、美食推荐以及旅行攻略分享服务。
                </p>
                <p class="card-text">
                    平台整合了全球热门旅游目的地，为用户打造一站式的旅行体验。
                    无论是商务出行还是休闲度假，都能在这里找到满意的解决方案。
                </p>
            </div>
        </section>

        <section class="about-section features-section">
            <h2 class="section-title">我们的核心功能</h2>
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"></circle>
                            <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                        </svg>
                    </div>
                    <h3>景点推荐</h3>
                    <p>汇集全球热门旅游景点，提供详细的景点信息和用户评价</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                    </div>
                    <h3>酒店预订</h3>
                    <p>精选优质酒店，提供多种房型选择和优惠价格</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M18 8h1a4 4 0 0 1 0 8h-1"></path>
                            <path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path>
                            <line x1="6" y1="1" x2="6" y2="4"></line>
                            <line x1="10" y1="1" x2="10" y2="4"></line>
                            <line x1="14" y1="1" x2="14" y2="4"></line>
                        </svg>
                    </div>
                    <h3>美食推荐</h3>
                    <p>探索各地特色美食，发现隐藏的美味餐厅</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                    </div>
                    <h3>旅行计划</h3>
                    <p>智能规划行程，轻松管理您的每一次旅行</p>
                </div>
            </div>
        </section>

        <section class="about-section tech-section">
            <div class="section-card">
                <h2 class="card-title">技术架构</h2>
                <div class="tech-stack">
                    <div class="tech-item">
                        <span class="tech-label">后端框架</span>
                        <span class="tech-value">Spring Boot + MyBatis</span>
                    </div>
                    <div class="tech-item">
                        <span class="tech-label">前端框架</span>
                        <span class="tech-value">Vue 3 + Element Plus</span>
                    </div>
                    <div class="tech-item">
                        <span class="tech-label">数据库</span>
                        <span class="tech-value">MySQL + Druid</span>
                    </div>
                    <div class="tech-item">
                        <span class="tech-label">认证方式</span>
                        <span class="tech-value">JWT + Spring Security</span>
                    </div>
                </div>
            </div>
        </section>

        <section class="about-section stats-section">
            <div class="stats-grid">
                <div class="stat-item">
                    <span class="stat-value">100+</span>
                    <span class="stat-label">热门景点</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">50+</span>
                    <span class="stat-label">精选酒店</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">99%</span>
                    <span class="stat-label">用户满意度</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">24/7</span>
                    <span class="stat-label">在线支持</span>
                </div>
            </div>
        </section>
    </div>
</template>

<script setup>
</script>

<style scoped>
.about-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 22px;
}

.about-hero {
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

.about-section {
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
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 24px;
}

.feature-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 32px 28px;
    text-align: center;
    transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

.feature-icon {
    width: 64px;
    height: 64px;
    margin: 0 auto 20px;
    background: #ffffff;
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.feature-icon svg {
    width: 32px;
    height: 32px;
    color: #2997ff;
}

.feature-card h3 {
    font-size: 20px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 8px;
}

.feature-card p {
    font-size: 15px;
    color: #6e6e73;
    line-height: 1.5;
}

.tech-section .section-card {
    text-align: center;
}

.tech-stack {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 32px;
    margin-top: 32px;
}

.tech-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.tech-label {
    font-size: 14px;
    color: #6e6e73;
}

.tech-value {
    font-size: 18px;
    font-weight: 600;
    color: #1d1d1f;
}

.stats-section {
    background: #000000;
    border-radius: 28px;
    padding: 48px 40px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 32px;
    text-align: center;
}

.stat-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.stat-value {
    font-size: 48px;
    font-weight: 700;
    color: #ffffff;
}

.stat-label {
    font-size: 14px;
    color: #a1a1a6;
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

    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .stat-value {
        font-size: 36px;
    }
}
</style>
