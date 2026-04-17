<template>
  <div class="recommendations-container">
    <!-- 分类 -->
    <div class="category-filter">
      <div class="category-list">
        <button 
          v-for="category in categories" 
          :key="category.id" 
          :class="['category-btn', { active: selectedCategory === category.id }]"
          @click="filterByCategory(category.id)"
        >
          {{ category.name }}
        </button>
      </div>
      <div class="category-list price-category-list">
        <button 
          v-for="priceCategory in priceCategories" 
          :key="priceCategory.id" 
          :class="['category-btn', { active: selectedPriceCategory === priceCategory.id }]"
          @click="filterByPriceCategory(priceCategory.id)"
        >
          {{ priceCategory.name }}
        </button>
      </div>
    </div>
    <div class="recommendations-grid">
      <div 
        v-for="item in recommendations" 
        :key="item.id" 
        class="recommendations"
      >
        <div class="recommendations-image">
          <img :src="item.coverImageUrl" :alt="item.name" />
        </div>
        <div class="container">
          <div class="text-wrap">
            <h3 class="recommendations-title">{{ item.name }}</h3>
            <div class="recommendations-category">
              <span class="category-tag">{{ item.categoryName }}</span>
            </div>
            <p class="recommendations-description">{{ (item.summary || item.recommendationReason).substring(0, 100) }}...</p>
            <div class="recommendations-meta">
              <span class="recommendations-location">{{ item.city }} {{ item.address }}</span>
              <span class="recommendations-price">{{ item.isFree ? '免费' : `¥${item.minPrice || 0}` }}</span>
            </div>
            <div class="button-wrap">
              <button class="primary-cta" @click="openDetailModal(item)">查看详情</button>
              <button class="secondary-cta">收藏</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情模态框 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ selectedItem?.name }}</h2>
          <button class="close-btn" @click="closeDetailModal">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="modal-image">
            <img :src="selectedItem?.coverImageUrl" :alt="selectedItem?.name" />
          </div>
          
          <div class="modal-info">
            <div class="info-section">
              <h3>推荐理由</h3>
              <p>{{ selectedItem?.recommendationReason }}</p>
            </div>
            
            <div class="info-section">
              <h3>亮点特色</h3>
              <p>{{ selectedItem?.highlights }}</p>
            </div>
            
            <div class="info-section">
              <h3>基本信息</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">分类</span>
                  <span class="value">{{ selectedItem?.categoryName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">地点</span>
                  <span class="value">{{ selectedItem?.address }}</span>
                </div>
                <div class="info-item">
                  <span class="label">最佳时间</span>
                  <span class="value">{{ selectedItem?.bestTravelTime }}</span>
                </div>
                <div class="info-item">
                  <span class="label">游玩时长</span>
                  <span class="value">{{ selectedItem?.visitDuration }}</span>
                </div>
                <div class="info-item">
                  <span class="label">门票</span>
                  <span class="value">{{ selectedItem?.ticketPriceDescription }}</span>
                </div>
                <div class="info-item">
                  <span class="label">开放时间</span>
                  <span class="value">{{ selectedItem?.openingHoursWeekday }}</span>
                </div>
              </div>
            </div>
            
            <div class="info-section">
              <h3>交通方式</h3>
              <p>{{ selectedItem?.transportation }}</p>
            </div>
            
            <div class="info-section">
              <h3>注意事项</h3>
              <p>{{ selectedItem?.tips }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getHotRecommendations } from '@/api/homeRecommendations'

const recommendations = ref([])
const allRecommendations = ref([])
const showDetailModal = ref(false)
const selectedItem = ref(null)
const selectedCategory = ref(null)

// 分类数据
const categories = ref([
  { id: null, name: '全部' },
  { id: 1, name: '自然风光' },
  { id: 2, name: '历史文化' },
  { id: 3, name: '主题乐园' },
  { id: 4, name: '美食探店' },
  { id: 5, name: '休闲度假' }
])

// 收费分类数据
const priceCategories = ref([
  { id: null, name: '全部' },
  { id: true, name: '免费' },
  { id: false, name: '收费' }
])

// 选中的收费分类
const selectedPriceCategory = ref(null)

onMounted(() => {
  loadHotRecommendations()
})

const loadHotRecommendations = async () => {
  try {
    const response = await getHotRecommendations(20)
    if (response.code === 200 && response.data) {
      allRecommendations.value = response.data
      recommendations.value = response.data
    }
  } catch (error) {
    console.error('Failed to load hot recommendations:', error)
  }
}

// 根据分类筛选
const filterByCategory = (categoryId) => {
  selectedCategory.value = categoryId
  applyFilters()
}

// 根据收费分类筛选
const filterByPriceCategory = (priceCategoryId) => {
  selectedPriceCategory.value = priceCategoryId
  applyFilters()
}

// 应用筛选条件
const applyFilters = () => {
  recommendations.value = allRecommendations.value.filter(item => {
    const categoryMatch = selectedCategory.value === null || item.categoryId === selectedCategory.value
    const priceMatch = selectedPriceCategory.value === null || item.isFree === selectedPriceCategory.value
    return categoryMatch && priceMatch
  })
}

const openDetailModal = (item) => {
  selectedItem.value = item
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedItem.value = null
}
</script>

<style scoped>
.recommendations-container {
  padding: 0;
}

/* 分类筛选 */
.category-filter {
  padding: 24px 0;
  margin-bottom: 32px;
}

.category-list {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.price-category-list {
  margin-top: 8px;
}

.category-btn {
  background: #f5f5f7;
  border: 1px solid #d2d2d6;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-btn:hover {
  background: #e8e8ed;
  border-color: #c7c7cc;
}

.category-btn.active {
  background: #1d1d1f;
  color: #ffffff;
  border-color: #1d1d1f;
}

/* 推荐网格 */
.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 32px;
}

/* 推荐卡片 */
.recommendations {
  background: #ffffff;
  border: 1px solid #d2d2d6;
  border-radius: 24px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.recommendations:hover {
  transform: scale(1.01);
  box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

.recommendations-image {
  height: 200px;
  width: 100%;
  overflow: hidden;
}

.recommendations-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.recommendations:hover .recommendations-image img {
  transform: scale(1.05);
}

.container {
  padding: 24px;
}

.text-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommendations-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #1d1d1f;
  line-height: 1.2;
}

.category-tag {
  display: inline-block;
  background: #2997ff;
  color: white;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 12px;
}

.recommendations-description {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommendations-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6e6e73;
  margin-top: 4px;
}

.button-wrap {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.primary-cta {
  background: #2997ff;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease;
  flex: 1;
}

.primary-cta:hover {
  background: #0066cc;
}

.secondary-cta {
  background: #f5f5f7;
  color: #1d1d1f;
  border: 1px solid #d2d2d6;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.secondary-cta:hover {
  background: #e8e8ed;
  border-color: #c7c7cc;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  width: 100%;
  /* 隐藏滚动条 */
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* 隐藏WebKit浏览器的滚动条 */
.modal-content::-webkit-scrollbar {
  display: none;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 0;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
}

.close-btn {
  background: #f5f5f7;
  border: 1px solid #d2d2d6;
  cursor: pointer;
  color: #1d1d1f;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.close-btn:hover {
  background: #e8e8ed;
  border-color: #c7c7cc;
}

.modal-body {
  padding: 24px;
}

.modal-image {
  width: 100%;
  height: 300px;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 24px;
}

.modal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #d2d2d6;
}

.info-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.info-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 12px 0;
  position: relative;
  padding-left: 12px;
}

.info-section h3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: #2997ff;
  border-radius: 2px;
}

.info-section p {
  font-size: 14px;
  color: #6e6e73;
  line-height: 1.5;
  margin: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: #f5f5f7;
  padding: 12px;
  border-radius: 8px;
}

.info-item .label {
  font-size: 12px;
  color: #6e6e73;
}

.info-item .value {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-list {
    gap: 12px;
  }
  
  .category-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
  
  .recommendations-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .recommendations {
    border-radius: 20px;
  }
  
  .recommendations-image {
    height: 180px;
  }
  
  .container {
    padding: 20px;
  }
  
  .recommendations-title {
    font-size: 18px;
  }
  
  .button-wrap {
    flex-direction: column;
    gap: 8px;
  }
  
  .primary-cta,
  .secondary-cta {
    width: 100%;
  }
  
  .modal-content {
    margin: 20px;
    max-height: calc(100vh - 40px);
    border-radius: 20px;
  }
  
  .modal-header {
    padding: 20px 20px 0;
  }
  
  .modal-header h2 {
    font-size: 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .modal-image {
    height: 200px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .info-section {
    margin-bottom: 20px;
    padding-bottom: 16px;
  }
  
  .info-section h3 {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .category-filter {
    padding: 16px 0;
  }
  
  .recommendations-grid {
    gap: 16px;
  }
  
  .recommendations {
    border-radius: 16px;
  }
  
  .recommendations-image {
    height: 160px;
  }
  
  .container {
    padding: 16px;
  }
  
  .recommendations-title {
    font-size: 16px;
  }
}
</style>