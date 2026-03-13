<template>
  <div class="home-main">
    <header>
      <Header />
    </header>
    <main>
      <!-- 轮播图片 -->
      <Carousel :items="mediaList.images" />
      <!-- 加载 -->
      <!-- <Loaded /> -->
      <div class="home-background">
        <!-- 推荐 -->
        <Recommend />
        <div class="home-content">
          <div>
            <!-- 安全提示 -->
            <SafetyTips />
          </div>
          <div>
            <!-- 新闻 -->
            <News />
          </div>
        </div>
        <!-- 热门推荐 -->
        <Recommendations />
        <!-- 首页内容 -->
        <Content />
      </div>

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
.home-background {
  background: linear-gradient(145deg, #fce9d8 0%, #f9d8c6 15%, #c0d9e8 70%, #b2d1e0 100%);
  position: relative;
  background-blend-mode: overlay;
  box-shadow: inset 0 0 150px rgba(255, 240, 220, 0.3);
  transition: background 0.3s ease;
}
.home-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 30%, rgba(255, 245, 215, 0.4) 0%, transparent 35%),
    radial-gradient(circle at 90% 70%, rgba(215, 235, 255, 0.5) 0%, transparent 40%),
    radial-gradient(circle at 40% 80%, rgba(255, 235, 210, 0.3) 0%, transparent 50%),
    repeating-linear-gradient(45deg, rgba(255, 255, 255, 0.02) 0px, rgba(255, 255, 255, 0.02) 15px, rgba(255, 240, 230, 0.02) 15px, rgba(255, 240, 230, 0.02) 30px);
  pointer-events: none;
  z-index: 1;
}
.home-background::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(170deg,
      rgba(205, 220, 235, 0.2) 0%,
      transparent 20%,
      transparent 80%,
      rgba(230, 215, 195, 0.15) 100%);
  pointer-events: none;
  z-index: 1;
}
.home-background>* {
  position: relative;
  z-index: 5;
}

.home-content {
  display: flex;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 30px;
  gap: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .home-content {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
    margin: 10px;
  }
}
</style>
