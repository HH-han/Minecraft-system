<template>
    <!-- ===== 导航栏 ===== -->
    <nav class="navbar" :class="{ scrolled: isScrolled }" role="navigation">
        <a href="#" class="logo"><i class="fas fa-compass"></i> 博览旅行</a>
        <ul class="nav-links" :class="{ open: isMenuOpen }">
            <li><a href="#destinations" @click="closeMenu">目的地</a></li>
            <li><a href="#experiences" @click="closeMenu">体验</a></li>
            <li><a href="#stories" @click="closeMenu">故事</a></li>
            <li><a href="#narrative" @click="closeMenu">叙事</a></li>
            <li><a href="#cta" @click="closeMenu">启程</a></li>
        </ul>
        <div class="nav-actions">
            <button class="btn-nav">开始探索</button>
            <button class="hamburger" @click="toggleMenu" :aria-expanded="isMenuOpen" aria-label="菜单">
                <span></span><span></span><span></span>
            </button>
        </div>
    </nav>

    <!-- ===== 英雄区 ===== -->
    <section class="hero" id="hero">
        <div class="hero-bg" :style="heroBgStyle"></div>
        <div class="hero-overlay"></div>
        <div class="hero-content" :style="heroContentStyle">
            <div class="badge">2026 · 叙事之旅</div>
            <h1>故事始于<br /><span class="highlight">足尖之下</span></h1>
            <p>每一段旅程都是一部未完成的小说，我们在风景中寻找自己的章节。</p>
            <button class="btn-hero">开启叙事 <i class="fas fa-arrow-right"></i></button>
        </div>
        <div class="scroll-indicator">
            <span>滚动阅读</span>
            <div class="line"></div>
        </div>
    </section>

    <!-- ===== 目的地 ===== -->
    <section class="destinations" id="destinations">
        <div class="container">
            <div class="section-header reveal">
                <span class="section-label">— 目的地</span>
                <h2 class="section-title">灵感之地</h2>
                <p class="section-subtitle">每一处风景都在低语，等待你倾听它们的故事。</p>
            </div>
            <div class="destinations-grid">
                <div class="dest-card reveal reveal-delay-1">
                    <div class="card-image">
                        <img src="https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=600&q=80" alt="巴黎"
                            loading="lazy" />
                    </div>
                    <div class="card-content">
                        <div class="card-tag">欧洲 · 浪漫</div>
                        <h3>巴黎</h3>
                        <p>光之城，艺术与时尚的永恒之都。</p>
                        <div class="card-meta"><span>6 天 5 晚</span><span class="price">¥ 12,800 起</span></div>
                    </div>
                </div>
                <div class="dest-card reveal reveal-delay-2">
                    <div class="card-image">
                        <img src="https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=600&q=80" alt="东京"
                            loading="lazy" />
                    </div>
                    <div class="card-content">
                        <div class="card-tag">亚洲 · 都市</div>
                        <h3>东京</h3>
                        <p>传统与未来交织的霓虹之城。</p>
                        <div class="card-meta"><span>5 天 4 晚</span><span class="price">¥ 9,600 起</span></div>
                    </div>
                </div>
                <div class="dest-card reveal reveal-delay-3">
                    <div class="card-image">
                        <img src="https://images.unsplash.com/photo-1504893524553-b855bce32c67?w=600&q=80" alt="冰岛"
                            loading="lazy" />
                    </div>
                    <div class="card-content">
                        <div class="card-tag">北欧 · 极光</div>
                        <h3>冰岛</h3>
                        <p>冰与火的国度，极光下的梦幻之地。</p>
                        <div class="card-meta"><span>8 天 7 晚</span><span class="price">¥ 18,200 起</span></div>
                    </div>
                </div>
                <div class="dest-card reveal reveal-delay-4">
                    <div class="card-image">
                        <img src="https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=600&q=80" alt="纽约"
                            loading="lazy" />
                    </div>
                    <div class="card-content">
                        <div class="card-tag">美洲 · 活力</div>
                        <h3>纽约</h3>
                        <p>不夜城，梦想与机遇的交汇点。</p>
                        <div class="card-meta"><span>6 天 5 晚</span><span class="price">¥ 14,500 起</span></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="parallax-layer layer-1" :style="paraLayer1Style"
            style="top:10%; right:-2%; font-size:180px; opacity:0.035;">✦</div>
    </section>

    <!-- ===== 体验 ===== -->
    <section class="experiences" id="experiences">
        <div class="container">
            <div class="section-header center reveal">
                <span class="section-label">— 非凡体验</span>
                <h2 class="section-title">不止是旅行</h2>
                <p class="section-subtitle">每一个细节都精心打磨，让您的旅程成为一生的珍藏。</p>
            </div>
            <div class="exp-grid">
                <div class="exp-item reveal reveal-delay-1">
                    <div class="exp-icon"><i class="fas fa-user-tie"></i></div>
                    <h4>私人定制</h4>
                    <p>专属旅行顾问，根据您的喜好量身打造独一无二的行程。</p>
                </div>
                <div class="exp-item reveal reveal-delay-2">
                    <div class="exp-icon"><i class="fas fa-utensils"></i></div>
                    <h4>味蕾之旅</h4>
                    <p>甄选米其林餐厅与在地风味，用舌尖品味目的地。</p>
                </div>
                <div class="exp-item reveal reveal-delay-3">
                    <div class="exp-icon"><i class="fas fa-camera"></i></div>
                    <h4>影像记录</h4>
                    <p>随行摄影师捕捉每一个动人瞬间，让回忆永不褪色。</p>
                </div>
                <div class="exp-item reveal reveal-delay-4">
                    <div class="exp-icon"><i class="fas fa-spa"></i></div>
                    <h4>身心休憩</h4>
                    <p>精选奢华水疗与静修之所，在旅途中找回内心的平衡。</p>
                </div>
            </div>
        </div>
        <div class="parallax-layer layer-2" :style="paraLayer2Style"
            style="bottom:15%; left:-4%; font-size:160px; opacity:0.03;">∞</div>
    </section>

    <!-- ===== 新增：叙事章节 ===== -->
    <section class="narrative" id="narrative">
        <div class="container">
            <div class="narrative-text reveal">
                <span class="section-label">— 叙事 · 沉浸</span>
                <h2 class="section-title">旅行的意义，在于故事</h2>
                <p class="section-subtitle">我们相信，每一次出发都是为了带回一个值得讲述的故事。博览旅行致力于将风景、文化与情感编织成你独有的叙事。</p>
                <div class="narrative-detail">
                    “从冰岛的极光下许愿，到巴黎左岸的咖啡馆写诗，我们为你设计的不只是路线，而是一段可以反复回味的人生片段。”
                </div>
            </div>
            <div class="narrative-media reveal reveal-delay-2">
                <img src="https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800&q=80" alt="叙事影像"
                    loading="lazy" />
            </div>
        </div>
    </section>

    <!-- ===== 沉浸式引用 ===== -->
    <section class="immersion" id="immersion">
        <div class="container">
            <div class="immersion-quote reveal">
                真正的旅行，不是抵达一个地方，而是用新的视角重新认识世界。
                <span class="author">—— 博览旅行 · 旅行哲学</span>
            </div>
        </div>
    </section>

    <!-- ===== 故事 ===== -->
    <section class="stories" id="stories">
        <div class="container">
            <div class="section-header reveal">
                <span class="section-label">— 旅行者说</span>
                <h2 class="section-title">他们的故事</h2>
                <p class="section-subtitle">真实的声音，来自那些已经在路上的灵魂。</p>
            </div>
            <div class="stories-grid">
                <div class="story-card reveal reveal-delay-1">
                    <div class="stars"><i class="fas fa-star"></i><i class="fas fa-star"></i><i
                            class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i></div>
                    <blockquote>“冰岛的极光是我见过最震撼的景象。博览旅行的行程安排恰到好处，既深度又舒适，完全超出了我的期待。”</blockquote>
                    <div class="story-author">
                        <div class="avatar"><img
                                src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&q=80" alt="林溪"
                                loading="lazy" /></div>
                        <div class="author-info">
                            <div class="name">林溪</div>
                            <div class="role">自由撰稿人 · 冰岛之旅</div>
                        </div>
                    </div>
                </div>
                <div class="story-card reveal reveal-delay-2">
                    <div class="stars"><i class="fas fa-star"></i><i class="fas fa-star"></i><i
                            class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i></div>
                    <blockquote>“巴黎的浪漫在博览旅行的诠释下更加动人。从卢浮宫到左岸咖啡馆，每一刻都像电影画面。”</blockquote>
                    <div class="story-author">
                        <div class="avatar"><img
                                src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80" alt="陈远"
                                loading="lazy" /></div>
                        <div class="author-info">
                            <div class="name">陈远</div>
                            <div class="role">建筑师 · 巴黎之旅</div>
                        </div>
                    </div>
                </div>
                <div class="story-card reveal reveal-delay-3">
                    <div class="stars"><i class="fas fa-star"></i><i class="fas fa-star"></i><i
                            class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i></div>
                    <blockquote>“东京的繁华与静谧在博览旅行的行程中完美平衡。感谢团队的精心安排，让我重新发现了旅行的意义。”</blockquote>
                    <div class="story-author">
                        <div class="avatar"><img
                                src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80" alt="苏雅"
                                loading="lazy" /></div>
                        <div class="author-info">
                            <div class="name">苏雅</div>
                            <div class="role">品牌总监 · 东京之旅</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="parallax-layer layer-3" :style="paraLayer3Style" style="top:30%; left:6%; font-size:140px; opacity:0.035;">"</div>
    </section>

    <!-- ===== CTA ===== -->
    <section class="cta-section" id="cta">
        <div class="cta-bg-glow"></div>
        <div class="container">
            <div class="reveal">
                <span class="section-label" style="color: rgba(255,255,255,0.4);">— 启程</span>
                <h2>开始您的叙事</h2>
                <p>与博览旅行一起，探索世界的另一种可能。让我们为您打造一段独一无二的记忆。</p>
                <button class="btn-cta">联系旅行顾问 <i class="fas fa-arrow-right"></i></button>
            </div>
        </div>
    </section>

    <!-- ===== 页脚 ===== -->
    <footer class="footer">
        <div class="container">
            <div class="footer-grid">
                <div class="footer-brand">
                    <div class="logo"><i class="fas fa-compass"></i> 博览旅行</div>
                    <p>高端旅行定制服务，以匠心打造每一次出发与抵达。</p>
                </div>
                <div class="footer-col">
                    <h5>探索</h5>
                    <ul>
                        <li><a href="#">目的地</a></li>
                        <li><a href="#">体验</a></li>
                        <li><a href="#">行程</a></li>
                        <li><a href="#">灵感</a></li>
                    </ul>
                </div>
                <div class="footer-col">
                    <h5>关于</h5>
                    <ul>
                        <li><a href="#">品牌故事</a></li>
                        <li><a href="#">团队</a></li>
                        <li><a href="#">合作</a></li>
                        <li><a href="#">联系</a></li>
                    </ul>
                </div>
                <div class="footer-col">
                    <h5>支持</h5>
                    <ul>
                        <li><a href="#">常见问题</a></li>
                        <li><a href="#">条款</a></li>
                        <li><a href="#">隐私</a></li>
                        <li><a href="#">客服</a></li>
                    </ul>
                </div>
            </div>
            <div class="footer-bottom">
                <span>© 2026 博览旅行旅行. 保留所有权利。</span>
                <div class="socials">
                    <a href="#"><i class="fab fa-instagram"></i></a>
                    <a href="#"><i class="fab fa-weixin"></i></a>
                    <a href="#"><i class="fab fa-youtube"></i></a>
                    <a href="#"><i class="fab fa-twitter"></i></a>
                </div>
            </div>
        </div>
    </footer>
