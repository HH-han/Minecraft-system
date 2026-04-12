<template>
  <div class="discover-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>发现世界</h1>
      <p>探索世界各地的独特特征和文化</p>
    </div>
    
    <!-- 分类筛选 -->
    <div class="category-filter">
      <div 
        v-for="category in categories" 
        :key="category"
        class="category-item"
        :class="{ 'active': selectedCategory === category }"
        @click="selectCategory(category)"
      >
        {{ category }}
      </div>
    </div>
    
    <!-- 世界特征列表 -->
    <div class="characteristics-list">
      <div 
        v-for="item in characteristics" 
        :key="item.id" 
        class="characteristic-item"
      >
        <img :src="item.image" :alt="item.name" class="characteristic-image">
        <div class="characteristic-info">
          <h3>{{ item.name }}</h3>
          <p class="characteristic-country">{{ item.country }}</p>
          <p class="characteristic-desc">{{ item.description }}</p>
          <p class="characteristic-culture">{{ item.culture }}</p>
          <p class="characteristic-history">{{ item.history }}</p>
          <div class="characteristic-features">
            <span class="feature-tag">{{ item.features }}</span>
          </div>
          <div class="characteristic-tags">
            <span v-for="tag in (item.tags ? item.tags.split(',') : [])" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="characteristic-category" :style="{ borderColor: item.color, backgroundColor: item.color + '20' }">
            <span class="category-tag" :style="{ color: item.color }">{{ item.category }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 无数据提示 -->
    <div v-if="characteristics.length === 0" class="empty-tip">
      暂无世界特征数据
    </div>
    
    <!-- 分页 -->
    <div v-if="characteristics.length > 0" class="pagination">
      <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">上一页</button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getWorldCharacteristicsPage, getWorldCharacteristicsByCategory } from '@/api/worldcharacteristics'

// 分类列表
const categories = ref(['全部', '自然景观', '文化遗产', '历史建筑', '特色美食', '传统习俗'])
const selectedCategory = ref('全部')

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)

// 世界特征数据
const characteristics = ref([])

// 计算总页数
const calculateTotalPages = (total) => {
  return Math.ceil(total / pageSize.value)
}

// 获取世界特征列表
const fetchCharacteristics = async () => {
  try {
    if (selectedCategory.value === '全部') {
      const response = await getWorldCharacteristicsPage(currentPage.value, pageSize.value)
      characteristics.value = response.data.records || []
      totalPages.value = calculateTotalPages(response.data.total || 0)
    } else {
      const response = await getWorldCharacteristicsByCategory(selectedCategory.value)
      characteristics.value = response.data || []
      totalPages.value = 1
    }
  } catch (error) {
    console.error('获取世界特征失败:', error)
  }
}

// 选择分类
const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1
  fetchCharacteristics()
}

// 切换页码
const changePage = (page) => {
  currentPage.value = page
  fetchCharacteristics()
}

// 页面加载时获取数据
onMounted(() => {
  fetchCharacteristics()
})
</script>

<style scoped>
.discover-container {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: 20px;
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

.category-filter {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.category-item {
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.category-item.active {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.characteristics-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.characteristic-item {
  background-color: var(--card-background);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.characteristic-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.characteristic-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.characteristic-info {
  padding: 16px;
}

.characteristic-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.characteristic-country {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: var(--primary-color);
}

.characteristic-desc {
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.4;
  color: var(--text-secondary-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.characteristic-culture {
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.4;
  color: var(--text-secondary-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.characteristic-history {
  margin: 0 0 12px 0;
  font-size: 14px;
  line-height: 1.4;
  color: var(--text-secondary-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.characteristic-features {
  margin-bottom: 12px;
}

.feature-tag {
  padding: 4px 12px;
  background-color: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
  border-radius: 12px;
  font-size: 12px;
}

.characteristic-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.tag {
  padding: 4px 12px;
  background-color: rgba(30, 136, 229, 0.1);
  color: var(--primary-color);
  border-radius: 12px;
  font-size: 12px;
}

.characteristic-category {
  display: flex;
  justify-content: center;
  border: 1px solid;
  border-radius: 12px;
  padding: 2px;
}

.category-tag {
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
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
  .characteristics-list {
    grid-template-columns: 1fr;
  }
  
  .category-filter {
    gap: 8px;
  }
  
  .category-item {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>