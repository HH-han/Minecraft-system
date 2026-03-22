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
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.user-review:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.15);
}

.review-header {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.review-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 20px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.review-stats {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255, 255, 255, 0.05);
  padding: 20px;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  min-width: 120px;
}

.rating-score {
  font-size: 32px;
  font-weight: 700;
  color: #ff4d4f;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.rating-stars {
  margin-bottom: 10px;
  display: flex;
  gap: 4px;
}

.star {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.star.active {
  color: #ff4d4f;
  text-shadow: 0 0 10px rgba(255, 77, 79, 0.5);
}

.rating-count {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

.rating-distribution {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  padding: 20px;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.rating-bar {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.rating-bar:last-child {
  margin-bottom: 0;
}

.star-label {
  width: 50px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.bar-container {
  flex: 1;
  height: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  margin: 0 15px;
  overflow: hidden;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.bar {
  height: 100%;
  background: linear-gradient(90deg, #ff4d4f 0%, #ff7875 100%);
  border-radius: 5px;
  transition: width 0.5s ease;
  box-shadow: 0 0 10px rgba(255, 77, 79, 0.3);
}

.count {
  width: 40px;
  text-align: right;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.review-list {
  margin-bottom: 25px;
}

.review-item {
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.review-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.review-user {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  gap: 15px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.user-avatar:hover img {
  transform: scale(1.1);
}

.user-info {
  flex: 1;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 5px;
}

.review-time {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-content {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  border-left: 4px solid #ff4d4f;
}

.review-images {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.review-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.review-image:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.review-spec {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.05);
  padding: 6px 12px;
  border-radius: 8px;
  display: inline-block;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.review-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  font-weight: 500;
}

.page-btn:hover:not(:disabled) {
  border-color: #ff4d4f;
  color: #ff4d4f;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
  background: rgba(255, 77, 79, 0.05);
}

.page-btn:disabled {
  color: rgba(255, 255, 255, 0.3);
  cursor: not-allowed;
  border-color: rgba(255, 255, 255, 0.1);
}

.page-info {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

@media (max-width: 768px) {
  .user-review {
    padding: 20px;
  }
  
  .review-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .average-rating {
    padding: 15px;
  }
  
  .rating-distribution {
    padding: 15px;
  }
  
  .review-user {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .user-info {
    width: 100%;
  }
  
  .review-rating {
    align-self: flex-start;
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
}

@media (max-width: 480px) {
  .user-review {
    padding: 15px;
  }
  
  .review-header h3 {
    font-size: 18px;
  }
  
  .rating-score {
    font-size: 24px;
  }
  
  .star {
    font-size: 16px;
  }
  
  .review-item {
    padding: 15px;
  }
  
  .user-avatar {
    width: 40px;
    height: 40px;
  }
  
  .review-image {
    width: 70px;
    height: 70px;
  }
}
</style>