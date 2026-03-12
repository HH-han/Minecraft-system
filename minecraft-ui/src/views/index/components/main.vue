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
        description: item.subtitle || '默认描述'
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
.home-content {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}
</style>
