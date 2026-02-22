<template>
    <div class="home-main">
        <header>
            <Header  />
        </header>
        <main>
            <!-- 轮播图片 -->
            <Carousel :mediaList="mediaList" />
            <!-- 加载 -->
            <!-- <Loaded /> -->
            <!-- 新闻 -->
            <News />
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
import Loaded from '@/components/DisplayBox/Loaded.vue';
import News from './news.vue';
import Content from './content.vue';
import { carouselItems } from '../HomeImage.js';

// 轮播图数据
const mediaList = ref({ images: [] });

// 获取轮播图数据
const fetchcarousel = async () => {
  try {
    // 使用本地轮播图数据
    mediaList.value.images = carouselItems.map((item) => ({
      image: item.image || '默认图片链接',
      title: item.title || '默认标题',
      location: item.location || '默认位置',
      description: item.description || '默认描述'
    }))
  } catch (error) {
    console.error('获取轮播图数据失败：', error)
  }
}

// 初始化
onMounted(() => {
  fetchcarousel();
});
</script>
