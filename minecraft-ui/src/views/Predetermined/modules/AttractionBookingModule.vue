<template>
  <div class="attraction-booking-module">
    <div v-if="loading" class="loading-inner" role="status" aria-live="polite">
      <div class="loading-spinner"></div>
      <span class="loading-text">加载景点信息中...</span>
    </div>

    <AttractionBooking
      v-else
      :dateFields="dateFields"
      :tags="tags"
      :facilities="facilities"
      :tickets="tickets"
      :attractionData="attractionData"
      :attractionId="currentAttractionId"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import AttractionBooking from '../components/AttractionBooking.vue'
import { useBookingStore } from '@/stores/bookingStore.js'
import { getAttractionDetail } from '@/api/attraction.js'

const bookingStore = useBookingStore()
const attractionData = ref(null)
const tickets = ref([])
const tags = ref([])
const facilities = ref([])
const loading = ref(false)

let loadTimer = null
let lastRequestId = null

const currentAttractionId = computed(() => {
  const id = attractionData.value?.id || 1
  return id
})

const dateFields = [
  {
    name: 'visitDate',
    label: '游玩日期',
    value: '',
    min: new Date().toISOString().split('T')[0]
  }
]

const loadData = async () => {
  if (loading.value) return
  
  const requestId = Date.now()
  lastRequestId = requestId
  
  loading.value = true
  
  try {
    const response = await getAttractionDetail(currentAttractionId.value)
    
    if (requestId !== lastRequestId) {
      return
    }
    
    if (response && response.code === 200 && response.data) {
      attractionData.value = response.data
      tickets.value = response.data.tickets || []
      tags.value = response.data.tags || []
      facilities.value = response.data.facilities || []
    } else {
      tickets.value = []
      tags.value = []
      facilities.value = []
    }
  } catch (error) {
    if (error.message && !error.message.includes('取消')) {
      console.error('加载景点数据失败:', error.message)
    }
    tickets.value = []
    tags.value = []
    facilities.value = []
  } finally {
    loading.value = false
  }
}

const debouncedLoadData = () => {
  if (loadTimer) {
    clearTimeout(loadTimer)
  }
  loadTimer = setTimeout(() => {
    loadData()
  }, 300)
}

onMounted(() => {
  attractionData.value = bookingStore.getAttractionData
  debouncedLoadData()
})

watch(currentAttractionId, () => {
  debouncedLoadData()
})
</script>

<style scoped>
.attraction-booking-module {
  position: relative;
  min-height: 500px;
  box-sizing: border-box;
}

.loading-inner {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  min-height: 500px;
  padding: 48px 0;
  box-sizing: border-box;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 2px solid #d2d2d6;
  border-top-color: #2997ff;
  border-right-color: #2997ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  box-sizing: border-box;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 14px;
  color: #6e6e73;
}

@media (max-width: 767px) {
  .attraction-booking-module,
  .loading-inner {
    min-height: 420px;
  }
  .loading-inner {
    padding: 32px 0;
  }
}
</style>
