<template>
  <div class="detail-container">
    <view class="detail-header">
      <image :src="hotel.coverImage" :alt="hotel.name" class="detail-image"></image>
      <view class="detail-title">
        <h1>{{ hotel.name }}</h1>
        <p class="location">{{ hotel.city }}</p>
        <div class="hotel-stars">
          <span v-for="i in hotel.starLevel" :key="i" class="star">★</span>
        </div>
        <div class="hotel-price">¥{{ hotel.price }}/晚</div>
        <div class="hotel-facilities">
          <span v-for="facility in (hotel.facilities ? hotel.facilities.split(',') : [])" :key="facility" class="hotel-facility">{{ facility }}</span>
        </div>
        <div class="hotel-stats">
          <span class="hotel-stat">{{ hotel.likeCount }} 点赞</span>
          <span class="hotel-stat">{{ hotel.commentCount }} 评论</span>
          <span class="hotel-stat">{{ hotel.collectCount }} 收藏</span>
        </div>
      </view>
    </view>
    
    <view class="detail-content">
      <view class="section">
        <h2>酒店介绍</h2>
        <p>{{ hotel.description }}</p>
      </view>
      
      <view class="section">
        <h2>地址</h2>
        <p>{{ hotel.address }}</p>
      </view>
      
      <view class="section">
        <h2>联系电话</h2>
        <p>{{ hotel.phone }}</p>
      </view>
      
      <view class="section">
        <h2>图片展示</h2>
        <div class="image-gallery">
          <image v-for="(img, index) in (hotel.images ? parseImages(hotel.images) : [])" :key="index" :src="img.trim()" :alt="hotel.name + index" class="gallery-image"></image>
        </div>
      </view>
    </view>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotelDetail } from '@/api/hotel'

const hotel = ref({})

// 安全解析图片数组
const parseImages = (imagesStr) => {
  try {
    return JSON.parse(imagesStr)
  } catch (error) {
    return []
  }
}

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
  margin: 0 0 12px 0;
  font-size: 14px;
  opacity: 0.9;
}

.hotel-stars {
  display: flex;
  align-items: center;
  margin: 8px 0;
}

.hotel-stars .star {
  color: #FF9800;
  font-size: 16px;
  margin-right: 4px;
}

.hotel-price {
  font-size: 20px;
  font-weight: 700;
  color: #F44336;
  margin-bottom: 12px;
}

.hotel-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.hotel-facility {
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-weight: 500;
}

.hotel-stats {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: white;
  opacity: 0.9;
}

.hotel-stat {
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
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.section p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-secondary-color);
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
</style>