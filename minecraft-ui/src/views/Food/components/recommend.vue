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
                        <option value="rating">按评分排序</option>
                        <option value="distance">按距离排序</option>
                        <option value="price">按价格排序</option>
                    </select>
                </div>
            </div>

            <div class="restaurant-list">
                <div class="restaurant-card" v-for="restaurant in sortedRestaurants" :key="restaurant.id">
                    <div class="restaurant-image">
                        <img :src="restaurant.image" :alt="restaurant.name">
                        <span class="rating-badge">★ {{ restaurant.rating }}</span>
                        <span class="distance">{{ restaurant.distance }}km</span>
                    </div>

                    <div class="restaurant-info">
                        <h3>{{ restaurant.name }}</h3>
                        <p class="cuisine-type">{{ restaurant.cuisine }}</p>

                        <div class="tags">
                            <span v-for="tag in restaurant.tags" :key="tag">{{ tag }}</span>
                        </div>

                        <div class="restaurant-footer">
                            <span class="price-range">
                                {{ '¥'.repeat(restaurant.priceLevel) }}
                            </span>
                            <button class="book-btn-food">立即预订</button>
                        </div>
                    </div>
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

// 搜索相关
const searchQuery = ref('')
const activeFilter = ref('all')
const selectedCuisine = ref('all')
const sortOption = ref('rating')
const isLoading = ref(false)

// 筛选选项
const quickFilters = [
    { id: 'all', label: '全部' },
    { id: 'nearby', label: '附近' },
    { id: 'popular', label: '热门' },
    { id: 'discount', label: '优惠' },
    { id: 'delivery', label: '外卖' }
]

// 美食分类
const cuisines = [
    { id: 'all', name: '全部', icon: '🧺' },
    { id: 'chinese', name: '中餐', icon: '🥢' },
    { id: 'western', name: '西餐', icon: '🍽️' },
    { id: 'japanese', name: '日料', icon: '🍣' },
    { id: 'korean', name: '韩餐', icon: '🍜' },
    { id: 'hotpot', name: '火锅', icon: '🍲' },
    { id: 'dessert', name: '甜点', icon: '🍰' },
    { id: 'street', name: '小吃', icon: '🍢' },
    { id: 'vegetarian', name: '素食', icon: '🥗' }
]

// 餐厅数据
// 更新餐厅数据中的cuisine字段
const restaurants = ref([
    {
        id: 1,
        name: '川味坊',
        cuisine: 'chinese',
        rating: 4.8,
        distance: 1.2,
        priceLevel: 3,
        image: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500',
        tags: ['麻辣', '地道', '人气'],
        isFavorite: false,
        delivery: true,
        discount: false,
        popular: true
    },
    {
        id: 2,
        name: '意大利风情',
        cuisine: 'western',
        rating: 4.6,
        distance: 2.5,
        priceLevel: 4,
        image: 'https://images.unsplash.com/photo-1592861956120-e524fc739696?w=500',
        tags: ['披萨', '浪漫', '红酒'],
        isFavorite: true,
        delivery: true,
        discount: true,
        popular: false
    },
    {
        id: 3,
        name: '东京寿司',
        cuisine: 'japanese',
        rating: 4.9,
        distance: 0.8,
        priceLevel: 4,
        image: 'https://images.unsplash.com/photo-1611143669185-af224c5e3252?w=500',
        tags: ['新鲜', '刺身', '清酒'],
        isFavorite: false,
        delivery: false,
        discount: false,
        popular: true
    },
    {
        id: 4,
        name: '韩式烤肉',
        cuisine: 'korean',
        rating: 4.5,
        distance: 1.5,
        priceLevel: 3,
        image: 'https://images.unsplash.com/photo-1606787366850-de6330128bfc?w=500',
        tags: ['烤肉', '泡菜', '啤酒'],
        isFavorite: false,
        delivery: true,
        discount: true,
        popular: false
    },
    {
        id: 5,
        name: '海底捞火锅',
        cuisine: 'hotpot',
        rating: 4.7,
        distance: 3.2,
        priceLevel: 3,
        image: 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500',
        tags: ['服务好', '24小时', '网红'],
        isFavorite: true,
        delivery: true,
        discount: false,
        popular: true
    },
    {
        id: 6,
        name: '甜蜜时光',
        cuisine: 'dessert',
        rating: 4.4,
        distance: 0.5,
        priceLevel: 2,
        image: 'https://images.unsplash.com/photo-1551024506-0bccd828d307?w=500',
        tags: ['下午茶', '蛋糕', '咖啡'],
        isFavorite: false,
        delivery: true,
        discount: true,
        popular: false
    }
])

// 更新筛选逻辑
if (selectedCuisine.value !== 'all') {
    result = result.filter(r => r.cuisine === selectedCuisine.value)
}

