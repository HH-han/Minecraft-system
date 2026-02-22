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

                    <div class="region-filter">
                        <button v-for="region in regions" :key="region.id"
                            :class="{ active: activeRegion === region.id }" @click="filterByRegion(region.id)">
                            {{ region.name }}
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
                <select v-model="sortOption" @change="sortSouvenirs">
                    <option value="popular">人气推荐</option>
                    <option value="price-asc">价格从低到高</option>
                    <option value="price-desc">价格从高到低</option>
                    <option value="newest">最新上架</option>
                </select>
            </div>

            <div class="grid">
                <div class="card" v-for="item in filteredSouvenirs" :key="item.id">
                    <div class="image">
                        <img :src="item.image" :alt="item.name">
                        <span class="tag" v-if="item.tag">{{ item.tag }}</span>
                        <button class="favorite-btn" @click="toggleFavorite(item.id)"
                            :class="{ favorited: item.isFavorite }">
                            ♥
                        </button>
                    </div>

                    <div class="info">
                        <h3>{{ item.name }}</h3>
                        <p class="origin">{{ item.origin }}</p>

                        <div class="price-section">
                            <span class="price">¥{{ item.price }}</span>
                            <span class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</span>
                        </div>

                        <div class="rating">
                            <span class="stars">★★★★★</span>
                            <span class="score">{{ item.rating }}</span>
                            <span class="sales" v-if="item.sales">已售{{ item.sales }}</span>
                        </div>

                        <button class="add-to-cart" @click="addToCart(item)">
                            加入购物车
                        </button>
                    </div>
                </div>
            </div>
        </main>

        <!-- 底部推荐 -->
        <section class="recommendation">
            <h2>你可能还喜欢</h2>
            <div class="recommendation-grid">
                <div class="recommend-item" v-for="item in recommendedItems" :key="item.id"
                    @click="viewDetail(item.id)">
                    <img :src="item.image" :alt="item.name">
                    <p>{{ item.name }}</p>
                    <span class="rec-price">¥{{ item.price }}</span>
                </div>
            </div>
        </section>
    </div>
</template>
<script setup>
import { ref, computed } from 'vue'

// 搜索相关
const searchQuery = ref('')
const activeRegion = ref('all')
const activeCategory = ref('all')
const sortOption = ref('popular')

// 地区筛选
const regions = [
    { id: 'all', name: '全部地区' },
    { id: 'asia', name: '亚洲' },
    { id: 'europe', name: '欧洲' },
    { id: 'america', name: '美洲' },
    { id: 'africa', name: '非洲' },
    { id: 'oceania', name: '大洋洲' }
]

// 分类导航
const categories = [
    { id: 'all', name: '全部', icon: '🛍️' },
    { id: 'food', name: '特色食品', icon: '🍪' },
    { id: 'craft', name: '手工艺品', icon: '✂️' },
    { id: 'clothing', name: '服饰配件', icon: '👕' },
    { id: 'decoration', name: '家居装饰', icon: '🏠' },
    { id: 'stationery', name: '文具用品', icon: '📝' },
    { id: 'jewelry', name: '珠宝首饰', icon: '💍' },
    { id: 'other', name: '其他', icon: '🎁' }
]

// 纪念品数据
const souvenirs = ref([
    {
        id: 1,
        name: '巴黎埃菲尔铁塔模型',
        origin: '法国巴黎',
        price: 129,
        originalPrice: 169,
        rating: 4.8,
        sales: 256,
        image: 'https://images.unsplash.com/photo-1431274172761-fca41d930114?w=500',
        region: 'europe',
        category: 'decoration',
        tag: '新品',
        isFavorite: false
    },
    {
        id: 2,
        name: '日本樱花和风折扇',
        origin: '日本京都',
        price: 89,
        originalPrice: 120,
        rating: 4.6,
        sales: 182,
        image: 'https://images.unsplash.com/photo-1518895949257-7621c3c786d7?w=500',
        region: 'asia',
        category: 'craft',
        tag: '限量',
        isFavorite: true
    },
    {
        id: 3,
        name: '埃及法老青铜书签',
        origin: '埃及开罗',
        price: 65,
        originalPrice: 85,
        rating: 4.5,
        sales: 97,
        image: 'https://images.unsplash.com/photo-1584735428869-0049d9e8d8f1?w=500',
        region: 'africa',
        category: 'stationery',
        tag: '热卖',
        isFavorite: false
    },
    {
        id: 4,
        name: '瑞士军刀纪念版',
        origin: '瑞士伯尔尼',
        price: 199,
        originalPrice: 249,
        rating: 4.9,
        sales: 312,
        image: 'https://images.unsplash.com/photo-1590856029826-c7a73142bbf1?w=500',
        region: 'europe',
        category: 'other',
        tag: '实用',
        isFavorite: false
    }
])

