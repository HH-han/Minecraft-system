<template>
    <div class="website-introduction" ref="rootEl">
        <!-- 电影感氛围层：极光光斑 / 网格 / 胶片颗粒 / 暗角 -->
        <div class="cine-bg" aria-hidden="true">
            <span class="cine-aura cine-aura--azure"></span>
            <span class="cine-aura cine-aura--violet"></span>
            <span class="cine-aura cine-aura--ember"></span>
            <span class="cine-grid"></span>
            <span class="cine-grain"></span>
            <span class="cine-vignette"></span>
        </div>

        <!-- 主视觉 Hero -->
        <section class="hero-section" data-reveal>
            <div class="hero-inner">
                <div class="hero-copy">
                    <p class="hero-eyebrow">
                        <span class="eyebrow-dot"></span>
                        {{ $t('website.hero.eyebrow') }}
                    </p>
                    <h1 class="hero-title">
                        <span class="hero-title-line">{{ $t('website.hero.titleLine1') }}</span>
                        <span class="hero-title-line hero-title-line--accent">{{ $t('website.hero.titleLine2') }}</span>
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

                <!-- 纯 CSS 星环视觉（无图片依赖） -->
                <div class="hero-visual" aria-hidden="true">
                    <span class="orbit orbit--1"></span>
                    <span class="orbit orbit--2"></span>
                    <span class="orbit orbit--3"></span>
                    <span class="orb"></span>
                    <span v-for="star in stars" :key="star.id" class="star" :style="{
                        left: star.left,
                        top: star.top,
                        width: star.size,
                        height: star.size,
                        animationDelay: star.delay,
                        animationDuration: star.duration
                    }"></span>
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

            <span class="scroll-cue" aria-hidden="true"><i></i></span>
        </section>

        <!-- 01 精选推荐 -->
        <section class="stage" data-reveal>
            <div class="stage-head">
                <span class="stage-index">01</span>
                <span class="stage-rule"></span>
            </div>
            <div class="stage-body stage-body--recommend">
                <Recommend />
            </div>
        </section>

        <!-- 02 关于本站 -->
        <section id="about" class="intro-section about-section" data-reveal>
            <div class="section-header section-header--center">
                <span class="section-eyebrow">
                    <span class="section-num">02</span>
                    <span class="section-rule"></span>
                    {{ $t('website.about.eyebrow') }}
                </span>
                <h2 class="section-title">{{ $t('website.about.title') }}</h2>
            </div>
            <div class="about-grid">
                <div class="about-card" data-reveal :style="{ '--d': '0ms' }">
                    <span class="card-index">01</span>
                    <div class="about-card-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                            <circle cx="12" cy="10" r="3" />
                        </svg>
                    </div>
                    <h3 class="about-card-title">{{ $t('website.about.card1Title') }}</h3>
                    <p class="about-card-desc">{{ $t('website.about.card1Desc') }}</p>
                </div>
                <div class="about-card" data-reveal :style="{ '--d': '90ms' }">
                    <span class="card-index">02</span>
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
                <div class="about-card" data-reveal :style="{ '--d': '180ms' }">
                    <span class="card-index">03</span>
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

        <!-- 03 核心功能 -->
        <section id="features" class="intro-section features-section" data-reveal>
            <div class="section-header section-header--center">
                <span class="section-eyebrow">
                    <span class="section-num">03</span>
                    <span class="section-rule"></span>
                    {{ $t('website.features.eyebrow') }}
                </span>
                <h2 class="section-title">{{ $t('website.features.title') }}</h2>
                <p class="section-subtitle">{{ $t('website.features.subtitle') }}</p>
            </div>
            <div class="features-grid">
                <div class="feature-card" v-for="(feature, index) in features" :key="feature.id" data-reveal
                    :style="{ '--d': `${(index % 3) * 90}ms` }">
                    <div class="feature-icon-wrapper" :style="{ background: feature.bgColor }">
                        <img v-if="isImageUrl(feature.icon)" :src="feature.icon" class="feature-icon-img" alt="" />
                        <Icon v-else :name="feature.icon" size="32px" />
                    </div>
                    <h3 class="feature-title">{{ feature.title }}</h3>
                    <p class="feature-desc">{{ feature.desc }}</p>
                    <ul class="feature-list">
                        <li v-for="item in feature.items" :key="item">
                            <svg class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2.5">
                                <polyline points="20 6 9 17 4 12" />
                            </svg>
                            <span>{{ item }}</span>
                        </li>
                    </ul>
                </div>
            </div>
        </section>

        <!-- 全宽深色特色条 -->
        <section class="feature-strip" data-reveal>
            <span class="strip-aura" aria-hidden="true"></span>
            <span class="cine-grain cine-grain--strip" aria-hidden="true"></span>
            <div class="strip-content">
                <p class="strip-eyebrow">
                    <span class="eyebrow-dot"></span>
                    {{ $t('website.strip.eyebrow') }}
                </p>
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

        <!-- 04 旅行故事（标题由子组件提供，此处仅保留编号分隔） -->
        <section id="stories" class="intro-section stories-section">
            <div class="stories-stack">
                <div class="stage" data-reveal>
                    <div class="stage-head">
                        <span class="stage-index">04</span>
                        <span class="stage-rule"></span>
                    </div>
                    <div class="stage-body stage-body--listing">
                        <Recommendations
                            @open-recommendation-detail="(item) => $emit('open-recommendation-detail', item)" />
                    </div>
                </div>
                <div class="stage" data-reveal>
                    <div class="stage-head">
                        <span class="stage-index">05</span>
                        <span class="stage-rule"></span>
                    </div>
                    <div class="stage-body stage-body--listing">
                        <Content @open-detail="(type, id) => $emit('open-detail', type, id)" />
                    </div>
                </div>
            </div>
        </section>

        <!-- 06 技术架构 -->
        <section class="intro-section tech-section" data-reveal>
            <div class="section-header section-header--center">
                <span class="section-eyebrow">
                    <span class="section-num">06</span>
                    <span class="section-rule"></span>
                    {{ $t('website.tech.eyebrow') }}
                </span>
                <h2 class="section-title">{{ $t('website.tech.title') }}</h2>
                <p class="section-subtitle">{{ $t('website.tech.subtitle') }}</p>
            </div>
            <div class="tech-grid">
                <div class="tech-card" v-for="(tech, index) in techStack" :key="tech.category" data-reveal
                    :style="{ '--d': `${(index % 3) * 90}ms` }">
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

        <!-- 07 发展历程 -->
        <section class="intro-section timeline-section" data-reveal>
            <div class="section-header section-header--center">
                <span class="section-eyebrow">
                    <span class="section-num">07</span>
                    <span class="section-rule"></span>
                    {{ $t('website.timeline.eyebrow') }}
                </span>
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
        <section class="stats-section" data-reveal>
            <div class="stats-grid">
                <div class="stat-card" v-for="(stat, index) in platformStats" :key="stat.label" data-reveal
                    :style="{ '--d': `${(index % 4) * 80}ms` }">
                    <span class="stat-card-number">{{ stat.value }}</span>
                    <span class="stat-card-label">{{ stat.label }}</span>
                </div>
            </div>
        </section>

    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import Recommendations from './recommendations.vue';
