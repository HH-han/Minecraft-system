<template>
  <div class="recommendations-container">
    <div class="section-header">
      <h2 class="section-title">精选推荐</h2>
      <span class="section-subtitle">探索值得去的目的地</span>
    </div>

    <div class="category-filter">
      <div class="category-row">
        <span class="filter-label">分类</span>
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
      </div>
      <div class="category-row">
        <span class="filter-label">收费</span>
        <div class="category-list">
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
    </div>

    <div class="recommendations-grid">
      <div 
        v-for="(item, index) in recommendations" 
        :key="item.id" 
        class="card"
        :style="{ animationDelay: `${index * 0.05}s` }"
        @click="openRecommendationDetail(item)"
      >
        <div class="card-image-wrapper">
          <img :src="item.coverImageUrl" :alt="item.name" class="card-image" />
          <div class="card-overlay"></div>
          <span class="category-tag">{{ item.categoryName }}</span>
          <span v-if="item.isFree" class="free-badge">免费</span>
        </div>
        
        <div class="card-content">
          <h3 class="card-title">{{ item.name }}</h3>
          <p class="card-description">{{ (item.summary || item.recommendationReason).substring(0, 100) }}...</p>
          
          <div class="card-meta">
            <span class="card-location">{{ item.city }} · {{ item.address }}</span>
            <span class="card-price">{{ item.isFree ? '免费' : `¥${item.minPrice || 0}` }}</span>
          </div>
          
          <div class="card-actions">
            <button class="primary-cta">
              查看详情
              <svg class="cta-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
            <button class="secondary-cta" @click.stop="toggleFavorite(item.id)">
              <svg class="heart-icon" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="recommendations.length === 0" class="empty-state">
      <div class="empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"/>
          <path d="M2 12h20M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>
        </svg>
      </div>
      <p class="empty-text">暂无推荐内容</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHotRecommendations } from '@/api/homeRecommendations'

const emit = defineEmits(['open-recommendation-detail'])

const recommendations = ref([])
const allRecommendations = ref([])
const selectedCategory = ref(null)
const selectedPriceCategory = ref(null)

const categories = ref([
  { id: null, name: '全部' },
  { id: 1, name: '自然风光' },
  { id: 2, name: '历史文化' },
  { id: 3, name: '主题乐园' },
  { id: 4, name: '美食探店' },
  { id: 5, name: '休闲度假' }
])

const priceCategories = ref([
  { id: null, name: '全部' },
  { id: true, name: '免费' },
  { id: false, name: '收费' }
])

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

const filterByCategory = (categoryId) => {
  selectedCategory.value = categoryId
  applyFilters()
}

const filterByPriceCategory = (priceCategoryId) => {
  selectedPriceCategory.value = priceCategoryId
  applyFilters()
}

const applyFilters = () => {
  recommendations.value = allRecommendations.value.filter(item => {
    const categoryMatch = selectedCategory.value === null || item.categoryId === selectedCategory.value
    const priceMatch = selectedPriceCategory.value === null || item.isFree === selectedPriceCategory.value
    return categoryMatch && priceMatch
  })
}

const openRecommendationDetail = (item) => {
  emit('open-recommendation-detail', item)
}

const toggleFavorite = (id) => {
  console.log('Toggle favorite:', id)
}
</script>

<style scoped>
.recommendations-container {
  width: 100%;
}

.section-header {
  margin-bottom: 40px;
}

.section-title {
  font-size: 48px;
  font-weight: 700;
  color: #000000;
  margin: 0;
  letter-spacing: -0.02em;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'SF Pro Display', sans-serif;
}

.section-subtitle {
  display: block;
  font-size: 18px;
  color: #6e6e73;
  margin-top: 8px;
  font-weight: 400;
}

.category-filter {
  padding: 32px;
  background: #f5f5f7;
  border-radius: 28px;
  margin-bottom: 48px;
}

