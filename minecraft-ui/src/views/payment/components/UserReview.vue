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
.user-review {
  --text-primary: #1d1d1f;
  --text-secondary: #6e6e73;
  --text-tertiary: #8e8e93;
  --bg-primary: #ffffff;
  --bg-secondary: #f5f5f7;
  --bg-tertiary: #fafafa;
  --accent: #2997ff;
  --star: #ff9500;
  --divider: #e5e5ea;

  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
  color: var(--text-primary);
}

/* Header */
.review-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--divider);
}

.review-header h3 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 20px 0;
  letter-spacing: -0.02em;
}

/* Rating Stats */
.review-stats {
  display: flex;
  gap: 20px;
  align-items: stretch;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff8ee, #fff3e0);
  padding: 24px 28px;
  border-radius: 16px;
  min-width: 150px;
  border: 1px solid rgba(255, 149, 0, 0.15);
}

.rating-score {
  font-size: 44px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
  letter-spacing: -0.03em;
  line-height: 1;
}

.rating-stars {
  margin-bottom: 8px;
  display: flex;
  gap: 3px;
}

.star {
  font-size: 18px;
  color: var(--divider);
  transition: color 0.2s ease, transform 0.2s ease;
}

.star.active {
  color: var(--star);
  filter: drop-shadow(0 1px 2px rgba(255, 149, 0, 0.3));
}

.rating-count {
  font-size: 13px;
  font-weight: 400;
  color: var(--text-secondary);
}

/* Rating Distribution */
.rating-distribution {
  flex: 1;
  background: var(--bg-secondary);
  padding: 20px 22px;
  border-radius: 16px;
  border: 1px solid #f0f0f2;
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.rating-bar:last-child {
  margin-bottom: 0;
}

.star-label {
  width: 36px;
  color: var(--text-secondary);
  font-weight: 500;
  flex-shrink: 0;
}

.star-label .mini-star {
  color: var(--star);
  margin-right: 2px;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: #e8e8ed;
  border-radius: 4px;
  margin: 0 12px;
  overflow: hidden;
}

.bar {
  height: 100%;
  background: linear-gradient(90deg, #ffb340, #ff9500);
  border-radius: 4px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.count {
  width: 28px;
  text-align: right;
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 12px;
}

/* Review List */
.review-list {
  margin-bottom: 24px;
}

.review-item {
  padding: 20px;
  margin-bottom: 12px;
  border-radius: 16px;
  background: var(--bg-tertiary);
  border: 1px solid #f0f0f2;
  transition: all 0.2s ease;
}

.review-item:last-child {
  margin-bottom: 0;
}

.review-item:hover {
  background: var(--bg-secondary);
  border-color: #e5e5ea;
}

/* Review User */
.review-user {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
  gap: 12px;
}

.user-avatar {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--bg-primary);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  background: var(--divider);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.review-time {
  font-size: 12px;
  font-weight: 400;
  color: var(--text-tertiary);
}

.review-rating {
  display: flex;
  gap: 1px;
}

.review-rating .star {
  font-size: 15px;
}

/* Review Content */
.review-content {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-primary);
  line-height: 1.7;
  margin-bottom: 14px;
}

/* Review Images */
.review-images {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.review-image {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: 12px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  border: 1px solid #f0f0f2;
  cursor: pointer;
}

.review-image:hover {
  transform: scale(1.03);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
}

/* Review Spec */
.review-spec {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary);
  background: var(--bg-primary);
  padding: 5px 12px;
  border-radius: 8px;
  display: inline-block;
  border: 1px solid #f0f0f2;
}

/* Pagination */
.review-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--divider);
}

.page-btn {
  padding: 9px 20px;
  border: 1px solid var(--divider);
  border-radius: 10px;
  background: var(--bg-primary);
  color: var(--text-primary);
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
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
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

/* Responsive */
@media (max-width: 767px) {
  .review-header h3 { font-size: 20px; }
  .review-stats {
    flex-direction: column;
    gap: 14px;
  }
  .average-rating {
    width: 100%;
    padding: 20px;
  }
  .rating-distribution {
    width: 100%;
    padding: 18px;
  }
  .rating-score { font-size: 36px; }
  .review-user { flex-wrap: wrap; }
  .review-rating { width: 100%; margin-top: 4px; }
  .review-image { width: 80px; height: 80px; }
  .review-item { padding: 18px; }
}

@media (max-width: 480px) {
  .review-header h3 { font-size: 18px; }
  .rating-score { font-size: 30px; }
  .star { font-size: 16px; }
  .review-item { padding: 16px; border-radius: 14px; }
  .user-avatar { width: 40px; height: 40px; }
  .username { font-size: 14px; }
  .review-content { font-size: 13px; }
  .review-image { width: 70px; height: 70px; border-radius: 10px; }
  .page-btn { padding: 8px 16px; font-size: 12px; }
}
</style>