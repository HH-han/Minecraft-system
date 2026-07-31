<template>
    <div class="website-introduction">
        <!-- 主视觉 Hero -->
        <section class="hero-section">
            <div class="hero-content">
                <p class="hero-eyebrow">探索 · 发现 · 启程</p>
                <h1 class="hero-title">世界那么大<br>带你去看尽精彩</h1>
                <p class="hero-subtitle">一站式旅游资讯与旅行规划平台，让每一次出行都成为难忘的旅程。</p>
                <div class="hero-cta-group">
                    <button class="cta-primary" @click="scrollToSection('features')">
                        开始探索
                        <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M5 12h14M12 5l7 7-7 7" />
                        </svg>
                    </button>
                    <button class="cta-secondary" @click="scrollToSection('about')">
                        了解更多
                    </button>
                </div>
            </div>
            <div class="hero-stats">
                <div class="stat-item">
                    <span class="stat-number">{{ animatedStats.destinations }}+</span>
                    <span class="stat-label">精选目的地</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ animatedStats.experiences }}+</span>
                    <span class="stat-label">旅行体验</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ animatedStats.users }}万+</span>
                    <span class="stat-label">用户信赖</span>
                </div>
            </div>
        </section>

        <!-- 推荐卡片 -->
        <section>
      <section class="product-card fade-in-up delay-100">
        <Recommend />
      </section>
        </section>

        <!-- 关于本站 -->
        <section id="about" class="intro-section about-section">
            <div class="section-header">
                <span class="section-eyebrow">关于我们</span>
                <h2 class="section-title">用心打造每一程旅程</h2>
            </div>
            <div class="about-grid">
                <div class="about-card about-card--primary">
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                            <circle cx="12" cy="10" r="3" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">精准目的地推荐</h3>
                    <p class="about-card-desc">
                        基于用户偏好与季节热点，智能推荐最值得一去的旅行目的地，让选择不再困难。
                    </p>
                </div>
                <div class="about-card about-card--primary">
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                            <line x1="16" y1="2" x2="16" y2="6" />
                            <line x1="8" y1="2" x2="8" y2="6" />
                            <line x1="3" y1="10" x2="21" y2="10" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">一站式行程规划</h3>
                    <p class="about-card-desc">
                        从景点、酒店到美食攻略，全流程覆盖旅行所需，轻松制定专属行程计划。
                    </p>
                </div>
                <div class="about-card about-card--primary">
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                            <circle cx="9" cy="7" r="4" />
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                            <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">活跃旅行社区</h3>
                    <p class="about-card-desc">
                        与数万旅行者交流心得，分享旅途故事，让每一次出行都有据可循、有人可问。
                    </p>
                </div>
            </div>
        </section>

        <!-- 核心功能 -->
        <section id="features" class="intro-section features-section">
            <div class="section-header section-header--center">
                <span class="section-eyebrow">核心功能</span>
                <h2 class="section-title">为旅行而生的全方位服务</h2>
                <p class="section-subtitle">从灵感到出发，我们提供完整的旅行解决方案</p>
            </div>
            <div class="features-grid">
                <div class="feature-card" v-for="feature in features" :key="feature.id">
                    <div class="feature-icon-wrapper" :style="{ background: feature.bgColor }">
                        <span class="feature-emoji">{{ feature.icon }}</span>
                    </div>
                    <h3 class="feature-title">{{ feature.title }}</h3>
                    <p class="feature-desc">{{ feature.desc }}</p>
                    <ul class="feature-list">
                        <li v-for="item in feature.items" :key="item">
                            <svg class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <polyline points="20 6 9 17 4 12" />
                            </svg>
                            <span>{{ item }}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </section>

        <!-- 全宽深色特色条 -->
        <section class="feature-strip">
            <div class="strip-content">
                <p class="strip-eyebrow">沉浸式体验</p>
                <h2 class="strip-title">足不出户，环游世界</h2>
                <p class="strip-desc">
                    高清全景影像与沉浸式叙事，让你在出发前就能感受目的地的独特魅力。
                </p>
                <button class="strip-cta" @click="scrollToSection('stories')">
                    立即体验
                    <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
                </button>
            </div>
        </section>

        <!-- 旅行故事 -->
        <section id="stories" class="intro-section stories-section">
            <!-- 双卡片区 -->
            <section class="two-column-section">
                <div class="two-column-flex">
                    <div class="two-column-rc-grid">
                        <!-- 三列配件区 -->
                        <section class="three-column-section fade-in-up delay-200">
                            <Recommendations
                                @open-recommendation-detail="(item) => $emit('open-recommendation-detail', item)" />
                        </section>
                        <!-- 首页内容 -->
                        <section class="product-card fade-in-up delay-200">
                            <Content @open-detail="(type, id) => $emit('open-detail', type, id)" />
                        </section>
                    </div>
                </div>
            </section>
        </section>

        <!-- 技术架构 -->
        <section class="intro-section tech-section">
            <div class="section-header section-header--center">
                <span class="section-eyebrow">技术架构</span>
                <h2 class="section-title">稳定可靠的技术底座</h2>
                <p class="section-subtitle">采用业界领先的技术栈，保障平台高性能与安全</p>
            </div>
            <div class="tech-grid">
                <div class="tech-card" v-for="tech in techStack" :key="tech.category">
                    <div class="tech-card-header">
                        <span class="tech-badge" :class="tech.theme">{{ tech.category }}</span>
                    </div>
                    <ul class="tech-list">
                        <li v-for="item in tech.items" :key="item.name">
                            <span class="tech-item-name">{{ item.name }}</span>
                            <span class="tech-item-desc">{{ item.desc }}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </section>

        <!-- 发展历程 -->
        <section class="intro-section timeline-section">
            <div class="section-header section-header--center">
                <span class="section-eyebrow">发展历程</span>
                <h2 class="section-title">一路成长，不忘初心</h2>
            </div>
            <div class="timeline">
                <div class="timeline-item" v-for="(milestone, index) in milestones" :key="index"
                    :class="{ 'timeline-item--right': index % 2 === 1 }">
                    <div class="timeline-content">
                        <span class="timeline-date">{{ milestone.date }}</span>
                        <h3 class="timeline-title">{{ milestone.title }}</h3>
                        <p class="timeline-desc">{{ milestone.desc }}</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- 数据统计 -->
        <section class="stats-section">
            <div class="stats-grid">
                <div class="stat-card" v-for="stat in platformStats" :key="stat.label">
                    <span class="stat-card-number">{{ stat.value }}</span>
                    <span class="stat-card-label">{{ stat.label }}</span>
                </div>
            </div>
        </section>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Recommendations from './recommendations.vue';
