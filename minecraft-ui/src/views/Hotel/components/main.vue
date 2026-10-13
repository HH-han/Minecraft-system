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
                :hotels="hotels" 
                :loading="loading" 
                :appending="appending"
                :error="error"
                :total-pages="totalPages"
                :current-page="currentPage"
                @retry="fetchHotels"
                @update:current-page="handlePageChange"
                @load-more="handleLoadMore"
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
import { getHotelList } from '@/api/hotel.js';
import { useAutoRefresh } from '@/hooks/useAutoRefresh';

// 轮播图数据
const mediaList = ref({ images: [] });

// 酒店数据
const hotels = ref([]);
const loading = ref(false);
// 下滑追加加载中：列表保持挂载，仅底部提示加载，避免滚动位置被重置
const appending = ref(false);
const error = ref('');

// 分页数据
const currentPage = ref(1);
const totalPages = ref(0);
const pageSize = ref(10);

// 获取轮播图数据
const fetchcarousel = async () => {
  try {
    const response = await carouselApi.getHotelCarousels();
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

// 数据变更后自动刷新
useAutoRefresh(fetchcarousel);

// 获取酒店数据（append=true 时在已有数据之后追加，不重置）
const fetchHotels = async (page = 1, { append = false } = {}) => {
  appending.value = append;
  loading.value = true;
  error.value = '';
  
  try {
    const response = await getHotelList({ pageNum: page, pageSize: pageSize.value });
    const records = response.data?.records || [];
    hotels.value = append ? [...hotels.value, ...records] : records;
    totalPages.value = response.data?.pages || 0;
  } catch (err) {
    error.value = err.message || '获取数据失败';
    console.error('获取酒店数据失败:', err);
  } finally {
    loading.value = false;
    appending.value = false;
  }
};

// 数据变更后自动刷新
useAutoRefresh(fetchHotels);

// 分页切换（点击页码/上下页：跳页并替换数据）
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchHotels(page);
};

// 下滑继续加载（在已有数据之后追加新数据）
const handleLoadMore = (page) => {
  currentPage.value = page;
  fetchHotels(page, { append: true });
};

// 初始化
onMounted(() => {
  fetchcarousel();
  fetchHotels();
});
</script>
