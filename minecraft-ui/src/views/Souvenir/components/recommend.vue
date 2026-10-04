<template>
    <div class="container">
        <!-- 顶部搜索区 -->
        <header class="header">

            <div class="bg"></div>
            <div class="blob"></div>

            <div class="header-content">
                <h1>特色旅游纪念品</h1>
                <p class="subtitle">带一份独特的记忆回家</p>

                <div class="search-section">
                    <div class="search-bar">
                        <input type="text" placeholder="搜索纪念品、城市或特色..." v-model="searchQuery"
                            @keyup.enter="searchSouvenirs">
                        <button @click="searchSouvenirs">
                            <i class="search-icon">🔍</i>
                            搜索
                        </button>
                    </div>
                </div>
            </div>

        </header>

        <!-- 分类导航 -->
        <nav class="category-nav">
            <div class="category-item" v-for="category in categories" :key="category.id"
                :class="{ active: activeCategory === category.id }" @click="filterByCategory(category.id)">
                <div class="category-icon">{{ category.icon }}</div>
                <span>{{ category.name }}</span>
            </div>
        </nav>

        <!-- 商品展示区 -->
        <main class="main">
            <div class="sort-options">
                <span>排序方式：</span>
                <select v-model="sortOption">
                    <option value="popular">综合推荐</option>
                    <option value="price-asc">价格从低到高</option>
                    <option value="price-desc">价格从高到低</option>
                    <option value="rating">评分优先</option>
                </select>
            </div>

            <div class="grid" v-loading="loading">
                <div class="card" v-for="item in filteredSouvenirs" :key="item.itemId">
                    <div class="image">
                        <img :src="item.image" :alt="item.name">
                        <span class="tag" v-if="item.ruleType === 'PIN'">置顶推荐</span>
                        <span class="tag normal-tag" v-else-if="item.subType">{{ item.subType }}</span>
                        <button class="favorite-btn" @click="toggleFavorite(item.itemId)"
                            :class="{ favorited: favoriteIds.has(item.itemId) }">
                            ♥
                        </button>
                    </div>

                    <div class="info">
                        <h3>{{ item.name }}</h3>
                        <p class="origin">{{ formatLocation(item) }}</p>

                        <div class="price-section">
                            <span class="price">¥{{ item.price }}</span>
                        </div>

                        <div class="rating">
                            <span class="stars">★★★★★</span>
                            <span class="score" v-if="item.rating">{{ displayRating(item.rating) }}分</span>
                        </div>

                        <button class="add-to-cart" @click="addToCart(item)">
                            加入购物车
                        </button>
                    </div>
                </div>
                <div class="empty-tip" v-if="!loading && filteredSouvenirs.length === 0">
                    暂无符合条件的纪念品推荐
                </div>
            </div>
        </main>

        <!-- 底部推荐 -->
        <section class="recommendation" v-if="recommendedItems.length > 0">
            <h2>你可能还喜欢</h2>
            <div class="recommendation-grid">
                <div class="recommend-item" v-for="item in recommendedItems" :key="item.itemId"
                    @click="viewDetail(item.itemId)">
                    <img :src="item.image" :alt="item.name">
                    <p>{{ item.name }}</p>
                    <span class="rec-price">¥{{ item.price }}</span>
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
const activeCategory = ref('all')
const sortOption = ref('popular')
const loading = ref(false)

// 分类导航（基于商品真实类型）
const categories = [
    { id: 'all', name: '全部', icon: '🛍️' },
    { id: 'food', name: '特色食品', icon: '🍪' },
    { id: 'craft', name: '手工艺品', icon: '✂️' },
    { id: 'clothing', name: '丝绸纺织', icon: '👕' },
    { id: 'decoration', name: '家居装饰', icon: '🏠' },
    { id: 'jewelry', name: '珠宝首饰', icon: '💍' },
    { id: 'beauty', name: '美妆保健', icon: '💄' },
    { id: 'other', name: '其他', icon: '🎁' }
]

// 分类与商品类型（subType）的映射
const categoryTypes = {
    food: ['食品', '调味品', '酒水', '茶叶'],
    craft: ['工艺品'],
    clothing: ['丝绸', '纺织品'],
    jewelry: ['珠宝'],
    beauty: ['化妆品', '护肤品', '保健品']
}

