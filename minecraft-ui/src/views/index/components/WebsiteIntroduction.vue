<template>
    <div class="website-introduction">
        <!-- 主视觉 Hero -->
        <section class="hero-section">
            <div class="hero-content">
                <p class="hero-eyebrow">{{ $t('website.hero.eyebrow') }}</p>
                <h1 class="hero-title">
                    <span class="hero-title-line">{{ $t('website.hero.titleLine1') }}</span>
                    <span class="hero-title-line">{{ $t('website.hero.titleLine2') }}</span>
                </h1>
                <p class="hero-subtitle">{{ $t('website.hero.subtitle') }}</p>
                <div class="hero-cta-group">
                    <button class="cta-primary" @click="scrollToSection('features')">
                        {{ $t('website.hero.ctaPrimary') }}
                        <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M5 12h14M12 5l7 7-7 7" />
                        </svg>
                    </button>
                    <button class="cta-secondary" @click="scrollToSection('about')">
                        {{ $t('website.hero.ctaSecondary') }}
                    </button>
                </div>
            </div>
            <div class="hero-stats">
                <div class="stat-item">
                    <span class="stat-number">{{ animatedStats.destinations }}+</span>
                    <span class="stat-label">{{ $t('website.hero.statDestinations') }}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ animatedStats.experiences }}+</span>
                    <span class="stat-label">{{ $t('website.hero.statExperiences') }}</span>
                </div>
                <div class="stat-item">
                    <span class="stat-number">{{ $t('website.hero.statUsersValue', { value: animatedStats.users }) }}</span>
                    <span class="stat-label">{{ $t('website.hero.statUsers') }}</span>
                </div>
            </div>
        </section>

        <!-- 推荐卡片 -->
        <section class="product-card fade-in-up delay-100">
            <Recommend />
        </section>

        <!-- 关于本站 -->
        <section id="about" class="intro-section about-section">
            <div class="section-header section-header--center">
                <span class="section-eyebrow">{{ $t('website.about.eyebrow') }}</span>
                <h2 class="section-title">{{ $t('website.about.title') }}</h2>
            </div>
            <div class="about-grid">
                <div class="about-card">
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                            <circle cx="12" cy="10" r="3" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">{{ $t('website.about.card1Title') }}</h3>
                    <p class="about-card-desc">{{ $t('website.about.card1Desc') }}</p>
                </div>
                <div class="about-card">
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                            <line x1="16" y1="2" x2="16" y2="6" />
                            <line x1="8" y1="2" x2="8" y2="6" />
                            <line x1="3" y1="10" x2="21" y2="10" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">{{ $t('website.about.card2Title') }}</h3>
                    <p class="about-card-desc">{{ $t('website.about.card2Desc') }}</p>
                </div>
                <div class="about-card">
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                            <circle cx="9" cy="7" r="4" />
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                            <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">{{ $t('website.about.card3Title') }}</h3>
                    <p class="about-card-desc">{{ $t('website.about.card3Desc') }}</p>
                </div>
            </div>
        </section>

        <!-- 核心功能 -->
        <section id="features" class="intro-section features-section">
            <div class="section-header section-header--center">
                <span class="section-eyebrow">{{ $t('website.features.eyebrow') }}</span>
                <h2 class="section-title">{{ $t('website.features.title') }}</h2>
                <p class="section-subtitle">{{ $t('website.features.subtitle') }}</p>
            </div>
            <div class="features-grid">
                <div class="feature-card" v-for="feature in features" :key="feature.id">
                    <div class="feature-icon-wrapper" :style="{ background: feature.bgColor }">
                        <img v-if="isImageUrl(feature.icon)" :src="feature.icon" class="feature-icon-img" alt="" />
                        <Icon v-else :name="feature.icon" size="32px" />
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
                <p class="strip-eyebrow">{{ $t('website.strip.eyebrow') }}</p>
                <h2 class="strip-title">{{ $t('website.strip.title') }}</h2>
                <p class="strip-desc">{{ $t('website.strip.desc') }}</p>
                <button class="strip-cta" @click="scrollToSection('stories')">
                    {{ $t('website.strip.cta') }}
                    <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
                </button>
            </div>
        </section>

        <!-- 旅行故事 -->
        <section id="stories" class="intro-section stories-section">
            <div class="stories-flex">
                <section class="product-card fade-in-up delay-200">
                    <Recommendations
                        @open-recommendation-detail="(item) => $emit('open-recommendation-detail', item)" />
                </section>
                <section class="product-card fade-in-up delay-200">
                    <Content @open-detail="(type, id) => $emit('open-detail', type, id)" />
                </section>
            </div>
        </section>

        <!-- 技术架构 -->
        <section class="intro-section tech-section">
            <div class="section-header section-header--center">
                <span class="section-eyebrow">{{ $t('website.tech.eyebrow') }}</span>
                <h2 class="section-title">{{ $t('website.tech.title') }}</h2>
                <p class="section-subtitle">{{ $t('website.tech.subtitle') }}</p>
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
                <span class="section-eyebrow">{{ $t('website.timeline.eyebrow') }}</span>
                <h2 class="section-title">{{ $t('website.timeline.title') }}</h2>
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
import Icon from './Icon.vue';
import websiteIntroductionApi from '@/api/websiteIntroduction';

