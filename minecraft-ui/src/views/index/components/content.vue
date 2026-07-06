<template>
    <div class="content-container">
        <div class="tab-container">
            <button 
                v-for="section in sections" 
                :key="section.type"
                class="tab-btn"
                :class="{ active: activeSection === section.type }"
                @click="switchSection(section.type)"
            >
                {{ section.title }}
            </button>
        </div>
        
        <section v-if="currentSection" class="content-section">
            <div class="section-header">
                <h2 class="section-title">{{ currentSection.title }}</h2>
                <span class="section-subtitle">{{ currentSection.subtitle }}</span>
            </div>
            
            <div v-if="loading[currentSection.type]" class="loading-state">
                <div class="loading-spinner"></div>
                <span class="loading-text">探索中...</span>
            </div>
            
            <div v-else-if="error[currentSection.type]" class="error-state">
                <span class="error-text">{{ error[currentSection.type] }}</span>
                <button class="retry-btn" @click="retryFetch(currentSection.type)">重新加载</button>
            </div>
            
            <div v-else class="card-grid">
                <div 
                    v-for="item in getItemList(currentSection.type)" 
                    :key="item.id" 
                    class="card"
                    @mouseenter="onCardHover(item.id, true)"
                    @mouseleave="onCardHover(item.id, false)"
                >
                    <div class="card-image-wrapper">
                        <img :src="item.coverImage" :alt="getImageAlt(currentSection.type)" class="card-image">
                        <div class="card-overlay"></div>
                        <div class="card-category">{{ getCategory(item, currentSection.type) }}</div>
                    </div>
                    
                    <div class="card-content">
                        <h3 class="card-title">{{ item.name }}</h3>
                        <p class="card-description">{{ item.description.substring(0, 100) }}...</p>
                        
                        <div class="card-meta">
                            <span class="card-location">{{ item.city }} · {{ getLocation(item, currentSection.type) }}</span>
                            <span class="card-price">{{ getPrice(item, currentSection.type) }}</span>
                        </div>
                        
                        <div class="card-actions">
                            <button class="primary-cta" @click="handleViewDetail(currentSection.type, item.id)">
                                查看详情
                                <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M5 12h14M12 5l7 7-7 7"/>
                                </svg>
                            </button>
                            <button class="secondary-cta" @click="toggleFavorite(item.id)">
                                <svg class="heart-icon" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { getRecommendHotels } from '@/api/hotel.js';
import { getRecommendFoods } from '@/api/food.js';
import { getHotAttractions } from '@/api/attraction.js';

const emit = defineEmits(['open-detail']);

const sectionsData = [
    { type: 'hotel', title: '推荐酒店', subtitle: '精选住宿体验' },
    { type: 'food', title: '特色美食', subtitle: '味蕾的环球之旅' },
    { type: 'attraction', title: '热门景点', subtitle: '探索世界奇观' }
];

const activeSection = ref('hotel');
const loadedSections = reactive(new Set());

const sections = computed(() => {
    return sectionsData.filter(item => item && item.type);
});

const currentSection = computed(() => {
    return sections.value.find(item => item && item.type === activeSection.value);
});

const hotelList = ref([]);
const foodList = ref([]);
const attractionList = ref([]);

const loading = ref({
    hotel: false,
    food: false,
    attraction: false
});

const error = ref({
    hotel: '',
    food: '',
    attraction: ''
});

const hoveredCards = ref(new Set());

const getItemList = (type) => {
    const lists = {
        hotel: hotelList.value,
        food: foodList.value,
        attraction: attractionList.value
    };
    return lists[type] || [];
};

const getImageAlt = (type) => {
    const alts = {
        hotel: '酒店图片',
        food: '美食图片',
        attraction: '景点图片'
    };
    return alts[type] || '';
};

