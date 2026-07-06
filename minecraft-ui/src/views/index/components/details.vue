<template>
  <div v-if="visible" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="modal-title">{{ title }}</h2>
        <button class="close-button" @click="closeModal">
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.5">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
      <div class="modal-body">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else>
          <div v-if="type === 'news'" class="detail-content">
            <div class="detail-image">
              <img :src="data.coverImage || defaultNewsImage" :alt="data.title" />
            </div>
            <div class="detail-info">
              <h3 class="detail-title">{{ data.title }}</h3>
              <div class="detail-meta">
                <span class="detail-date">{{ formatDate(data.createTime) }}</span>
                <span class="detail-view">{{ data.viewCount || 0 }} 阅读</span>
              </div>
              <div class="detail-text">{{ data.content }}</div>
            </div>
          </div>
          
          <div v-if="type === 'hotel'" class="detail-content">
            <div class="detail-image">
              <img :src="data.coverImage || defaultHotelImage" :alt="data.name" />
            </div>
            <div class="detail-info">
              <div class="info-header">
                <h3 class="detail-title">{{ data.name }}</h3>
                <span class="detail-price">¥{{ data.price }}<span class="price-unit">/晚</span></span>
              </div>
              <div class="detail-meta">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>{{ data.city }} {{ data.address }}</span>
              </div>
              <div class="detail-text">{{ data.description }}</div>
            </div>
          </div>
          
          <div v-if="type === 'food'" class="detail-content">
            <div class="detail-image">
              <img :src="data.coverImage || defaultFoodImage" :alt="data.name" />
            </div>
            <div class="detail-info">
              <div class="info-header">
                <h3 class="detail-title">{{ data.name }}</h3>
                <span class="detail-price">¥{{ data.price }}</span>
              </div>
              <div class="detail-meta">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>{{ data.city }} {{ data.restaurant }}</span>
              </div>
              <div class="detail-text">{{ data.description }}</div>
            </div>
          </div>
          
          <div v-if="type === 'attraction'" class="detail-content">
            <div class="detail-image">
              <img :src="data.coverImage || defaultAttractionImage" :alt="data.name" />
            </div>
            <div class="detail-info">
              <div class="info-header">
                <h3 class="detail-title">{{ data.name }}</h3>
                <span class="detail-price">¥{{ data.ticketPrice || 0 }}</span>
              </div>
              <div class="detail-meta">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>{{ data.city }} {{ data.address }}</span>
              </div>
              <div class="detail-text">{{ data.description }}</div>
            </div>
          </div>
          
          <div v-if="type === 'safety'" class="detail-content">
            <div class="detail-image" v-if="data.imageUrl">
              <img :src="data.imageUrl" :alt="data.title" />
            </div>
            <div class="detail-info">
              <h3 class="detail-title">{{ data.title }}</h3>
              <div class="detail-text">{{ data.description }}</div>
            </div>
          </div>
          
          <div v-if="type === 'recommendation'" class="detail-content">
            <div class="detail-image">
              <img :src="data.coverImageUrl || defaultRecommendationImage" :alt="data.name" />
            </div>
            <div class="detail-info">
              <div class="info-header">
                <h3 class="detail-title">{{ data.name }}</h3>
                <span class="detail-price">{{ data.isFree ? '免费' : `¥${data.minPrice || 0}` }}</span>
              </div>
              <div class="detail-meta">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>{{ data.city }} · {{ data.address }}</span>
              </div>
              <div class="detail-section">
                <h4 class="section-title">推荐理由</h4>
                <p>{{ data.recommendationReason }}</p>
              </div>
              <div class="detail-section">
                <h4 class="section-title">亮点特色</h4>
                <p>{{ data.highlights }}</p>
              </div>
              <div class="detail-grid">
                <div class="grid-item">
                  <span class="grid-label">分类</span>
                  <span class="grid-value">{{ data.categoryName }}</span>
                </div>
                <div class="grid-item">
                  <span class="grid-label">最佳时间</span>
                  <span class="grid-value">{{ data.bestTravelTime }}</span>
                </div>
                <div class="grid-item">
                  <span class="grid-label">游玩时长</span>
                  <span class="grid-value">{{ data.visitDuration }}</span>
                </div>
                <div class="grid-item">
                  <span class="grid-label">门票</span>
                  <span class="grid-value">{{ data.ticketPriceDescription }}</span>
                </div>
                <div class="grid-item">
                  <span class="grid-label">开放时间</span>
                  <span class="grid-value">{{ data.openingHoursWeekday }}</span>
                </div>
                <div class="grid-item">
                  <span class="grid-label">交通方式</span>
                  <span class="grid-value">{{ data.transportation }}</span>
                </div>
              </div>
              <div class="detail-section">
                <h4 class="section-title">注意事项</h4>
                <p>{{ data.tips }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    type: {
        type: String,
        default: ''
    },
    data: {
        type: Object,
        default: () => ({})
    },
    loading: {
        type: Boolean,
        default: false
    },
    error: {
        type: String,
        default: ''
    }
});