import Content from './content.vue';
import Recommend from '@/views/Recommend/index.vue';
import Icon from './Icon.vue';
import websiteIntroductionApi from '@/api/websiteIntroduction';

// 声明向上转发的详情事件，避免监听器穿透到根 DOM 元素
defineEmits(['open-detail', 'open-recommendation-detail']);

const rootEl = ref(null);

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

// Hero 右侧星环的装饰星点（固定数据，避免随机导致抖动）
const stars = [
    { id: 1, left: '8%', top: '16%', size: '2px', delay: '0s', duration: '3.2s' },
    { id: 2, left: '22%', top: '6%', size: '1.5px', delay: '.6s', duration: '2.6s' },
    { id: 3, left: '76%', top: '12%', size: '2px', delay: '1.1s', duration: '3.8s' },
    { id: 4, left: '92%', top: '30%', size: '1.5px', delay: '.3s', duration: '2.9s' },
    { id: 5, left: '86%', top: '68%', size: '2px', delay: '1.4s', duration: '3.4s' },
    { id: 6, left: '66%', top: '88%', size: '1.5px', delay: '.9s', duration: '2.4s' },
    { id: 7, left: '30%', top: '92%', size: '2px', delay: '1.7s', duration: '3.6s' },
    { id: 8, left: '6%', top: '62%', size: '1.5px', delay: '.2s', duration: '2.8s' },
    { id: 9, left: '48%', top: '2%', size: '1.5px', delay: '1.9s', duration: '3.1s' },
    { id: 10, left: '52%', top: '96%', size: '1.5px', delay: '1.2s', duration: '2.7s' }
];

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

// 滚动入场观察器
let revealObserver = null;

const setupReveal = () => {
    const targets = rootEl.value?.querySelectorAll('[data-reveal]');
    if (!targets?.length) return;

    // 不支持 IntersectionObserver 时直接展示，避免内容永久隐藏
    if (!('IntersectionObserver' in window)) return;

    rootEl.value.classList.add('is-armed');

    revealObserver = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            entry.target.classList.add('is-revealed');
            revealObserver.unobserve(entry.target);
        });
    }, { threshold: 0.1, rootMargin: '0px 0px -6% 0px' });

    targets.forEach((el) => {
        // 首屏元素直接展示，其余等待滚动触发
        const rect = el.getBoundingClientRect();
        if (rect.top < window.innerHeight * 0.9) {
            el.classList.add('is-revealed');
        } else {
            revealObserver.observe(el);
        }
    });
};

onMounted(async () => {
    await loadData();
    setTimeout(animateNumbers, 300);
    setupReveal();
});

onBeforeUnmount(() => {
    revealObserver?.disconnect();
    revealObserver = null;
});
</script>

<style scoped>
/* ============================================================
   暗色电影感设计系统（局部作用域变量）
   ============================================================ */
