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
            <Content 
                :attractions="attractions" 
                :loading="loading" 
                :error="error"
                @retry="fetchAttractions"
            />
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
import BottomPage from '@/components/DisplayBox/BottomPage.vue';
import carouselApi from '@/api/carousel.js';
import { getAttractionList } from '@/api/attraction.js';

// 轮播图数据
const mediaList = ref({ images: [] });

// 景点数据
const attractions = ref([]);
const loading = ref(false);
const error = ref('');

// 获取轮播图数据
const fetchcarousel = async () => {
  try {
    const response = await carouselApi.getAttractionCarousels();
    if (response.code === 200 && response.data) {
      mediaList.value.images = response.data.map((item) => ({
        image: item.imageUrl || '默认图片链接',
        title: item.title || '默认标题',
        location: item.subtitle || '默认位置',
        updatedAt: item.updatedAt || '默认描述'
      }));
    }
  } catch (err) {
    console.error('获取轮播图数据失败:', err);
  }
};

// 获取景点数据
const fetchAttractions = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    const response = await getAttractionList();
    attractions.value = response.data?.records || [];
  } catch (err) {
    error.value = err.message || '获取数据失败';
    console.error('获取景点数据失败:', err);
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  fetchcarousel();
  fetchAttractions();
});
</script>
