<template>
  <div class="detail-container">
    <view class="detail-header">
      <view class="detail-title">
        <h1>{{ news.title }}</h1>
        <div class="news-meta">
          <span class="news-date">{{ news.createTime }}</span>
          <span class="news-view">{{ news.viewCount }} 浏览</span>
        </div>
      </view>
    </view>
    
    <view class="detail-content">
      <view class="section">
        <h2>新闻内容</h2>
        <p>{{ news.content }}</p>
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
import { getNewsById } from '@/api/news'

const news = ref({})

onMounted(async () => {
  const { id } = uni.getStorageSync('uniRouter')?.query || {}
  if (id) {
    try {
      const response = await getNewsById(id)
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
}

.detail-title h1 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
}

.news-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--text-secondary-color);
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
</style>