</template>
<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';

const isMenuOpen = ref(false);
const isScrolled = ref(false);
const scrollY = ref(0);

const heroBgStyle = computed(() => ({
    transform: `scale(1.05) translateY(${scrollY.value * 0.4}px)`
}));

const heroContentStyle = computed(() => ({
    transform: `translateY(${scrollY.value * 0.15}px)`
}));

const paraLayer1Style = computed(() => ({
    transform: `translateY(${scrollY.value * 0.08}px)`
}));

const paraLayer2Style = computed(() => ({
    transform: `translateY(${scrollY.value * 0.12}px)`
}));

const paraLayer3Style = computed(() => ({
    transform: `translateY(${scrollY.value * 0.06}px)`
}));

const toggleMenu = () => {
    isMenuOpen.value = !isMenuOpen.value;
};

const closeMenu = () => {
    isMenuOpen.value = false;
};

let ticking = false;

const updateScroll = () => {
    const currentScrollY = window.scrollY;
    if (!ticking) {
        window.requestAnimationFrame(() => {
            scrollY.value = currentScrollY;
            isScrolled.value = currentScrollY > 40;
            ticking = false;
        });
        ticking = true;
    }
};

const setupIntersectionObserver = () => {
    const observerOptions = {
        root: null,
        rootMargin: '0px 0px -60px 0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
};

const handleAnchorClick = (e) => {
    const target = e.target.closest('a[href^="#"]');
    if (!target) return;

    const targetId = target.getAttribute('href');
    if (targetId === '#') return;

    e.preventDefault();
    const targetEl = document.querySelector(targetId);
    if (targetEl) {
        const top = targetEl.getBoundingClientRect().top + window.scrollY - 80;
        window.scrollTo({ top, behavior: 'smooth' });
    }
};

onMounted(() => {
    window.addEventListener('scroll', updateScroll, { passive: true });
    document.addEventListener('click', handleAnchorClick);

    updateScroll();
    setupIntersectionObserver();
});

onUnmounted(() => {
    window.removeEventListener('scroll', updateScroll);
    document.removeEventListener('click', handleAnchorClick);
});
</script>
<style>
/* ========== 全局重置 ========== */
*,
*::before,
*::after {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

:root {
    --color-bg: #f5f5f7;
    --color-bg-dark: #1d1d1f;
    --color-text: #1d1d1f;
    --color-text-light: #86868b;
    --color-text-white: #f5f5f7;
    --color-accent: #0071e3;
    --color-accent-hover: #0077ed;
    --color-border: #d2d2d7;
    --font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
    --nav-height: 64px;
    --section-spacing: 140px;
    --max-width: 1200px;
}

html {
    scroll-behavior: smooth;
}

body {
    font-family: var(--font-sans);
    background: var(--color-bg);
    color: var(--color-text);
    -webkit-font-smoothing: antialiased;
    overflow-x: hidden;
    line-height: 1.6;
}

img {
    display: block;
    max-width: 100%;
    height: auto;
}

a {
    text-decoration: none;
    color: inherit;
}

.container {
    max-width: var(--max-width);
    margin: 0 auto;
    padding: 0 24px;
}

/* ========== 导航栏 ========== */
.navbar {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    height: var(--nav-height);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 32px;
    background: transparent;
    transition: background 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        backdrop-filter 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        box-shadow 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    will-change: background, box-shadow;
}

.navbar.scrolled {
    background: rgba(245, 245, 247, 0.82);
    backdrop-filter: saturate(180%) blur(20px);
    -webkit-backdrop-filter: saturate(180%) blur(20px);
    box-shadow: 0 1px 0 rgba(0, 0, 0, 0.06);
}

.navbar .logo {
    font-size: 22px;
    font-weight: 600;
    letter-spacing: -0.02em;
    display: flex;
    align-items: center;
    gap: 8px;
}

.navbar .logo i {
    color: var(--color-accent);
}

.navbar .nav-links {
    color: var(--color-text);
    display: flex;
    align-items: center;
    gap: 32px;
    list-style: none;
    font-size: 14px;
    font-weight: 400;
}

.navbar .nav-links a {
    position: relative;
    padding: 4px 0;
    transition: color 0.3s;
}

.navbar .nav-links a::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -2px;
    width: 0;
    height: 1.5px;
    background: var(--color-accent);
    transition: width 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.navbar .nav-links a:hover::after {
    width: 100%;
}

.navbar .nav-actions {
    display: flex;
    align-items: center;
    gap: 16px;
}

.navbar .nav-actions .btn-nav {
    font-size: 14px;
    font-weight: 500;
    padding: 8px 20px;
    border-radius: 980px;
    background: var(--color-accent);
    color: #fff;
    border: none;
    cursor: pointer;
    transition: background 0.3s, transform 0.2s;
}

.navbar .nav-actions .btn-nav:hover {
    background: var(--color-accent-hover);
    transform: scale(1.02);
}

.navbar .hamburger {
    display: none;
    flex-direction: column;
    gap: 5px;
    cursor: pointer;
    background: none;
    border: none;
    padding: 4px;
}

.navbar .hamburger span {
    display: block;
    width: 24px;
    height: 2px;
    background: var(--color-text);
    transition: 0.3s;
    border-radius: 2px;
}

/* ========== 英雄区 ========== */
.hero {
    position: relative;
    height: 100vh;
    min-height: 700px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    background: var(--color-bg-dark);
}

.hero .hero-bg {
    position: absolute;
    inset: 0;
    background: url('https://images.unsplash.com/photo-1506929562872-bb421503ef21?w=1920&q=80') center center / cover no-repeat;
    transform: scale(1.05);
    will-change: transform;
}

.hero .hero-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(0, 0, 0, 0.40) 0%, rgba(0, 0, 0, 0.15) 60%, rgba(0, 0, 0, 0.50) 100%);
    z-index: 1;
}

