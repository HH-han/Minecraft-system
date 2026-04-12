<template>
  <div class="food-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" v-model="searchQuery" placeholder="搜索美食" class="search-input">
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
      <select v-model="filterCuisine" class="filter-select">
        <option value="">全部菜系</option>
        <option value="川菜">川菜</option>
        <option value="粤菜">粤菜</option>
        <option value="鲁菜">鲁菜</option>
        <option value="淮扬菜">淮扬菜</option>
      </select>
    </div>
    
    <!-- 美食列表 -->
    <div class="food-list">
      <div 
        v-for="food in foods" 
        :key="food.id" 
        class="food-item"
        @click="navigateToDetail(food.id)"
      >
        <img :src="food.coverImage" :alt="food.name" class="food-image">
        <div class="food-info">
          <h3>{{ food.name }}</h3>
          <p class="food-meta">{{ food.city }} - {{ food.cuisineType }}</p>
          <div class="food-price">¥{{ food.price }}</div>
          <div class="food-address">{{ food.address }}</div>
          <div class="food-tags">
            <span v-for="tag in (food.tags ? food.tags.split(',') : [])" :key="tag" class="food-tag">{{ tag }}</span>
          </div>
          <div class="food-stats">
            <div class="food-rating">
              <span class="star">★</span>
              <span>{{ food.rating }}</span>
            </div>
            <div class="food-stat">
              <span>{{ food.likeCount }} 点赞</span>
              <span>{{ food.commentCount }} 评论</span>
              <span>{{ food.collectCount }} 收藏</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 无数据提示 -->
    <div v-if="foods.length === 0" class="empty-tip">
      暂无美食数据
    </div>
    
    <!-- 分页 -->
    <div v-if="foods.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFoodList } from '@/api/food'

// 搜索和筛选参数
const searchQuery = ref('')
const filterCity = ref('')
const filterCuisine = ref('')

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 美食列表数据
const foods = ref([])

// 计算总页数
const calculateTotalPages = (total) => {
  return Math.ceil(total / pageSize.value)
}

// 获取美食列表
const fetchFoods = async () => {
  try {
    const response = await getFoodList({
      page: currentPage.value,
      pageSize: pageSize.value,
      city: filterCity.value,
      cuisine: filterCuisine.value,
      keyword: searchQuery.value
    })
    foods.value = response.data.records || []
    totalPages.value = calculateTotalPages(response.data.total || 0)
  } catch (error) {
    console.error('获取美食列表失败:', error)
  }
}

// 导航到详情页
const navigateToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/food/detail?id=${id}`
  })
}

// 切换页码
const changePage = (page) => {
  currentPage.value = page
  fetchFoods()
}

// 页面加载时获取数据
onMounted(() => {
  fetchFoods()
})
</script>

<style scoped>
.food-container {
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

.food-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.food-item {
  background-color: var(--card-background);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.food-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.food-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.food-info {
  padding: 16px;
}

.food-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-meta {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.food-price {
  font-size: 18px;
  font-weight: 700;
  color: #F44336;
  margin: 8px 0;
}

.food-address {
  font-size: 12px;
  color: var(--text-secondary-color);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.food-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.food-tag {
  font-size: 11px;
  background: rgba(30, 136, 229, 0.1);
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.food-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.food-rating {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #FF9800;
}

.star {
  margin-right: 4px;
}

.food-stat {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-secondary-color);
}

.food-stat span {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.food-item:hover .food-stat span {
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
  .food-list {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
  }
}
</style>