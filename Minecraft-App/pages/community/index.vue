<template>
  <div class="community-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>社区</h1>
      <p>分享你的旅行经历和故事</p>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" v-model="searchQuery" placeholder="搜索帖子" class="search-input">
      <button class="search-btn">搜索</button>
    </div>
    
    <!-- 帖子列表 -->
    <div class="post-list">
      <div 
        v-for="post in posts" 
        :key="post.id" 
        class="post-item"
        @click="navigateToDetail(post.id)"
      >
        <div class="post-header">
          <div class="user-info">
            <div class="user-avatar">
              <img :src="post.userAvatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square'" :alt="post.userName">
            </div>
            <div class="user-details">
              <h4>{{ post.userName }}</h4>
              <p class="post-time">{{ post.createTime }}</p>
            </div>
          </div>
        </div>
        <div class="post-content">
          <h3>{{ post.title }}</h3>
          <p class="post-desc">{{ post.content }}</p>
          <img v-if="post.images && post.images.length > 0" :src="post.images[0]" :alt="post.title" class="post-image">
        </div>
        <div class="post-footer">
          <div class="post-stats">
            <span class="stat-item">
              <span class="stat-icon">❤️</span>
              <span>{{ post.likeCount || 0 }}</span>
            </span>
            <span class="stat-item">
              <span class="stat-icon">💬</span>
              <span>{{ post.commentCount || 0 }}</span>
            </span>
            <span class="stat-item">
              <span class="stat-icon">👁️</span>
              <span>{{ post.viewCount || 0 }}</span>
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 无数据提示 -->
    <div v-if="posts.length === 0" class="empty-tip">
      暂无帖子数据
    </div>
    
    <!-- 分页 -->
    <div v-if="posts.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
    
    <!-- 发布按钮 -->
    <div class="create-post-btn">
      <button @click="createPost">+</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPostList } from '@/api/community'

// 搜索参数
const searchQuery = ref('')

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 帖子列表数据
const posts = ref([])

// 计算总页数
const calculateTotalPages = (total) => {
  return Math.ceil(total / pageSize.value)
}

// 获取帖子列表
const fetchPosts = async () => {
  try {
    const response = await getPostList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value
    })
    posts.value = response.data.records || []
    totalPages.value = calculateTotalPages(response.data.total || 0)
  } catch (error) {
    console.error('获取帖子列表失败:', error)
  }
}

// 导航到详情页
const navigateToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/community/detail?id=${id}`
  })
}

// 切换页码
const changePage = (page) => {
  currentPage.value = page
  fetchPosts()
}

// 创建帖子
const createPost = () => {
  uni.navigateTo({
    url: '/pages/community/create'
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.community-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
  position: relative;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: var(--text-color);
}

.page-header p {
  margin: 0;
  font-size: 16px;
  color: var(--text-secondary-color);
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

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 80px;
}

.post-item {
  background-color: var(--card-background);
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.post-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.post-header {
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.post-time {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.post-content {
  margin-bottom: 12px;
}

.post-content h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 500;
  color: var(--text-color);
}

.post-desc {
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

.post-image {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.post-footer {
  border-top: 1px solid var(--border-color);
  padding-top: 12px;
}

.post-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.stat-icon {
  font-size: 16px;
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: var(--text-secondary-color);
  font-size: 16px;
  margin-bottom: 80px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
  margin-bottom: 80px;
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

.create-post-btn {
  position: fixed;
  bottom: 70px;
  right: 30px;
  z-index: 1000;
}

.create-post-btn button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: var(--primary-color);
  color: white;
  border: none;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.4);
  transition: all 0.3s ease;
}

.create-post-btn button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(30, 136, 229, 0.5);
}

@media (max-width: 768px) {
  .post-list {
    gap: 16px;
  }
  
  .post-item {
    padding: 12px;
  }
  
  .create-post-btn {
    bottom: 70px;
    right: 20px;
  }
  
  .create-post-btn button {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
}
</style>