.category-row {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.category-row:last-child {
  margin-bottom: 0;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #86868b;
  min-width: 48px;
}

.category-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.category-btn {
  padding: 12px 24px;
  background: #ffffff;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.category-btn:hover {
  background: #e8e8ed;
  transform: translateY(-1px);
}

.category-btn.active {
  background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
  color: #ffffff;
  box-shadow: 0 4px 15px rgba(0, 122, 255, 0.3);
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 40px;
}

.card {
  background: #ffffff;
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.06);
  transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94), box-shadow 0.5s ease;
  cursor: pointer;
  opacity: 0;
  transform: translateY(30px);
  animation: fadeInUp 0.6s ease-out forwards;
}

.card:hover {
  transform: translateY(-8px) scale(1.01);
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.12);
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-image-wrapper {
  position: relative;
  height: 280px;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.card:hover .card-image {
  transform: scale(1.08);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 40%, rgba(0, 0, 0, 0.3) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.card:hover .card-overlay {
  opacity: 1;
}

.category-tag {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: #1d1d1f;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.free-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #34c759 0%, #30d158 100%);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: #ffffff;
}

.card-content {
  padding: 28px;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  color: #000000;
  margin: 0 0 12px;
  line-height: 1.25;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'SF Pro Display', sans-serif;
}

.card-description {
  font-size: 16px;
  color: #6e6e73;
  margin: 0 0 20px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-location {
  font-size: 14px;
  color: #86868b;
}

.card-price {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-actions {
  display: flex;
  gap: 16px;
}

.primary-cta {
  flex: 1;
  padding: 14px 24px;
  background: linear-gradient(135deg, #007aff 0%, #5ac8fa 100%);
  color: #ffffff;
  border: none;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 122, 255, 0.3);
}

.primary-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 122, 255, 0.4);
}

.cta-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.primary-cta:hover .cta-arrow {
  transform: translateX(4px);
}

.secondary-cta {
  width: 48px;
  height: 48px;
  padding: 0;
  background: #f5f5f7;
  color: #86868b;
  border: none;
  border-radius: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.secondary-cta:hover {
  background: #e8e8ed;
  color: #ff3b30;
  transform: scale(1.05);
}

.heart-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.secondary-cta:hover .heart-icon {
  transform: scale(1.1);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  background: #f5f5f7;
  border-radius: 32px;
  margin-top: 40px;
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: #c7c7cc;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
  color: #6e6e73;
}

@media (max-width: 768px) {
  .recommendations-container {
    padding: 0 40px;
  }
  
  .section-title {
    font-size: 32px;
  }
  
  .section-subtitle {
    font-size: 16px;
  }
  
  .category-filter {
    padding: 24px;
    border-radius: 24px;
    margin-bottom: 32px;
  }
  
  .category-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .filter-label {
    min-width: auto;
  }
  
  .category-btn {
    padding: 10px 20px;
    font-size: 13px;
  }
  
  .recommendations-grid {
    grid-template-columns: 1fr;
    gap: 28px;
  }
  
  .card-image-wrapper {
    height: 240px;
  }
  
  .card-content {
    padding: 24px;
  }
  
  .card-title {
    font-size: 20px;
  }
  
  .card-description {
    font-size: 15px;
  }
  
  .card-actions {
    gap: 12px;
  }
  
  .primary-cta {
    padding: 12px 20px;
    font-size: 14px;
  }
  
  .secondary-cta {
    width: 44px;
    height: 44px;
  }
  
  .empty-state {
    padding: 60px 0;
  }
}

@media (max-width: 375px) {
  .recommendations-container {
    padding: 0 24px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .category-filter {
    padding: 20px;
  }
  
  .card-image-wrapper {
    height: 200px;
  }
  
  .card-content {
    padding: 20px;
  }
  
  .card-title {
    font-size: 18px;
    margin-bottom: 10px;
  }
  
  .card-description {
    font-size: 14px;
    margin-bottom: 16px;
  }
  
  .card-meta {
    margin-bottom: 20px;
  }
  
  .card-price {
    font-size: 18px;
  }
  
  .primary-cta {
    padding: 12px;
    font-size: 14px;
  }
  
  .secondary-cta {
    width: 40px;
    height: 40px;
  }
  
  .heart-icon {
    width: 18px;
    height: 18px;
  }
  
  .empty-state {
    padding: 48px 0;
  }
  
  .empty-icon {
    width: 48px;
    height: 48px;
  }
}
</style>