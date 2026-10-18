<template>
  <div class="strategy-page">
    <main class="strategy-main">
      <!-- 轮播图片 -->
      <section class="strategy-stage">
        <Carousel :items="mediaList.images" />
      </section>

      <!-- 群组工作台（含页面主视觉与推荐） -->
      <Content />
    </main>

    <footer class="strategy-footer">
      <BottomPage />
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Carousel from '@/views/Carousel/components/Carousel.vue';
import Content from './content.vue';
import BottomPage from '@/components/DisplayBox/BottomPage.vue'
import carouselApi from '@/api/carousel.js';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

// 轮播图数据
const mediaList = ref({ images: [] });

// 获取轮播图数据
const fetchcarousel = async () => {
  try {
    // 使用API获取轮播图数据
    const response = await carouselApi.getStrategyCarousels();
    if (response.code === 200 && response.data) {
      mediaList.value.images = response.data.map((item) => ({
        image: item.imageUrl || '',
        title: item.title || '',
        location: item.subtitle || '',
        updatedAt: item.updatedAt || ''
      }));
    }
  } catch (error) {
    console.error('获取轮播图数据失败：', error)
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchcarousel)

// 初始化
onMounted(() => {
  fetchcarousel();
});
</script>

<style scoped>
.strategy-page {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.strategy-main {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 64px;
  box-sizing: border-box;
}

.strategy-stage {
  position: relative;
  border-radius: var(--theme-radius-xl);
  overflow: hidden;
  box-shadow: var(--theme-shadow-lg);
}

.strategy-stage::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  box-shadow: inset 0 0 0 1px var(--theme-hairline);
  pointer-events: none;
}

.strategy-footer {
  margin-top: 72px;
}

@media (max-width: 1199px) {
  .strategy-main {
    padding: 20px 20px 0;
    gap: 48px;
  }
}

@media (max-width: 767px) {
  .strategy-main {
    padding: 12px 16px 0;
    gap: 36px;
  }

  .strategy-stage {
    border-radius: var(--theme-radius-lg);
  }

  .strategy-footer {
    margin-top: 40px;
  }
}
</style>