.website-introduction {
    /* 私有设计变量统一别名到全局主题令牌 */
    --wi-surface: var(--theme-bg-surface);
    --wi-hairline: var(--theme-hairline);
    --wi-hairline-2: var(--theme-border-strong);
    --wi-txt-1: var(--theme-text-1);
    --wi-txt-2: var(--theme-text-2);
    --wi-txt-3: var(--theme-text-3);
    --wi-azure: var(--theme-primary);
    --wi-violet: var(--theme-accent-2);
    --wi-mint: var(--theme-accent-3);
    --wi-accent: var(--theme-gradient);
    --wi-radius-lg: 32px;
    --wi-ease: cubic-bezier(0.22, 1, 0.36, 1);
    --wi-shadow: var(--theme-shadow-lg);

    position: relative;
    isolation: isolate;
    overflow: hidden;
    margin: 0 0 72px;
    padding: 0 0 8px;
    border-radius: 44px;
    color: var(--wi-txt-1);
    font-family: 'SF Pro Display', 'SF Pro', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
    background:
        radial-gradient(1200px 620px at 10% -8%, rgba(77, 163, 255, 0.16), transparent 62%),
        radial-gradient(900px 540px at 90% 4%, rgba(139, 108, 255, 0.14), transparent 64%),
        var(--theme-bg-app);
}

/* ============ 氛围层 ============ */
.cine-bg {
    position: absolute;
    inset: 0;
    z-index: 0;
    pointer-events: none;
}

.cine-aura {
    position: absolute;
    border-radius: 50%;
    will-change: transform;
}

.cine-aura--azure {
    top: -190px;
    left: -140px;
    width: 660px;
    height: 660px;
    background: radial-gradient(circle, rgba(77, 163, 255, 0.3) 0%, transparent 68%);
    animation: wi-drift 19s ease-in-out infinite alternate;
}

.cine-aura--violet {
    top: 32%;
    right: -200px;
    width: 600px;
    height: 600px;
    background: radial-gradient(circle, rgba(139, 108, 255, 0.26) 0%, transparent 68%);
    animation: wi-drift 23s ease-in-out infinite alternate-reverse;
}

.cine-aura--ember {
    bottom: 4%;
    left: 16%;
    width: 560px;
    height: 560px;
    background: radial-gradient(circle, rgba(255, 143, 94, 0.16) 0%, transparent 68%);
    animation: wi-drift 27s ease-in-out infinite alternate;
}

/* 细网格，边缘由中心向外淡出 */
.cine-grid {
    position: absolute;
    inset: 0;
    background-image:
        linear-gradient(var(--theme-hairline) 1px, transparent 1px),
        linear-gradient(90deg, var(--theme-hairline) 1px, transparent 1px);
    background-size: 68px 68px;
    -webkit-mask-image: radial-gradient(115% 78% at 50% 32%, #000 12%, transparent 72%);
    mask-image: radial-gradient(115% 78% at 50% 32%, #000 12%, transparent 72%);
}

/* 胶片颗粒（灰度化后在暗底上呈细密噪点） */
.cine-grain {
    position: absolute;
    inset: 0;
    opacity: 0.05;
    filter: grayscale(1);
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='180' height='180'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='180' height='180' filter='url(%23n)'/%3E%3C/svg%3E");
}

.cine-grain--strip {
    border-radius: inherit;
    opacity: 0.06;
}

/* 暗角，让中心内容更聚焦 */
.cine-vignette {
    position: absolute;
    inset: 0;
    background: radial-gradient(125% 82% at 50% 38%, transparent 38%, var(--theme-overlay) 100%);
}

/* ============ Hero 主视觉 ============ */
.hero-section {
    position: relative;
    z-index: 1;
}

.hero-inner {
    max-width: 1244px;
    margin: 0 auto;
    padding: 132px 22px 24px;
    display: grid;
    grid-template-columns: minmax(0, 1.08fr) minmax(0, 0.92fr);
    gap: 56px;
    align-items: center;
}

.hero-copy {
    max-width: 640px;
}

.hero-eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    margin: 0 0 22px;
    font-size: 12.5px;
    font-weight: 600;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: var(--wi-azure);
}

.eyebrow-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: var(--wi-azure);
    box-shadow: 0 0 12px 3px var(--theme-primary-soft);
    animation: wi-pulse 2.6s ease-in-out infinite;
}

.hero-title {
    margin: 0 0 22px;
    font-size: clamp(38px, 5.2vw, 68px);
    font-weight: 700;
    line-height: 1.06;
    letter-spacing: -0.03em;
}

.hero-title-line {
    display: block;
    color: var(--wi-txt-1);
}

