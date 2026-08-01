<template>
    <div class="content-container">
        <section class="section-container">
            <div class="section-header">
                <h2 class="section-title">{{ currentSection.title }}</h2>
                <span class="section-subtitle">{{ currentSection.subtitle }}</span>
            </div>
            <div class="tab-container">
                <button v-for="section in sections" :key="section.type" class="tab-btn"
                    :class="{ active: activeSection === section.type }" @click="switchSection(section.type)">
                    {{ section.title }}
                </button>
            </div>
        </section>

        <section v-if="currentSection" class="content-section">
            <div v-if="loading[currentSection.type]" class="loading-state">
                <div class="loading-spinner"></div>
                <span class="loading-text">探索中...</span>
            </div>

            <div v-else-if="error[currentSection.type]" class="error-state">
                <span class="error-text">{{ error[currentSection.type] }}</span>
                <button class="retry-btn" @click="retryFetch(currentSection.type)">重新加载</button>
            </div>

            <div v-else class="card-grid">
                <div v-for="(item, index) in displayedItems" :key="item.id" class="card"
                    :class="{ 'card--expanded': isExpanded && index >= DEFAULT_DISPLAY_COUNT }"
                    :style="{ animationDelay: `${Math.min(index, DEFAULT_DISPLAY_COUNT) * 0.05}s` }"
                    @mouseenter="onCardHover(item.id, true)" @mouseleave="onCardHover(item.id, false)">
                    <div class="card-image-wrapper">
                        <img :src="item.coverImage" :alt="getImageAlt(currentSection.type)" class="card-image">
                        <div class="card-overlay"></div>
                        <div class="card-category">{{ getCategory(item, currentSection.type) }}</div>
                    </div>

                    <div class="card-content">
                        <h3 class="card-title">{{ item.name }}</h3>
                        <p class="card-description">{{ item.description.substring(0, 100) }}...</p>

                        <div class="card-meta">
                            <span class="card-location">{{ item.city }} · {{ getLocation(item, currentSection.type)
                                }}</span>
                            <span class="card-price">{{ getPrice(item, currentSection.type) }}</span>
                        </div>

                        <div class="card-actions">
                            <button class="primary-cta" @click="handleViewDetail(currentSection.type, item.id)">
                                查看详情
                                <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2">
                                    <path d="M5 12h14M12 5l7 7-7 7" />
                                </svg>
                            </button>
                            <button class="secondary-cta" @click="toggleFavorite(item.id)">
                                <svg class="heart-icon" viewBox="0 0 24 24" fill="currentColor">
                                    <path
                                        d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="hasMoreData" class="expand-actions">
                <button class="expand-btn" @click="toggleExpand">
                    <span>{{ isExpanded ? '收起' : '展开全部' }}</span>
                    <svg
                        class="expand-arrow"
                        :class="{ 'expand-arrow--up': isExpanded }"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                    >
                        <polyline points="6 9 12 15 18 9" />
                    </svg>
                </button>
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

// 默认展示的数据条数
const DEFAULT_DISPLAY_COUNT = 4;

const sectionsData = [
    { type: 'hotel', title: '推荐酒店', subtitle: '精选住宿体验' },
    { type: 'food', title: '特色美食', subtitle: '味蕾的环球之旅' },
    { type: 'attraction', title: '热门景点', subtitle: '探索世界奇观' }
];

const activeSection = ref('hotel');
const loadedSections = reactive(new Set());
const isExpanded = ref(false);

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

// 默认仅展示前 8 条，展开后显示全部
const displayedItems = computed(() => {
    const list = getItemList(activeSection.value);
    if (isExpanded.value || list.length <= DEFAULT_DISPLAY_COUNT) {
        return list;
    }
    return list.slice(0, DEFAULT_DISPLAY_COUNT);
});

// 当前分类是否存在折叠数据
const hasMoreData = computed(() => {
    return getItemList(activeSection.value).length > DEFAULT_DISPLAY_COUNT;
});

