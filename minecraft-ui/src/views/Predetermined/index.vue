<template>
  <div class="predetermined-container">
    <h1>预订服务</h1>

    <main class="content-area">
      <div v-if="loading" class="loading-overlay">
        <div class="loading-spinner"></div>
        <span class="loading-text">加载中...</span>
      </div>
      
      <Transition name="fade" mode="out-in">
        <KeepAlive>
          <component :is="currentComponent" :key="currentTab" />
        </KeepAlive>
      </Transition>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, defineAsyncComponent } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBookingStore } from '@/stores/bookingStore.js'

const router = useRouter()
const route = useRoute()
const bookingStore = useBookingStore()
const loading = ref(true)
const currentTab = ref('hotel')

const hotelModule = defineAsyncComponent(() => import('./modules/HotelBookingModule.vue'))
const attractionModule = defineAsyncComponent(() => import('./modules/AttractionBookingModule.vue'))

const currentComponent = computed(() => {
  return currentTab.value === 'hotel' ? hotelModule : attractionModule
})

const getInitialTab = () => {
  const queryTab = route.query.tab
  if (queryTab === 'attraction') {
    return 'attraction'
  }
  const storeTab = bookingStore.activeTab
  if (storeTab === 'attraction') {
    return 'attraction'
  }
  return 'hotel'
}

const updateQuery = (tab) => {
  router.replace({
    query: {
      ...route.query,
      tab
    }
  })
}

onMounted(() => {
  const tab = getInitialTab()
  currentTab.value = tab
  bookingStore.activeTab = tab
  updateQuery(tab)
  loading.value = false
})

watch(() => bookingStore.activeTab, (newTab) => {
  if (newTab && newTab !== currentTab.value) {
    loading.value = true
    setTimeout(() => {
      currentTab.value = newTab
      updateQuery(newTab)
      loading.value = false
    }, 300)
  }
})

watch(() => route.query.tab, (newTab) => {
  if (newTab && newTab !== currentTab.value) {
    loading.value = true
    setTimeout(() => {
      currentTab.value = newTab
      bookingStore.activeTab = newTab
      loading.value = false
    }, 300)
  }
})
</script>

<style scoped>
.predetermined-container {
  margin: 0 auto;
  background-image: url('@/assets/scenery/scenery-2.webp');
  background-size: cover;
  background-position: center;
  min-height: 100vh;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: bold;
  color: #333;
  background-color: rgba(255, 255, 255, 0.4);
  padding: 15px 20px;
  backdrop-filter: blur(5px);
}

.content-area {
  flex: 1;
  min-height: 600px;
  position: relative;
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .predetermined-container {
    padding: 10px;
  }

  h1 {
    font-size: 24px;
  }
}
</style>