<template>
  <div class="home-page">
    <main class="main-content">
      <Main 
        @open-detail="handleOpenDetail" 
        @open-news-detail="handleOpenNewsDetail"
        @open-safety-detail="handleOpenSafetyDetail"
        @open-recommendation-detail="handleOpenRecommendationDetail"
      />
    </main>
    <Footer />
    
    <Teleport to="body">
      <Details
        :visible="detailVisible"
        :type="detailType"
        :data="detailData"
        :loading="detailLoading"
        :error="detailError"
        @close="handleCloseDetail"
      />
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Main from '@/views/index/components/main.vue';
import Footer from '@/components/DisplayBox/BottomPage.vue'
import Details from '@/views/index/components/details.vue';
import { getNewsDetail } from '@/api/news.js';
import { getHotelDetail } from '@/api/hotel.js';
import { getFoodDetail } from '@/api/food.js';
import { getAttractionDetail } from '@/api/attraction.js';
import { getHomeRecommendationById } from '@/api/homeRecommendations.js';

const detailVisible = ref(false);
const detailType = ref('');
const detailData = ref({});
const detailLoading = ref(false);
const detailError = ref('');

const handleOpenDetail = async (type, id) => {
    detailType.value = type;
    detailLoading.value = true;
    detailError.value = '';
    detailVisible.value = true;
    
    try {
        let response;
        if (type === 'hotel') response = await getHotelDetail(id);
        else if (type === 'food') response = await getFoodDetail(id);
        else if (type === 'attraction') response = await getAttractionDetail(id);
        
        if (response && response.code === 200) {
            detailData.value = response.data;
        } else {
            detailError.value = `获取${getTypeLabel(type)}详情失败`;
        }
    } catch (err) {
        detailError.value = '网络错误，请稍后重试';
        console.error(`获取${getTypeLabel(type)}详情失败:`, err);
    } finally {
        detailLoading.value = false;
    }
};

const handleOpenNewsDetail = async (id) => {
    detailType.value = 'news';
    detailLoading.value = true;
    detailError.value = '';
    detailVisible.value = true;
    
    try {
        const response = await getNewsDetail(id);
        if (response && response.code === 200) {
            detailData.value = response.data;
        } else {
            detailError.value = '获取新闻详情失败';
        }
    } catch (err) {
        detailError.value = '网络错误，请稍后重试';
        console.error('获取新闻详情失败:', err);
    } finally {
        detailLoading.value = false;
    }
};

const handleOpenSafetyDetail = (tip) => {
    detailType.value = 'safety';
    detailLoading.value = false;
    detailError.value = '';
    detailData.value = tip;
    detailVisible.value = true;
};

const handleOpenRecommendationDetail = async (item) => {
    detailType.value = 'recommendation';
    detailLoading.value = true;
    detailError.value = '';
    detailVisible.value = true;
    
    try {
        const response = await getHomeRecommendationById(item.id);
        if (response && response.code === 200) {
            detailData.value = response.data;
        } else {
            detailData.value = item;
        }
    } catch (err) {
        detailData.value = item;
        console.error('获取推荐详情失败:', err);
    } finally {
        detailLoading.value = false;
    }
};

const handleCloseDetail = () => {
    detailVisible.value = false;
    detailType.value = '';
    detailData.value = {};
    detailLoading.value = false;
    detailError.value = '';
};

const getTypeLabel = (type) => {
    const labels = { hotel: '酒店', food: '美食', attraction: '景点', news: '新闻', recommendation: '推荐' };
    return labels[type] || '';
};
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #ffffff;
}

.main-content {
  position: relative;
  z-index: 1;
}
</style>
