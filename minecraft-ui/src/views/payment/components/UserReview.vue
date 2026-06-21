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
])


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
/* Apple Design System Variables */
.user-review {
  --text-primary: #1d1d1f;
  --text-secondary: #6e6e73;
  --bg-primary: #ffffff;
  --bg-secondary: #f5f5f7;
  --accent: #2997ff;
  --divider: #d2d2d6;
  
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--bg-primary);
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 24px;
}

/* Header Section */
.review-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--divider);
}

.review-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 20px 0;
  letter-spacing: -0.02em;
}

/* Rating Stats */
.review-stats {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: var(--bg-secondary);
  padding: 24px;
  border-radius: 24px;
  min-width: 140px;
}

.rating-score {
  font-size: 48px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  letter-spacing: -0.03em;
}

.rating-stars {
  margin-bottom: 8px;
  display: flex;
  gap: 4px;
}

.star {
  font-size: 18px;
  color: var(--divider);
  transition: color 0.2s ease;
}

.star.active {
  color: var(--accent);
}

.rating-count {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-secondary);
}

/* Rating Distribution */
.rating-distribution {
  flex: 1;
  background: var(--bg-secondary);
  padding: 24px;
  border-radius: 24px;
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.rating-bar:last-child {
  margin-bottom: 0;
}

.star-label {
  width: 44px;
  color: var(--text-secondary);
  font-weight: 400;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: var(--divider);
  border-radius: 4px;
  margin: 0 12px;
  overflow: hidden;
}

.bar {
  height: 100%;
  background: var(--accent);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.count {
  width: 32px;
  text-align: right;
  color: var(--text-secondary);
  font-weight: 400;
}

/* Review List */
.review-list {
  margin-bottom: 24px;
}

.review-item {
  padding: 24px;
  margin-bottom: 16px;
  border-radius: 24px;
  background: var(--bg-secondary);
  transition: background 0.2s ease;
}

.review-item:last-child {
  margin-bottom: 0;
}

.review-item:hover {
  background: #ebebed;
}

/* Review User */
.review-user {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid var(--divider);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.review-time {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-secondary);
}

.review-rating {
  display: flex;
  gap: 2px;
}

/* Review Content */
.review-content {
  font-size: 16px;
  font-weight: 400;
  color: var(--text-primary);
  line-height: 1.6;
  margin-bottom: 16px;
}

/* Review Images */
.review-images {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.review-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 16px;
  transition: transform 0.2s ease;
  border: 1px solid var(--divider);
}

.review-image:hover {
  transform: scale(1.02);
}

/* Review Spec */
.review-spec {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-secondary);
  background: var(--bg-primary);
  padding: 8px 16px;
  border-radius: 12px;
  display: inline-block;
  border: 1px solid var(--divider);
}

/* Pagination */
.review-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--divider);
}

.page-btn {
  padding: 10px 20px;
  border: 1px solid var(--divider);
  border-radius: 12px;
  background: var(--bg-primary);
  color: var(--text-primary);
  cursor: pointer;
  font-size: 14px;
  font-weight: 400;
  transition: all 0.2s ease;
  font-family: inherit;
}

.page-btn:hover:not(:disabled) {
  border-color: var(--accent);
  color: var(--accent);
}

.page-btn:disabled {
  color: var(--divider);
  cursor: not-allowed;
  background: var(--bg-secondary);
}

.page-info {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-secondary);
}

/* Responsive Design */
@media (max-width: 767px) {
  .user-review {
    padding: 20px;
  }
  
  .review-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .average-rating {
    padding: 20px;
    width: 100%;
  }
  
  .rating-distribution {
    padding: 20px;
    width: 100%;
  }
  
  .review-user {
    flex-wrap: wrap;
  }
  
  .user-info {
    flex: 1;
    min-width: 0;
  }
  
  .review-rating {
    width: 100%;
    margin-top: 8px;
  }
  
  .review-images {
    gap: 10px;
  }
  
  .review-image {
    width: 80px;
    height: 80px;
  }
  
  .review-item {
    padding: 20px;
  }
  
  .review-header h3 {
    font-size: 20px;
  }
  
  .rating-score {
    font-size: 36px;
  }
}

@media (max-width: 480px) {
  .user-review {
    padding: 16px;
    border-radius: 20px;
  }
  
  .review-header h3 {
    font-size: 18px;
  }
  
  .rating-score {
    font-size: 28px;
  }
  
  .star {
    font-size: 16px;
  }
  
  .review-item {
    padding: 16px;
    border-radius: 20px;
  }
  
  .user-avatar {
    width: 40px;
    height: 40px;
  }
  
  .username {
    font-size: 15px;
  }
  
  .review-content {
    font-size: 15px;
  }
  
  .review-image {
    width: 70px;
    height: 70px;
    border-radius: 12px;
  }
  
  .page-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
}
</style>