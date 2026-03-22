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
                </div>
            </div>
        </div>
        <div v-if="hotels.length === 0 && !loading" class="empty-state">
            <p>暂无酒店数据</p>
        </div>
    </div>

    <!-- 酒店详情模态框 -->
    <div v-if="showModal" class="glass-modal" @click="closeModal">
        <div class="glass-modal-content" @click.stop>
            <button class="close-btn" @click="closeModal">&times;</button>
            <div class="modal-body">
                <div class="modal-image">
                    <img :src="selectedHotel?.coverImage" :alt="selectedHotel?.name">
                </div>
                <div class="modal-info">
                    <h2 class="modal-title">{{ selectedHotel?.name }}</h2>
                    <div class="modal-rating">
                        <span class="rating">{{ selectedHotel?.rating }}</span>
                        <span class="rating-text">{{ selectedHotel?.rating >= 4.5 ? '超棒' : selectedHotel?.rating >= 4 ? '很好' : '好' }}</span>
                        <span class="review-count">({{ selectedHotel?.commentCount }}条点评)</span>
                    </div>
                    <div class="modal-location">
                        <i class="location-icon">📍</i>
                        <span>{{ selectedHotel?.address }}</span>
                    </div>
                    <div class="modal-description">
                        <h3>酒店介绍</h3>
                        <p>{{ selectedHotel?.description }}</p>
                    </div>
                    <div class="modal-stats">
                        <div class="stat-item">
                            <span class="stat-label">收藏数</span>
                            <span class="stat-value">{{ selectedHotel?.collectCount }}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">点赞数</span>
                            <span class="stat-value">{{ selectedHotel?.likeCount }}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-label">价格</span>
                            <span class="stat-value">¥{{ selectedHotel?.price }}</span>
                        </div>
                    </div>
                    <div class="modal-actions">
                        <button class="btn secondary" @click="closeModal">关闭</button>
                        <button class="btn primary" @click="OrderDetails(selectedHotel)">前往预订</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getHotelList } from '@/api/hotel.js'

// 响应式数据
const hotels = ref([])
const loading = ref(false)
const error = ref('')
const router = useRouter()
const showModal = ref(false)
const selectedHotel = ref(null)

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
    selectedHotel.value = hotel
    showModal.value = true
}

// 关闭详情页
const closeModal = () => {
    showModal.value = false
    selectedHotel.value = null
}

// 跳转到订单详情页
const OrderDetails = (hotel) => {
    console.log('前往预订:', hotel)
    router.push({
        path: '/predetermined',
        query: {
            activeTab: 'hotel',
            hotelData: JSON.stringify(hotel)
        }
    })
    closeModal()
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

/* 液态玻璃模态框 */
.glass-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.glass-modal-content {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  max-width: 900px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: #333;
  transition: all 0.3s ease;
  z-index: 10;
}

.close-btn:hover {
  color: #ff6a00;
  transform: scale(1.1);
}

.modal-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  padding: 30px;
}

.modal-image {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.modal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.modal-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.modal-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-rating .rating {
  background-color: #ff6a00;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 16px;
}

.modal-rating .rating-text {
  color: #ff6a00;
  font-weight: bold;
}

.modal-rating .review-count {
  color: #666;
  font-size: 14px;
}

.modal-location {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.modal-description {
  margin-top: 10px;
}

.modal-description h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.modal-description p {
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

.modal-stats {
  display: flex;
  gap: 30px;
  margin-top: 10px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.modal-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.modal-actions .btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-actions .btn.secondary {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.modal-actions .btn.secondary:hover {
  background-color: #e0e0e0;
}

.modal-actions .btn.primary {
  background-color: #ff6a00;
  color: white;
}

.modal-actions .btn.primary:hover {
  background-color: #ff8c00;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 106, 0, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modal-body {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .modal-image {
    height: 250px;
  }
  
  .modal-title {
    font-size: 24px;
  }
  
  .modal-stats {
    gap: 20px;
  }
}
</style>