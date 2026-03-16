<template>
  <div class="user-review">
    <div class="review-header">
      <h3>用户评价</h3>
      <div class="review-stats">
        <div class="average-rating">
          <div class="rating-score">{{ averageRating }}</div>
          <div class="rating-stars">
            <span 
              v-for="i in 5" 
              :key="i"
              class="star"
              :class="{ active: i <= Math.round(averageRating) }"
            >★</span>
          </div>
          <div class="rating-count">{{ reviews.length }} 条评价</div>
        </div>
        <div class="rating-distribution">
          <div v-for="(count, star) in ratingDistribution" :key="star" class="rating-bar">
            <span class="star-label">{{ star }}星</span>
            <div class="bar-container">
              <div 
                class="bar" 
                :style="{ width: (count / reviews.length * 100) + '%' }"
              ></div>
            </div>
            <span class="count">{{ count }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="review-list">
      <div v-for="(review, index) in reviews" :key="index" class="review-item">
        <div class="review-user">
          <div class="user-avatar">
            <img :src="review.avatar" :alt="review.username">
          </div>
          <div class="user-info">
            <div class="username">{{ review.username }}</div>
            <div class="review-time">{{ review.time }}</div>
          </div>
          <div class="review-rating">
            <span 
              v-for="i in 5" 
              :key="i"
              class="star"
              :class="{ active: i <= review.rating }"
            >★</span>
          </div>
        </div>
        <div class="review-content">{{ review.content }}</div>
        <div class="review-images" v-if="review.images && review.images.length > 0">
          <img 
            v-for="(image, imgIndex) in review.images" 
            :key="imgIndex"
            :src="image"
            :alt="'Review image ' + (imgIndex + 1)"
            class="review-image"
          >
        </div>
        <div class="review-spec" v-if="review.spec">
          购买规格：{{ review.spec }}
        </div>
      </div>
    </div>
    
    <div class="review-pagination" v-if="totalPages > 1">
      <button 
        class="page-btn" 
        :disabled="currentPage === 1"
        @click="currentPage--"
      >上一页</button>
      <span class="page-info">
        {{ currentPage }} / {{ totalPages }}
      </span>
      <button 
        class="page-btn" 
        :disabled="currentPage === totalPages"
        @click="currentPage++"
      >下一页</button>
    </div>
  </div>
</template>

<script setup>

import { onMounted } from 'vue' 
import { ref, computed } from 'vue'

const reviews = ref([
  {
    id: 1,
    username: '史蒂夫',
    avatar: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20Steve%20avatar&size=128x128',
    rating: 5,
    time: '2024-01-15 10:30',
    content: '非常棒的游戏！画面精美，玩法丰富，是我玩过的最好的沙盒游戏之一。推荐给所有喜欢创造和探索的玩家。',
    images: [
      'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20gameplay%20screenshot&size=512x512'
    ],
    spec: 'Java版 标准版'
  },
  {
    id: 2,
    username: '爱丽克斯',
    avatar: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20Alex%20avatar&size=128x128',
    rating: 4,
    time: '2024-01-10 14:20',
    content: '游戏很好玩，但是有时候会有点卡。总体来说还是很值得购买的。',
    images: [],
    spec: '基岩版 豪华版'
  },
  {
    id: 3,
    username: ' Notch',
    avatar: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20Notch%20avatar&size=128x128',
    rating: 5,
    time: '2024-01-05 12:15',
    content: '游戏的创建者，非常有才华和创新精神。',
    images: [],
    spec: 'Java版 标准版'
  },
  {
    id: 4,
    username: '开发者',
    avatar: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Minecraft%20Notch%20avatar&size=128x128',
    rating: 5,
    time: '2024-01-05 09:15',
    content: '作为开发者，我非常满意这款游戏的表现。希望大家喜欢！',
    images: [],
    spec: 'Java版 终极版'
  }
]);


const currentPage = ref(1)
const pageSize = 10

const totalPages = computed(() => {
  return Math.ceil(reviews.value.length / pageSize)
})

const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0
  const sum = reviews.value.reduce((acc, review) => acc + review.rating, 0)
  return (sum / reviews.value.length).toFixed(1)
})

const ratingDistribution = computed(() => {
  const distribution = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
  reviews.value.forEach(review => {
    distribution[review.rating]++
  })
  return distribution
})
</script>

<style scoped>
.user-review {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.review-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.review-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
}

.review-stats {
  display: flex;
  gap: 30px;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rating-score {
  font-size: 24px;
  font-weight: 600;
  color: #ff4d4f;
  margin-bottom: 8px;
}

.rating-stars {
  margin-bottom: 8px;
}

.star {
  font-size: 16px;
  color: #d9d9d9;
  margin-right: 2px;
}

.star.active {
  color: #ff4d4f;
}

.rating-count {
  font-size: 14px;
  color: #666;
}

.rating-distribution {
  flex: 1;
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.star-label {
  width: 40px;
  color: #666;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  margin: 0 10px;
  overflow: hidden;
}

.bar {
  height: 100%;
  background: #ff4d4f;
  border-radius: 4px;
  transition: width 0.3s;
}

.count {
  width: 30px;
  text-align: right;
  color: #666;
}

.review-list {
  margin-bottom: 20px;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.review-item:last-child {
  border-bottom: none;
}

.review-user {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  margin-right: 12px;
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-rating {
  display: flex;
}

.review-content {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.review-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.review-spec {
  font-size: 12px;
  color: #999;
  background: #f9f9f9;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.review-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.page-btn:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .user-review {
    padding: 15px;
  }
  
  .review-stats {
    flex-direction: column;
    gap: 15px;
  }
  
  .review-user {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .user-info {
    width: 100%;
  }
  
  .review-rating {
    align-self: flex-start;
  }
  
  .review-images {
    flex-wrap: wrap;
  }
  
  .review-image {
    width: 70px;
    height: 70px;
  }
}
</style>