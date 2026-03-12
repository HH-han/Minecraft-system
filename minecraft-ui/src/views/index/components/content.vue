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
                        <div class="left">
                            <div class="status-ind"></div>
                        </div>
                        <div class="right">
                            <div class="text-wrap">
                                <h3 class="card-title">{{ hotel.name }}</h3>
                                <p class="card-description">{{ hotel.description.substring(0, 100) }}...</p>
                                <div class="card-meta">
                                    <span class="card-location">{{ hotel.city }} {{ hotel.address }}</span>
                                    <span class="card-price">¥{{ hotel.price }}/晚</span>
                                </div>
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
                        <div class="left">
                            <div class="status-ind"></div>
                        </div>
                        <div class="right">
                            <div class="text-wrap">
                                <h3 class="card-title">{{ food.name }}</h3>
                                <p class="card-description">{{ food.description.substring(0, 100) }}...</p>
                                <div class="card-meta">
                                    <span class="card-location">{{ food.city }} {{ food.restaurant }}</span>
                                    <span class="card-price">¥{{ food.price }}</span>
                                </div>
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
                        <div class="left">
                            <div class="status-ind"></div>
                        </div>
                        <div class="right">
                            <div class="text-wrap">
                                <h3 class="card-title">{{ attraction.name }}</h3>
                                <p class="card-description">{{ attraction.description.substring(0, 100) }}...</p>
                                <div class="card-meta">
                                    <span class="card-location">{{ attraction.city }} {{ attraction.address }}</span>
                                    <span class="card-price">¥{{ attraction.ticketPrice || 0 }}</span>
                                </div>
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
    padding: 40px 20px;
    margin: 0 auto;
    position: relative;
    z-index: 1;
}

.content-section {
    margin-bottom: 60px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    padding: 30px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-title {
    font-size: 26px;
    font-weight: 700;
    margin-bottom: 25px;
    color: #fff;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    border-left: 4px solid #4facfe;
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
    background: linear-gradient(90deg, #4facfe, #00f2fe);
    border-radius: 1px;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(550px, 1fr));
    gap: 24px;
}

.card {
    width: 100%;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    display: flex;
    transition: transform 0.3s ease, box-shadow 0.3s ease, background 0.3s ease;
    padding: 20px;
    cursor: pointer;
}

.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
    background: rgba(255, 255, 255, 0.15);
    border-color: rgba(255, 255, 255, 0.3);
}

.card-image {
    height: 250px;
    flex-shrink: 0;
}

.card-image img {
    max-width: 300px;
    height: 100%;
    border-radius: 12px;
    transition: transform 0.5s ease;
    object-fit: cover;
}

.card:hover .card-image img {
    transform: scale(1.05);
}

.container {
    margin-left: 20px;
    display: flex;
    flex-direction: row;
    gap: 12px;
    flex: 1;
}

.status-ind {
    width: 8px;
    height: 8px;
    background-color: #ff4757;
    margin: 6px 0;
    border-radius: 50%;
    flex-shrink: 0;
}

.text-wrap {
    display: flex;
    flex-direction: column;
    gap: 8px;
    color: #fff;
    flex: 1;
}

.card-title {
    font-size: 18px;
    font-weight: 600;
    margin: 0;
    color: #fff;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    line-height: 1.3;
}

.card-description {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.8);
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
    color: rgba(255, 255, 255, 0.7);
    margin-top: 8px;
}

.card-location, .card-price {
    display: flex;
    align-items: center;
}

.button-wrap {
    display: flex;
    flex-direction: row;
    gap: 16px;
    align-items: center;
}

.secondary-cta {
    background-color: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    font-size: 14px;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.8);
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 16px;
    transition: all 0.3s ease;
}

.secondary-cta:hover {
    background-color: rgba(255, 255, 255, 0.2);
    color: #fff;
    text-decoration: none;
}

.primary-cta {
    font-size: 14px;
    background: linear-gradient(135deg, #4facfe, #00f2fe);
    font-weight: 600;
    color: #fff;
    border: none;
    border-radius: 16px;
    cursor: pointer;
    padding: 8px 16px;
    transition: all 0.3s ease;
}

.primary-cta:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
    text-decoration: none;
}

.right {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-end;
    gap: 14px;
}

.left {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.loading {
    text-align: center;
    padding: 40px;
    color: rgba(255, 255, 255, 0.8);
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255, 255, 255, 0.1);
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4757;
    background: rgba(255, 71, 87, 0.1);
    border-radius: 12px;
    backdrop-filter: blur(5px);
    border: 1px solid rgba(255, 71, 87, 0.2);
}

@media (max-width: 768px) {
    .content-container {
        padding: 20px 15px;
    }
    
    .content-section {
        padding: 20px;
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
        padding: 16px;
    }
    
    .card-image {
        height: 200px;
        width: 100%;
    }
    
    .card-image img {
        max-width: 100%;
    }
    
    .container {
        margin-left: 0;
        margin-top: 16px;
    }
    
    .card-title {
        font-size: 16px;
    }
    
    .button-wrap {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }
    
    .primary-cta,
    .secondary-cta {
        width: 100%;
        text-align: center;
    }
}
</style>