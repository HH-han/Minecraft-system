<template>
    <div class="home-main">
        <header>
            
        </header>
        <main>
            <!-- 轮播图片 -->
            <Carousel :items="mediaList.images" />
            <!-- 推荐 -->
            <Recommend />
            <!-- 搜索 -->
            <SearchBox />
            <!-- 内容 -->
            <Content />
        </main>
        <footer>
            <BottomPage />
        </footer>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import Carousel from '@/views/Carousel/components/Carousel.vue';
import SearchBox from '@/components/DisplayBox/SearchBox.vue';
import Recommend from './recommend.vue';
import Content from './content.vue';
import BottomPage from '@/components/DisplayBox/BottomPage.vue'
import carouselApi from '@/api/carousel.js';

// 轮播图数据
const mediaList = ref({ images: [] });

// 获取轮播图数据
const fetchcarousel = async () => {
  try {
    // 使用API获取轮播图数据
    const response = await carouselApi.getHotelCarousels();
    if (response.code === 200 && response.data) {
      mediaList.value.images = response.data.map((item) => ({
        image: item.imageUrl || '默认图片链接',
        title: item.title || '默认标题',
        location: item.subtitle || '默认位置',
        updatedAt: item.updatedAt || '默认描述'
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
