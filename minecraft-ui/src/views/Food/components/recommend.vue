<template>
    <div class="recommendation">
        <header class="header">

            <div class="bg"></div>
            <div class="blob"></div>

            <div class="header-content">
                <h1>美食探索</h1>
                <p class="subtitle">发现城市中最受欢迎的美食</p>
                <div class="search-container">
                    <div class="search-bar">
                        <input type="text" placeholder="输入地点、餐厅或美食..." v-model="searchQuery">
                        <button @click="searchFood">
                            <i class="search-icon">🔍</i>
                            搜索
                        </button>
                    </div>
                    <div class="quick-filters">
                        <button v-for="filter in quickFilters" :key="filter.id"
                            :class="{ active: activeFilter === filter.id }" @click="activeFilter = filter.id">
                            {{ filter.label }}
                        </button>
                    </div>
                </div>
            </div>

        </header>

        <section class="cuisine-types">
            <h2>美食分类</h2>
            <div class="cuisine-grid">
                <div class="cuisine-card" v-for="cuisine in cuisines" :key="cuisine.id"
                    @click="selectCuisine(cuisine.id)" :class="{ active: selectedCuisine === cuisine.id }">
                    <div class="cuisine-icon">{{ cuisine.icon }}</div>
                    <h3>{{ cuisine.name }}</h3>
                </div>
            </div>
        </section>

        <section class="featured-restaurants">
            <div class="section-header">
                <h2>精选推荐</h2>
                <div class="sort-options">
                    <select v-model="sortOption">
                        <option value="default">综合推荐</option>
                        <option value="rating">按评分排序</option>
                        <option value="price">按价格排序</option>
                    </select>
                </div>
            </div>

            <div class="restaurant-list" v-loading="isLoading">
                <div class="restaurant-card" v-for="restaurant in sortedRestaurants" :key="restaurant.itemId">
                    <div class="restaurant-image">
                        <img :src="restaurant.image" :alt="restaurant.name">
                        <span class="pin-badge" v-if="restaurant.ruleType === 'PIN'">置顶推荐</span>
                        <span class="rating-badge" v-if="restaurant.rating">★ {{ displayRating(restaurant.rating) }}</span>
                        <span class="distance">{{ restaurant.city || '美食' }}</span>
                    </div>

                    <div class="restaurant-info">
                        <h3>{{ restaurant.name }}</h3>
                        <p class="cuisine-type">{{ restaurant.subType || '地方美食' }}</p>

                        <div class="tags">
                            <span v-for="tag in (restaurant.tags || []).slice(0, 3)" :key="tag">{{ tag }}</span>
                        </div>

                        <div class="restaurant-footer">
                            <span class="price-range" v-if="restaurant.price">¥{{ restaurant.price }}/人</span>
                            <span class="price-range" v-else>价格待定</span>
                            <button class="book-btn-food">立即预订</button>
                        </div>
                    </div>
                </div>
                <div class="empty-tip" v-if="!isLoading && sortedRestaurants.length === 0">
                    暂无符合条件的美食推荐
                </div>
            </div>
        </section>

        <section class="tips">
            <h2>美食小贴士</h2>
            <div class="tips-grid">
                <div class="tip-card" v-for="tip in tips" :key="tip.id">
                    <div class="tip-image">
                        <img :src="tip.image" :alt="tip.title">
                    </div>
                    <h3>{{ tip.title }}</h3>
                    <p>{{ tip.description }}</p>
                </div>
            </div>
        </section>
    </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClientRecommendations } from '@/api/clientRecommendation'

// 搜索相关
const searchQuery = ref('')
const activeFilter = ref('all')
const selectedCuisine = ref('all')
const sortOption = ref('default')
const isLoading = ref(false)

// 快速筛选选项（仅保留数据可支持的维度）
const quickFilters = [
    { id: 'all', label: '全部' },
    { id: 'popular', label: '热门高分' }
]

