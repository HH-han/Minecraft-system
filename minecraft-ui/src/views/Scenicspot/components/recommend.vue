<template>
    <div class="recommendation">
        <header class="header">

            <div class="bg"></div>
            <div class="blob"></div>

            <div class="header-content">
                <h1>景点推荐</h1>
                <div class="search-bar">
                    <input type="text" placeholder="输入目的地" v-model="search.destination" />
                    <input type="date" placeholder="开始日期" v-model="search.startDate" />
                    <input type="date" placeholder="结束日期" v-model="search.endDate" />
                    <input type="number" placeholder="人数" v-model="search.travelers" />
                    <button @click="searchAttractions">搜索</button>
                </div>
            </div>

        </header>

        <section class="features">
            <div class="feature" v-for="feature in features" :key="feature.title">
                <h2>{{ feature.title }}</h2>
                <p>{{ feature.description }}</p>
            </div>
        </section>

        <section class="types">
            <div class="section-header">
                <h2>精选景点</h2>
                <div class="type-tabs">
                    <button v-for="type in attractionTypes" :key="type.id"
                        :class="{ active: activeAttractionType === type.id }" @click="activeAttractionType = type.id">
                        {{ type.label }}
                    </button>
                </div>
            </div>

            <div class="list" v-loading="loading">
                <div class="item" v-for="attraction in filteredAttractions" :key="attraction.itemId">
                    <div class="image">
                        <img :src="attraction.image" :alt="attraction.name" />
                        <span class="pin-badge" v-if="attraction.ruleType === 'PIN'">置顶推荐</span>
                        <span class="type-badge">{{ getAttractionType(attraction) }}</span>
                    </div>
                    <div class="info">
                        <h3>{{ attraction.name }}</h3>
                        <p class="location">{{ formatLocation(attraction) }}</p>
                        <div class="tags">
                            <span v-for="tag in attraction.tags" :key="tag">{{ tag }}</span>
                        </div>
                        <div class="meta">
                            <span class="price" v-if="attraction.price > 0">¥{{ attraction.price }}起</span>
                            <span class="free" v-else>免费</span>
                            <span class="rating">★ {{ attraction.rating ?? '暂无评分' }}</span>
                        </div>
                    </div>
                </div>
                <div class="empty-tip" v-if="!loading && filteredAttractions.length === 0">
                    暂无符合条件的景点推荐
                </div>
            </div>
        </section>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClientRecommendations } from '@/api/clientRecommendation'

// 当前选中的景点类型
const activeAttractionType = ref('all')

// 加载状态
const loading = ref(false)

// 搜索条件
const search = ref({
    destination: '',
    startDate: '',
    endDate: '',
    travelers: 1
})

// 景点类型分类
const attractionTypes = [
    { id: 'all', label: '全部景点' },
    { id: 'nature', label: '自然风光' },
    { id: 'history', label: '历史古迹' },
    { id: 'amusement', label: '主题乐园' },
    { id: 'museum', label: '博物馆' },
    { id: 'shopping', label: '购物中心' }
]

// 各类型对应的标签关键词
const typeKeywords = {
    nature: ['自然', '风光', '山', '湖', '海', '森林', '草原', '湿地', '峡谷', '风景'],
    history: ['历史', '古迹', '古城', '文化', '遗址', '遗产', '寺', '庙', '陵', '宫', '长城'],
    amusement: ['乐园', '游乐', '主题', '迪士尼', '亲子', '欢乐'],
    museum: ['博物馆', '纪念馆', '美术馆', '展览馆'],
    shopping: ['购物', '商圈', '步行街', '商业']
}

// 特色推荐
const features = [
    {
        title: '免排队',
        description: '电子票快速入园'
    },
    {
        title: '超值套餐',
        description: '门票+交通+导游优惠组合'
    },
    {
        title: '语音导览',
        description: '多语言讲解服务'
    }
]

// 推荐景点数据（来自推荐接口）
const attractions = ref([])

// 加载推荐景点
async function loadRecommendations() {
    loading.value = true
    try {
        const city = search.value.destination?.trim()
        const res = await getClientRecommendations('attraction', {
            limit: 50,
            city: city || undefined
        })
        attractions.value = res.data || []
    } catch (e) {
        ElMessage.error('景点推荐加载失败，请稍后重试')
        attractions.value = []
    } finally {
        loading.value = false
    }
}

// 根据类型筛选景点
const filteredAttractions = computed(() => {
    if (activeAttractionType.value === 'all') return attractions.value
    const keywords = typeKeywords[activeAttractionType.value] || []
    return attractions.value.filter(item =>
        (item.tags || []).some(tag => keywords.some(k => tag.includes(k)))
    )
})