// 声明向上转发的详情事件，避免监听器穿透到根 DOM 元素
defineEmits(['open-detail', 'open-recommendation-detail']);

// Hero 区数据统计动画（装饰性数字，独立于平台统计数据）
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
const features = ref([]);

// 技术栈
const techStack = ref([]);

// 发展历程
const milestones = ref([]);

// 平台数据
const platformStats = ref([]);

// 判断 icon 是否为图片地址（后端存储 SVG 路径或上传图片 URL），否则按图标名渲染
const isImageUrl = (icon) => {
    if (!icon) return false;
    return icon.startsWith('/') || icon.startsWith('http') || icon.startsWith('data:');
};

// 加载网站介绍页数据（聚合接口，一次拉取功能、技术栈、历程、统计）
const loadData = async () => {
    try {
        const response = await websiteIntroductionApi.getIntroductionData();
        if (response.code === 200 && response.data) {
            const data = response.data;

            // 核心功能：FeatureVO → 前端展示结构
            features.value = (data.features || []).map(feature => ({
                id: feature.id,
                icon: feature.icon,
                title: feature.title,
                desc: feature.description,
                bgColor: feature.bgColor,
                items: (feature.items || []).map(item => item.itemName)
            }));

            // 技术栈：TechCategoryVO → 前端展示结构
            techStack.value = (data.techStack || []).map(category => ({
                category: category.categoryName,
                theme: category.themeClass,
                items: (category.items || []).map(item => ({
                    name: item.techName,
                    desc: item.description
                }))
            }));

            // 发展历程：MilestoneVO → 前端展示结构
            milestones.value = (data.milestones || []).map(milestone => ({
                date: milestone.dateFormatted,
                title: milestone.title,
                desc: milestone.description
            }));

            // 平台数据统计：PlatformStat → 前端展示结构
            platformStats.value = (data.stats || []).map(stat => ({
                label: stat.statLabel,
                value: stat.statValue
            }));
        }
    } catch (e) {
        // 静默失败（接口已设置 silent），保留空数组占位避免渲染异常
        console.warn('加载网站介绍页数据失败:', e);
    }
};

