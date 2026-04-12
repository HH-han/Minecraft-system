<template>
  <div class="attraction-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" v-model="searchQuery" placeholder="搜索景点" class="search-input">
      <button class="search-btn">搜索</button>
    </div>
    
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <select v-model="filterCity" class="filter-select">
        <option value="">全部城市</option>
        <option value="北京">北京</option>
        <option value="上海">上海</option>
        <option value="广州">广州</option>
        <option value="深圳">深圳</option>
      </select>
      <select v-model="filterSeason" class="filter-select">
        <option value="">全部季节</option>
        <option value="spring">春季</option>
        <option value="summer">夏季</option>
        <option value="autumn">秋季</option>
        <option value="winter">冬季</option>
      </select>
    </div>
    
    <!-- 景点列表 -->
    <div class="attraction-list">
      <div 
        v-for="attraction in attractions" 
        :key="attraction.id" 
        class="attraction-item"
        @click="navigateToDetail(attraction.id)"
      >
        <img :src="attraction.coverImage" :alt="attraction.name" class="attraction-image">
        <div class="attraction-info">
          <h3>{{ attraction.name }}</h3>
          <p class="attraction-city">{{ attraction.city }}</p>
          <div class="attraction-price" v-if="attraction.price > 0">¥{{ attraction.price }}</div>
          <div class="attraction-price free" v-else>免费</div>
          <div class="attraction-address">{{ attraction.address }}</div>
          <div class="attraction-season" v-if="attraction.season">{{ attraction.season }}</div>
          <div class="attraction-stats">
            <div class="attraction-rating">
              <span class="star">★</span>
              <span>{{ attraction.rating }}</span>
            </div>
            <div class="attraction-stat">
              <span>{{ attraction.likeCount }} 点赞</span>
              <span>{{ attraction.commentCount }} 评论</span>
              <span>{{ attraction.collectCount }} 收藏</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 无数据提示 -->
    <div v-if="attractions.length === 0" class="empty-tip">
      暂无景点数据
    </div>
    
    <!-- 分页 -->
    <div v-if="attractions.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getAttractionList } from '@/api/attraction'

// 搜索和筛选参数
const searchQuery = ref('')
const filterCity = ref('')
const filterSeason = ref('')

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 景点列表数据
const attractions = ref([])

// 计算总页数
const calculateTotalPages = (total) => {
  return Math.ceil(total / pageSize.value)
}

// 获取景点列表
const fetchAttractions = async () => {
  try {
    const response = await getAttractionList({
      page: currentPage.value,
      pageSize: pageSize.value,
      city: filterCity.value,
      season: filterSeason.value,
      keyword: searchQuery.value
    })
    attractions.value = response.data.records || []
    totalPages.value = calculateTotalPages(response.data.total || 0)
  } catch (error) {
    console.error('获取景点列表失败:', error)
  }
}

// 导航到详情页
const navigateToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/attraction/detail?id=${id}`
  })
}

// 切换页码
const changePage = (page) => {
  currentPage.value = page
  fetchAttractions()
}

// 页面加载时获取数据
onMounted(() => {
  fetchAttractions()
})
</script>

<style scoped>
.attraction-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
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

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.filter-select {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background-color: white;
  cursor: pointer;
}

.attraction-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.attraction-item {
  background-color: var(--card-background);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.attraction-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.attraction-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.attraction-info {
  padding: 16px;
}

.attraction-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-city {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.attraction-price {
  font-size: 16px;
  font-weight: 700;
  color: #F44336;
  margin: 8px 0;
}

.attraction-price.free {
  color: #4CAF50;
}

.attraction-address {
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-season {
  font-size: 12px;
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
  margin-bottom: 8px;
  display: inline-block;
}

.attraction-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.attraction-rating {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #FF9800;
}

.star {
  margin-right: 4px;
}

.attraction-stat {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.attraction-stat span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.attraction-item:hover .attraction-stat span {
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
  .attraction-list {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
  }
}
</style>