<template>
  <div class="hotel-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" v-model="searchQuery" placeholder="搜索酒店" class="search-input">
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
      <select v-model="filterPrice" class="filter-select">
        <option value="">价格区间</option>
        <option value="0-200">0-200</option>
        <option value="200-500">200-500</option>
        <option value="500-1000">500-1000</option>
        <option value="1000+">1000+</option>
      </select>
    </div>
    
    <!-- 酒店列表 -->
    <div class="hotel-list">
      <div 
        v-for="hotel in hotels" 
        :key="hotel.id" 
        class="hotel-item"
        @click="navigateToDetail(hotel.id)"
      >
        <img :src="hotel.coverImage" :alt="hotel.name" class="hotel-image">
        <div class="hotel-info">
          <h3>{{ hotel.name }}</h3>
          <p class="hotel-city">{{ hotel.city }}</p>
          <div class="hotel-price">¥{{ hotel.price }}/晚</div>
        </div>
      </div>
    </div>
    
    <!-- 无数据提示 -->
    <div v-if="hotels.length === 0" class="empty-tip">
      暂无酒店数据
    </div>
    
    <!-- 分页 -->
    <div v-if="hotels.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotelList } from '@/api/hotel'

// 搜索和筛选参数
const searchQuery = ref('')
const filterCity = ref('')
const filterPrice = ref('')

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 酒店列表数据
const hotels = ref([])

// 计算总页数
const calculateTotalPages = (total) => {
  return Math.ceil(total / pageSize.value)
}

// 获取酒店列表
const fetchHotels = async () => {
  try {
    const response = await getHotelList({
      page: currentPage.value,
      pageSize: pageSize.value,
      city: filterCity.value,
      priceRange: filterPrice.value,
      keyword: searchQuery.value
    })
    hotels.value = response.data.records || []
    totalPages.value = calculateTotalPages(response.data.total || 0)
  } catch (error) {
    console.error('获取酒店列表失败:', error)
  }
}

// 导航到详情页
const navigateToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/hotel/detail?id=${id}`
  })
}

// 切换页码
const changePage = (page) => {
  currentPage.value = page
  fetchHotels()
}

// 页面加载时获取数据
onMounted(() => {
  fetchHotels()
})
</script>

<style scoped>
.hotel-container {
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

.hotel-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.hotel-item {
  background-color: var(--card-background);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.hotel-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.hotel-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.hotel-info {
  padding: 16px;
}

.hotel-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hotel-city {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
}

.hotel-price {
  font-size: 16px;
  font-weight: 600;
  color: #F44336;
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
  .hotel-list {
    grid-template-columns: 1fr;
  }
  
  .filter-bar {
    flex-direction: column;
  }
}
</style>