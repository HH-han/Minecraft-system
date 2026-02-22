<template>
    <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
    </div>
    <div v-else-if="error" class="error-state">
        <p>加载失败: {{ error }}</p>
        <button @click="fetchHotels" class="btn retry">重试</button>
    </div>
    <div v-else class="card-container">
        <div v-for="hotel in hotels" :key="hotel.id" class="card" @click="openDetail(hotel)">
            <img :src="hotel.coverImage" :alt="hotel.name" class="card-image">
            <div class="card-content">
                <h3 class="card-title">{{ hotel.name }}</h3>
                <p class="card-desc">{{ hotel.description }}</p>
                <div class="card-stats">
                    <span class="card-sales">
                        <svg t="1745755200324" class="icon" viewBox="0 0 1331 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="5802" width="16" height="16">
                            <path
                                d="M330.67008 602.84928V1024h198.4V602.84928l-99.20512-82.5856-99.19488 82.5856zM0 1024h198.4V701.93152L0 858.83904V1024z m992-520.25344V1024h198.4V338.5856L992 503.74656z m-330.65984 206.4384V1024h198.38976V611.10272L702.6688 743.2192l-41.32864-33.03424zM1322.67008 0H942.3872l157.07136 148.64384-405.07392 330.32192L429.8752 256 0 594.5856V743.2192l429.8752-338.5856L702.6688 627.6096l487.7312-396.3904 132.27008 123.87328V0z"
                                fill="#d81e06" p-id="5803"></path>
                        </svg> 收藏:{{ hotel.collectCount }}
                    </span>
                    <span class="card-rating">
                        <svg t="1745755283144" class="icon" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="6920" width="16" height="16">
                            <path
                                d="M264.107 948.053a34.133 34.133 0 0 1-50.774-35.84l46.934-276.053L61.44 440.747A34.133 34.133 0 0 1 80.213 384l277.334-42.667 124.16-250.88a34.133 34.133 0 0 1 61.013 0l123.733 250.88L943.787 384a34.133 34.133 0 0 1 18.773 58.027L762.453 637.44l48.214 274.773a34.133 34.133 0 0 1-49.494 35.84l-247.893-128z"
                                fill="#d81e06" p-id="6921"></path>
                        </svg> 评分:{{ hotel.rating }}
                    </span>
                </div>
                <div class="card-footer">
                    <span class="card-price">¥{{ hotel.price }}</span>
                    <div class="card-buttons">
                        <button @click="OrderDetails(hotel.id)" class="btn pay">前往预订</button>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="hotels.length === 0 && !loading" class="empty-state">
            <p>暂无酒店数据</p>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getHotelList } from '@/api/hotel.js'

// 响应式数据
const hotels = ref([])
const loading = ref(false)
const error = ref('')

// 获取酒店数据
const fetchHotels = async () => {
    loading.value = true
    error.value = ''
    
    try {
        const response = await getHotelList()
        hotels.value = response.data?.records || []
    } catch (err) {
        error.value = err.message || '获取数据失败'
        console.error('获取酒店数据失败:', err)
    } finally {
        loading.value = false
    }
}

// 组件挂载时获取数据
onMounted(() => {
    fetchHotels()
})

// 打开详情页
const openDetail = (hotel) => {
    console.log('打开酒店详情:', hotel)
}

// 跳转到订单详情页
const OrderDetails = (hotelId) => {
    console.log('前往预订:', hotelId)
}
</script>
<style scoped>
/* 美食卡片 */
.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
}

.card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.card-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.card-content {
  padding: 20px;
}

.card-title {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 1.2em;
}

.card-desc {
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
}

.card-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  color: #e74c3c;
  font-size: 14px;
}

.card-sales,
.card-rating {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-price {
  color: #e74c3c;
  font-size: 1.2em;
  font-weight: bold;
}

.card-buttons {
  display: flex;
  gap: 10px;
}

.cart-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.cart-btn:hover {
  background: #2980b9;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin: 30px 0;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover {
  background: #f5f5f5;
}

.page-btn.active {
  background: #e74c3c;
  color: white;
  border-color: #e74c3c;
}

/* 美食详情弹窗 */
.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  -webkit-backdrop-filter: blur(5px);
  /* 为 Safari 和 iOS Safari 添加前缀 */
  backdrop-filter: blur(5px);
  /* 背景模糊效果 */
}

.detail-content {
  background: white;
  border-radius: 15px;
  max-width: 800px;
  width: 90%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  overflow: hidden;
  position: relative;
  animation: modal-enter 0.3s ease-out;
}

@keyframes modal-enter {
  from {
    opacity: 0;
    transform: scale(0.9);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

.detail-close {
  position: absolute;
  right: 20px;
  top: 20px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.detail-close:hover {
  color: #333;
}

.detail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  padding: 20px;
}

.detail-desc {
  color: #666;
  line-height: 1.6;
  margin: 20px 0;
}

.detail-stats {
  background: #f8f8f8;
  box-shadow: 0 0 20px rgba(38, 38, 38, 0.252);
  padding: 15px;
  border-radius: 8px;
  margin: 20px 0;
}

.detail-stats p {
  margin: 10px 0;
  color: #444;
}

.detail-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.detail-cart,
.detail-buy {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.detail-cart {
  background: #3498db;
  color: white;
}

.detail-buy {
  background: #e74c3c;
  color: white;
}

.detail-cart:hover {
  background: #2980b9;
}

.detail-buy:hover {
  background: #c0392b;
}
</style>