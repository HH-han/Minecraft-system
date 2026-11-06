<template>
    <!-- 仅首屏/跳页加载时替换列表；下滑追加加载时保留列表，避免页面高度塌缩导致滚动位置回到顶部 -->
    <div v-if="loading && !appending" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
    </div>
    <div v-else-if="error" class="error-state">
        <p>加载失败: {{ error }}</p>
        <button @click="fetchProducts" class="btn retry">重试</button>
    </div>
    <!-- 图片列表 -->
    <div v-else class="card-container">
        <div v-for="item in products" :key="item.id" class="card-souvenir">
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
                </div>
            </div>
        </div>
        <div v-if="products.length === 0 && !loading" class="empty-state">
            <p>暂无商品数据</p>
        </div>
    </div>

    <Paging 
        v-if="totalPages > 0"
        :total-pages="totalPages" 
        :current-page="currentPage" 
        :loading="loading"
        @update:current-page="handlePageChange" 
        @load-more="handleLoadMore"
    />
    
    <!-- 商品详情模态框 -->
    <Select 
        :visible="showSelectModal" 
        :product-id="selectedProductId" 
        :commodity="'1'" 
        @close="closeModal"
    />
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getProductList } from '@/api/product.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'
import Select from '@/components/Payment/Select.vue'
import Paging from '@/components/paging/index.vue'

// 响应式数据
const products = ref([])
const loading = ref(false)
// 下滑追加加载中：列表保持挂载，仅底部提示加载，避免滚动位置被重置
const appending = ref(false)
const error = ref('')
const showSelectModal = ref(false)
const selectedProductId = ref('')

// 分页数据
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = ref(10)

// 获取商品数据（append=true 时在已有数据之后追加，不重置）
const fetchProducts = async (page = 1, { append = false } = {}) => {
    appending.value = append
    loading.value = true
    error.value = ''
    
    try {
        const response = await getProductList({ pageNum: page, pageSize: pageSize.value })
        const records = response.data?.records || []
        products.value = append ? [...products.value, ...records] : records
        totalPages.value = response.data?.pages || 0
    } catch (err) {
        error.value = err.message || '获取数据失败'
        console.error('获取商品数据失败:', err)
    } finally {
        loading.value = false
        appending.value = false
    }
}

// 数据变更后自动刷新
useAutoRefresh(fetchProducts)

// 分页切换（点击页码/上下页：跳页并替换数据）
const handlePageChange = (page) => {
    currentPage.value = page
    fetchProducts(page)
}

// 下滑继续加载（在已有数据之后追加新数据）
const handleLoadMore = (page) => {
    currentPage.value = page
    fetchProducts(page, { append: true })
}

// 组件挂载时获取数据
onMounted(() => {
    fetchProducts()
})

// 打开详情模态框
const showDetail = (product) => {
    console.log('点击了商品卡片:', product)
    selectedProductId.value = product.id
    showSelectModal.value = true
}

// 关闭模态框
const closeModal = () => {
    showSelectModal.value = false
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
    justify-items: stretch;
    animation: fadeInUp 1s ease-out;
}

/* 卡片容器 */
.card-souvenir {
    height: 380px;
    width: 100%;
    min-width: 0;
    background: var(--theme-bg-surface);
    box-shadow: var(--theme-shadow-md);
    display: flex;
    border-radius: 16px;
    overflow: hidden;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    cursor: pointer;
}

.card-souvenir:hover {
    transform: translateY(-5px);
    box-shadow: var(--theme-shadow-lg);
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
    min-width: 0;
    padding: 2rem;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.title {
    font-size: 1.8rem;
    color: var(--theme-text-1);
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
    background: var(--theme-primary);
    border-radius: 2px;
}

.description {
    font-size: 1rem;
    color: var(--theme-text-2);
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
    color: var(--theme-danger);
    font-size: 1.2em;
    font-weight: bold;
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
    border: 4px solid var(--theme-bg-surface-2);
    border-top: 4px solid var(--theme-primary);
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
    color: var(--theme-danger);
}

/* 空状态 */
.empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px;
    color: var(--theme-text-2);
    grid-column: 1 / -1;
}

/* ========== 响应式 ========== */
/* 平板：卡片高度与字号收紧 */
@media (max-width: 992px) {
    .card-container {
        padding: 1.5rem;
        gap: 1.5rem;
        grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    }

    .card-souvenir {
        height: 320px;
    }

    .content {
        padding: 1.5rem;
    }

    .title {
        font-size: 1.45rem;
        margin-bottom: 1rem;
    }

    .description {
        font-size: 0.95rem;
        line-height: 1.65;
        margin-bottom: 1rem;
    }
}

/* 移动端：单列 + 上下结构卡片 */
@media (max-width: 768px) {
    .card-container {
        padding: 1rem;
        gap: 1rem;
        grid-template-columns: 1fr;
    }

    .card-souvenir {
        height: auto;
        flex-direction: column;
        border-radius: 14px;
    }

    .card-image-wrapper {
        flex: 0 0 auto;
        width: 100%;
        height: 200px;
    }

    .image-main {
        border-radius: 14px 14px 0 0;
    }

    .content {
        flex: 0 0 auto;
        padding: 1rem 1.15rem 1.15rem;
        justify-content: flex-start;
    }

    .title {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        font-size: 1.2rem;
        line-height: 1.35;
        margin-bottom: 0.85rem;
        letter-spacing: 0.2px;
    }

    .title::after {
        bottom: -6px;
        width: 36px;
        height: 2px;
    }

    .description {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        font-size: 0.9rem;
        line-height: 1.6;
        margin-bottom: 0.85rem;
    }

    .card-price {
        font-size: 1.1em;
    }

    .loading,
    .error-state,
    .empty-state {
        padding: 28px 16px;
    }
}

/* 小屏手机：进一步压缩图片高度 */
@media (max-width: 420px) {
    .card-container {
        padding: 0.75rem;
        gap: 0.85rem;
    }

    .card-image-wrapper {
        height: 170px;
    }

    .title {
        font-size: 1.1rem;
    }
}
</style>