const getCategory = (item, type) => {
    if (type === 'hotel') return item.starRating ? `${item.starRating}星级` : '精选';
    if (type === 'food') return item.category || '特色';
    if (type === 'attraction') return item.category || '热门';
    return '精选';
};

const getLocation = (item, type) => {
    if (type === 'hotel') return item.address || '';
    if (type === 'food') return item.restaurant || '';
    if (type === 'attraction') return item.address || '';
    return '';
};

const getPrice = (item, type) => {
    if (type === 'hotel') return `¥${item.price}/晚`;
    if (type === 'food') return `¥${item.price}`;
    if (type === 'attraction') return `¥${item.ticketPrice || 0}`;
    return '';
};

const onCardHover = (id, isHovered) => {
    if (isHovered) {
        hoveredCards.value.add(id);
    } else {
        hoveredCards.value.delete(id);
    }
};

const toggleFavorite = (id) => {
    console.log('Toggle favorite:', id);
};

const retryFetch = (type) => {
    const fetchers = {
        hotel: fetchHotels,
        food: fetchFoods,
        attraction: fetchAttractions
    };
    if (fetchers[type]) {
        fetchers[type]();
    }
};

const switchSection = (type) => {
    activeSection.value = type;
    if (!loadedSections.has(type)) {
        retryFetch(type);
        loadedSections.add(type);
    }
};

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

const handleViewDetail = (type, id) => {
    emit('open-detail', type, id);
};

onMounted(() => {
    switchSection('hotel');
});
</script>

<style scoped>
.content-container {
    width: 100%;
}

.tab-container {
    width: 100%;
    display: flex;
    gap: 16px;
    justify-content: center;
    margin-bottom: 48px;
    background: #f5f5f7;
    padding: 8px;
    border-radius: 24px;
}

.tab-btn {
    padding: 14px 32px;
    background: transparent;
    border: none;
    border-radius: 20px;
    font-size: 16px;
    font-weight: 500;
    color: #6e6e73;
    cursor: pointer;
    transition: all 0.3s ease;
    font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'SF Pro Display', sans-serif;
}

.tab-btn:hover {
    color: #1d1d1f;
}

.tab-btn.active {
    background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
    color: #ffffff;
    box-shadow: 0 4px 15px rgba(0, 122, 255, 0.3);
}

.content-section {
    margin-bottom: 100px;
    animation: fadeInUp 0.6s ease-out forwards;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.section-header {
    margin-bottom: 40px;
}

.section-title {
    font-size: 48px;
    font-weight: 700;
    color: #000000;
    margin: 0;
    letter-spacing: -0.02em;
    font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'SF Pro Display', sans-serif;
}

.section-subtitle {
    display: block;
    font-size: 18px;
    color: #6e6e73;
    margin-top: 8px;
    font-weight: 400;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 40px;
}

.card {
    background: #ffffff;
    border-radius: 32px;
    overflow: hidden;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.06);
    transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94), box-shadow 0.5s ease;
    cursor: pointer;
}

.card:hover {
    transform: translateY(-8px) scale(1.01);
    box-shadow: 0 25px 60px rgba(0, 0, 0, 0.12);
}

.card-image-wrapper {
    position: relative;
    height: 280px;
    overflow: hidden;
}

.card-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.card:hover .card-image {
    transform: scale(1.08);
}

.card-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, transparent 40%, rgba(0, 0, 0, 0.3) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
}

.card:hover .card-overlay {
    opacity: 1;
}

.card-category {
    position: absolute;
    top: 20px;
    right: 20px;
    padding: 8px 16px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
    color: #1d1d1f;
    letter-spacing: 0.05em;
    text-transform: uppercase;
    opacity: 0;
    transform: translateY(-10px);
    transition: all 0.3s ease;
}

.card:hover .card-category {
    opacity: 1;
    transform: translateY(0);
}

.card-content {
    padding: 28px;
}

