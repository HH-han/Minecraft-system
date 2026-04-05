<template>
  <div class="detail-container">
    <view class="detail-header">
      <view class="detail-title">
        <h1>{{ news.title }}</h1>
        <div class="news-meta">
          <span class="news-source">{{ news.source }}</span>
          <span class="news-date">{{ news.createTime }}</span>
        </div>
        <div class="news-stats">
          <span class="news-stat">{{ news.viewCount }} 浏览</span>
          <span class="news-stat">{{ news.likeCount }} 点赞</span>
          <span class="news-stat">{{ news.commentCount }} 评论</span>
          <span class="news-stat">{{ news.collectCount }} 收藏</span>
        </div>
      </view>
      <div class="detail-image" v-if="news.coverImage">
        <img :src="news.coverImage" :alt="news.title">
      </div>
    </view>
    
    <view class="detail-content">
      <view class="section">
        <h2>新闻内容</h2>
        <div class="content-text">{{ news.content }}</div>
      </view>
      
      <view class="section">
        <h2>新闻摘要</h2>
        <p>{{ news.description }}</p>
      </view>
    </view>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewsDetail } from '@/api/news'

const news = ref({})

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const { id } = currentPage.options || {}
  if (id) {
    try {
      const response = await getNewsDetail(id)
      news.value = response.data
    } catch (error) {
      console.error('获取新闻详情失败:', error)
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
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background-color: white;
}

.detail-title h1 {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
  line-height: 1.3;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: var(--text-secondary-color);
  margin-bottom: 12px;
}

.news-source {
  background: rgba(30, 136, 229, 0.1);
  padding: 4px 12px;
  border-radius: 4px;
  color: var(--primary-color);
  font-weight: 500;
  font-size: 12px;
}

.news-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: var(--text-secondary-color);
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  margin-top: 12px;
}

.news-stat {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-image {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.detail-image img {
  width: 100%;
  height: auto;
  max-height: 400px;
  object-fit: cover;
}

.detail-content {
  padding: 20px;
  background-color: white;
  margin-top: 10px;
}

.section {
  margin-bottom: 30px;
}

.section h2 {
  margin: 0 0 16px 0;
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

.content-text {
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-color);
  white-space: pre-wrap;
  word-break: break-word;
}

@media (max-width: 768px) {
  .detail-header {
    padding: 16px;
  }
  
  .detail-title h1 {
    font-size: 20px;
  }
  
  .detail-content {
    padding: 16px;
  }
  
  .news-stats {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .detail-image img {
    max-height: 250px;
  }
}
</style>