.hero-title-line--accent {
    background: var(--wi-accent);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.hero-subtitle {
    margin: 0 0 38px;
    max-width: 560px;
    font-size: 18px;
    line-height: 1.72;
    color: var(--wi-txt-2);
}

.hero-cta-group {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
}

.cta-primary,
.cta-secondary {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 9px;
    padding: 15px 30px;
    border-radius: 980px;
    font-size: 15.5px;
    font-weight: 600;
    letter-spacing: 0.01em;
    cursor: pointer;
    border: none;
    transition: transform 0.35s var(--wi-ease), box-shadow 0.35s var(--wi-ease),
        background-color 0.35s var(--wi-ease), border-color 0.35s var(--wi-ease);
}

.cta-primary {
    color: var(--theme-primary-text);
    background: var(--theme-gradient);
    box-shadow: var(--theme-shadow-md);
}

.cta-primary:hover {
    transform: translateY(-2px);
    box-shadow: var(--theme-shadow-lg);
    filter: brightness(1.07);
}

.cta-secondary {
    color: var(--wi-txt-1);
    background: var(--wi-surface);
    border: 1px solid var(--wi-hairline-2);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
}

.cta-secondary:hover {
    transform: translateY(-2px);
    background: var(--theme-bg-hover);
    border-color: var(--theme-border-strong);
}

.cta-arrow {
    width: 17px;
    height: 17px;
    transition: transform 0.35s var(--wi-ease);
}

.cta-primary:hover .cta-arrow,
.strip-cta:hover .cta-arrow {
    transform: translateX(4px);
}

/* Hero 右侧星环 */
.hero-visual {
    position: relative;
    width: 100%;
    max-width: 440px;
    aspect-ratio: 1 / 1;
    margin: 0 auto;
}

.hero-visual::after {
    content: '';
    position: absolute;
    left: 50%;
    bottom: -6%;
    width: 118%;
    height: 26%;
    transform: translateX(-50%);
    background: radial-gradient(50% 100% at 50% 0%, rgba(77, 163, 255, 0.3), transparent 72%);
    filter: blur(18px);
}

.orbit {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    border-radius: 50%;
    border: 1px solid var(--wi-hairline);
}

.orbit--1 {
    width: 100%;
    height: 100%;
    animation: wi-spin 46s linear infinite;
}

.orbit--2 {
    width: 74%;
    height: 74%;
    border-color: var(--theme-primary-soft);
    animation: wi-spin 32s linear infinite reverse;
}

.orbit--3 {
    width: 50%;
    height: 50%;
    border-style: dashed;
    border-color: var(--theme-border);
    animation: wi-spin 64s linear infinite;
}

.orbit--1::after,
.orbit--2::after {
    content: '';
    position: absolute;
    top: -3.5px;
    left: 50%;
    width: 7px;
    height: 7px;
    margin-left: -3.5px;
    border-radius: 50%;
    background: var(--wi-azure);
    box-shadow: 0 0 16px 4px var(--theme-primary-soft);
}

.orbit--2::after {
    background: var(--wi-violet);
    box-shadow: 0 0 16px 4px var(--theme-primary-soft);
}

.orb {
    position: absolute;
    inset: 27%;
    border-radius: 50%;
    background: radial-gradient(circle at 32% 28%, rgba(168, 210, 255, 0.92) 0%, rgba(77, 163, 255, 0.34) 42%, rgba(8, 11, 22, 0.94) 74%);
    box-shadow:
        inset 0 0 70px rgba(120, 180, 255, 0.34),
        0 34px 96px -22px rgba(77, 163, 255, 0.6);
}

.star {
    position: absolute;
    border-radius: 50%;
    background: var(--theme-primary);
    box-shadow: 0 0 7px 1.5px var(--theme-primary-soft);
    animation: wi-twinkle 3s ease-in-out infinite;
}

/* Hero 数据条 */
.hero-stats {
    position: relative;
    z-index: 1;
    width: calc(100% - 44px);
    max-width: 1200px;
    margin: 40px auto 0;
    padding: 34px 44px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    background: var(--wi-surface);
    border: 1px solid var(--wi-hairline);
    border-radius: 30px;
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
}

.stat-item {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.stat-item + .stat-item::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 1px;
    height: 46px;
    background: linear-gradient(180deg, transparent, var(--theme-border-strong), transparent);
}

.stat-number {
    font-size: clamp(30px, 3.4vw, 42px);
    font-weight: 700;
    letter-spacing: -0.03em;
    font-variant-numeric: tabular-nums;
    background: var(--wi-accent);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.stat-label {
    margin-top: 8px;
    font-size: 13.5px;
    letter-spacing: 0.06em;
    color: var(--wi-txt-3);
}

.scroll-cue {
    position: relative;
    z-index: 1;
    display: block;
    width: 1px;
    height: 54px;
    margin: 46px auto 0;
    background: linear-gradient(180deg, var(--theme-border-strong), transparent);
}

.scroll-cue i {
    position: absolute;
    top: 0;
    left: 50%;
    width: 5px;
    height: 5px;
    margin-left: -2.5px;
    border-radius: 50%;
    background: var(--wi-azure);
    box-shadow: 0 0 10px 2px var(--theme-primary-soft);
    animation: wi-cue 2.2s ease-in-out infinite;
}

/* ============ 通用区块 ============ */
.intro-section {
    position: relative;
    z-index: 1;
    padding: 0 22px;
    margin-bottom: 96px;
    scroll-margin-top: 96px;
}

.section-header {
    max-width: 1200px;
    margin: 0 auto 52px;
}

.section-header--center {
    text-align: center;
}

.section-eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: var(--wi-azure);
}

.section-num {
    font-variant-numeric: tabular-nums;
    color: var(--wi-txt-3);
    letter-spacing: 0.1em;
}

.section-rule {
    width: 34px;
    height: 1px;
    background: linear-gradient(90deg, var(--wi-hairline-2), transparent);
}

.section-header--center .section-rule {
    display: none;
}

.section-title {
    margin: 0 0 14px;
    font-size: clamp(27px, 3.4vw, 45px);
    font-weight: 700;
    line-height: 1.12;
    letter-spacing: -0.03em;
    color: var(--wi-txt-1);
}

.section-subtitle {
    max-width: 640px;
    margin: 0 auto;
    font-size: 16px;
    line-height: 1.7;
    color: var(--wi-txt-2);
}

/* ============ 分段容器（承载子组件） ============ */
.stage {
    position: relative;
    z-index: 1;
    max-width: 1244px;
    margin: 0 auto 96px;
    padding: 0 22px;
}

.stage-head {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 26px;
}

.stage-index {
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.22em;
    font-variant-numeric: tabular-nums;
    color: var(--wi-txt-3);
}

.stage-rule {
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, var(--wi-hairline-2), transparent 82%);
}