.hero .hero-content {
    position: relative;
    z-index: 2;
    text-align: center;
    color: #fff;
    padding: 0 24px;
    max-width: 800px;
    will-change: transform;
}

.hero .hero-content .badge {
    display: inline-block;
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    padding: 6px 18px;
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 980px;
    backdrop-filter: blur(4px);
    background: rgba(255, 255, 255, 0.08);
    margin-bottom: 28px;
}

.hero .hero-content h1 {
    font-size: clamp(44px, 8vw, 88px);
    font-weight: 600;
    letter-spacing: -0.03em;
    line-height: 1.05;
    margin-bottom: 20px;
    text-shadow: 0 2px 40px rgba(0, 0, 0, 0.2);
}

.hero .hero-content h1 .highlight {
    background: linear-gradient(135deg, #f5f5f7 30%, #a8a8ad 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.hero .hero-content p {
    font-size: clamp(18px, 2vw, 24px);
    font-weight: 300;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 36px;
    letter-spacing: 0.02em;
    max-width: 560px;
    margin-left: auto;
    margin-right: auto;
}

.hero .hero-content .btn-hero {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    padding: 16px 44px;
    font-size: 17px;
    font-weight: 500;
    border-radius: 980px;
    background: #fff;
    color: var(--color-text);
    border: none;
    cursor: pointer;
    transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        box-shadow 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.hero .hero-content .btn-hero:hover {
    transform: translateY(-3px) scale(1.02);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

.hero .hero-content .btn-hero i {
    transition: transform 0.3s;
}

.hero .hero-content .btn-hero:hover i {
    transform: translateX(4px);
}

.hero .scroll-indicator {
    position: absolute;
    bottom: 40px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 2;
    color: rgba(255, 255, 255, 0.6);
    font-size: 13px;
    font-weight: 300;
    letter-spacing: 0.1em;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    animation: bounceDown 2.4s ease-in-out infinite;
}

.hero .scroll-indicator .line {
    width: 1px;
    height: 40px;
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.6), transparent);
}

@keyframes bounceDown {

    0%,
    100% {
        transform: translateX(-50%) translateY(0);
        opacity: 0.6;
    }

    50% {
        transform: translateX(-50%) translateY(8px);
        opacity: 1;
    }
}

/* ========== 通用 ========== */
section {
    padding: var(--section-spacing) 0;
    position: relative;
    overflow: hidden;
}

.section-label {
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    color: var(--color-text-light);
    margin-bottom: 12px;
}

.section-title {
    font-size: clamp(36px, 5vw, 56px);
    font-weight: 600;
    letter-spacing: -0.02em;
    line-height: 1.1;
    margin-bottom: 16px;
}

.section-subtitle {
    font-size: clamp(16px, 1.2vw, 20px);
    font-weight: 300;
    color: var(--color-text-light);
    max-width: 520px;
    line-height: 1.7;
}

.section-header {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 56px;
}

.section-header.center {
    align-items: center;
    text-align: center;
}

.section-header.center .section-subtitle {
    max-width: 560px;
}

/* ========== 视差装饰层 ========== */
.parallax-layer {
    position: absolute;
    pointer-events: none;
    will-change: transform;
    user-select: none;
    z-index: 0;
    opacity: 0.04;
    font-weight: 700;
    letter-spacing: -0.04em;
    color: var(--color-text);
}

/* ========== 目的地 ========== */
.destinations {
    background: #fff;
}

.destinations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 28px;
}

