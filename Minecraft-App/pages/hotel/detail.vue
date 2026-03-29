<template>
  <div class="detail-container">
    <view class="detail-header">
      <image :src="hotel.coverImage" :alt="hotel.name" class="detail-image"></image>
      <view class="detail-title">
        <h1>{{ hotel.name }}</h1>
        <p class="location">{{ hotel.city }}</p>
      </view>
    </view>
    
    <view class="detail-content">
      <view class="section">
        <h2>酒店介绍</h2>
        <p>{{ hotel.description }}</p>
      </view>
      
      <view class="section">
        <h2>价格</h2>
        <div class="price">¥{{ hotel.price }}/晚</div>
      </view>
      
      <view class="section">
        <h2>地址</h2>
        <p>{{ hotel.address }}</p>
      </view>
      
      <view class="section">
        <h2>联系电话</h2>
        <p>{{ hotel.phone }}</p>
      </view>
    </view>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotelDetail } from '@/api/hotel'

const hotel = ref({})

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const { id } = currentPage.options || {}
  if (id) {
    try {
      const response = await getHotelDetail(id)
      hotel.value = response.data
    } catch (error) {
      console.error('获取酒店详情失败:', error)
    }
  }
})
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  background-color: var(--background-color);
}

.detail-header {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.detail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: white;
}

.detail-title h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.location {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.detail-content {
  padding: 20px;
}

.section {
  margin-bottom: 30px;
}

.section h2 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 500;
  color: var(--text-color);
}

.section p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-secondary-color);
}

.price {
  font-size: 20px;
  font-weight: 600;
  color: #F44336;
}
</style>