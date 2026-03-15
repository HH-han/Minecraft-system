<template>
  <div class="recommendations-container">
    <div class="recommendations-header">
      <h2>热门推荐</h2>
      <a href="#" class="see-more">查看更多</a>
    </div>
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
          <div class="left">
            <div class="status-ind"></div>
          </div>
          <div class="right">
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
      <div class="modal-content glass-morphism" @click.stop>
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
  padding: 40px 0;
  position: relative;
  z-index: 1;
}

.recommendations-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  margin: 0 20px 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.recommendations-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.see-more {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.see-more:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.2);
}

.category-filter {
  padding: 0 20px 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  margin: 0 20px 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.category-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.price-category-list {
  margin-top: 12px;
}

.category-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.3);
}

.category-btn.active {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: white;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(550px, 1fr));
  gap: 50px;
  padding: 0 20px;
}

.recommendations {
  width: 100%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  padding: 20px;
}

.recommendations:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.recommendations-image {
  height: 250px;
  flex-shrink: 0;
}

.recommendations-image img {
  max-width: 300px;
  height: 100%;
  border-radius: 12px;
  transition: transform 0.5s ease;
  object-fit: cover;
}

.recommendations:hover .recommendations-image img {
  transform: scale(1.05);
}

.container {
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  gap: 12px;
  flex: 1;
}

.status-ind {
  width: 8px;
  height: 8px;
  background-color: #ff4757;
  margin: 6px 0;
  border-radius: 50%;
  flex-shrink: 0;
}

.text-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: #fff;
  flex: 1;
}

.recommendations-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  line-height: 1.3;
}

.recommendations-category {
  margin-bottom: 8px;
}

.category-tag {
  display: inline-block;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: white;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(79, 172, 254, 0.4);
}

.recommendations-description {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
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
  color: rgba(255, 255, 255, 0.7);
  margin-top: 8px;
}

.recommendations-location, .recommendations-price {
  display: flex;
  align-items: center;
}

.button-wrap {
  display: flex;
  flex-direction: row;
  gap: 16px;
  align-items: center;
}

.secondary-cta {
  background-color: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.secondary-cta:hover {
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  text-decoration: none;
}

.primary-cta {
  font-size: 14px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  font-weight: 600;
  color: #fff;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.primary-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
  text-decoration: none;
}

.right {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
  gap: 14px;
}

.left {
  display: flex;
  flex-direction: column;
  align-items: center;
}

@media (max-width: 768px) {
  .recommendations-header {
    padding: 16px;
    margin: 0 10px 20px;
  }
  
  .recommendations-header h2 {
    font-size: 24px;
  }
  
  .category-filter {
    padding: 16px;
    margin: 0 10px 20px;
  }
  
  .category-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
  
  .recommendations-grid {
    grid-template-columns: 1fr;
    padding: 0 10px;
    gap: 20px;
  }
  
  .recommendations {
    flex-direction: column;
    padding: 16px;
  }
  
  .recommendations-image {
    height: 200px;
    width: 100%;
  }
  
  .recommendations-image img {
    max-width: 100%;
  }
  
  .container {
    margin-left: 0;
    margin-top: 16px;
  }
  
  .recommendations-title {
    font-size: 16px;
  }
  
  .button-wrap {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .primary-cta,
  .secondary-cta {
    width: 100%;
    text-align: center;
  }
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  width: 100%;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  margin: 0;
}

.close-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  color: #fff;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.modal-body {
  padding: 24px;
}

.modal-image {
  width: 100%;
  height: 300px;
  border-radius: 12px;
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
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.info-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.info-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
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
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  border-radius: 2px;
}

.info-section p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
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
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.info-item .label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.info-item .value {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

/* 隐藏滚动条 */
.modal-content::-webkit-scrollbar {
  display: none;
}

.modal-content {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .modal-content {
    margin: 20px;
    max-height: calc(100vh - 40px);
  }
  
  .modal-header {
    padding: 16px;
  }
  
  .modal-header h2 {
    font-size: 20px;
  }
  
  .modal-body {
    padding: 16px;
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
</style>