import Content from './content.vue';
import Recommend from '@/views/Recommend/index.vue';
const contactEmail = ref('https://wx.mail.qq.com/login/loginpage?auth_type=3&login_exception=true');
const currentYear = new Date().getFullYear();

// Hero 区数据统计动画
const animatedStats = ref({
    destinations: 0,
    experiences: 0,
    users: 0
});

const targetStats = {
    destinations: 120,
    experiences: 380,
    users: 50
};

// 核心功能
const features = ref([
    {
        id: 1,
        icon: '🗺️',
        title: '资讯中心',
        desc: '提供实时、全面的旅游资讯与攻略',
        items: ['热门景点推荐', '季节性旅行指南', '安全出行提示', '签证办理攻略'],
        bgColor: 'linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)'
    },
    {
        id: 2,
        icon: '🏨',
        title: '预订服务',
        desc: '一站式预订酒店、机票与门票',
        items: ['酒店比价预订', '特价机票查询', '景点门票购买', '行程套餐定制'],
        bgColor: 'linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)'
    },
    {
        id: 3,
        icon: '🍜',
        title: '美食探索',
        desc: '发现地道美食，品味地方特色',
        items: ['本地美食推荐', '餐厅评价排行', '特色菜谱收藏', '美食路线规划'],
        bgColor: 'linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%)'
    },
    {
        id: 4,
        icon: '💬',
        title: '社区互动',
        desc: '与旅友交流分享，记录旅途点滴',
        items: ['旅行笔记发布', '论坛话题讨论', '私信即时沟通', '游记点赞收藏'],
        bgColor: 'linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)'
    },
    {
        id: 5,
        icon: '📦',
        title: '商品商城',
        desc: '精选旅行好物，让出行更轻松',
        items: ['旅行装备选购', '纪念品特卖', '土特产直购', '限时优惠活动'],
        bgColor: 'linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%)'
    },
    {
        id: 6,
        icon: '📊',
        title: '数据洞察',
        desc: '智能分析旅行趋势与用户偏好',
        items: ['热门目的地排行', '价格趋势预测', '个性化推荐', '旅行数据分析'],
        bgColor: 'linear-gradient(135deg, #ffe4e6 0%, #fecdd3 100%)'
    }
]);

