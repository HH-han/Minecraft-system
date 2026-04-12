<template>
  <div class="detail-container">
    <view class="detail-header">
      <image :src="food.coverImage" :alt="food.name" class="detail-image"></image>
      <view class="detail-title">
        <h1>{{ food.name }}</h1>
        <p class="location">{{ food.city }} - {{ food.cuisineType }}</p>
        <div class="food-price">¥{{ food.price }}</div>
        <div class="food-tags">
          <span v-for="tag in food.tags.split(',')" :key="tag" class="food-tag">{{ tag }}</span>
        </div>
        <div class="food-stats">
          <span class="food-stat">{{ food.likeCount }} 点赞</span>
          <span class="food-stat">{{ food.commentCount }} 评论</span>
          <span class="food-stat">{{ food.collectCount }} 收藏</span>
        </div>
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
        <h2>图片展示</h2>
        <div class="image-gallery">
          <image v-for="(img, index) in food.images.split(',')" :key="index" :src="img.trim()" :alt="food.name + index" class="gallery-image"></image>
        </div>
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
  margin: 0 0 12px 0;
  font-size: 14px;
  opacity: 0.9;
}

.food-price {
  font-size: 20px;
  font-weight: 700;
  color: #F44336;
  margin-bottom: 12px;
}

.food-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.food-tag {
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-weight: 500;
}

.food-stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: white;
  opacity: 0.9;
}

.food-stat {
  display: flex;
  align-items: center;
  gap: 4px;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.gallery-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.gallery-image:hover {
  transform: scale(1.05);
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