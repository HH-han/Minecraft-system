<template>
  <div class="home-main">
    <header>
      <Header />
    </header>
    <main>
      <!-- 主视觉 Hero -->
      <section class="hero-section">
        <transition name="fade" mode="out-in">
          <Carousel :items="mediaList.images" />
        </transition>
      </section>

      <!-- 双卡片区 -->
      <section class="two-column-section">
        <div class="two-column-flex">
          <!-- 推荐 -->
          <div class="product-card fade-in-up delay-100">
            <Recommend />
          </div>
          <!-- 三列配件区 -->
          <section class="three-column-section fade-in-up delay-200">
            <h2 class="section-title">热门推荐</h2>
            <Recommendations />
          </section>

        </div>
      </section>

      <!-- 全宽特色条 -->
      <section class="feature-section fade-in-up">
        <div class="feature-content breath">
          <h2 class="pulse">探索 Minecraft 的无限可能</h2>
          <p class="fade-in-up delay-200">创造、探索、生存，在方块的世界中开启你的冒险</p>
          <div class="cta-links fade-in-up delay-300">
            <a href="#">进一步了解 ›</a>
            <a href="#">开始游戏 ›</a>
          </div>
        </div>
      </section>
      <!-- 新闻和内容区 -->
      <section class="two-column-section">
        <div class="two-column-grid">
          <!-- 首页内容 -->
          <div class="product-card fade-in-up delay-100">
            <Content />
          </div>
          <!-- 安全提示 -->
          <div class="product-card fade-in-up delay-200">
            <SafetyTips />
          </div>
          <!-- 新闻 -->
          <div class="product-card fade-in-up delay-300">
            <News />
          </div>
        </div>
      </section>
    </main>
    <footer>
      <HomeFooter />
    </footer>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import Header from './Header.vue';
import Carousel from '@/views/Carousel/index.vue';
import HomeFooter from './Footer.vue';
import Recommend from '/src/views/Recommend/index.vue';
import SafetyTips from './safetytips.vue';
import News from './news.vue';
import Content from './content.vue';
import carouselApi from '@/api/carousel.js';
import Recommendations from './recommendations.vue';

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

// 初始化
onMounted(() => {
  fetchcarousel();
});
</script>
<style scoped>
.home-main {
  font-family: -apple-system, BlinkMacSystemFont, 'Inter', sans-serif;
  color: #1d1d1f;
  background-color: #ffffff;
  min-height: 100vh;
  scroll-behavior: smooth;
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
  0%, 100% {
    opacity: 0.8;
  }
  50% {
    opacity: 1;
  }
}

/* 双卡片区 */
.two-column-section {
  padding: 0px 22px;
}

.two-column-grid {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.two-column-flex {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 产品卡片 */
.product-card {
  background: #f5f5f7;
  border-radius: 28px;
  padding: 40px 24px 30px;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
}

.product-card:hover {
  transform: scale(1.01);
  box-shadow: 0 20px 30px -12px rgba(0, 0, 0, 0.1);
}

/* 全宽特色条 */
.feature-section {
  background-color: #000000;
  color: #ffffff;
  padding: 80px 22px;
  text-align: center;
  margin: 40px 0;
}

.feature-content {
  max-width: 800px;
  margin: 0 auto;
}

.feature-content h2 {
  font-size: 48px;
  font-weight: 700;
  line-height: 1.05;
  margin-bottom: 16px;
}

.feature-content p {
  font-size: 18px;
  line-height: 1.4;
  margin-bottom: 32px;
  color: #a1a1a6;
}

/* CTA链接 */
.cta-links {
  display: flex;
  gap: 32px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-links a {
  color: #2997ff;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  padding: 8px 16px;
  border-radius: 8px;
  overflow: hidden;
}

.cta-links a:hover {
  color: #0066cc;
  text-decoration: none;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(41, 151, 255, 0.3);
}

.cta-links a::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.cta-links a:hover::after {
  left: 100%;
}

/* 三列配件区 */
.three-column-section {
  padding: 40px 22px;
}

.section-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 40px;
  color: #1d1d1f;
  position: relative;
  padding-bottom: 15px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: #2997ff;
  border-radius: 2px;
}

/* 响应式设计 */
@media (max-width: 1199px) {
  .feature-content h2 {
    font-size: 44px;
  }

  .two-column-section,
  .three-column-section {
    padding: 32px 20px;
  }
}

@media (max-width: 768px) {
  .two-column-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .product-card {
    padding: 24px 16px 20px;
    border-radius: 24px;
  }

  .feature-content h2 {
    font-size: 32px;
  }

  .feature-content p {
    font-size: 16px;
  }

  .cta-links {
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }

  .section-title {
    font-size: 28px;
    margin-bottom: 32px;
  }
}

@media (max-width: 480px) {
  .feature-content h2 {
    font-size: 28px;
  }

  .two-column-section,
  .three-column-section {
    padding: 24px 16px;
  }
}
</style>