// 美食分类（按真实菜系数据归组）
const cuisines = [
    { id: 'all', name: '全部', icon: '🧺' },
    { id: 'sichuan', name: '川渝风味', icon: '🍲' },
    { id: 'north', name: '北方风味', icon: '🥟' },
    { id: 'east', name: '江浙沪徽', icon: '🦀' },
    { id: 'south', name: '粤闽桂琼', icon: '🍵' },
    { id: 'hunan', name: '湘黔风味', icon: '🌶️' },
    { id: 'yunnan', name: '云贵风味', icon: '🍄' },
    { id: 'central', name: '华中赣鄂', icon: '🍚' },
    { id: 'snack', name: '小吃面食', icon: '🍢' }
]

// 菜系分组关键词
const cuisineKeywords = {
    sichuan: ['川菜', '渝菜'],
    north: ['京菜', '鲁菜', '东北菜', '晋菜', '冀菜', '津菜', '豫菜', '陕菜', '蒙菜', '宁菜', '陇菜', '青海菜', '新疆菜', '藏菜'],
    east: ['沪菜', '浙菜', '苏菜', '徽菜'],
    south: ['粤菜', '闽菜', '琼菜', '桂菜'],
    hunan: ['湘菜', '黔菜'],
    yunnan: ['滇菜'],
    central: ['鄂菜', '赣菜']
}

// 小吃面食关键词（匹配菜名）
const snackKeywords = ['小吃', '火锅', '面', '粉', '馍', '饺', '包子', '汤圆', '麻花', '糕', '饼', '粽', '丸子', '肠', '串']

// 餐厅数据（来自推荐接口）
const restaurants = ref([])

// 加载推荐餐厅
async function loadRecommendations() {
    isLoading.value = true
    try {
        const res = await getClientRecommendations('food', { limit: 50 })
        restaurants.value = res.data || []
    } catch (e) {
        ElMessage.error('美食推荐加载失败，请稍后重试')
        restaurants.value = []
    } finally {
        isLoading.value = false
    }
}

// 判断餐厅是否属于某分类
function matchCuisine(restaurant, cuisineId) {
    if (cuisineId === 'snack') {
        const text = `${restaurant.name || ''} ${(restaurant.tags || []).join(' ')}`
        return snackKeywords.some(k => text.includes(k))
    }
    const list = cuisineKeywords[cuisineId] || []
    return list.includes(restaurant.subType)
}

// 评分兼容 5 分制与百分制两种历史数据
function displayRating(rating) {
    if (rating === null || rating === undefined) return ''
    return rating > 5 ? (rating / 20).toFixed(1) : rating
}

// 计算属性：筛选和排序后的餐厅
const sortedRestaurants = computed(() => {
    let result = [...restaurants.value]

    // 根据搜索词筛选（餐厅名/城市/菜系/标签）
    if (searchQuery.value) {
        const query = searchQuery.value.trim().toLowerCase()
        result = result.filter(restaurant =>
            `${restaurant.name || ''} ${restaurant.city || ''} ${restaurant.subType || ''} ${(restaurant.tags || []).join(' ')}`
                .toLowerCase()
                .includes(query)
        )
    }

    // 热门高分
    if (activeFilter.value === 'popular') {
        result = result.filter(r => {
            const score = r.rating > 5 ? r.rating / 20 : r.rating
            return score >= 4.8 || r.featured || r.ruleType === 'BOOST'
        })
    }

    // 根据美食分类筛选
    if (selectedCuisine.value !== 'all') {
        result = result.filter(r => matchCuisine(r, selectedCuisine.value))
    }

    // 排序
    switch (sortOption.value) {
        case 'rating':
            return result.sort((a, b) => (b.rating || 0) - (a.rating || 0))
        case 'price':
            return result.sort((a, b) => (a.price || 0) - (b.price || 0))
        default:
            // 综合推荐：保持接口顺序（已结合置顶规则与综合推荐分）
            return result
    }
})

