<template>
  <div class="hotel-booking-module">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <span class="loading-text">加载中...</span>
    </div>
    
    <HotelBooking 
      v-else
      :dateFields="dateFields" 
      :facilities="facilities"
      :rooms="rooms" 
      :hotelData="hotelData" 
      :hotelId="currentHotelId" 
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import HotelBooking from '../components/HotelBooking.vue'
import { useBookingStore } from '@/stores/bookingStore.js'
import { getHotelDetail } from '@/api/hotel.js'

const bookingStore = useBookingStore()
const hotelData = ref(null)
const rooms = ref([])
const facilities = ref([])
const loading = ref(false)

let loadTimer = null
let lastRequestId = null

const currentHotelId = computed(() => {
  const id = hotelData.value?.id || 1
  return id
})

const dateFields = [
  {
    name: 'checkInDate',
    label: '入住日期',
    value: '',
    min: new Date().toISOString().split('T')[0]
  },
  {
    name: 'checkOutDate',
    label: '离店日期',
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
    const response = await getHotelDetail(currentHotelId.value)
    
    if (requestId !== lastRequestId) {
      return
    }
    
    if (response && response.code === 200 && response.data) {
      hotelData.value = response.data
      rooms.value = response.data.rooms || []
      facilities.value = response.data.facilities || []
    } else {
      rooms.value = []
      facilities.value = []
    }
  } catch (error) {
    if (error.message && !error.message.includes('取消')) {
      console.error('加载酒店数据失败:', error.message)
    }
    rooms.value = []
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
  hotelData.value = bookingStore.getHotelData
  debouncedLoadData()
})

watch(currentHotelId, () => {
  debouncedLoadData()
})
</script>

<style scoped>
.hotel-booking-module {
  position: relative;
  min-height: 600px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.8);
  z-index: 100;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #ff6a00;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 15px;
  color: #666;
  font-size: 16px;
}
</style>