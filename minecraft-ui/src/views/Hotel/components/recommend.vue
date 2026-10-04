<template>
    <div class="booking">
        <header class="header">
            <div class="bg"></div>
            <div class="blob"></div>
            <div class="header-content">
                <h1>订酒店</h1>
                <div class="search-bar">
                    <input type="text" placeholder="出行目的地" v-model="search.destination" />
                    <input type="date" placeholder="入住日期" v-model="search.checkIn" />
                    <input type="date" placeholder="离店日期" v-model="search.checkOut" />
                    <input type="number" placeholder="人数" v-model="search.numberOfPeople" />
                    <button @click="searchHotels">搜索</button>
                </div>
            </div>

        </header>
        <section class="features">
            <div class="feature" v-for="feature in features" :key="feature.title">
                <h2>{{ feature.title }}</h2>
                <p>{{ feature.description }}</p>
            </div>
        </section>
        <section class="themed-hotels">
            <div class="section-header">
                <h2>主题住宿</h2>
                <div class="theme-tabs">
                    <button v-for="tab in tabs" :key="tab.id" :class="{ active: activeTab === tab.id }"
                        @click="activeTab = tab.id">
                        {{ tab.label }}
                    </button>
                </div>
            </div>

            <div class="list" v-loading="loading">
                <div class="item-list" v-for="hotel in filteredHotels" :key="hotel.itemId"
                    :data-theme="getHotelTheme(hotel)">
                    <div class="image">
                        <img :src="hotel.image" :alt="hotel.name" />
                        <span class="pin-badge" v-if="hotel.ruleType === 'PIN'">置顶推荐</span>
                        <span class="theme-badge">{{ getHotelThemeLabel(hotel) }}</span>
                    </div>
                    <div class="info">
                        <h3>{{ hotel.name }}</h3>
                        <p class="location">{{ formatLocation(hotel) }}</p>
                        <div class="facilities" v-if="(hotel.tags || []).length">
                            <span v-for="facility in (hotel.tags || []).slice(0, 3)" :key="facility">{{ facility }}</span>
                        </div>
                        <div class="meta">
                            <span class="price">¥{{ hotel.price }}<small>/晚</small></span>
                            <span class="rating" v-if="hotel.rating">★ {{ displayRating(hotel.rating) }}</span>
                        </div>
                    </div>
                </div>
                <div class="empty-tip" v-if="!loading && filteredHotels.length === 0">
                    暂无符合条件的酒店推荐
                </div>
            </div>
        </section>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getClientRecommendations } from '@/api/clientRecommendation';

const search = ref({
    destination: '',
    checkIn: '',
    checkOut: '',
    numberOfPeople: ''
});

const loading = ref(false)

const activeTab = ref('all')

const tabs = [
    { id: 'all', label: '全部' },
    { id: 'luxury', label: '奢华酒店' },
    { id: 'boutique', label: '精品设计' },
    { id: 'resort', label: '度假村' },
    { id: 'business', label: '商务酒店' }
]

// 主题关键词（基于酒店名称与设施标签匹配）
const themeKeywords = {
    resort: ['度假', '沙滩', '水上乐园', '温泉'],
    business: ['会议', '商务'],
    boutique: ['精品', '设计', '艺术', '博舍', '民宿', '客栈']
}

const features = [
    {
        title: '价格保障',
        description: '预订后降价，双倍差价赔付'
    },
    {
        title: '免费取消',
        description: '多数酒店可免费取消'
    },
    {
        title: '会员特权',
        description: '会员专享折扣及礼遇'
    },
    {
        title: '住宿攻略',
        description: '区域攻略到特色主题，应有尽有'
    },
    {
        title: '专享价格',
        description: '多平台价格对比，天天专享特惠'
    },
    {
        title: '真实点评',
        description: '超过100万真实用户点评和游记'
    }
]

// 推荐酒店数据（来自推荐接口）
const hotels = ref([])

// 加载推荐酒店
async function loadRecommendations() {
    loading.value = true
    try {
        const city = search.value.destination?.trim()
        const res = await getClientRecommendations('hotel', {
            limit: 50,
            city: city || undefined
        })
        hotels.value = res.data || []
    } catch (e) {
        ElMessage.error('酒店推荐加载失败，请稍后重试')
        hotels.value = []
    } finally {
        loading.value = false
    }
}