// 判断景点所属类型
function getAttractionType(item) {
    if (activeAttractionType.value !== 'all') {
        return attractionTypes.find(t => t.id === activeAttractionType.value)?.label || '景点'
    }
    for (const type of attractionTypes.slice(1)) {
        const keywords = typeKeywords[type.id] || []
        if ((item.tags || []).some(tag => keywords.some(k => tag.includes(k)))) {
            return type.label
        }
    }
    return item.subType || (item.tags || [])[0] || '景点'
}

// 地点展示（省市重复时只展示一个）
function formatLocation(item) {
    if (item.province && item.city && item.province !== item.city && !item.province.includes(item.city)) {
        return `${item.province} · ${item.city}`
    }
    return item.city || item.province || ''
}

// 搜索景点（按目的地城市重新请求推荐）
function searchAttractions() {
    activeAttractionType.value = 'all'
    loadRecommendations()
}

onMounted(loadRecommendations)
</script>

<style scoped>
/* 基础样式 */
.recommendation {
    padding: 0 15px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
    color: #333;
}

/* 头部样式 */
.header {
    position: relative;
    height: 250px;
    margin: 24px 0 24px 0;
    border-radius: 14px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.header h1 {
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
    background: linear-gradient(135deg, #cc43ee, #3791c9);
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
    height: 240px;
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
    width: 100%;
    height: 150px;
    border-radius: 50%;
    background-color: #00ff5e;
    opacity: 1;
    filter: blur(12px);
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
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 12px;
    background: #fff;
    padding: 18px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    margin-top: 20px;
}

.search-bar input {
    padding: 12px 16px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    font-size: 15px;
    transition: all 0.3s ease;
}

.search-bar input:focus {
    outline: none;
    border-color: #4361ee;
    box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.2);
}

.search-bar button {
    background: linear-gradient(135deg, #4361ee, #3a0ca3);
    color: white;
    border: none;
    border-radius: 8px;
    padding: 12px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.search-bar button:hover {
    background: linear-gradient(135deg, #3a0ca3, #4361ee);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(67, 97, 238, 0.3);
}

/* 特色推荐样式 */
.features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 24px;
    margin-bottom: 40px;
}

.feature {
    background: #fff;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
}

.feature:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.feature h2 {
    color: #4361ee;
    margin-bottom: 8px;
    font-size: 18px;
}

.feature p {
    color: #666;
    line-height: 1.6;
    font-size: 14px;
}

/* 景点类型切换 */
.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 12px;
}

.types h2 {
    font-size: 1.8rem;
    color: #333;
    position: relative;
}

.types h2::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 0;
    width: 50px;
    height: 3px;
    background: #4361ee;
    border-radius: 2px;
}

.type-tabs {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

.type-tabs button {
    padding: 8px 16px;
    background: #fff;
    border: 1px solid #e0e0e0;
    border-radius: 20px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.type-tabs button:hover {
    background: #f5f5f5;
}

.type-tabs button.active {
    background: #4361ee;
    color: white;
    border-color: #4361ee;
}

/* 景点列表 */
.list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}

.item {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transition: transform 0.3s ease;
}

.item:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.image {
    position: relative;
    height: 180px;
    overflow: hidden;
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

.type-badge {
    position: absolute;
    bottom: 12px;
    left: 12px;
    background: rgba(0, 0, 0, 0.7);
    color: white;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
}

.info {
    padding: 16px;
}

.info h3 {
    font-size: 18px;
    margin-bottom: 6px;
    color: #333;
}

.location {
    color: #666;
    font-size: 14px;
    margin-bottom: 10px;
}

.tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 12px;
}

.tags span {
    background: #f0f0f0;
    color: #555;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
}

.meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

.price {
    color: #4361ee;
    font-weight: 600;
    font-size: 16px;
}

.free {
    color: #00a854;
    font-weight: 600;
    font-size: 16px;
}

.rating {
    color: #ffb400;
    font-weight: 600;
}

.pin-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    background: linear-gradient(135deg, #ff6b6b, #ee5a24);
    color: white;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    z-index: 2;
}

.empty-tip {
    grid-column: 1 / -1;
    text-align: center;
    color: #999;
    padding: 60px 0;
    font-size: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .search-bar {
        grid-template-columns: 1fr;
    }

    .features {
        grid-template-columns: 1fr;
    }

    .section-header {
        flex-direction: column;
        align-items: flex-start;
    }

    .type-tabs {
        width: 100%;
        overflow-x: auto;
        padding-bottom: 8px;
    }

    .recommendation h1 {
        font-size: 1.8rem;
    }
}
</style>