.dest-card {
    position: relative;
    border-radius: 20px;
    overflow: hidden;
    background: #fff;
    box-shadow: 0 2px 20px rgba(0, 0, 0, 0.04);
    transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        box-shadow 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    cursor: pointer;
    will-change: transform;
}

.dest-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
}

.dest-card .card-image {
    position: relative;
    padding-top: 75%;
    overflow: hidden;
    background: #f0f0f2;
}

.dest-card .card-image img {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.dest-card:hover .card-image img {
    transform: scale(1.04);
}

.dest-card .card-content {
    padding: 24px 28px 28px;
}

.dest-card .card-content .card-tag {
    font-size: 11px;
    font-weight: 500;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--color-text-light);
    margin-bottom: 4px;
}

.dest-card .card-content h3 {
    font-size: 20px;
    font-weight: 600;
    letter-spacing: -0.01em;
    margin-bottom: 6px;
}

.dest-card .card-content p {
    font-size: 15px;
    color: var(--color-text-light);
    font-weight: 300;
    line-height: 1.6;
}

.dest-card .card-content .card-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid var(--color-border);
    font-size: 14px;
    color: var(--color-text-light);
}

.dest-card .card-content .card-meta .price {
    font-weight: 600;
    color: var(--color-text);
}

/* ========== 体验 ========== */
.experiences {
    background: var(--color-bg);
}