// 识别酒店主题
function getHotelTheme(hotel) {
    const text = `${hotel.name || ''} ${(hotel.tags || []).join(' ')}`
    for (const id of ['resort', 'business', 'boutique']) {
        if (themeKeywords[id].some(k => text.includes(k))) return id
    }
    // 五星级及以上归为奢华酒店
    const star = Number(hotel.subType)
    if (star >= 5) return 'luxury'
    return ''
}

// 主题标签文案
function getHotelThemeLabel(hotel) {
    const theme = getHotelTheme(hotel)
    const tab = tabs.find(t => t.id === theme)
    if (tab) return tab.label
    return hotel.subType ? `${hotel.subType}星酒店` : '酒店'
}

const filteredHotels = computed(() => {
    if (activeTab.value === 'all') return hotels.value
    return hotels.value.filter(hotel => getHotelTheme(hotel) === activeTab.value)
})

// 地点展示（省市重复时只展示一个）
function formatLocation(item) {
    if (item.province && item.city && item.province !== item.city && !item.province.includes(item.city)) {
        return `${item.province} · ${item.city}`
    }
    return item.city || item.province || ''
}

// 评分兼容 5 分制与百分制两种历史数据
function displayRating(rating) {
    if (rating === null || rating === undefined) return ''
    return rating > 5 ? (rating / 20).toFixed(1) : rating
}

// 搜索酒店（按目的地城市重新请求推荐）
function searchHotels() {
    activeTab.value = 'all'
    loadRecommendations()
}

onMounted(loadRecommendations)
</script>

<style scoped>
.booking {
    padding: 0 15px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
    color: #333;
}