.stories-stack {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 88px;
}

.stories-stack .stage {
    max-width: none;
    margin: 0;
    padding: 0;
}

/* 卡片通用的玻璃底与顶部高光 */
.about-card::before,
.feature-card::before,
.tech-card::before,
.timeline-content::before,
.stat-card::before {
    content: '';
    position: absolute;
    inset: 0 0 auto 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--theme-border-strong), transparent);
    opacity: 0.7;
}

/* ============ 关于本站 ============ */
.about-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.about-card {
    position: relative;
    overflow: hidden;
    padding: 40px 32px;
    border-radius: var(--wi-radius-lg);
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    transition: transform 0.5s var(--wi-ease), border-color 0.5s var(--wi-ease), box-shadow 0.5s var(--wi-ease);
}

.about-card:hover {
    transform: translateY(-6px);
    border-color: var(--theme-primary);
    box-shadow: var(--wi-shadow), 0 0 0 1px var(--theme-primary-soft);
}

.card-index {
    position: absolute;
    top: 30px;
    right: 32px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.2em;
    font-variant-numeric: tabular-nums;
    color: var(--theme-text-3);
}

.about-card-icon {
    width: 60px;
    height: 60px;
    margin-bottom: 26px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 20px;
    background: radial-gradient(circle at 30% 24%, var(--theme-primary-soft), var(--theme-bg-surface) 72%);
    border: 1px solid var(--theme-border);
    box-shadow: var(--theme-shadow-md);
}

.about-card-icon svg {
    width: 27px;
    height: 27px;
    color: var(--theme-primary);
}

.about-card-title {
    margin: 0 0 12px;
    font-size: 21px;
    font-weight: 600;
    letter-spacing: -0.01em;
    color: var(--wi-txt-1);
}

.about-card-desc {
    margin: 0;
    font-size: 14.5px;
    line-height: 1.72;
    color: var(--wi-txt-2);
}

/* ============ 核心功能 ============ */
.features-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.feature-card {
    position: relative;
    padding: 36px 28px;
    border-radius: 28px;
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    transition: transform 0.5s var(--wi-ease), border-color 0.5s var(--wi-ease), box-shadow 0.5s var(--wi-ease);
}

.feature-card:hover {
    transform: translateY(-6px);
    border-color: var(--theme-primary);
    box-shadow: var(--wi-shadow), 0 0 0 1px var(--theme-primary-soft);
}

.feature-icon-wrapper {
    width: 64px;
    height: 64px;
    margin-bottom: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 18px;
    border: 1px solid var(--theme-border);
    box-shadow: var(--theme-shadow-md);
}

.feature-icon-img {
    width: 32px;
    height: 32px;
    object-fit: contain;
}

.feature-title {
    margin: 0 0 10px;
    font-size: 20px;
    font-weight: 600;
    color: var(--wi-txt-1);
}

.feature-desc {
    margin: 0 0 20px;
    font-size: 14.5px;
    line-height: 1.72;
    color: var(--wi-txt-2);
}

.feature-list {
    margin: 0;
    padding: 0;
    list-style: none;
}

.feature-list li {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 7px 0;
    font-size: 14px;
    color: var(--theme-text-2);
}

.check-icon {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
    color: var(--wi-mint);
    filter: drop-shadow(0 0 6px rgba(55, 224, 160, 0.5));
}

/* ============ 全宽电影条 ============ */
.feature-strip {
    position: relative;
    z-index: 1;
    overflow: hidden;
    margin: 0 22px 96px;
    padding: 118px 22px;
    text-align: center;
    border-radius: 40px;
    border: 1px solid var(--wi-hairline);
    background:
        radial-gradient(ellipse 46% 92% at 14% 12%, rgba(77, 163, 255, 0.26), transparent 62%),
        radial-gradient(ellipse 44% 86% at 88% 88%, rgba(139, 108, 255, 0.22), transparent 62%),
        var(--theme-bg-app);
}

.strip-aura {
    position: absolute;
    top: -30%;
    left: 50%;
    width: 120%;
    height: 80%;
    transform: translateX(-50%);
    background: radial-gradient(ellipse at center, rgba(255, 143, 94, 0.16), transparent 68%);
    pointer-events: none;
}

.strip-content {
    position: relative;
    z-index: 1;
    max-width: 840px;
    margin: 0 auto;
}

.strip-eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    margin: 0 0 20px;
    font-size: 12px;
    font-weight: 600;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: var(--wi-azure);
}

.strip-title {
    margin: 0 0 18px;
    font-size: clamp(30px, 4.2vw, 54px);
    font-weight: 700;
    line-height: 1.1;
    letter-spacing: -0.03em;
    color: var(--wi-txt-1);
}

.strip-desc {
    margin: 0 0 36px;
    font-size: 17px;
    line-height: 1.72;
    color: var(--wi-txt-2);
}

.strip-cta {
    display: inline-flex;
    align-items: center;
    gap: 9px;
    padding: 15px 34px;
    border: none;
    border-radius: 980px;
    background: var(--theme-gradient);
    color: var(--theme-primary-text);
    font-size: 15.5px;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.35s var(--wi-ease), box-shadow 0.35s var(--wi-ease);
}

