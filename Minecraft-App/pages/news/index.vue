<template>
  <div class="news-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" v-model="searchQuery" placeholder="搜索新闻" class="search-input">
      <button class="search-btn">搜索</button>
    </div>

    <!-- 新闻列表 -->
    <div class="news-list">
      <div v-for="news in newsList" :key="news.id" class="news-item" @click="navigateToDetail(news.id)">
        <div class="news-content">
          <div class="news-image" v-if="news.coverImage">
            <img :src="news.coverImage" :alt="news.title">
          </div>
          <div class="news-info">
            <h3>{{ news.title }}</h3>
            <p class="news-desc">{{ news.description }}</p>
            <div class="news-meta">
              <span class="news-source">{{ news.source }}</span>
              <span class="news-date">{{ news.createTime }}</span>
            </div>
          </div>
        </div>
        <div class="news-stats">
          <span class="news-stat">{{ news.viewCount }} 浏览</span>
          <span class="news-stat">{{ news.likeCount }} 点赞</span>
          <span class="news-stat">{{ news.commentCount }} 评论</span>
          <span class="news-stat">{{ news.collectCount }} 收藏</span>
        </div>
      </div>
    </div>

    <!-- 无数据提示 -->
    <div v-if="newsList.length === 0" class="empty-tip">
      暂无新闻数据
    </div>

    <!-- 分页 -->
    <div v-if="newsList.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewsList } from '@/api/news'

// 搜索参数
const searchQuery = ref('')

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 新闻列表数据
const newsList = ref([])

// 计算总页数
const calculateTotalPages = (total) => {
  return Math.ceil(total / pageSize.value)
}

// 获取新闻列表
const fetchNewsList = async () => {
  try {
    const response = await getNewsList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value
    })
    newsList.value = response.data.records || []
    totalPages.value = calculateTotalPages(response.data.total || 0)
  } catch (error) {
    console.error('获取新闻列表失败:', error)
  }
}

// 导航到详情页
const navigateToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/news/detail?id=${id}`
  })
}

// 切换页码
const changePage = (page) => {
  currentPage.value = page
  fetchNewsList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.news-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
}

.search-input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(30, 136, 229, 0.1);
}

.search-btn {
  padding: 12px 24px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn:hover {
  background-color: var(--primary-dark);
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.news-item {
  background-color: var(--card-background);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.news-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.news-content {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.news-image {
  flex: 0 0 100px;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.news-item:hover .news-image img {
  transform: scale(1.05);
}

.news-info {
  flex: 1;
  min-width: 0;
}

.news-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.news-desc {
  margin: 0 0 12px 0;
  font-size: 14px;
  line-height: 1.4;
  color: var(--text-secondary-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
}

.news-source {
  background: rgba(30, 136, 229, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  color: var(--primary-color);
  font-weight: 500;
  font-size: 11px;
}

.news-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-stats {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  font-size: 12px;
  color: var(--text-secondary-color);
  padding-top: 8px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.news-stat {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.news-item:hover .news-stat {
  color: var(--primary-color);
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: var(--text-secondary-color);
  font-size: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  background-color: white;
  color: var(--text-color);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: var(--text-color);
}

@media (max-width: 768px) {
  .news-list {
    gap: 12px;
  }

  .news-item {
    padding: 12px;
  }
}
</style>