// 家居装饰通过标签关键词识别
const decorationKeywords = ['装饰', '摆件', '瓷器', '茶具', '灯', '画']

// 已收藏商品
const favoriteIds = ref(new Set())

// 纪念品数据（来自推荐接口）
const souvenirs = ref([])

// 加载推荐纪念品
async function loadRecommendations() {
    loading.value = true
    try {
        const res = await getClientRecommendations('product', { limit: 50 })
        souvenirs.value = res.data || []
    } catch (e) {
        ElMessage.error('纪念品推荐加载失败，请稍后重试')
        souvenirs.value = []
    } finally {
        loading.value = false
    }
}

// 判断商品是否属于某分类
function matchCategory(item, categoryId) {
    if (categoryId === 'decoration') {
        return (item.tags || []).some(tag => decorationKeywords.some(k => tag.includes(k)))
    }
    if (categoryId === 'other') {
        const mapped = Object.values(categoryTypes).flat()
        const isDecoration = (item.tags || []).some(tag => decorationKeywords.some(k => tag.includes(k)))
        return item.subType && !mapped.includes(item.subType) && !isDecoration
    }
    return (categoryTypes[categoryId] || []).includes(item.subType)
}

// 筛选后的纪念品
const filteredSouvenirs = computed(() => {
    let result = [...souvenirs.value]

    // 搜索筛选（名称/城市/标签/类型）
    if (searchQuery.value) {
        const query = searchQuery.value.trim().toLowerCase()
        result = result.filter(item =>
            `${item.name || ''} ${item.city || ''} ${item.subType || ''} ${(item.tags || []).join(' ')}`
                .toLowerCase()
                .includes(query)
        )
    }

    // 分类筛选
    if (activeCategory.value !== 'all') {
        result = result.filter(item => matchCategory(item, activeCategory.value))
    }

    // 排序
    switch (sortOption.value) {
        case 'price-asc':
            return result.sort((a, b) => (a.price || 0) - (b.price || 0))
        case 'price-desc':
            return result.sort((a, b) => (b.price || 0) - (a.price || 0))
        case 'rating':
            return result.sort((a, b) => (b.rating || 0) - (a.rating || 0))
        default:
            // 综合推荐：保持接口顺序（已结合置顶规则与综合推荐分）
            return result
    }
})