.strip-cta:hover {
    transform: translateY(-2px);
    box-shadow: var(--theme-shadow-lg);
}

/* ============ 技术架构 ============ */
.tech-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
}

.tech-card {
    position: relative;
    padding: 32px 28px;
    border-radius: 28px;
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    transition: transform 0.5s var(--wi-ease), border-color 0.5s var(--wi-ease), box-shadow 0.5s var(--wi-ease);
}

.tech-card:hover {
    transform: translateY(-6px);
    border-color: var(--theme-primary);
    box-shadow: var(--wi-shadow), 0 0 0 1px var(--theme-primary-soft);
}

.tech-card-header {
    margin-bottom: 22px;
}

.tech-badge {
    display: inline-block;
    padding: 7px 18px;
    border-radius: 980px;
    font-size: 12.5px;
    font-weight: 600;
    letter-spacing: 0.06em;
}

.tech-badge.backend {
    background: var(--theme-info-soft);
    border: 1px solid var(--theme-border);
    color: var(--theme-info);
}

.tech-badge.frontend {
    background: var(--theme-success-soft);
    border: 1px solid var(--theme-border);
    color: var(--theme-success);
}

.tech-badge.database {
    background: var(--theme-warning-soft);
    border: 1px solid var(--theme-border);
    color: var(--theme-warning);
}

.tech-list {
    margin: 0;
    padding: 0;
    list-style: none;
}

.tech-list li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 14px;
    padding: 14px 0;
    border-bottom: 1px solid var(--theme-hairline);
}

.tech-list li:last-child {
    border-bottom: none;
}

.tech-item-name {
    font-size: 14.5px;
    font-weight: 500;
    color: var(--wi-txt-1);
}

.tech-item-desc {
    font-size: 12.5px;
    text-align: right;
    color: var(--wi-txt-3);
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
    width: 1px;
    transform: translateX(-50%);
    background: linear-gradient(180deg,
            transparent 0%,
            rgba(77, 163, 255, 0.55) 14%,
            rgba(139, 108, 255, 0.45) 62%,
            transparent 100%);
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
    right: -6px;
    top: 46px;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: var(--wi-azure);
    border: 3px solid var(--theme-bg-app);
    box-shadow: 0 0 0 4px var(--theme-primary-soft), 0 0 24px 5px var(--theme-primary-soft);
    animation: wi-pulse 3.4s ease-in-out infinite;
}

.timeline-item--right::after {
    right: auto;
    left: -6px;
}

.timeline-content {
    position: relative;
    overflow: hidden;
    padding: 26px 28px;
    border-radius: 26px;
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    transition: transform 0.5s var(--wi-ease), border-color 0.5s var(--wi-ease), box-shadow 0.5s var(--wi-ease);
}

.timeline-content:hover {
    transform: translateY(-5px);
    border-color: var(--theme-primary);
    box-shadow: var(--wi-shadow);
}

.timeline-date {
    display: inline-block;
    margin-bottom: 12px;
    padding: 5px 14px;
    border-radius: 980px;
    background: var(--theme-primary-soft);
    border: 1px solid var(--theme-primary);
    font-size: 12.5px;
    font-weight: 600;
    letter-spacing: 0.05em;
    color: var(--theme-primary);
}

.timeline-title {
    margin: 0 0 8px;
    font-size: 18.5px;
    font-weight: 600;
    color: var(--wi-txt-1);
}

.timeline-desc {
    margin: 0;
    font-size: 14px;
    line-height: 1.7;
    color: var(--wi-txt-2);
}

/* ============ 数据统计 ============ */
.stats-section {
    position: relative;
    z-index: 1;
    padding: 96px 22px 24px;
    border-top: 1px solid var(--theme-hairline);
}

.stats-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
}

.stat-card {
    position: relative;
    overflow: hidden;
    padding: 32px 24px;
    text-align: center;
    border-radius: 28px;
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    transition: transform 0.5s var(--wi-ease), border-color 0.5s var(--wi-ease), box-shadow 0.5s var(--wi-ease);
}

.stat-card:hover {
    transform: translateY(-5px);
    border-color: var(--theme-primary);
    box-shadow: var(--wi-shadow);
}

