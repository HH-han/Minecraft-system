<template>
  <div v-if="loading" class="loading">
    <div class="loading-spinner"></div>
    <p>加载中...</p>
  </div>
  <div v-else-if="error" class="error-state">
    <p>加载失败: {{ error }}</p>
    <button @click="$emit('retry')" class="btn retry">重试</button>
  </div>
  <div v-else class="card-container">
    <div v-for="hotel in hotels" :key="hotel.id" class="card" @click="openDetail(hotel)">
      <img :src="hotel.coverImage" :alt="hotel.name" class="card-image">
      <div class="card-content">
        <h3 class="card-title">{{ hotel.name }}</h3>
        <p class="card-desc">{{ hotel.description }}</p>
        <div class="card-stats">
          <span class="card-sales">
            <svg t="1745755200324" class="icon" viewBox="0 0 1331 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="5802" width="16" height="16">
              <path
                d="M330.67008 602.84928V1024h198.4V602.84928l-99.20512-82.5856-99.19488 82.5856zM0 1024h198.4V701.93152L0 858.83904V1024z m992-520.25344V1024h198.4V338.5856L992 503.74656z m-330.65984 206.4384V1024h198.38976V611.10272L702.6688 743.2192l-41.32864-33.03424zM1322.67008 0H942.3872l157.07136 148.64384-405.07392 330.32192L429.8752 256 0 594.5856V743.2192l429.8752-338.5856L702.6688 627.6096l487.7312-396.3904 132.27008 123.87328V0z"
                fill="#d81e06" p-id="5803"></path>
            </svg> 收藏:{{ hotel.collectCount }}
          </span>
          <span class="card-rating">
            <svg t="1745755283144" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="6920" width="16" height="16">
              <path
                d="M264.107 948.053a34.133 34.133 0 0 1-50.774-35.84l46.934-276.053L61.44 440.747A34.133 34.133 0 0 1 80.213 384l277.334-42.667 124.16-250.88a34.133 34.133 0 0 1 61.013 0l123.733 250.88L943.787 384a34.133 34.133 0 0 1 18.773 58.027L762.453 637.44l48.214 274.773a34.133 34.133 0 0 1-49.494 35.84l-247.893-128z"
                fill="#d81e06" p-id="6921"></path>
            </svg> 评分:{{ hotel.rating }}
          </span>
        </div>
        <div class="card-footer">
          <span class="card-price">¥{{ hotel.price }}</span>
        </div>
      </div>
    </div>
    <div v-if="hotels.length === 0 && !loading" class="empty-state">
      <p>暂无酒店数据</p>
    </div>
  </div>

  <Paging 
    v-if="totalPages > 0"
    :total-pages="totalPages" 
    :current-page="currentPage" 
    @update:current-page="$emit('update:currentPage', $event)" 
  />

  <!-- 酒店详情模态框 -->
  <Select :visible="showModal" :productId="selectedHotelId" :commodity="'2'" @close="closeModal" />
</template>
<script setup>
import { ref } from 'vue'
import Paging from '@/components/paging/index.vue'
import Select from '@/components/Payment/Select.vue'

const props = defineProps({
  hotels: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  totalPages: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  }
})

const emit = defineEmits(['retry', 'update:currentPage'])

const showModal = ref(false)
const selectedHotelId = ref('')

const openDetail = (hotel) => {
  selectedHotelId.value = hotel.id
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedHotelId.value = ''
}
</script>
<style scoped>
@import '@/css/cart/card.css';
</style>