// 猜你喜欢：取当前未展示的推荐商品
const recommendedItems = computed(() => {
    const showingIds = new Set(filteredSouvenirs.value.map(i => i.itemId))
    return souvenirs.value.filter(item => !showingIds.has(item.itemId)).slice(0, 4)
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

// 搜索纪念品（对已加载数据进行筛选）
function searchSouvenirs() {
    activeCategory.value = 'all'
}

// 按分类筛选
function filterByCategory(categoryId) {
    activeCategory.value = activeCategory.value === categoryId ? 'all' : categoryId
}

// 收藏/取消收藏
function toggleFavorite(itemId) {
    const next = new Set(favoriteIds.value)
    if (next.has(itemId)) {
        next.delete(itemId)
    } else {
        next.add(itemId)
    }
    favoriteIds.value = next
}

// 加入购物车
function addToCart(item) {
    ElMessage.success(`已将「${item.name}」加入购物车`)
}

// 查看详情
function viewDetail(itemId) {
    // 这里可以添加路由跳转或显示详情弹窗
    console.log('查看商品详情:', itemId)
}

onMounted(loadRecommendations)
</script>
<style scoped>
/* 基础样式 */
.container {
    padding: 0 15px;
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
    color: #333;
}

/* 顶部区域 */
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
    font-size: 2.2rem;
    margin-bottom: 10px;
    background: linear-gradient(135deg, #ff6b6b, #ff9a44);
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

.header-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    z-index: 3;
}

.subtitle {
    font-size: 1.1rem;
    color: #555;
    margin-bottom: 25px;
}

.search-section {
    max-width: 900px;
    margin: 0 auto;
    padding: 0 20px;
}

.search-bar {
    display: flex;
    margin-bottom: 20px;
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
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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

.region-filter {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 10px;
}

.region-filter button {
    padding: 8px 16px;
    background: rgba(255, 255, 255, 0.7);
    color: #333;
    border: none;
    border-radius: 20px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.region-filter button.active {
    background: #ff6b6b;
    color: white;
    font-weight: 600;
}

/* 分类导航 */
.category-nav {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
    margin-bottom: 30px;
    padding: 15px 10px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.category-item {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 10px 16px;
    border-radius: 999px;
    transition: all 0.3s ease;
}

.category-item:hover {
    background: #fff0f0;
}

.category-item.active {
    background: linear-gradient(135deg, #ff6b6b, #ff8f5c);
    color: white;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(255, 107, 107, 0.35);
}

.category-icon {
    font-size: 1.4rem;
    line-height: 1;
}

.category-item span {
    font-size: 14px;
}

/* 商品展示区 */
.sort-options {
    text-align: right;
    margin-bottom: 20px;
    font-size: 14px;
}

.sort-options select {
    padding: 8px 14px;
    border-radius: 8px;
    border: 1px solid #e5e5e5;
    margin-left: 10px;
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

.grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
}

.empty-tip {
    grid-column: 1 / -1;
    text-align: center;
    color: #999;
    padding: 60px 0;
    font-size: 15px;
}

.card {
    background: white;
    border-radius: 14px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.07);
    transition: all 0.3s ease;
    cursor: pointer;
}

.card:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(255, 107, 107, 0.16);
}

.image {
    position: relative;
    height: 200px;
    overflow: hidden;
}

.image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.card:hover .image img {
    transform: scale(1.05);
}

.tag {
    position: absolute;
    top: 10px;
    right: 10px;
    background: #ff6b6b;
    color: white;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 12px;
}

.tag.normal-tag {
    background: rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(6px);
}

.favorite-btn {
    position: absolute;
    bottom: 10px;
    right: 10px;
    width: 32px;
    height: 32px;
    background: rgba(255, 255, 255, 0.75);
    backdrop-filter: blur(6px);
    border: none;
    border-radius: 50%;
    font-size: 16px;
    color: #ccc;
    cursor: pointer;
    transition: all 0.2s ease;
}

.favorite-btn.favorited {
    color: #ff6b6b;
}

.favorite-btn:hover {
    background: white;
    transform: scale(1.1);
}

.info {
    padding: 15px;
}

.info h3 {
    font-size: 16px;
    margin-bottom: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.origin {
    color: #666;
    font-size: 13px;
    margin-bottom: 10px;
}

.price-section {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
}

.price {
    font-size: 18px;
    font-weight: bold;
    color: #ff6b6b;
}

.original-price {
    font-size: 13px;
    color: #999;
    text-decoration: line-through;
    margin-left: 8px;
}

.rating {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    font-size: 13px;
}

.stars {
    color: #ffb400;
    letter-spacing: 2px;
    margin-right: 5px;
}

.score {
    margin-right: 10px;
}

.sales {
    color: #666;
}

.add-to-cart {
    width: 100%;
    padding: 10px;
    background: linear-gradient(135deg, #ff6b6b, #ff8f5c);
    color: white;
    border: none;
    border-radius: 999px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
}

.add-to-cart:hover {
    background: linear-gradient(135deg, #ff5252, #ff7b45);
    box-shadow: 0 6px 14px rgba(255, 107, 107, 0.35);
}

/* 推荐区 */
.recommendation {
    margin: 40px 0;
}

.recommendation h2 {
    font-size: 1.5rem;
    margin-bottom: 20px;
    padding-left: 10px;
    border-left: 4px solid #ff6b6b;
}

.recommendation-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
}

.recommend-item {
    background: white;
    border-radius: 10px;
    padding: 15px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.recommend-item:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.recommend-item img {
    width: 100%;
    height: 120px;
    object-fit: cover;
    border-radius: 8px;
    margin-bottom: 10px;
}

.recommend-item p {
    font-size: 14px;
    margin-bottom: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.rec-price {
    color: #ff6b6b;
    font-weight: bold;
    font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .header h1 {
        font-size: 1.8rem;
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

    .category-nav {
        overflow-x: auto;
        justify-content: flex-start;
        padding: 15px 10px;
    }

    .category-item {
        min-width: 70px;
    }

    .grid {
        grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    }

    .recommendation-grid {
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    }
}
</style>