.stat-card-number {
    display: block;
    margin-bottom: 10px;
    font-size: clamp(32px, 3.6vw, 46px);
    font-weight: 700;
    letter-spacing: -0.03em;
    font-variant-numeric: tabular-nums;
    background: var(--wi-accent);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.stat-card-label {
    font-size: 13.5px;
    letter-spacing: 0.06em;
    color: var(--wi-txt-3);
}

/* ============================================================
   子组件深色主题适配（包裹层）
   通过 .website-introduction 前缀提升权重，稳定覆盖子组件浅色样式
   ============================================================ */

/* —— 01 精选推荐（Recommend） —— */
.website-introduction :deep(.stage-body--recommend .recommend-container) {
    padding: 0;
}

.website-introduction :deep(.stage-body--recommend .recommend-title) {
    justify-content: flex-start;
    margin-bottom: 26px;
}

.website-introduction :deep(.stage-body--recommend .recommend-title h1) {
    margin: 0;
    font-size: clamp(26px, 3.2vw, 40px);
    letter-spacing: -0.03em;
    color: var(--wi-txt-1);
}

.website-introduction :deep(.stage-body--recommend .recommend-grid) {
    grid-template-columns: repeat(auto-fill, minmax(min(100%, 340px), 1fr));
    gap: 22px;
}

.website-introduction :deep(.stage-body--recommend .card) {
    height: 312px;
    border: 1px solid var(--wi-hairline-2);
    background: var(--theme-bg-surface);
    box-shadow: var(--theme-shadow-lg);
}

/* .front / .back 为绝对定位，需以 .content 为参照系；
   否则会相对页面级祖先定位，导致卡片内的标题与简介被 overflow 裁掉 */
.website-introduction :deep(.stage-body--recommend .content) {
    position: relative;
}

.website-introduction :deep(.stage-body--recommend .card:hover) {
    border-color: var(--theme-primary);
    box-shadow: var(--theme-shadow-lg);
}

.website-introduction :deep(.stage-body--recommend .front-content .title) {
    font-size: 13px;
}

.website-introduction :deep(.stage-body--recommend .front-content .badge) {
    font-size: 12px;
    letter-spacing: 0.04em;
}

.website-introduction :deep(.stage-body--recommend .card-footer) {
    font-size: 11.5px;
    line-height: 1.55;
}

/* —— 04 内容区（Recommendations / Content） —— */
.website-introduction :deep(.stage-body--listing .section-header) {
    margin-bottom: 28px;
}

.website-introduction :deep(.stage-body--listing .section-title) {
    font-size: clamp(24px, 3vw, 34px);
    letter-spacing: -0.03em;
    color: var(--wi-txt-1);
}

.website-introduction :deep(.stage-body--listing .section-subtitle) {
    font-size: 15px;
    color: var(--wi-txt-2);
}

.website-introduction :deep(.stage-body--listing .content-section) {
    margin-bottom: 0;
}

/* 筛选胶囊与切换标签 */
.website-introduction :deep(.stage-body--listing .category-filter),
.website-introduction :deep(.stage-body--listing .tab-container) {
    min-height: auto;
    padding: 18px 20px;
    margin-bottom: 34px;
    border-radius: 26px;
    border: 1px solid var(--wi-hairline);
    background: var(--wi-surface);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
}

.website-introduction :deep(.stage-body--listing .filter-label) {
    color: var(--wi-txt-3);
}

.website-introduction :deep(.stage-body--listing .category-btn),
.website-introduction :deep(.stage-body--listing .tab-btn) {
    padding: 11px 22px;
    font-size: 13.5px;
    color: var(--wi-txt-2);
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    border-radius: 980px;
    box-shadow: none;
}

.website-introduction :deep(.stage-body--listing .tab-btn) {
    padding: 14px 30px;
    font-size: 14.5px;
}

.website-introduction :deep(.stage-body--listing .category-btn:hover),
.website-introduction :deep(.stage-body--listing .tab-btn:hover) {
    color: var(--wi-txt-1);
    background: var(--theme-primary-soft);
    transform: translateY(-1px);
}

.website-introduction :deep(.stage-body--listing .category-btn.active),
.website-introduction :deep(.stage-body--listing .tab-btn.active) {
    color: var(--theme-primary-text);
    border-color: transparent;
    background: var(--theme-gradient);
    box-shadow: var(--theme-shadow-md);
}

/* 内容卡片改为玻璃底 */
.website-introduction :deep(.stage-body--listing .card) {
    border-radius: 28px;
    border: 1px solid var(--wi-hairline);
    background: var(--theme-bg-surface);
    box-shadow: var(--theme-shadow-lg);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
}

.website-introduction :deep(.stage-body--listing .card:hover) {
    border-color: var(--theme-primary);
    box-shadow: var(--theme-shadow-lg), 0 0 0 1px var(--theme-primary-soft);
}

.website-introduction :deep(.stage-body--listing .card-title) {
    color: var(--wi-txt-1);
}

.website-introduction :deep(.stage-body--listing .card-description) {
    color: var(--wi-txt-2);
}

.website-introduction :deep(.stage-body--listing .card-location) {
    color: var(--wi-txt-3);
}

.website-introduction :deep(.stage-body--listing .card-price) {
    background: var(--wi-accent);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
}

.website-introduction :deep(.stage-body--listing .primary-cta),
.website-introduction :deep(.stage-body--listing .retry-btn) {
    border-radius: 16px;
    background: var(--theme-gradient);
    box-shadow: var(--theme-shadow-md);
}

.website-introduction :deep(.stage-body--listing .secondary-cta) {
    background: var(--theme-bg-surface);
    border: 1px solid var(--wi-hairline);
    color: var(--wi-txt-2);
}

.website-introduction :deep(.stage-body--listing .secondary-cta:hover) {
    background: var(--theme-danger-soft);
    color: var(--theme-danger);
}

.website-introduction :deep(.stage-body--listing .expand-btn) {
    color: var(--wi-txt-1);
    background: var(--theme-bg-surface);
    border-color: var(--wi-hairline-2);
    box-shadow: none;
}

.website-introduction :deep(.stage-body--listing .expand-btn:hover) {
    color: var(--wi-txt-1);
    background: var(--theme-primary-soft);
    border-color: var(--theme-primary);
    box-shadow: var(--theme-shadow-md);
}

/* 空态 / 加载 / 错误 */
.website-introduction :deep(.stage-body--listing .empty-state),
.website-introduction :deep(.stage-body--listing .loading-state) {
    background: var(--wi-surface);
    border: 1px dashed var(--wi-hairline-2);
    border-radius: 28px;
}

.website-introduction :deep(.stage-body--listing .empty-icon) {
    color: var(--theme-text-3);
}

.website-introduction :deep(.stage-body--listing .empty-text),
.website-introduction :deep(.stage-body--listing .loading-text) {
    color: var(--wi-txt-2);
}

.website-introduction :deep(.stage-body--listing .loading-spinner) {
    border-color: var(--theme-border);
    border-top-color: var(--wi-azure);
}

.website-introduction :deep(.stage-body--listing .error-state) {
    background: var(--theme-danger-soft);
    border: 1px solid var(--theme-danger);
    border-radius: 28px;
}

.website-introduction :deep(.stage-body--listing .error-text) {
    color: var(--theme-danger);
}

/* ============================================================
   滚动入场
   ============================================================ */
.website-introduction.is-armed [data-reveal] {
    opacity: 0;
    transform: translateY(26px);
    transition-property: opacity, transform;
    transition-duration: 0.9s;
    transition-timing-function: var(--wi-ease);
    transition-delay: var(--d, 0ms);
}

.website-introduction.is-armed [data-reveal].is-revealed {
    opacity: 1;
    transform: translateY(0);
}

/* ============================================================
   关键帧
   ============================================================ */
@keyframes wi-drift {
    0% {
        transform: translate3d(0, 0, 0) scale(1);
    }

    100% {
        transform: translate3d(28px, -22px, 0) scale(1.08);
    }
}

@keyframes wi-spin {
    to {
        transform: translate(-50%, -50%) rotate(360deg);
    }
}

@keyframes wi-twinkle {

    0%,
    100% {
        opacity: 0.22;
        transform: scale(0.75);
    }

    50% {
        opacity: 1;
        transform: scale(1.15);
    }
}

@keyframes wi-pulse {

    0%,
    100% {
        opacity: 0.65;
    }

    50% {
        opacity: 1;
    }
}

@keyframes wi-cue {
    0% {
        transform: translateY(0);
        opacity: 0;
    }

    35% {
        opacity: 1;
    }

    100% {
        transform: translateY(46px);
        opacity: 0;
    }
}

/* ============================================================
   响应式
   ============================================================ */
@media (max-width: 1199px) {
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
    .hero-inner {
        grid-template-columns: 1fr;
        gap: 12px;
        padding: 104px 22px 12px;
    }

    .hero-copy {
        max-width: none;
    }

    .hero-visual {
        display: none;
    }

    .hero-stats {
        padding: 28px 24px;
    }
}

@media (max-width: 768px) {
    .website-introduction {
        border-radius: 32px;
    }

    .hero-inner {
        padding: 76px 20px 8px;
    }

    .hero-subtitle {
        font-size: 16px;
    }

    .hero-cta-group {
        width: 100%;
    }

    .cta-primary,
    .cta-secondary {
        flex: 1 1 100%;
    }

    .hero-stats {
        width: calc(100% - 40px);
        grid-template-columns: 1fr;
        gap: 22px;
        padding: 26px 20px;
    }

    .stat-item + .stat-item::before {
        left: 50%;
        top: -11px;
        width: 56px;
        height: 1px;
        transform: translateX(-50%);
        background: linear-gradient(90deg, transparent, var(--theme-border-strong), transparent);
    }

    .intro-section {
        padding: 0 20px;
        margin-bottom: 72px;
    }

    .stage {
        padding: 0 20px;
        margin-bottom: 72px;
    }

    .stories-stack {
        gap: 64px;
    }

    .about-grid,
    .features-grid,
    .tech-grid {
        grid-template-columns: 1fr;
    }

    .about-card {
        padding: 32px 26px;
    }

    .feature-strip {
        margin: 0 20px 72px;
        padding: 76px 20px;
        border-radius: 32px;
    }

    .timeline::before {
        left: 0;
    }

    .timeline-item,
    .timeline-item--right {
        width: 100%;
        margin-left: 0;
        padding: 0 0 36px 30px;
    }

    .timeline-item::after,
    .timeline-item--right::after {
        left: -6px;
        right: auto;
    }

    .stats-section {
        padding: 72px 20px 16px;
    }

    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .scroll-cue {
        margin-top: 34px;
    }
}

@media (max-width: 576px) {
    .stats-grid {
        grid-template-columns: 1fr;
    }

    .tech-list li {
        flex-direction: column;
        align-items: flex-start;
        gap: 4px;
    }

    .tech-item-desc {
        text-align: left;
    }
}

/* ============================================================
   无障碍：尊重系统动效偏好
   ============================================================ */
@media (prefers-reduced-motion: reduce) {

    .cine-aura,
    .orbit,
    .star,
    .eyebrow-dot,
    .scroll-cue i,
    .timeline-item::after {
        animation: none !important;
    }

    .website-introduction.is-armed [data-reveal] {
        opacity: 1 !important;
        transform: none !important;
        transition: none !important;
    }

    .about-card,
    .feature-card,
    .tech-card,
    .timeline-content,
    .stat-card,
    .cta-primary,
    .cta-secondary,
    .strip-cta {
        transition: none !important;
    }
}
</style>