// 技术栈
const techStack = ref([
    {
        category: '后端',
        theme: 'backend',
        items: [
            { name: 'Spring Boot', desc: '应用框架' },
            { name: 'Spring Security', desc: '安全认证' },
            { name: 'MyBatis', desc: 'ORM 框架' },
            { name: 'JWT', desc: '令牌认证' },
            { name: 'Redis', desc: '缓存中间件' }
        ]
    },
    {
        category: '前端',
        theme: 'frontend',
        items: [
            { name: 'Vue 3', desc: '视图框架' },
            { name: 'Element Plus', desc: 'UI 组件库' },
            { name: 'Vite', desc: '构建工具' },
            { name: 'Pinia', desc: '状态管理' },
            { name: 'Vue Router', desc: '路由管理' }
        ]
    },
    {
        category: '数据',
        theme: 'database',
        items: [
            { name: 'MySQL', desc: '关系型数据库' },
            { name: 'Druid', desc: '连接池' },
            { name: 'Redis', desc: '缓存数据库' },
            { name: 'MyBatis', desc: '数据访问层' }
        ]
    }
]);

// 发展历程
const milestones = ref([
    { date: '2024.01', title: '项目启动', desc: 'Minecraft 旅游平台正式立项，开始需求分析与架构设计。' },
    { date: '2024.06', title: '核心功能上线', desc: '完成景点推荐、酒店预订、美食探索等核心模块开发。' },
    { date: '2024.12', title: '社区功能完善', desc: '上线论坛、评论、私信等社交功能，构建旅行者社区。' },
    { date: '2025.06', title: '性能优化迭代', desc: '引入 Redis 缓存与 CDN 加速，平台响应速度提升 60%。' },
    { date: '2026.01', title: '沉浸式体验', desc: '新增全景影像与叙事内容，打造沉浸式旅行预览体验。' },
    { date: '2026.07', title: '持续进化中', desc: '不断打磨细节，致力于为用户提供更优质的旅行服务。' }
]);

// 平台数据
const platformStats = ref([
    { label: '注册用户', value: '52万+' },
    { label: '目的地覆盖', value: '120+' },
    { label: '旅行笔记', value: '8.6万+' },
    { label: '好评率', value: '98%' }
]);

// 滚动到指定区块
const scrollToSection = (id) => {
    const element = document.getElementById(id);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
};

// 查看故事详情
const viewStory = (story) => {
    console.log('查看故事:', story.title);
};

// 数字滚动动画
const animateNumbers = () => {
    const duration = 1500;
    const steps = 60;
    const interval = duration / steps;
    let currentStep = 0;

    const timer = setInterval(() => {
        currentStep++;
        const progress = currentStep / steps;
        const easeProgress = 1 - Math.pow(1 - progress, 3);

        animatedStats.value = {
            destinations: Math.floor(targetStats.destinations * easeProgress),
            experiences: Math.floor(targetStats.experiences * easeProgress),
            users: Math.floor(targetStats.users * easeProgress)
        };

        if (currentStep >= steps) {
            clearInterval(timer);
            animatedStats.value = { ...targetStats };
        }
    }, interval);
};

onMounted(() => {
    setTimeout(animateNumbers, 300);
});
</script>

<style scoped>
.website-introduction {
    font-family: 'SF Pro Display', 'SF Pro', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
    color: #1d1d1f;
    background: #ffffff;
    scroll-behavior: smooth;
}