const toggleExpand = () => {
    isExpanded.value = !isExpanded.value;
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
    // 切换分类时重置为折叠状态
    isExpanded.value = false;
    if (!loadedSections.has(type)) {
        retryFetch(type);
        loadedSections.add(type);
    }
};

const fetchHotels = async () => {
    loading.value.hotel = true;
    error.value.hotel = '';
    try {
        const response = await getRecommendHotels(null, 20);
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
        const response = await getRecommendFoods(null, 20);
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
        const response = await getHotAttractions(20);
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

.section-container {
    width: 100%;
    display: flex;
    flex-direction: column;
}

.tab-container {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    min-height: 128px;
    margin-bottom: 48px;
    padding: 12px;
    background: #f5f5f7;
    border-radius: 24px;
}

.tab-btn {
    padding: 18px 32px;
    background: #ffffff;
    border: none;
    border-radius: 24px;
    font-size: 15px;
    font-weight: 500;
    color: #0c0c0c;
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
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 40px;
}

.card {
    background: #ffffff;
    border-radius: 32px;
    overflow: hidden;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.06);
    transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94), box-shadow 0.5s ease;
    cursor: pointer;
    opacity: 0;
    display: flex;
    flex-direction: column;
    animation: fadeInUp 0.5s ease-out forwards;
}

.card:hover {
    transform: translateY(-8px) scale(1.01);
    box-shadow: 0 25px 60px rgba(0, 0, 0, 0.12);
}

/* 展开时新增卡片使用更轻量的淡入动画 */
.card--expanded {
    animation: fadeInExpand 0.5s ease-out forwards;
}

@keyframes fadeInExpand {
    from {
        opacity: 0;
        transform: translateY(20px) scale(0.98);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.expand-actions {
    display: flex;
    justify-content: center;
    margin-top: 48px;
}

.expand-btn {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    padding: 14px 40px;
    background: #ffffff;
    border: 1px solid #d2d2d6;
    border-radius: 980px;
    font-size: 15px;
    font-weight: 500;
    color: #1d1d1f;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.expand-btn:hover {
    background: #f5f5f7;
    border-color: #007aff;
    color: #007aff;
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 122, 255, 0.12);
}

.expand-btn:active {
    transform: translateY(0);
}

.expand-arrow {
    width: 18px;
    height: 18px;
    transition: transform 0.3s ease;
}

.expand-arrow--up {
    transform: rotate(180deg);
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
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 28px;
}

.card-title {
    font-size: 24px;
    font-weight: 600;
    color: #000000;
    margin: 0 0 12px;
    line-height: 1.25;
    min-height: 60px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'SF Pro Display', sans-serif;
}

.card-description {
    font-size: 16px;
    color: #6e6e73;
    margin: 0 0 20px;
    line-height: 1.6;
    min-height: 52px;
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
    justify-content: space-between;
    margin-top: auto;
}

.primary-cta {
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

    .tab-container {
        min-height: 80px;
        padding: 10px;
    }

    .card-grid {
        grid-template-columns: 1fr;
        gap: 28px;
    }

    .expand-actions {
        margin-top: 32px;
    }

    .expand-btn {
        padding: 12px 32px;
        font-size: 14px;
    }

    .card-image-wrapper {
        height: 240px;
    }

    .card-content {
        padding: 24px;
    }

    .card-title {
        font-size: 20px;
        min-height: 50px;
    }

    .card-description {
        font-size: 15px;
        min-height: 48px;
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

    .tab-container {
        min-height: 64px;
        padding: 8px;
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
        min-height: 45px;
    }

    .card-description {
        font-size: 14px;
        margin-bottom: 16px;
        min-height: 45px;
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

    .expand-actions {
        margin-top: 24px;
    }

    .expand-btn {
        padding: 10px 24px;
        font-size: 13px;
    }
}
</style>