const emit = defineEmits(['close']);

const title = computed(() => {
    const titles = {
        news: '新闻详情',
        hotel: '酒店详情',
        food: '美食详情',
        attraction: '景点详情',
        safety: '安全知识提示',
        recommendation: '推荐详情'
    };
    return titles[props.type] || '详情';
});

const defaultNewsImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=news%20article%20placeholder%2C%20simple%20clean%20design&image_size=square'
const defaultHotelImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=hotel%20exterior%20placeholder%2C%20modern%20design&image_size=square'
const defaultFoodImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20food%20placeholder%2C%20appetizing%20presentation&image_size=square'
const defaultAttractionImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tourist%20attraction%20placeholder%2C%20scenic%20view&image_size=square'
const defaultRecommendationImage = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=travel%20recommendation%20placeholder%2C%20beautiful%20destination&image_size=square'

const closeModal = () => {
    emit('close');
};

const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: #ffffff;
  border-radius: 28px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalSlideUp 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.modal-content::-webkit-scrollbar {
  display: none;
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f5f5f7;
  position: sticky;
  top: 0;
  background: #ffffff;
  z-index: 10;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #1d1d1f;
}

.close-button {
  background: #f5f5f7;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #1d1d1f;
  transition: all 0.2s;
}

.close-button:hover {
  background: #e8e8ed;
}

.modal-body {
  padding: 0;
}

.loading {
  text-align: center;
  padding: 80px;
  color: #6e6e73;
}

.error {
  text-align: center;
  padding: 80px;
  color: #ff4757;
}

.detail-content {
  display: flex;
  flex-direction: column;
}

.detail-image {
  width: 100%;
  height: 300px;
  overflow: hidden;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  padding: 24px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  color: #1d1d1f;
}

.detail-price {
  font-size: 28px;
  font-weight: 700;
  color: #2997ff;
}

.price-unit {
  font-size: 16px;
  font-weight: 400;
  color: #6e6e73;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6e6e73;
  margin-bottom: 20px;
}

.detail-text {
  font-size: 16px;
  line-height: 1.7;
  color: #6e6e73;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section .section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 12px;
}

.detail-section p {
  font-size: 14px;
  line-height: 1.6;
  color: #6e6e73;
  margin: 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 24px;
}

.grid-item {
  background: #f5f5f7;
  padding: 16px;
  border-radius: 12px;
}

.grid-label {
  display: block;
  font-size: 12px;
  color: #86868b;
  font-weight: 500;
  margin-bottom: 6px;
}

.grid-value {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 20px;
    border-radius: 24px;
  }
  
  .detail-image {
    height: 200px;
  }
  
  .detail-title {
    font-size: 20px;
  }
  
  .detail-price {
    font-size: 24px;
  }
  
  .detail-text {
    font-size: 14px;
  }
  
  .detail-info {
    padding: 20px;
  }
}

@media (max-width: 480px) {
  .detail-image {
    height: 180px;
  }
  
  .detail-title {
    font-size: 18px;
  }
  
  .detail-info {
    padding: 16px;
  }
}
</style>