.exp-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 40px 32px;
}

.exp-item {
    text-align: center;
    padding: 20px 12px;
    transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.exp-item:hover {
    transform: translateY(-6px);
}

.exp-item .exp-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
    font-size: 28px;
    color: var(--color-accent);
    margin-bottom: 20px;
    transition: box-shadow 0.4s, transform 0.4s;
}

.exp-item:hover .exp-icon {
    box-shadow: 0 8px 32px rgba(0, 113, 227, 0.12);
    transform: scale(1.04);
}

.exp-item h4 {
    font-size: 18px;
    font-weight: 600;
    letter-spacing: -0.01em;
    margin-bottom: 8px;
}

.exp-item p {
    font-size: 15px;
    color: var(--color-text-light);
    font-weight: 300;
    line-height: 1.7;
    max-width: 280px;
    margin: 0 auto;
}

/* ========== 故事 ========== */
.stories {
    background: #fff;
}

.stories-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 28px;
}

.story-card {
    background: var(--color-bg);
    border-radius: 20px;
    padding: 36px 32px 32px;
    transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        box-shadow 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.story-card:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.04);
}

.story-card .stars {
    color: #f5b342;
    font-size: 14px;
    letter-spacing: 2px;
    margin-bottom: 14px;
}

.story-card blockquote {
    font-size: 17px;
    font-weight: 300;
    line-height: 1.7;
    margin-bottom: 20px;
    font-style: italic;
}

