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
      :prefill="prefill"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AttractionBooking from '../components/AttractionBooking.vue'
import { useBookingStore } from '@/stores/bookingStore.js'
import { getAttractionDetail } from '@/api/attraction.js'
import { getTicketBookingDetail } from '@/api/booking.js'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const bookingStore = useBookingStore()
const route = useRoute()
const attractionData = ref(null)
const tickets = ref([])
const tags = ref([])
const facilities = ref([])
const loading = ref(false)
// 修改预订时的回填数据
const prefill = ref(null)

let loadTimer = null
let lastRequestId = null

const currentAttractionId = computed(() => {
  const id = attractionData.value?.id || 1
  return id
})

const dateFields = reactive([
  {
    name: 'visitDate',
    label: '游玩日期',
    value: '',
    min: new Date().toISOString().split('T')[0]
  }
])

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

// 数据变更后自动刷新
useAutoRefresh(loadData)

const debouncedLoadData = () => {
  if (loadTimer) {
    clearTimeout(loadTimer)
  }
  loadTimer = setTimeout(() => {
    loadData()
  }, 300)
}

// 修改预订：按预订详情回填景点/票种/日期/联系人
const loadEditPrefill = async () => {
  if (route.query.editType !== 'ticket' || !route.query.editId) return false
  try {
    const res = await getTicketBookingDetail(route.query.editId)
    const data = res && res.data
    if (!data) return false
    const items = data.items || []
    const attractionId = items.find(i => i.attractionId)?.attractionId
    if (!attractionId) return false

    const attractionRes = await getAttractionDetail(attractionId)
    if (!(attractionRes && attractionRes.code === 200 && attractionRes.data)) return false

    attractionData.value = attractionRes.data
    tickets.value = attractionRes.data.tickets || []
    tags.value = attractionRes.data.tags || []
    facilities.value = attractionRes.data.facilities || []

    dateFields[0].value = data.visitDate || ''

    prefill.value = {
      visitDate: data.visitDate || '',
      items: items.map(i => ({ ticketId: i.ticketId, quantity: i.quantity || 0 })),
      contact: {
        name: data.contactName || '',
        phone: data.contactPhone || '',
        email: data.contactEmail || '',
        specialRequest: data.specialRequest || ''
      }
    }
    return true
  } catch (error) {
    console.error('加载门票预订回填数据失败:', error)
    return false
  }
}

onMounted(async () => {
  const handled = await loadEditPrefill()
  if (handled) return
  attractionData.value = bookingStore.getAttractionData
  debouncedLoadData()
})

watch(currentAttractionId, () => {
  debouncedLoadData()
})

// 推荐弹窗「去预订」后同步切换当前景点
watch(() => bookingStore.getAttractionData, (data) => {
  if (!data?.id || data.id === attractionData.value?.id) return
  attractionData.value = data
  tickets.value = data.tickets || []
  tags.value = data.tags || []
  facilities.value = data.facilities || []
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
  border: 2px solid var(--theme-border-strong);
  border-top-color: var(--theme-primary);
  border-right-color: var(--theme-primary);
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
  color: var(--theme-text-2);
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