/* 头部样式 */
.header {
    position: relative;
    height: 300px;
    border-radius: 14px;
    margin: 24px 0 24px 0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.header h1 {
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
    background: linear-gradient(135deg, #ee7f43, #377ec9);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    display: inline-block;
}

.bg {
    position: absolute;
    top: 5px;
    left: 5px;
    width: calc(100% - 10px);
    height: 290px;
    z-index: 2;
    background: rgba(255, 255, 255, .95);
    backdrop-filter: blur(24px);
    border-radius: 10px;
    overflow: hidden;
    outline: 2px solid white;
}

.blob {
    position: absolute;
    z-index: 1;
    top: 50%;
    left: 50%;
    width: 110%;
    height: 220px;
    border-radius: 50%;
    background: linear-gradient(135deg, #377ec9, #7b5cff);
    opacity: 0.55;
    filter: blur(48px);
    animation: blob-bounce 5s infinite ease;
}

@keyframes blob-bounce {
    0% {
        transform: translate(-100%, -100%) translate3d(0, 0, 0);
    }

    25% {
        transform: translate(-100%, -100%) translate3d(100%, 0, 0);
    }

    50% {
        transform: translate(-100%, -100%) translate3d(100%, 100%, 0);
    }

    75% {
        transform: translate(-100%, -100%) translate3d(0, 100%, 0);
    }

    100% {
        transform: translate(-100%, -100%) translate3d(0, 0, 0);
    }
}

.header-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    z-index: 3;
}

/* 搜索栏样式 */
.search-bar {
    flex-wrap: wrap;
    display: flex;
    flex-direction: row;
    gap: 1rem;
    background: white;
    padding: 1.5rem;
    margin: 1rem;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.search-bar input {
    padding: 0.8rem 1.2rem;
    border: 1px solid rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    font-size: 1rem;
    transition: all 0.3s ease;
}

.search-bar input:focus {
    outline: none;
    border-color: #4361ee;
    box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.2);
}

.search-bar button {
    background: linear-gradient(135deg, #4361ee, #3f37c9);
    color: white;
    border: none;
    border-radius: 8px;
    padding: 0.8rem;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.search-bar button:hover {
    background: linear-gradient(135deg, #3f37c9, #4361ee);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(67, 97, 238, 0.3);
}

/* 特色服务样式 */
.features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 2rem;
    margin-bottom: 3rem;
}

.feature {
    position: relative;
    background: white;
    padding: 1.5rem;
    border-radius: 14px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    overflow: hidden;
}

.feature::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4361ee, transparent);
    opacity: 0;
    transition: opacity 0.3s ease;
}

.feature:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 24px rgba(67, 97, 238, 0.14);
}

.feature:hover::before {
    opacity: 1;
}

.feature h2 {
    color: #4361ee;
    margin-bottom: 0.5rem;
    font-size: 1.2rem;
}

.feature p {
    color: #adb5bd;
    line-height: 1.6;
}

/* 主题酒店样式 */
.themed-hotels {
    margin-top: 3rem;
}

.themed-hotels h2 {
    font-size: 1.8rem;
    margin-bottom: 1.5rem;
    color: #1e1e24;
    position: relative;
    display: inline-block;
}

.themed-hotels h2::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 0;
    width: 50%;
    height: 4px;
    background: linear-gradient(90deg, #4361ee, transparent);
    border-radius: 2px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
    flex-wrap: wrap;
    gap: 1rem;
}

.theme-tabs {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
}

.theme-tabs button {
    padding: 0.5rem 1rem;
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 20px;
    font-size: 0.9rem;
    cursor: pointer;
    transition: all 0.2s ease;
}

.theme-tabs button:hover {
    background: #f5f5f5;
}

.theme-tabs button.active {
    background: linear-gradient(135deg, #4361ee, #3f37c9);
    color: white;
    border-color: transparent;
    box-shadow: 0 4px 12px rgba(67, 97, 238, 0.3);
}

.item {
    background: white;
    border-radius: 12px;
    width: 100%;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s ease;
    padding: 10px;
}

.image {
    position: relative;
    overflow: hidden;
    border-radius: 12px 12px 0 0;
    height: 180px;
}

.image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.item:hover .image img {
    transform: scale(1.05);
}

.theme-badge {
    position: absolute;
    bottom: 10px;
    left: 10px;
    background: rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(6px);
    color: white;
    padding: 0.25rem 0.75rem;
    border-radius: 999px;
    font-size: 0.75rem;
}

.info {
    padding: 1rem;
    background: white;
}

.meta {
    display: flex;
    justify-content: space-between;
    margin-top: 0.5rem;
    font-size: 0.9rem;
}

.price {
    color: #4361ee;
    font-weight: bold;
}

.rating {
    color: #ffb400;
}

.price small {
    color: #adb5bd;
    font-weight: normal;
    font-size: 0.75rem;
}

.pin-badge {
    position: absolute;
    top: 10px;
    left: 10px;
    background: linear-gradient(135deg, #ff6b6b, #ee5a24);
    color: white;
    padding: 0.25rem 0.75rem;
    border-radius: 12px;
    font-size: 0.75rem;
    z-index: 2;
}

.empty-tip {
    grid-column: 1 / -1;
    text-align: center;
    color: #adb5bd;
    padding: 3rem 0;
    font-size: 0.95rem;
}

.list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
}

.item-list {
    background: white;
    border-radius: 14px;
    overflow: hidden;
    padding: 10px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.07);
    transition: all 0.3s ease;
    cursor: pointer;
}

.item-list:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(67, 97, 238, 0.16);
}

.item-list img {
    width: 100%;
    height: 180px;
    object-fit: cover;
    border-radius: 10px;
    transition: transform 0.3s ease;
}

.item-list:hover img {
    transform: scale(1.04);
}

.item-list h3 {
    padding: 1rem 0.5rem 0.25rem;
    font-size: 1.1rem;
    color: #1e1e24;
}

.item-list .location {
    padding: 0 0.5rem;
}

.facilities {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    padding: 0.5rem 0.5rem 0.75rem;
}

.facilities span {
    background: rgba(67, 97, 238, 0.07);
    color: #4361ee;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 12px;
}

.item-list .meta {
    padding: 0 0.5rem 0.75rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .search-bar {
        flex-direction: column;
    }

    .search-bar button {
        grid-column: auto;
    }

    header h1 {
        font-size: 2rem;
    }

    .features {
        grid-template-columns: 1fr;
    }

    .section-header {
        flex-direction: column;
        align-items: flex-start;
    }

    .theme-tabs {
        width: 100%;
        overflow-x: auto;
        padding-bottom: 0.5rem;
    }
}
</style>