/* ============ Hero 主视觉 ============ */
.hero-section {
    padding: 80px 22px 60px;
    text-align: center;
    background: linear-gradient(180deg, #f5f5f7 0%, #ffffff 100%);
    border-radius: 28px;
    margin-top: 80px;
}

.hero-eyebrow {
    font-size: 16px;
    font-weight: 600;
    color: #2997ff;
    letter-spacing: 0.1em;
    margin-bottom: 16px;
    text-transform: uppercase;
}

.hero-title {
    font-size: 64px;
    font-weight: 700;
    line-height: 1.05;
    letter-spacing: -0.02em;
    color: #1d1d1f;
    margin: 0 0 20px;
}

.hero-subtitle {
    font-size: 21px;
    font-weight: 400;
    line-height: 1.4;
    color: #6e6e73;
    max-width: 640px;
    margin: 0 auto 36px;
}

.hero-cta-group {
    display: flex;
    justify-content: center;
    gap: 16px;
    flex-wrap: wrap;
}

.cta-primary,
.cta-secondary {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 14px 28px;
    border-radius: 980px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    border: none;
}

.cta-primary {
    background: #2997ff;
    color: #ffffff;
}

.cta-primary:hover {
    background: #0066cc;
    transform: translateY(-1px);
}

.cta-secondary {
    background: transparent;
    color: #2997ff;
    border: 1px solid #2997ff;
}

.cta-secondary:hover {
    background: rgba(41, 151, 255, 0.08);
}

.cta-arrow {
    width: 18px;
    height: 18px;
}

.hero-stats {
    display: flex;
    justify-content: center;
    gap: 80px;
    margin-top: 60px;
    padding-top: 40px;
    border-top: 1px solid #d2d2d6;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-number {
    font-size: 40px;
    font-weight: 700;
    color: #1d1d1f;
    letter-spacing: -0.02em;
}

.stat-label {
    font-size: 14px;
    color: #6e6e73;
    margin-top: 4px;
}

/* ============ 通用区块 ============ */
.intro-section {
    padding: 0 22px;
    margin-bottom: 80px;
}

.section-header {
    margin-bottom: 40px;
}

.section-header--center {
    text-align: center;
}

.section-eyebrow {
    display: inline-block;
    font-size: 14px;
    font-weight: 600;
    color: #2997ff;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    margin-bottom: 12px;
}

.section-title {
    font-size: 44px;
    font-weight: 700;
    line-height: 1.1;
    letter-spacing: -0.02em;
    color: #1d1d1f;
    margin: 0 0 12px;
}

.section-subtitle {
    font-size: 19px;
    color: #6e6e73;
    max-width: 640px;
    margin: 0 auto;
    line-height: 1.4;
}

.two-column-flex {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.two-column-rc-grid {
    display: flex;
    flex-direction: row;
    gap: 24px;
}

/* 三列配件区 */
.three-column-section {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 40px 24px 30px;
    transition: transform 0.2s, box-shadow 0.2s;
    overflow: hidden;
    flex: 1;
}

/* 滚动入场动画 */
.fade-in-up {
    opacity: 0;
    transform: translateY(30px);
    animation: fadeInUp 0.8s ease forwards;
}

@keyframes fadeInUp {
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 延迟动画 */
.delay-100 {
    animation-delay: 0.1s;
}

.delay-200 {
    animation-delay: 0.2s;
}

.delay-300 {
    animation-delay: 0.3s;
}

.delay-400 {
    animation-delay: 0.4s;
}

.delay-500 {
    animation-delay: 0.5s;
}

/* 脉冲动画 */
.pulse {
    animation: pulse 2s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.05);
    }

    100% {
        transform: scale(1);
    }
}

/* 呼吸动画 */
.breath {
    animation: breath 3s ease-in-out infinite;
}

@keyframes breath {

    0%,
    100% {
        opacity: 0.8;
    }

    50% {
        opacity: 1;
    }
}

/* 产品卡片 */
.product-card {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 40px 24px 30px;
    transition: transform 0.2s, box-shadow 0.2s;
    overflow: hidden;
    flex: 1;
}

.product-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

/* ============ 关于本站 ============ */
.about-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.about-card {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 40px 32px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.about-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

.about-card-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    background: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 24px;
}

.about-card-icon svg {
    width: 28px;
    height: 28px;
    color: #2997ff;
}

.about-card-title {
    font-size: 22px;
    font-weight: 600;
    color: #1d1d1f;
    margin: 0 0 12px;
}

.about-card-desc {
    font-size: 15px;
    line-height: 1.5;
    color: #6e6e73;
    margin: 0;
}

/* ============ 核心功能 ============ */
.features-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.feature-card {
    background: #ffffff;
    border: 1px solid #d2d2d6;
    border-radius: 24px;
    padding: 36px 28px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.feature-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

.feature-icon-wrapper {
    width: 64px;
    height: 64px;
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
}

.feature-emoji {
    font-size: 32px;
    line-height: 1;
}

.feature-title {
    font-size: 22px;
    font-weight: 600;
    color: #1d1d1f;
    margin: 0 0 10px;
}

.feature-desc {
    font-size: 15px;
    line-height: 1.5;
    color: #6e6e73;
    margin: 0 0 20px;
}

.feature-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.feature-list li {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 0;
    font-size: 14px;
    color: #1d1d1f;
}

.check-icon {
    width: 18px;
    height: 18px;
    color: #34c759;
    flex-shrink: 0;
}

/* ============ 全宽深色特色条 ============ */
.feature-strip {
    background: #000000;
    padding: 80px 22px;
    margin-bottom: 80px;
    text-align: center;
}

.strip-content {
    max-width: 840px;
    margin: 0 auto;
}

.strip-eyebrow {
    font-size: 14px;
    font-weight: 600;
    color: #2997ff;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    margin-bottom: 16px;
}

.strip-title {
    font-size: 48px;
    font-weight: 700;
    color: #ffffff;
    letter-spacing: -0.02em;
    margin: 0 0 16px;
}

.strip-desc {
    font-size: 19px;
    color: #a1a1a6;
    line-height: 1.4;
    margin: 0 0 32px;
}

.strip-cta {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 14px 32px;
    background: #ffffff;
    color: #1d1d1f;
    border: none;
    border-radius: 980px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
}

.strip-cta:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 24px rgba(255, 255, 255, 0.2);
}

/* ============ 旅行故事 ============ */
.stories-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.story-card {
    background: #ffffff;
    border: 1px solid #d2d2d6;
    border-radius: 24px;
    overflow: hidden;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    cursor: pointer;
}

.story-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

.story-image-wrapper {
    position: relative;
    width: 100%;
    height: 200px;
    overflow: hidden;
}

.story-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.story-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, transparent 50%, rgba(0, 0, 0, 0.4) 100%);
}

