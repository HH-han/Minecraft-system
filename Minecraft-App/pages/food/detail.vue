<template>
  <div class="detail-container">
    <view class="detail-header">
      <image :src="food.coverImage" :alt="food.name" class="detail-image"></image>
      <view class="detail-title">
        <h1>{{ food.name }}</h1>
        <p class="location">{{ food.city }} - {{ food.cuisine }}</p>
      </view>
    </view>
    
    <view class="detail-content">
      <view class="section">
        <h2>美食介绍</h2>
        <p>{{ food.description }}</p>
      </view>
      
      <view class="section">
        <h2>评分</h2>
        <div class="rating">
          <span class="star">★</span>
          <span>{{ food.rating }}</span>
        </div>
      </view>
      
      <view class="section">
        <h2>地址</h2>
        <p>{{ food.address }}</p>
      </view>
      
      <view class="section">
        <h2>推荐餐厅</h2>
        <p>{{ food.restaurant }}</p>
      </view>
    </view>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFoodDetail } from '@/api/food'

const food = ref({})

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const { id } = currentPage.options || {}
  if (id) {
    try {
      const response = await getFoodDetail(id)
      food.value = response.data
    } catch (error) {
      console.error('获取美食详情失败:', error)
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

.rating {
  display: flex;
  align-items: center;
  font-size: 18px;
  color: #FF9800;
}

.star {
  margin-right: 8px;
}
</style>