// 推荐商品
const recommendedItems = ref([
    {
        id: 101,
        name: '荷兰木鞋钥匙扣',
        price: 39,
        image: 'https://images.unsplash.com/photo-1583847268964-b28dc8f51f92?w=500',
        origin: '荷兰阿姆斯特丹'
    },
    {
        id: 102,
        name: '泰国手工皂花',
        price: 45,
        image: 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500',
        origin: '泰国曼谷'
    },
    {
        id: 103,
        name: '澳大利亚袋鼠玩偶',
        price: 78,
        image: 'https://images.unsplash.com/photo-1557050543-4d5f4e07ef46?w=500',
        origin: '澳大利亚悉尼'
    },
    {
        id: 104,
        name: '巴西咖啡豆礼盒',
        price: 128,
        image: 'https://images.unsplash.com/photo-1515442261605-65987783cb6a?w=500',
        origin: '巴西里约热内卢'
    }
])

// 筛选后的纪念品
const filteredSouvenirs = computed(() => {
    let result = [...souvenirs.value]

    // 搜索筛选
    if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(item =>
            item.name.toLowerCase().includes(query) ||
            item.origin.toLowerCase().includes(query)
        )
    }

    // 地区筛选
    if (activeRegion.value !== 'all') {
        result = result.filter(item => item.region === activeRegion.value)
    }

    // 分类筛选
    if (activeCategory.value !== 'all') {
        result = result.filter(item => item.category === activeCategory.value)
    }

    // 排序
    switch (sortOption.value) {
        case 'price-asc':
            return result.sort((a, b) => a.price - b.price)
        case 'price-desc':
            return result.sort((a, b) => b.price - a.price)
        case 'newest':
            return result.sort((a, b) => b.id - a.id) // 假设ID越大越新
        default: // popular
            return result.sort((a, b) => (b.rating * 10 + b.sales) - (a.rating * 10 + a.sales))
    }
})

// 搜索纪念品
function searchSouvenirs() {
    console.log('搜索:', searchQuery.value)
}

// 按地区筛选
function filterByRegion(regionId) {
    activeRegion.value = regionId
}

// 按分类筛选
function filterByCategory(categoryId) {
    activeCategory.value = categoryId
}

// 排序
function sortSouvenirs() {
    console.log('排序方式:', sortOption.value)
}

// 收藏/取消收藏
function toggleFavorite(itemId) {
    const item = souvenirs.value.find(item => item.id === itemId)
    if (item) {
        item.isFavorite = !item.isFavorite
        console.log(`${item.name} ${item.isFavorite ? '已收藏' : '已取消收藏'}`)
    }
}

// 加入购物车
function addToCart(item) {
    console.log('添加到购物车:', item.name)
    // 这里可以添加实际的购物车逻辑
}

// 查看详情
function viewDetail(itemId) {
    console.log('查看商品详情:', itemId)
    // 这里可以添加路由跳转或显示详情弹窗
}
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
    color: #333;
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
    background-color: #ff006a;
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
}

.search-bar button {
    padding: 0 25px;
    background: #ff6b6b;
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
    background: #ff5252;
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
    justify-content: space-around;
    margin-bottom: 30px;
    padding: 15px 0;
    background: white;
    border-radius: 15px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.category-item {
    display: flex;
    flex-direction: row;
    align-items: center;
    cursor: pointer;
    padding: 10px;
    border-radius: 10px;
    transition: all 0.3s ease;
}

.category-item:hover {
    background: #f8f8f8;
}

.category-item.active {
    background: #fff0f0;
    color: #ff6b6b;
}

.category-icon {
    font-size: 1.8rem;
    margin-bottom: 5px;
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
    padding: 8px 12px;
    border-radius: 6px;
    border: 1px solid #ddd;
    margin-left: 10px;
}

.grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
}

.card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
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

.favorite-btn {
    position: absolute;
    bottom: 10px;
    right: 10px;
    width: 30px;
    height: 30px;
    background: rgba(255, 255, 255, 0.8);
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
    background: #ff6b6b;
    color: white;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.add-to-cart:hover {
    background: #ff5252;
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
    object-fit: contain;
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