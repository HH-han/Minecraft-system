<template>
    <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
    </div>
    <div v-else-if="error" class="error-state">
        <p>加载失败: {{ error }}</p>
        <button @click="fetchProducts" class="btn retry">重试</button>
    </div>
    <!-- 图片列表 -->
    <div v-else class="card-container">
        <div v-for="item in products" :key="item.id" class="card">
            <div @click="() => showDetail(item)" class="card-image-wrapper">
                <div class="image-container">
                    <img :src="item.coverImage" :alt="item.name" class="image-main">
                    <div class="image-hover-effect"></div>
                </div>
            </div>
            <div class="content">
                <h2 class="title">{{ item.name }}</h2>
                <p class="description">{{ item.description }}</p>
                <div class="card-footer">
                    <span class="card-price">¥{{ item.price }}</span>
                    <button @click="OrderDetails(item.id)" class="btn pay">前往购买</button>
                </div>
            </div>
        </div>
        <div v-if="products.length === 0 && !loading" class="empty-state">
            <p>暂无商品数据</p>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getProductList } from '@/api/product.js'

// 响应式数据
const products = ref([])
const loading = ref(false)
const error = ref('')

// 获取商品数据
const fetchProducts = async () => {
    loading.value = true
    error.value = ''
    
    try {
        const response = await getProductList()
        products.value = response.data?.records || []
    } catch (err) {
        error.value = err.message || '获取数据失败'
        console.error('获取商品数据失败:', err)
    } finally {
        loading.value = false
    }
}

// 组件挂载时获取数据
onMounted(() => {
    fetchProducts()
})

// 打开详情页
const showDetail = (product) => {
    console.log('打开商品详情:', product)
}

// 跳转到订单详情页
const OrderDetails = (productId) => {
    console.log('前往购买:', productId)
}
</script>
<style scoped>
.container-all {
    animation: fadeInUp 1s ease-out;
}

.card-container {
    margin: 0 auto;
    padding: 2rem;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
    gap: 2rem;
    justify-items: center;
    animation: fadeInUp 1s ease-out;
}

/* 卡片容器 */
.card {
    width: 450px;
    height: 380px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    display: flex;
    border-radius: 16px;
    margin: 2rem auto;
    overflow: hidden;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    cursor: pointer;
}

.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 16px 32px rgba(0, 0, 0, 0.15);
}

/* 图片容器 */
.card-image-wrapper {
    flex: 0 0 45%;
    position: relative;
    overflow: hidden;
}

.image-container {
    width: 100%;
    height: 100%;
    position: relative;
    transition: transform 0.3s ease;
}

.image-main {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 16px 0 0 16px;
}

.image-hover-effect {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(45deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0));
    opacity: 0;
    transition: opacity 0.3s ease;
}

.image-container:hover .image-hover-effect {
    opacity: 1;
}

/* 内容区域 */
.content {
    flex: 1;
    padding: 2rem;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.title {
    font-size: 1.8rem;
    color: #2d3436;
    margin-bottom: 1.2rem;
    font-weight: 600;
    letter-spacing: 0.5px;
    position: relative;
}

.title::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 0;
    width: 50px;
    height: 3px;
    background: #ff6b6b;
    border-radius: 2px;
}

.description {
    font-size: 1rem;
    color: #636e72;
    line-height: 1.8;
    margin-bottom: 1.5rem;
}

/* 卡片底部 */
.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;
}

.card-price {
    color: #e74c3c;
    font-size: 1.2em;
    font-weight: bold;
}

.btn {
    padding: 8px 16px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.3s;
}

.btn.pay {
    background: #e74c3c;
    color: white;
}

.btn.pay:hover {
    background: #c0392b;
}

.btn.retry {
    background: #3498db;
    color: white;
    margin-top: 10px;
}

/* 加载状态 */
.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #e74c3c;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: #e74c3c;
}

/* 空状态 */
.empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px;
    color: #636e72;
    grid-column: 1 / -1;
}
</style>