// 搜索美食（对已加载数据进行筛选）
function searchFood() {
    activeFilter.value = 'all'
    selectedCuisine.value = 'all'
}

// 选择美食分类
function selectCuisine(cuisineId) {
    selectedCuisine.value = selectedCuisine.value === cuisineId ? 'all' : cuisineId
}

// 美食小贴士（静态内容）
const tips = [
    {
        id: 1,
        title: '如何辨别新鲜海鲜',
        description: '选购海鲜时要注意眼睛是否明亮，气味是否新鲜，肉质是否有弹性。',
        image: 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=500'
    },
    {
        id: 2,
        title: '吃火锅的正确顺序',
        description: '先涮肉后涮菜，最后再下面食，这样汤底味道会越来越好。',
        image: 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500'
    },
    {
        id: 3,
        title: '意大利面煮法技巧',
        description: '煮面时水中加盐，煮至"al dente"(有嚼劲)状态最佳。',
        image: 'https://images.unsplash.com/photo-1598866594230-a7c12756260f?w=500'
    },
    {
        id: 4,
        title: '品鉴寿司的正确方式',
        description: '用手而非筷子食用，鱼片朝下蘸酱油，米饭不能沾到酱油。',
        image: 'https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=500'
    }
]

// 初始化
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
    margin-bottom: 10px;
    font-weight: 700;
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
    width: 110%;
    height: 220px;
    border-radius: 50%;
    background: linear-gradient(135deg, #ff6b6b, #ff9a44);
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

.header-content{
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    z-index: 3;
}

.subtitle {
    font-size: 1.1rem;
    margin-bottom: 25px;
    opacity: 0.9;
}

.search-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
}

.search-bar {
    display: flex;
    margin-bottom: 15px;
}

.search-bar input {
    flex: 1;
    padding: 14px 20px;
    border: none;
    border-radius: 30px 0 0 30px;
    font-size: 16px;
    outline: none;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s ease;
}

.search-bar input:focus {
    box-shadow: 0 4px 16px rgba(255, 107, 107, 0.35);
}

