<template>
    <div class="content-container">
        <!-- 新闻卡片 -->
        <section class="content-section">
            <h2 class="section-title">热门新闻</h2>
            <div v-if="loading.news" class="loading">加载中...</div>
            <div v-else-if="error.news" class="error">{{ error.news }}</div>
            <div v-else class="card-grid">
                <div v-for="news in newsList" :key="news.id" class="card">
                    <div class="card-image">
                        <img :src="news.coverImage" alt="新闻图片">
                    </div>
                    <div class="container">
                        <div class="left">
                            <div class="status-ind"></div>
                        </div>
                        <div class="right">
                            <div class="text-wrap">
                                <h3 class="card-title">{{ news.title }}</h3>
                                <p class="card-description">{{ news.content.substring(0, 100) }}...</p>
                                <div class="card-meta">
                                    <span class="card-date">{{ formatDate(news.createTime) }}</span>
                                    <span class="card-view">{{ news.viewCount || 0 }} 阅读</span>
                                </div>
                            </div>
                            <div class="button-wrap">
                                <button class="primary-cta" @click="viewNewsDetail(news.id)">查看详情</button>
                                <button class="secondary-cta">标记为已读</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

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
import { getHotNews, getNewsDetail } from '@/api/news.js';
import { getRecommendHotels, getHotelDetail } from '@/api/hotel.js';
import { getRecommendFoods, getFoodDetail } from '@/api/food.js';
import { getHotAttractions, getAttractionDetail } from '@/api/attraction.js';
import { ElMessage } from 'element-plus';
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

// 获取新闻数据
const fetchNews = async () => {
    loading.value.news = true;
    error.value.news = '';
    try {
        const response = await getHotNews({ limit: 6 });
        if (response.code === 200 && response.data) {
            newsList.value = response.data;
        } else {
            error.value.news = '获取新闻失败';
        }
    } catch (err) {
        error.value.news = '网络错误，请稍后重试';
        console.error('获取新闻失败:', err);
    } finally {
        loading.value.news = false;
    }
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
    fetchNews();
    fetchHotels();
    fetchFoods();
    fetchAttractions();
});
</script>

<style scoped>
.content-container {
    padding: 20px;
    min-width: 1200px;
    margin: 0 auto;
}

.content-section {
    margin-bottom: 40px;
}

.section-title {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 20px;
    color: #333;
    border-left: 4px solid #4facfe;
    padding-left: 12px;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(550px, 1fr));
    gap: 20px;
}

.card {
    width: 100%;
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.card-image {
    height: 250px;
}

.card-image img {
    height: 100%;
    transition: transform 0.5s ease;
}

.card:hover .card-image img {
    transform: scale(1.05);
}

.card-content {
    padding: 16px;
}

.card-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 10px;
    color: #333;
    line-height: 1.3;
}

.card-description {
    font-size: 14px;
    color: #666;
    margin-bottom: 12px;
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
    color: #999;
}

.card-date, .card-view, .card-location, .card-price {
    display: flex;
    align-items: center;
}

/* 新卡片模板样式 */
.card {
    width: 100%;
    background-color: #f2f3f7;
    border-radius: 0.75em;
    cursor: pointer;
    transition: ease 0.2s;
    box-shadow: 1em 1em 1em #d8dae0b1, -0.75em -0.75em 1em #ffffff;
    border: 1.5px solid #f2f3f7;
    overflow: hidden;
}

.card:hover {
    background-color: #d3ddf1;
    border: 1.5px solid #1677ff;
}

.container {
    margin-left: 1.375em;
    display: flex;
    flex-direction: row;
    gap: 0.75em;
}

.status-ind {
    width: 0.625em;
    height: 0.625em;
    background-color: #ff0000;
    margin: 0.375em 0;
    border-radius: 0.5em;
}

.text-wrap {
    display: flex;
    flex-direction: column;
    gap: 0.25em;
    color: #333;
}

.time {
    font-size: 0.875em;
    color: #777;
}

.text-link {
    font-weight: 500;
    text-decoration: none;
    color: black;
}

.button-wrap {
    display: flex;
    flex-direction: row;
    gap: 1em;
    align-items: center;
}

.secondary-cta {
    background-color: transparent;
    border: none;
    font-size: 15px;
    font-weight: 400;
    color: #666;
    cursor: pointer;
}

.primary-cta {
    font-size: 15px;
    background-color: transparent;
    font-weight: 600;
    color: #1677ff;
    border: none;
    border-radius: 1.5em;
    cursor: pointer;
}

button:hover {
    text-decoration: underline;
}

.right {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-end;
    gap: 0.875em;
}

.left {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.loading {
    text-align: center;
    padding: 40px;
    color: #666;
}

.error {
    text-align: center;
    padding: 40px;
    color: #ff4d4f;
}

@media (max-width: 768px) {
    .card-grid {
        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    }
    
    .section-title {
        font-size: 20px;
    }
    
    .card-content {
        padding: 12px;
    }
    
    .card-title {
        font-size: 16px;
    }
}
</style>