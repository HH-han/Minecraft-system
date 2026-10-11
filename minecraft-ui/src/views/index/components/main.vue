<template>
  <div class="home-main" ref="rootEl">
    <main>
      <!-- 主视觉 Hero -->
      <section class="hero-section">
        <transition name="fade" mode="out-in">
          <Carousel :items="mediaList.images" />
        </transition>
      </section>

      <!-- 公告轮播（displayMode=3） -->
      <AnnouncementCarousel />

      <!-- 网站介绍 -->
      <section>
        <WebsiteIntroduction @open-detail="(type, id) => $emit('open-detail', type, id)"
          @open-recommendation-detail="(item) => $emit('open-recommendation-detail', item)" />
      </section>

      <!-- 资讯与安全（08 / 09） -->
      <section class="insight-section">
        <div class="insight-grid">
          <div class="stage" data-reveal>
            <div class="stage-head">
              <span class="stage-index">08</span>
              <span class="stage-rule"></span>
            </div>
            <News @open-news-detail="(id) => $emit('open-news-detail', id)" />
          </div>

          <div class="stage" data-reveal>
            <div class="stage-head">
              <span class="stage-index">09</span>
              <span class="stage-rule"></span>
            </div>
            <SafetyTips @open-safety-detail="(tip) => $emit('open-safety-detail', tip)" />
          </div>
        </div>
      </section>
    </main>
  </div>
</template>
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import SafetyTips from './safetytips.vue';
import News from './news.vue';
import Carousel from '@/views/Carousel/index.vue';
import carouselApi from '@/api/carousel.js';
import WebsiteIntroduction from './WebsiteIntroduction.vue';
import AnnouncementCarousel from '@/components/AnnouncementComponents/AnnouncementCarousel.vue';

defineEmits(['open-detail', 'open-news-detail', 'open-safety-detail', 'open-recommendation-detail']);

const rootEl = ref(null);

// 轮播图数据
const mediaList = ref({ images: [] });

// 获取轮播图数据
const fetchcarousel = async () => {
  try {
    // 使用API获取轮播图数据
    const response = await carouselApi.getHomeTopCarousels();
    if (response.code === 200 && response.data) {
      mediaList.value.images = response.data.map((item) => ({
        image: item.imageUrl || '默认图片链接',
        title: item.title || '默认标题',
        location: item.subtitle || '默认位置',
        description: item.startTime || '默认描述'
      }));
    }
  } catch (error) {
    console.error('获取轮播图数据失败：', error)
  }
}

// 滚动入场观察器（与 WebsiteIntroduction 保持一致的表现）
let revealObserver = null;

const setupReveal = () => {
  const targets = rootEl.value?.querySelectorAll('[data-reveal]');
  if (!targets?.length || !('IntersectionObserver' in window)) return;

  rootEl.value.classList.add('is-armed');

  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add('is-revealed');
      revealObserver.unobserve(entry.target);
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -6% 0px' });

  targets.forEach((el) => {
    const rect = el.getBoundingClientRect();
    if (rect.top < window.innerHeight * 0.9) {
      el.classList.add('is-revealed');
    } else {
      revealObserver.observe(el);
    }
  });
};

// 初始化
onMounted(() => {
  fetchcarousel();
  setupReveal();
});

onBeforeUnmount(() => {
  revealObserver?.disconnect();
  revealObserver = null;
});
</script>
<style scoped>
/* ============================================================
   页面外壳：暗色电影感画布
   这里定义的 --wi-* 设计变量同时供 news.vue / safetytips.vue 继承使用
   ============================================================ */
.home-main {
  --wi-surface: rgba(255, 255, 255, 0.045);
  --wi-hairline: rgba(255, 255, 255, 0.09);
  --wi-hairline-2: rgba(255, 255, 255, 0.16);
  --wi-txt-1: #f6f7fb;
  --wi-txt-2: rgba(236, 238, 245, 0.66);
  --wi-txt-3: rgba(236, 238, 245, 0.42);
  --wi-azure: #4da3ff;
  --wi-violet: #8b6cff;
  --wi-mint: #37e0a0;
  --wi-accent: linear-gradient(120deg, #6cc4ff 0%, #8b6cff 58%, #ff8f5e 118%);
  --wi-ease: cubic-bezier(0.22, 1, 0.36, 1);

  position: relative;
  isolation: isolate;
  min-height: 100vh;
  color: var(--wi-txt-1);
  font-family: 'SF Pro Display', 'SF Pro', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background-color: #05060a;
  background-image:
    radial-gradient(1100px 560px at 50% -10%, rgba(77, 163, 255, 0.1), transparent 62%),
    radial-gradient(900px 500px at 92% 24%, rgba(139, 108, 255, 0.08), transparent 66%);
  scroll-behavior: smooth;
}

/* ============================================================
   亮色主题（html[data-theme="light"]）
   深色为设计基底，此处覆盖为亮色；--wi-* 会随继承传给
   news.vue / safetytips.vue 等子组件
   ============================================================ */
html[data-theme="light"] .home-main {
  --wi-surface: rgba(15, 23, 42, 0.035);
  --wi-hairline: rgba(15, 23, 42, 0.1);
  --wi-hairline-2: rgba(15, 23, 42, 0.18);
  --wi-txt-1: #101828;
  --wi-txt-2: rgba(16, 24, 40, 0.66);
  --wi-txt-3: rgba(16, 24, 40, 0.45);
  --wi-azure: #0b74e5;
  --wi-violet: #6a48ff;
  --wi-mint: #0f9d68;
  --wi-accent: linear-gradient(120deg, #0b74e5 0%, #6a48ff 58%, #e2542a 118%);

  color: var(--wi-txt-1);
  background-color: #f4f6fb;
  background-image:
    radial-gradient(1100px 560px at 50% -10%, rgba(77, 163, 255, 0.16), transparent 62%),
    radial-gradient(900px 500px at 92% 24%, rgba(139, 108, 255, 0.12), transparent 66%);
}

/* 主视觉 Hero */
.hero-section {
  width: 100%;
  position: relative;
  overflow: hidden;
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 淡入淡出过渡效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ============ 资讯与安全 ============ */
.insight-section {
  position: relative;
  z-index: 1;
  max-width: 1244px;
  margin: 0 auto;
  padding: 0 22px 96px;
}

.insight-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 44px;
}

.stage {
  position: relative;
  min-width: 0;
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

/* ============ 滚动入场 ============ */
.home-main.is-armed [data-reveal] {
  opacity: 0;
  transform: translateY(26px);
  transition-property: opacity, transform;
  transition-duration: 0.9s;
  transition-timing-function: var(--wi-ease);
  transition-delay: var(--d, 0ms);
}

.home-main.is-armed [data-reveal].is-revealed {
  opacity: 1;
  transform: translateY(0);
}

/* ============ 响应式 ============ */
@media (max-width: 992px) {
  .insight-grid {
    grid-template-columns: 1fr;
    gap: 64px;
  }
}

@media (max-width: 768px) {
  .insight-section {
    padding: 0 20px 72px;
  }

  .insight-grid {
    gap: 56px;
  }
}

/* ============ 无障碍：尊重系统动效偏好 ============ */
@media (prefers-reduced-motion: reduce) {
  .home-main.is-armed [data-reveal] {
    opacity: 1 !important;
    transform: none !important;
    transition: none !important;
  }
}
</style>