.card-title {
    font-size: 24px;
    font-weight: 600;
    color: #000000;
    margin: 0 0 12px;
    line-height: 1.25;
    font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'SF Pro Display', sans-serif;
}

.card-description {
    font-size: 16px;
    color: #6e6e73;
    margin: 0 0 20px;
    line-height: 1.6;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.card-location {
    font-size: 14px;
    color: #86868b;
    display: flex;
    align-items: center;
    gap: 4px;
}

.card-price {
    font-size: 20px;
    font-weight: 600;
    background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.card-actions {
    display: flex;
    gap: 16px;
}

.primary-cta {
    flex: 1;
    padding: 14px 24px;
    background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
    color: #ffffff;
    border: none;
    border-radius: 16px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(0, 122, 255, 0.3);
}

.primary-cta:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 122, 255, 0.4);
}

.primary-cta:active {
    transform: translateY(0);
}

.cta-arrow {
    width: 18px;
    height: 18px;
    transition: transform 0.3s ease;
}

.primary-cta:hover .cta-arrow {
    transform: translateX(4px);
}

.secondary-cta {
    width: 48px;
    height: 48px;
    padding: 0;
    background: #f5f5f7;
    color: #86868b;
    border: none;
    border-radius: 16px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
}

.secondary-cta:hover {
    background: #e8e8ed;
    color: #ff3b30;
    transform: scale(1.05);
}

.heart-icon {
    width: 20px;
    height: 20px;
    transition: transform 0.3s ease;
}

.secondary-cta:hover .heart-icon {
    transform: scale(1.1);
}

.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 0;
    background: #f5f5f7;
    border-radius: 32px;
}

.loading-spinner {
    width: 48px;
    height: 48px;
    border: 3px solid #e8e8ed;
    border-top-color: #007aff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.loading-text {
    margin-top: 20px;
    font-size: 16px;
    color: #6e6e73;
}

.error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 0;
    background: #fef2f2;
    border-radius: 32px;
}

.error-text {
    font-size: 16px;
    color: #ff3b30;
    margin-bottom: 16px;
}

.retry-btn {
    padding: 12px 32px;
    background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
    color: #ffffff;
    border: none;
    border-radius: 20px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
}

.retry-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(0, 122, 255, 0.3);
}

@media (max-width: 768px) {
    .content-container {
        padding: 0 40px;
    }
    
    .content-section {
        margin-bottom: 60px;
    }
    
    .section-title {
        font-size: 32px;
    }
    
    .section-subtitle {
        font-size: 16px;
    }
    
    .card-grid {
        grid-template-columns: 1fr;
        gap: 28px;
    }
    
    .card-image-wrapper {
        height: 240px;
    }
    
    .card-content {
        padding: 24px;
    }
    
    .card-title {
        font-size: 20px;
    }
    
    .card-description {
        font-size: 15px;
    }
    
    .card-actions {
        gap: 12px;
    }
    
    .primary-cta {
        padding: 12px 20px;
        font-size: 14px;
    }
    
    .secondary-cta {
        width: 44px;
        height: 44px;
    }
}

@media (max-width: 375px) {
    .content-container {
        padding: 0 24px;
    }
    
    .content-section {
        margin-bottom: 48px;
    }
    
    .section-title {
        font-size: 28px;
    }
    
    .card-image-wrapper {
        height: 200px;
    }
    
    .card-content {
        padding: 20px;
    }
    
    .card-title {
        font-size: 18px;
        margin-bottom: 10px;
    }
    
    .card-description {
        font-size: 14px;
        margin-bottom: 16px;
    }
    
    .card-meta {
        margin-bottom: 20px;
    }
    
    .card-price {
        font-size: 18px;
    }
    
    .primary-cta {
        padding: 12px;
        font-size: 14px;
    }
    
    .secondary-cta {
        width: 40px;
        height: 40px;
    }
    
    .heart-icon {
        width: 18px;
        height: 18px;
    }
}
</style>