.search-bar button {
    padding: 0 25px;
    background: linear-gradient(135deg, #ff6b6b, #ff8f5c);
    color: white;
    border: none;
    border-radius: 0 30px 30px 0;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    transition: all 0.3s ease;
}

.search-bar button:hover {
    background: linear-gradient(135deg, #ff5252, #ff7b45);
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(255, 107, 107, 0.4);
}

.search-icon {
    margin-right: 8px;
}

.quick-filters {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 10px;
}

.quick-filters button {
    padding: 8px 18px;
    background: white;
    color: #555;
    border: none;
    border-radius: 999px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.quick-filters button:hover {
    color: #ff6b6b;
    transform: translateY(-1px);
}

.quick-filters button.active {
    background: linear-gradient(135deg, #ff6b6b, #ff8f5c);
    color: white;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);
}

/* 美食分类 */
.cuisine-types {
    margin: 40px 0;
}

.cuisine-types h2 {
    font-size: 1.8rem;
    margin-bottom: 20px;
    color: #333;
    position: relative;
    padding-left: 15px;
}

.cuisine-types h2::before {
    content: '';
    position: absolute;
    left: 0;
    top: 5px;
    height: 70%;
    width: 5px;
    background: #ff6b6b;
    border-radius: 3px;
}

.cuisine-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 15px;
}

.cuisine-card {
    background: white;
    border-radius: 12px;
    padding: 20px 10px;
    display: flex;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.cuisine-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.cuisine-card.active {
    background: #fff5f5;
    border: 1px solid #ff6b6b;
    box-shadow: 0 8px 20px rgba(255, 107, 107, 0.2);
    transform: translateY(-3px);
}

.cuisine-icon {
    font-size: 2rem;
    margin-bottom: 10px;
}

.cuisine-card h3 {
    font-size: 16px;
    font-weight: 500;
}

/* 餐厅列表 */
.featured-restaurants {
    margin: 40px 0;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 12px;
}

.section-header h2 {
    font-size: 1.8rem;
    color: #333;
    position: relative;
    padding-left: 15px;
}

.section-header h2::before {
    content: '';
    position: absolute;
    left: 0;
    top: 5px;
    height: 70%;
    width: 5px;
    background: linear-gradient(180deg, #ff6b6b, #ff9a44);
    border-radius: 3px;
}

.sort-options select {
    padding: 8px 14px;
    border-radius: 8px;
    border: 1px solid #e5e5e5;
    background: white;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.sort-options select:focus {
    outline: none;
    border-color: #ff6b6b;
    box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.15);
}

.restaurant-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}

.restaurant-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
}

.restaurant-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.restaurant-image {
    position: relative;
    height: 180px;
    overflow: hidden;
}

.restaurant-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.restaurant-card:hover .restaurant-image img {
    transform: scale(1.05);
}

.rating-badge {
    position: absolute;
    top: 10px;
    left: 10px;
    background: rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(6px);
    color: #ffb400;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 14px;
    font-weight: 600;
}

.distance {
    position: absolute;
    top: 10px;
    right: 10px;
    background: rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(6px);
    color: white;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 14px;
}

.pin-badge {
    position: absolute;
    bottom: 10px;
    right: 10px;
    background: linear-gradient(135deg, #ff6b6b, #ee5a24);
    color: white;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 12px;
}

.empty-tip {
    grid-column: 1 / -1;
    text-align: center;
    color: #999;
    padding: 60px 0;
    font-size: 15px;
}

.restaurant-info {
    padding: 15px;
}

.restaurant-info h3 {
    font-size: 18px;
    margin-bottom: 5px;
}

.cuisine-type {
    color: #666;
    font-size: 14px;
    margin-bottom: 10px;
}

.tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 15px;
}

.tags span {
    background: rgba(255, 107, 107, 0.08);
    color: #ff6b6b;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 12px;
}

.restaurant-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.price-range {
    color: #ff6b6b;
    font-size: 16px;
    font-weight: 600;
}

.book-btn-food {
    background: linear-gradient(135deg, #ff6b6b, #ff8f5c);
    color: white;
    border: none;
    border-radius: 999px;
    padding: 8px 18px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.book-btn-food:hover {
    background: linear-gradient(135deg, #ff5252, #ff7b45);
    transform: translateY(-1px);
    box-shadow: 0 6px 14px rgba(255, 107, 107, 0.35);
}

/* 美食小贴士 */
.tips {
    margin: 40px 0;
}

.tips h2 {
    font-size: 1.8rem;
    margin-bottom: 20px;
    color: #333;
    position: relative;
    padding-left: 15px;
}

.tips h2::before {
    content: '';
    position: absolute;
    left: 0;
    top: 5px;
    height: 70%;
    width: 5px;
    background: linear-gradient(180deg, #ff6b6b, #ff9a44);
    border-radius: 3px;
}

.tips-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
}

.tip-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
}

.tip-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.tip-image {
    height: 150px;
    overflow: hidden;
}

.tip-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.tip-card:hover .tip-image img {
    transform: scale(1.05);
}

.tip-card h3 {
    padding: 15px 15px 5px;
    font-size: 18px;
}

.tip-card p {
    padding: 0 15px 15px;
    color: #666;
    font-size: 14px;
    line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .header h1 {
        font-size: 2rem;
    }

    .search-bar {
        flex-direction: column;
    }

    .search-bar input {
        border-radius: 30px;
        margin-bottom: 10px;
    }

    .search-bar button {
        border-radius: 30px;
        justify-content: center;
    }

    .cuisine-grid {
        grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    }

    .restaurant-list,
    .tips-grid {
        grid-template-columns: 1fr;
    }
}
</style>