.story-category {
    position: absolute;
    top: 16px;
    left: 16px;
    padding: 6px 14px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border-radius: 980px;
    font-size: 12px;
    font-weight: 600;
    color: #1d1d1f;
}

.story-content {
    padding: 24px;
}

.story-meta {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: #6e6e73;
    margin-bottom: 10px;
}

.story-title {
    font-size: 20px;
    font-weight: 600;
    color: #1d1d1f;
    line-height: 1.3;
    margin: 0 0 10px;
}

.story-excerpt {
    font-size: 14px;
    line-height: 1.5;
    color: #6e6e73;
    margin: 0 0 16px;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.story-link {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 15px;
    font-weight: 500;
    color: #2997ff;
    cursor: pointer;
    text-decoration: none;
}

.story-link:hover {
    color: #0066cc;
}

/* ============ 技术架构 ============ */
.tech-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.tech-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 32px 28px;
}

.tech-card-header {
    margin-bottom: 24px;
}

.tech-badge {
    display: inline-block;
    padding: 6px 18px;
    border-radius: 980px;
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
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 14px 0;
    border-bottom: 1px solid #e5e5e7;
}

.tech-list li:last-child {
    border-bottom: none;
}

.tech-item-name {
    font-size: 15px;
    font-weight: 500;
    color: #1d1d1f;
}

.tech-item-desc {
    font-size: 13px;
    color: #6e6e73;
}

/* ============ 发展历程 ============ */
.timeline {
    position: relative;
    max-width: 900px;
    margin: 0 auto;
    padding: 20px 0;
}

.timeline::before {
    content: '';
    position: absolute;
    left: 50%;
    top: 0;
    bottom: 0;
    width: 2px;
    background: #d2d2d6;
    transform: translateX(-50%);
}

.timeline-item {
    position: relative;
    width: 50%;
    padding: 0 40px 40px 0;
}

.timeline-item--right {
    margin-left: 50%;
    padding: 0 0 40px 40px;
}

.timeline-item::after {
    content: '';
    position: absolute;
    right: -7px;
    top: 8px;
    width: 14px;
    height: 14px;
    background: #2997ff;
    border: 3px solid #ffffff;
    border-radius: 50%;
    box-shadow: 0 0 0 2px #d2d2d6;
}

.timeline-item--right::after {
    right: auto;
    left: -7px;
}

