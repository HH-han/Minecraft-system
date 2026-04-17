<template>
    <div class="content-container">
        <!-- 酒店卡片 -->
        <section class="content-section">
            <h2 class="section-title">推荐酒店</h2>
            <div v-if="loading.hotel" class="loading">加载中...</div>
            <div v-else-if="error.hotel" class="error">{{ error.hotel }}</div>
            <div v-else class="card-grid">
                <div v-for="hotel in hotelList" :key="hotel.id" class="card">
                    <div class="card-image">
                        <img :src="hotel.coverImage" alt="酒店图片">
                    </div>
                    <div class="container">
                        <div class="text-wrap">
                            <h3 class="card-title">{{ hotel.name }}</h3>
                            <p class="card-description">{{ hotel.description.substring(0, 100) }}...</p>
                            <div class="card-meta">
                                <span class="card-location">{{ hotel.city }} {{ hotel.address }}</span>
                                <span class="card-price">¥{{ hotel.price }}/晚</span>
                            </div>
                            <div class="button-wrap">
                                <button class="primary-cta" @click="viewHotelDetail(hotel.id)">查看详情</button>
                                <button class="secondary-cta">收藏</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- 美食卡片 -->
        <section class="content-section">
            <h2 class="section-title">特色美食</h2>
            <div v-if="loading.food" class="loading">加载中...</div>
            <div v-else-if="error.food" class="error">{{ error.food }}</div>
            <div v-else class="card-grid">
                <div v-for="food in foodList" :key="food.id" class="card">
                    <div class="card-image">
                        <img :src="food.coverImage" alt="美食图片">
                    </div>
                    <div class="container">
                        <div class="text-wrap">
                            <h3 class="card-title">{{ food.name }}</h3>
                            <p class="card-description">{{ food.description.substring(0, 100) }}...</p>
                            <div class="card-meta">
                                <span class="card-location">{{ food.city }} {{ food.restaurant }}</span>
                                <span class="card-price">¥{{ food.price }}</span>
                            </div>
                            <div class="button-wrap">
                                <button class="primary-cta" @click="viewFoodDetail(food.id)">查看详情</button>
                                <button class="secondary-cta">收藏</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- 景点卡片 -->
        <section class="content-section">
            <h2 class="section-title">热门景点</h2>
            <div v-if="loading.attraction" class="loading">加载中...</div>
            <div v-else-if="error.attraction" class="error">{{ error.attraction }}</div>
            <div v-else class="card-grid">
                <div v-for="attraction in attractionList" :key="attraction.id" class="card">
                    <div class="card-image">
                        <img :src="attraction.coverImage" alt="景点图片">
                    </div>
                    <div class="container">
                        <div class="text-wrap">
                            <h3 class="card-title">{{ attraction.name }}</h3>
                            <p class="card-description">{{ attraction.description.substring(0, 100) }}...</p>
                            <div class="card-meta">
                                <span class="card-location">{{ attraction.city }} {{ attraction.address }}</span>
                                <span class="card-price">¥{{ attraction.ticketPrice || 0 }}</span>
                            </div>
                            <div class="button-wrap">
                                <button class="primary-cta" @click="viewAttractionDetail(attraction.id)">查看详情</button>
                                <button class="secondary-cta">收藏</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        
        <!-- 详情模态框 -->
        <DetailsModal
            :visible="modalVisible"
            :type="modalType"
            :data="modalData"
            :loading="modalLoading"
            :error="modalError"
            @close="closeModal"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getNewsDetail } from '@/api/news.js';
import { getRecommendHotels, getHotelDetail } from '@/api/hotel.js';
import { getRecommendFoods, getFoodDetail } from '@/api/food.js';
import { getHotAttractions, getAttractionDetail } from '@/api/attraction.js';
import DetailsModal from './details.vue';

// 响应式数据
const newsList = ref([]);
const hotelList = ref([]);
const foodList = ref([]);
const attractionList = ref([]);

