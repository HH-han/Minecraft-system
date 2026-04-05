<template>
  <div class="detail-container">
    <view class="detail-header">
      <image :src="attraction.coverImage" :alt="attraction.name" class="detail-image"></image>
      <view class="detail-title">
        <h1>{{ attraction.name }}</h1>
        <p class="location">{{ attraction.city }}</p>
        <div class="attraction-price" v-if="attraction.price > 0">¥{{ attraction.price }}</div>
        <div class="attraction-price free" v-else>免费</div>
        <div class="attraction-season" v-if="attraction.season">{{ attraction.season }}</div>
        <div class="attraction-stats">
          <span class="attraction-stat">{{ attraction.likeCount }} 点赞</span>
          <span class="attraction-stat">{{ attraction.commentCount }} 评论</span>
          <span class="attraction-stat">{{ attraction.collectCount }} 收藏</span>
        </div>
      </view>
    </view>
    
    <view class="detail-content">
      <view class="section">
        <h2>景点介绍</h2>
        <p>{{ attraction.description }}</p>
      </view>
      
      <view class="section">
        <h2>评分</h2>
        <div class="rating">
          <span class="star">★</span>
          <span>{{ attraction.rating }}</span>
        </div>
      </view>
      
      <view class="section">
        <h2>地址</h2>
        <p>{{ attraction.address }}</p>
      </view>
      
      <view class="section">
        <h2>开放时间</h2>
        <p>{{ attraction.openTime }}</p>
      </view>
    </view>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAttractionDetail } from '@/api/attraction'

const attraction = ref({})

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const { id } = currentPage.options || {}
  if (id) {
    try {
      const response = await getAttractionDetail(id)
      attraction.value = response.data
    } catch (error) {
      console.error('获取景点详情失败:', error)
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
  margin: 0 0 12px 0;
  font-size: 14px;
  opacity: 0.9;
}

.attraction-price {
  font-size: 20px;
  font-weight: 700;
  color: #F44336;
  margin-bottom: 12px;
}

.attraction-price.free {
  color: #4CAF50;
}

.attraction-season {
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-weight: 500;
  margin-bottom: 16px;
  display: inline-block;
}

.attraction-stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: white;
  opacity: 0.9;
}

.attraction-stat {
  display: flex;
  align-items: center;
  gap: 4px;
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