// 美食小贴士
const tips = ref([
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
])

// 用户位置
const userLocation = ref(null)

// 计算属性：排序和筛选后的餐厅
const sortedRestaurants = computed(() => {
    let result = [...restaurants.value]

    // 根据搜索词筛选
    if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(restaurant =>
            restaurant.name.toLowerCase().includes(query) ||
            restaurant.cuisine.toLowerCase().includes(query) ||
            restaurant.tags.some(tag => tag.toLowerCase().includes(query))
        )
    }

    // 根据快速筛选条件筛选
    switch (activeFilter.value) {
        case 'nearby':
            result = result.filter(r => r.distance <= 2)
            break
        case 'popular':
            result = result.filter(r => r.popular)
            break
        case 'discount':
            result = result.filter(r => r.discount)
            break
        case 'delivery':
            result = result.filter(r => r.delivery)
            break
    }

    // 根据美食分类筛选
    if (selectedCuisine.value !== 'all') {
        result = result.filter(r => r.cuisine.includes(selectedCuisine.value))
    }

    // 排序逻辑
    switch (sortOption.value) {
        case 'rating':
            return result.sort((a, b) => b.rating - a.rating)
        case 'distance':
            return result.sort((a, b) => a.distance - b.distance)
        case 'price':
            return result.sort((a, b) => a.priceLevel - b.priceLevel)
        default:
            return result
    }
})

// 获取用户位置
function getUserLocation() {
    return new Promise((resolve, reject) => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                position => {
                    userLocation.value = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    }
                    resolve(userLocation.value)
                },
                error => {
                    console.error('获取位置失败:', error)
                    // 使用默认位置
                    userLocation.value = { lat: 39.9042, lng: 116.4074 } // 北京
                    resolve(userLocation.value)
                }
            )
        } else {
            console.log('浏览器不支持地理位置')
            userLocation.value = { lat: 39.9042, lng: 116.4074 } // 北京
            resolve(userLocation.value)
        }
    })
}

// 搜索美食
function searchFood() {
    isLoading.value = true
    console.log('正在搜索:', searchQuery.value)

    // 模拟API请求延迟
    setTimeout(() => {
        isLoading.value = false
        console.log('搜索结果:', sortedRestaurants.value)
    }, 800)
}

// 选择美食分类
function selectCuisine(cuisineId) {
    selectedCuisine.value = cuisineId
    console.log('选择了美食分类:', cuisineId)
}

// 收藏/取消收藏餐厅
function toggleFavorite(restaurantId) {
    const restaurant = restaurants.value.find(r => r.id === restaurantId)
    if (restaurant) {
        restaurant.isFavorite = !restaurant.isFavorite
        console.log(`${restaurant.name} ${restaurant.isFavorite ? '已收藏' : '已取消收藏'}`)
    }
}

// 预订餐厅
function bookRestaurant(restaurantId) {
    const restaurant = restaurants.value.find(r => r.id === restaurantId)
    if (restaurant) {
        console.log(`正在预订: ${restaurant.name}`)
        // 这里可以添加实际的预订逻辑
    }
}

// 初始化
onMounted(async () => {
    await getUserLocation()
    console.log('用户位置:', userLocation.value)
})
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
    width: 100%;
    height: 150px;
    border-radius: 50%;
    background-color: #ff0000;
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
    transition: all 0.3s ease;
}

.search-bar button:hover {
    background: #ff5252;
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
    padding: 8px 16px;
    background: rgba(255, 255, 255, 0.2);
    color: rgb(0, 0, 0);
    border: none;
    border-radius: 20px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.quick-filters button.active {
    background: white;
    color: #ff6b6b;
    font-weight: 600;
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
}

.section-header h2 {
    font-size: 1.8rem;
    color: #333;
}

.sort-options select {
    padding: 8px 12px;
    border-radius: 6px;
    border: 1px solid #ddd;
    background: white;
    font-size: 14px;
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
    background: rgba(0, 0, 0, 0.7);
    color: #ffb400;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
}

.distance {
    position: absolute;
    top: 10px;
    right: 10px;
    background: rgba(0, 0, 0, 0.7);
    color: white;
    padding: 4px 10px;
    border-radius: 12px;
    font-size: 14px;
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
    background: #f3f3f3;
    color: #555;
    padding: 4px 10px;
    border-radius: 4px;
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
    background: #ff6b6b;
    color: white;
    border: none;
    border-radius: 6px;
    padding: 8px 15px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.book-btn-food:hover {
    background: #ff5252;
}

/* 美食小贴士 */
.tips {
    margin: 40px 0;
}

.tips h2 {
    font-size: 1.8rem;
    margin-bottom: 20px;
    color: #333;
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