// 加载状态
const loading = ref({
    news: false,
    hotel: false,
    food: false,
    attraction: false
});

// 错误信息
const error = ref({
    news: '',
    hotel: '',
    food: '',
    attraction: ''
});

// 模态框状态
const modalVisible = ref(false);
const modalType = ref('');
const modalData = ref({});
const modalLoading = ref(false);
const modalError = ref('');

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
};
// 获取酒店数据
const fetchHotels = async () => {
    loading.value.hotel = true;
    error.value.hotel = '';
    try {
        const response = await getRecommendHotels(null, 6);
        if (response.code === 200 && response.data) {
            hotelList.value = response.data;
        } else {
            error.value.hotel = '获取酒店失败';
        }
    } catch (err) {
        error.value.hotel = '网络错误，请稍后重试';
        console.error('获取酒店失败:', err);
    } finally {
        loading.value.hotel = false;
    }
};

// 获取美食数据
const fetchFoods = async () => {
    loading.value.food = true;
    error.value.food = '';
    try {
        const response = await getRecommendFoods(null, 6);
        if (response.code === 200 && response.data) {
            foodList.value = response.data;
        } else {
            error.value.food = '获取美食失败';
        }
    } catch (err) {
        error.value.food = '网络错误，请稍后重试';
        console.error('获取美食失败:', err);
    } finally {
        loading.value.food = false;
    }
};

// 获取景点数据
const fetchAttractions = async () => {
    loading.value.attraction = true;
    error.value.attraction = '';
    try {
        const response = await getHotAttractions(6);
        if (response.code === 200 && response.data) {
            attractionList.value = response.data;
        } else {
            error.value.attraction = '获取景点失败';
        }
    } catch (err) {
        error.value.attraction = '网络错误，请稍后重试';
        console.error('获取景点失败:', err);
    } finally {
        loading.value.attraction = false;
    }
};

// 关闭模态框
const closeModal = () => {
    modalVisible.value = false;
    modalType.value = '';
    modalData.value = {};
    modalLoading.value = false;
    modalError.value = '';
};

// 查看新闻详情
const viewNewsDetail = async (newsId) => {
    try {
        modalType.value = 'news';
        modalLoading.value = true;
        modalError.value = '';
        
        const response = await getNewsDetail(newsId);
        if (response.code === 200) {
            modalData.value = response.data;
            modalVisible.value = true;
        } else {
            modalError.value = '获取新闻详情失败';
            modalVisible.value = true;
        }
    } catch (error) {
        modalError.value = '网络错误，请稍后重试';
        modalVisible.value = true;
        console.error('获取新闻详情失败:', error);
    } finally {
        modalLoading.value = false;
    }
};

// 查看酒店详情
const viewHotelDetail = async (hotelId) => {
    try {
        modalType.value = 'hotel';
        modalLoading.value = true;
        modalError.value = '';
        
        const response = await getHotelDetail(hotelId);
        if (response.code === 200) {
            modalData.value = response.data;
            modalVisible.value = true;
        } else {
            modalError.value = '获取酒店详情失败';
            modalVisible.value = true;
        }
    } catch (error) {
        modalError.value = '网络错误，请稍后重试';
        modalVisible.value = true;
        console.error('获取酒店详情失败:', error);
    } finally {
        modalLoading.value = false;
    }
};

// 查看美食详情
const viewFoodDetail = async (foodId) => {
    try {
        modalType.value = 'food';
        modalLoading.value = true;
        modalError.value = '';
        
        const response = await getFoodDetail(foodId);
        if (response.code === 200) {
            modalData.value = response.data;
            modalVisible.value = true;
        } else {
            modalError.value = '获取美食详情失败';
            modalVisible.value = true;
        }
    } catch (error) {
        modalError.value = '网络错误，请稍后重试';
        modalVisible.value = true;
        console.error('获取美食详情失败:', error);
    } finally {
        modalLoading.value = false;
    }
};