.story-card .story-author {
    display: flex;
    align-items: center;
    gap: 14px;
}

.story-card .story-author .avatar {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
    background: #d2d2d7;
}

.story-card .story-author .avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.story-card .story-author .author-info .name {
    font-weight: 500;
    font-size: 15px;
}

.story-card .story-author .author-info .role {
    font-size: 13px;
    color: var(--color-text-light);
    font-weight: 300;
}

/* ========== 新增: 叙事章节 ========== */
.narrative {
    background: var(--color-bg-dark);
    color: #fff;
}

.narrative .container {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
}

.narrative .narrative-media {
    position: relative;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.narrative .narrative-media img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 1.2s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.narrative .narrative-media:hover img {
    transform: scale(1.03);
}

.narrative .narrative-text .section-label {
    color: rgba(255, 255, 255, 0.4);
}

.narrative .narrative-text .section-title {
    color: #fff;
}

.narrative .narrative-text .section-subtitle {
    color: rgba(255, 255, 255, 0.65);
    max-width: 100%;
}

.narrative .narrative-text .narrative-detail {
    margin-top: 24px;
    font-size: 17px;
    font-weight: 300;
    color: rgba(255, 255, 255, 0.7);
    line-height: 1.8;
    border-left: 3px solid var(--color-accent);
    padding-left: 24px;
}

/* ========== 新增: 沉浸式引用 ========== */
.immersion {
    background: #fff;
    text-align: center;
}

.immersion .immersion-quote {
    max-width: 800px;
    margin: 0 auto;
    font-size: clamp(24px, 3vw, 40px);
    font-weight: 300;
    line-height: 1.4;
    letter-spacing: -0.01em;
    color: var(--color-text);
    position: relative;
}

.immersion .immersion-quote::before {
    content: '“';
    font-size: 80px;
    line-height: 0.6;
    color: var(--color-accent);
    opacity: 0.2;
    display: block;
    margin-bottom: -10px;
}

.immersion .immersion-quote .author {
    display: block;
    margin-top: 20px;
    font-size: 16px;
    font-weight: 400;
    color: var(--color-text-light);
    letter-spacing: 0.02em;
}

/* ========== CTA ========== */
.cta-section {
    background: var(--color-bg-dark);
    color: #fff;
    padding: 100px 0;
    text-align: center;
    position: relative;
}

.cta-section .cta-bg-glow {
    position: absolute;
    top: -50%;
    left: 50%;
    transform: translateX(-50%);
    width: 800px;
    height: 800px;
    background: radial-gradient(circle, rgba(0, 113, 227, 0.08) 0%, transparent 70%);
    pointer-events: none;
}

.cta-section .container {
    position: relative;
    z-index: 1;
}

.cta-section h2 {
    font-size: clamp(36px, 5vw, 56px);
    font-weight: 600;
    letter-spacing: -0.02em;
    margin-bottom: 16px;
}

.cta-section p {
    font-size: clamp(16px, 1.2vw, 20px);
    font-weight: 300;
    color: rgba(255, 255, 255, 0.6);
    max-width: 480px;
    margin: 0 auto 36px;
    line-height: 1.7;
}

.cta-section .btn-cta {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    padding: 16px 48px;
    font-size: 17px;
    font-weight: 500;
    border-radius: 980px;
    background: #fff;
    color: var(--color-text);
    border: none;
    cursor: pointer;
    transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        box-shadow 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.cta-section .btn-cta:hover {
    transform: translateY(-3px) scale(1.02);
    box-shadow: 0 16px 48px rgba(0, 0, 0, 0.4);
}

.cta-section .btn-cta i {
    transition: transform 0.3s;
}

.cta-section .btn-cta:hover i {
    transform: translateX(4px);
}

/* ========== 页脚 ========== */
.footer {
    background: #fff;
    padding: 60px 0 32px;
    border-top: 1px solid var(--color-border);
}

.footer-grid {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 48px;
    margin-bottom: 48px;
}

.footer-brand .logo {
    font-size: 22px;
    font-weight: 600;
    letter-spacing: -0.02em;
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
}

.footer-brand .logo i {
    color: var(--color-accent);
}

.footer-brand p {
    font-size: 15px;
    color: var(--color-text-light);
    font-weight: 300;
    max-width: 280px;
    line-height: 1.7;
}

.footer-col h5 {
    font-size: 14px;
    font-weight: 600;
    letter-spacing: 0.02em;
    margin-bottom: 16px;
}

.footer-col ul {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.footer-col ul a {
    font-size: 14px;
    color: var(--color-text-light);
    font-weight: 300;
    transition: color 0.3s;
}

.footer-col ul a:hover {
    color: var(--color-text);
}

.footer-bottom {
    border-top: 1px solid var(--color-border);
    padding-top: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 13px;
    color: var(--color-text-light);
    font-weight: 300;
    flex-wrap: wrap;
    gap: 12px;
}

.footer-bottom .socials {
    display: flex;
    gap: 20px;
}

.footer-bottom .socials a {
    color: var(--color-text-light);
    transition: color 0.3s;
    font-size: 16px;
}

.footer-bottom .socials a:hover {
    color: var(--color-text);
}

/* ========== 渐入 ========== */
.reveal {
    opacity: 0;
    transform: translateY(40px);
    transition: opacity 0.9s cubic-bezier(0.25, 0.46, 0.45, 0.94),
        transform 0.9s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    will-change: opacity, transform;
}

.reveal.visible {
    opacity: 1;
    transform: translateY(0);
}

.reveal-delay-1 {
    transition-delay: 0.1s;
}

.reveal-delay-2 {
    transition-delay: 0.2s;
}

.reveal-delay-3 {
    transition-delay: 0.3s;
}

.reveal-delay-4 {
    transition-delay: 0.4s;
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
    .footer-grid {
        grid-template-columns: 1fr 1fr;
    }

    .narrative .container {
        grid-template-columns: 1fr;
        gap: 40px;
    }

    .narrative .narrative-media {
        order: -1;
    }
}

@media (max-width: 768px) {
    :root {
        --section-spacing: 80px;
        --nav-height: 56px;
    }

    .navbar {
        padding: 0 20px;
    }

    .navbar .nav-links {
        display: none;
        position: absolute;
        top: var(--nav-height);
        left: 0;
        right: 0;
        background: rgba(245, 245, 247, 0.96);
        backdrop-filter: saturate(180%) blur(20px);
        flex-direction: column;
        padding: 20px 24px;
        gap: 16px;
        border-bottom: 1px solid var(--color-border);
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
    }

    .navbar .nav-links.open {
        display: flex;
    }

    .navbar .hamburger {
        display: flex;
    }

    .hero .hero-content h1 {
        font-size: 44px;
    }

    .hero .hero-content p {
        font-size: 18px;
    }

    .destinations-grid {
        grid-template-columns: 1fr 1fr;
    }

    .exp-grid {
        grid-template-columns: 1fr 1fr;
    }

    .stories-grid {
        grid-template-columns: 1fr;
    }

    .footer-grid {
        grid-template-columns: 1fr;
        gap: 32px;
    }

    .footer-bottom {
        flex-direction: column;
        text-align: center;
    }

    .section-title {
        font-size: 32px;
    }

    .cta-section {
        padding: 72px 0;
    }

    .parallax-layer {
        display: none;
    }

    .immersion .immersion-quote {
        font-size: 24px;
    }
}

@media (max-width: 480px) {
    .destinations-grid {
        grid-template-columns: 1fr;
    }

    .exp-grid {
        grid-template-columns: 1fr;
    }

    .hero .hero-content h1 {
        font-size: 32px;
    }

    .hero .hero-content .btn-hero {
        padding: 14px 32px;
        font-size: 15px;
    }

    .navbar .nav-actions .btn-nav {
        padding: 6px 16px;
        font-size: 13px;
    }
}
</style>