// 滚动到指定区块
const scrollToSection = (id) => {
    const element = document.getElementById(id);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
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

onMounted(async () => {
    await loadData();
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
    position: relative;
    overflow: hidden;
    padding: 100px 22px 70px;
    text-align: center;
    background:
        radial-gradient(ellipse 55% 55% at 50% -8%, rgba(41, 151, 255, 0.12), transparent 65%),
        linear-gradient(180deg, #f5f5f7 0%, #ffffff 100%);
    border-radius: 28px;
    margin-bottom: 24px;
}

/* 背景光斑装饰 */
.hero-section::before,
.hero-section::after {
    content: '';
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
}

.hero-section::before {
    top: -140px;
    left: -100px;
    width: 380px;
    height: 380px;
    background: radial-gradient(circle, rgba(41, 151, 255, 0.14) 0%, transparent 70%);
}

.hero-section::after {
    top: 18%;
    right: -140px;
    width: 320px;
    height: 320px;
    background: radial-gradient(circle, rgba(100, 210, 255, 0.12) 0%, transparent 70%);
}

.hero-content {
    position: relative;
    z-index: 1;
    max-width: 800px;
    margin: 0 auto;
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
    line-height: 1.08;
    letter-spacing: -0.02em;
    margin: 0 0 20px;
    background: linear-gradient(180deg, #1d1d1f 40%, #515154 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.hero-title-line {
    display: block;
}

.hero-subtitle {
    font-size: 21px;
    font-weight: 400;
    line-height: 1.5;
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
    justify-content: center;
    gap: 8px;
    padding: 14px 28px;
    border-radius: 980px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.25s ease;
    border: none;
}

.cta-primary {
    background: #2997ff;
    color: #ffffff;
    box-shadow: 0 8px 24px rgba(41, 151, 255, 0.3);
}

.cta-primary:hover {
    background: #0066cc;
    transform: translateY(-2px);
    box-shadow: 0 12px 28px rgba(41, 151, 255, 0.38);
}

.cta-secondary {
    background: transparent;
    color: #2997ff;
    border: 1px solid #2997ff;
}

.cta-secondary:hover {
    background: rgba(41, 151, 255, 0.08);
    transform: translateY(-2px);
}

.cta-arrow {
    width: 18px;
    height: 18px;
    transition: transform 0.25s ease;
}

.cta-primary:hover .cta-arrow {
    transform: translateX(4px);
}

.hero-stats {
    position: relative;
    z-index: 1;
    display: flex;
    justify-content: center;
    gap: 48px;
    margin-top: 60px;
    padding-top: 40px;
    border-top: 1px solid #d2d2d6;
}

.stat-item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    min-width: 140px;
}

/* 统计项之间的细分隔线 */
.stat-item + .stat-item::before {
    content: '';
    position: absolute;
    left: -24px;
    top: 50%;
    transform: translateY(-50%);
    width: 1px;
    height: 44px;
    background: #d2d2d6;
}

.stat-number {
    font-size: 40px;
    font-weight: 700;
    letter-spacing: -0.02em;
    background: linear-gradient(135deg, #1d1d1f 30%, #515154 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.stat-label {
    font-size: 14px;
    color: #6e6e73;
    margin-top: 6px;
}

/* ============ 通用区块 ============ */
.intro-section {
    padding: 0 22px;
    margin-bottom: 80px;
    /* 锚点跳转时避开固定导航 */
    scroll-margin-top: 96px;
}

.section-header {
    max-width: 1200px;
    margin: 0 auto 48px;
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

/* 面板卡片容器（推荐位 / 内容区共用） */
.product-card {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 40px 24px 30px;
    overflow: hidden;
    transition: transform 0.25s ease, box-shadow 0.25s ease;
}

/* 顶层推荐卡片与后续区块留出间距 */
.website-introduction > .product-card {
    margin-bottom: 80px;
}

/* ============ 关于本站 ============ */
.about-grid {
    max-width: 1200px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    margin: 0 auto;
}

.about-card {
    background: #f5f5f7;
    border-radius: 28px;
    padding: 40px 32px;
    transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.about-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.12);
}

.about-card-icon {
    width: 56px;
    height: 56px;
    border-radius: 16px;
    background: #ffffff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
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
    max-width: 1200px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    margin: 0 auto;
}

.feature-card {
    background: #ffffff;
    border: 1px solid #e5e5e7;
    border-radius: 24px;
    padding: 36px 28px;
    transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.feature-card:hover {
    transform: translateY(-4px);
    border-color: rgba(41, 151, 255, 0.4);
    box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.1);
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

.feature-icon-img {
    width: 32px;
    height: 32px;
    object-fit: contain;
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
    position: relative;
    overflow: hidden;
    padding: 100px 22px;
    margin-bottom: 80px;
    text-align: center;
    background:
        radial-gradient(ellipse 45% 90% at 18% 15%, rgba(41, 151, 255, 0.2), transparent 60%),
        radial-gradient(ellipse 40% 80% at 85% 85%, rgba(94, 92, 230, 0.18), transparent 60%),
        #000000;
}

.strip-content {
    position: relative;
    z-index: 1;
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
    transition: all 0.25s ease;
}

.strip-cta:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(255, 255, 255, 0.2);
}

/* ============ 旅行故事 ============ */
.stories-flex {
    display: flex;
    gap: 24px;
}

.stories-flex > * {
    flex: 1;
    min-width: 0;
}

/* ============ 技术架构 ============ */
.tech-grid {
    max-width: 1200px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    margin: 0 auto;
}

.tech-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 32px 28px;
    transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.tech-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.1);
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
    top: 48px;
    width: 14px;
    height: 14px;
    background: #2997ff;
    border: 3px solid #ffffff;
    border-radius: 50%;
    box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.15);
}

.timeline-item--right::after {
    right: auto;
    left: -7px;
}

.timeline-content {
    background: #f5f5f7;
    border-radius: 20px;
    padding: 24px 28px;
    transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.timeline-content:hover {
    transform: translateY(-4px);
    box-shadow: 0 20px 40px -12px rgba(0, 0, 0, 0.1);
}

.timeline-date {
    display: inline-block;
    padding: 4px 12px;
    background: rgba(41, 151, 255, 0.1);
    border-radius: 980px;
    font-size: 13px;
    font-weight: 600;
    color: #2997ff;
    margin-bottom: 10px;
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
    padding: 70px 22px;
    margin-bottom: 80px;
}

.stats-grid {
    max-width: 1200px;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
    margin: 0 auto;
}

.stat-card {
    text-align: center;
    padding: 24px;
}

.stat-card-number {
    display: block;
    font-size: 48px;
    font-weight: 700;
    letter-spacing: -0.02em;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #2997ff 0%, #64d2ff 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.stat-card-label {
    font-size: 15px;
    color: #6e6e73;
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
    .tech-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (max-width: 992px) {
    .stories-flex {
        flex-direction: column;
    }
}

@media (max-width: 768px) {
    .hero-section {
        padding: 60px 20px 40px;
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
    }

    .hero-stats {
        gap: 24px;
        flex-wrap: wrap;
    }

    .stat-item + .stat-item::before {
        display: none;
    }

    .stat-number {
        font-size: 28px;
    }

    .intro-section {
        margin-bottom: 60px;
    }

    .website-introduction > .product-card {
        margin-bottom: 60px;
    }

    .section-title {
        font-size: 28px;
    }

    .about-grid,
    .features-grid,
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