.timeline-content {
    background: #f5f5f7;
    border-radius: 20px;
    padding: 24px 28px;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.timeline-content:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

.timeline-date {
    display: inline-block;
    font-size: 13px;
    font-weight: 600;
    color: #2997ff;
    margin-bottom: 8px;
}

.timeline-title {
    font-size: 19px;
    font-weight: 600;
    color: #1d1d1f;
    margin: 0 0 8px;
}

.timeline-desc {
    font-size: 14px;
    line-height: 1.5;
    color: #6e6e73;
    margin: 0;
}

/* ============ 数据统计 ============ */
.stats-section {
    background: #f5f5f7;
    padding: 60px 22px;
    margin-bottom: 80px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
}

.stat-card {
    text-align: center;
    padding: 24px;
}

.stat-card-number {
    display: block;
    font-size: 48px;
    font-weight: 700;
    color: #1d1d1f;
    letter-spacing: -0.02em;
    margin-bottom: 8px;
}

.stat-card-label {
    font-size: 15px;
    color: #6e6e73;
}

/* ============ 联系我们 ============ */
.contact-card {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 60px 40px;
    margin-bottom: 80px;
}

.contact-content {
    max-width: 720px;
    margin: 0 auto;
    text-align: center;
}

.contact-title {
    font-size: 36px;
    font-weight: 700;
    color: #1d1d1f;
    letter-spacing: -0.02em;
    margin: 0 0 16px;
}

.contact-desc {
    font-size: 17px;
    color: #6e6e73;
    line-height: 1.5;
    margin: 0 0 40px;
}

.contact-methods {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    text-align: left;
}

.contact-method {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 24px;
    background: #ffffff;
    border-radius: 20px;
    text-decoration: none;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
    cursor: pointer;
}

.contact-method:hover {
    transform: scale(1.02);
    box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

.contact-method-icon {
    width: 48px;
    height: 48px;
    border-radius: 14px;
    background: #f5f5f7;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.contact-method-icon svg {
    width: 24px;
    height: 24px;
    color: #2997ff;
}

.contact-method-info {
    display: flex;
    flex-direction: column;
}

.contact-method-label {
    font-size: 13px;
    color: #6e6e73;
    margin-bottom: 2px;
}

.contact-method-value {
    font-size: 15px;
    font-weight: 600;
    color: #1d1d1f;
}

.copyright {
    font-size: 12px;
    color: #6e6e73;
    margin: 0;
}

.social-links {
    display: flex;
    gap: 12px;
}

.social-link {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6e6e73;
    transition: all 0.2s ease;
    cursor: pointer;
}

.social-link:hover {
    color: #2997ff;
    transform: translateY(-2px);
}

.social-link svg {
    width: 18px;
    height: 18px;
}

/* ============ 响应式 ============ */
@media (max-width: 1199px) {
    .hero-title {
        font-size: 48px;
    }

    .section-title {
        font-size: 36px;
    }

    .strip-title {
        font-size: 40px;
    }

    .about-grid,
    .features-grid,
    .stories-grid,
    .tech-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 768px) {
    .hero-section {
        padding: 60px 20px 40px;
        margin-bottom: 60px;
    }

    .hero-title {
        font-size: 36px;
    }

    .hero-subtitle {
        font-size: 17px;
    }

    .hero-cta-group {
        flex-direction: column;
        width: 100%;
    }

    .cta-primary,
    .cta-secondary {
        width: 100%;
        justify-content: center;
    }

    .hero-stats {
        gap: 32px;
        flex-wrap: wrap;
    }

    .stat-number {
        font-size: 28px;
    }

    .intro-section {
        margin-bottom: 60px;
    }

    .section-title {
        font-size: 28px;
    }

    .about-grid,
    .features-grid,
    .stories-grid,
    .tech-grid {
        grid-template-columns: 1fr;
    }

    .feature-strip {
        padding: 60px 20px;
    }

    .strip-title {
        font-size: 32px;
    }

    .timeline::before {
        left: 0;
    }

    .timeline-item,
    .timeline-item--right {
        width: 100%;
        margin-left: 0;
        padding: 0 0 40px 32px;
    }

    .timeline-item::after,
    .timeline-item--right::after {
        left: -7px;
        right: auto;
    }

    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .stat-card-number {
        font-size: 36px;
    }

    .contact-card {
        padding: 40px 24px;
    }

    .contact-title {
        font-size: 28px;
    }

    .contact-methods {
        grid-template-columns: 1fr;
    }

    .footer-content {
        grid-template-columns: 1fr;
        gap: 32px;
    }

    .footer-links {
        grid-template-columns: repeat(2, 1fr);
        gap: 24px;
    }

    .footer-bottom {
        flex-direction: column;
        text-align: center;
    }
}

@media (max-width: 576px) {
    .hero-title {
        font-size: 32px;
    }

    .section-title {
        font-size: 24px;
    }

    .stats-grid {
        grid-template-columns: 1fr;
    }
}
</style>