// 查看景点详情
const viewAttractionDetail = async (attractionId) => {
    try {
        modalType.value = 'attraction';
        modalLoading.value = true;
        modalError.value = '';
        
        const response = await getAttractionDetail(attractionId);
        if (response.code === 200) {
            modalData.value = response.data;
            modalVisible.value = true;
        } else {
            modalError.value = '获取景点详情失败';
            modalVisible.value = true;
        }
    } catch (error) {
        modalError.value = '网络错误，请稍后重试';
        modalVisible.value = true;
        console.error('获取景点详情失败:', error);
    } finally {
        modalLoading.value = false;
    }
};

// 组件挂载时获取数据
onMounted(() => {
    fetchHotels();
    fetchFoods();
    fetchAttractions();
});
</script>

<style scoped>
.content-container {
    padding: 0;
}

.content-section {
    margin-bottom: 60px;
}

.section-title {
    font-size: 26px;
    font-weight: 700;
    margin-bottom: 25px;
    color: #1d1d1f;
    border-left: 4px solid #2997ff;
    padding-left: 16px;
    position: relative;
}

.section-title::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 16px;
    width: 80px;
    height: 2px;
    background: #2997ff;
    border-radius: 1px;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
    gap: 32px;
}

.card {
    background: #ffffff;
    border: 1px solid #d2d2d6;
    border-radius: 24px;
    overflow: hidden;
    display: flex;
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
    padding: 16px;
}

.card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

.card-image {
    height: 200px;
    flex-shrink: 0;
}

.card-image img {
    max-width: 200px;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.card:hover .card-image img {
    transform: scale(1.05);
}

.container {
    margin-left: 20px;
    flex: 1;
}

.text-wrap {
    display: flex;
    flex-direction: column;
    gap: 12px;
    height: 100%;
}

.card-title {
    font-size: 18px;
    font-weight: 600;
    margin: 0;
    color: #1d1d1f;
    line-height: 1.3;
}

.card-description {
    font-size: 14px;
    color: #6e6e73;
    margin: 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #6e6e73;
    margin-top: 4px;
}

.button-wrap {
    display: flex;
    gap: 12px;
    margin-top: 8px;
}

.primary-cta {
    background: #2997ff;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: background 0.2s ease;
    flex: 1;
}

.primary-cta:hover {
    background: #0066cc;
}

.secondary-cta {
    background: #f5f5f7;
    color: #1d1d1f;
    border: 1px solid #d2d2d6;
    border-radius: 8px;
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
}

.secondary-cta:hover {
    background: #e8e8ed;
    border-color: #c7c7cc;
}

.loading {
    text-align: center;
    padding: 40px;
    color: #6e6e73;
    background: #f5f5f7;
    border-radius: 12px;
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4757;
    background: #ffebee;
    border-radius: 12px;
    border: 1px solid #ffcdd2;
}

@media (max-width: 768px) {
    .content-section {
        margin-bottom: 40px;
    }
    
    .section-title {
        font-size: 20px;
    }
    
    .card-grid {
        grid-template-columns: 1fr;
        gap: 20px;
    }
    
    .card {
        flex-direction: column;
        border-radius: 20px;
    }
    
    .card-image {
        height: 180px;
        width: 100%;
    }
    
    .card-image img {
        max-width: 100%;
    }
    
    .container {
        margin-left: 0;
        padding: 16px;
    }
    
    .card-title {
        font-size: 16px;
    }
    
    .button-wrap {
        flex-direction: column;
        gap: 8px;
    }
    
    .primary-cta,
    .secondary-cta {
        width: 100%;
    }
}

@media (max-width: 480px) {
    .card {
        border-radius: 16px;
    }
    
    .card-image {
        height: 160px;
    }
    
    .container {
        padding: 14px;
    }
    
    .card-title {
        font-size: 16px;
    }
}
</style>