<template>
  <div class="predetermined-page">
    <div class="page-wrapper">
      <!-- 页面头部：标题 + 描述 -->
      <header class="page-header">
        <h1 class="page-title">预订服务</h1>
        <p class="page-subtitle">选择酒店或景点，完成您的旅行预订</p>

        <!-- Segmented Control：酒店 / 景点 切换 Tab -->
        <nav class="segmented-control" role="tablist" aria-label="预订类型">
          <button
            role="tab"
            :aria-selected="currentTab === 'hotel'"
            class="segmented-item"
            :class="{ active: currentTab === 'hotel' }"
            @click="switchTab('hotel')"
            :tabindex="currentTab === 'hotel' ? 0 : -1"
          >
            酒店
          </button>
          <button
            role="tab"
            :aria-selected="currentTab === 'attraction'"
            class="segmented-item"
            :class="{ active: currentTab === 'attraction' }"
            @click="switchTab('attraction')"
            :tabindex="currentTab === 'attraction' ? 0 : -1"
          >
            景点
          </button>
        </nav>
      </header>

      <!-- 主内容区 -->
      <main class="content-wrap">
        <div v-if="loading" class="loading-wrapper" role="status" aria-live="polite">
          <div class="loading-spinner"></div>
          <span class="loading-text">加载中...</span>
        </div>

        <Transition name="fade" mode="out-in">
          <KeepAlive>
            <component
              :is="currentComponent"
              :key="currentTab"
              v-show="!loading"
            />
          </KeepAlive>
        </Transition>
      </main>
    </div>
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

const switchTab = (tab) => {
  if (tab === currentTab.value) return
  loading.value = true
  currentTab.value = tab
  bookingStore.activeTab = tab
  updateQuery(tab)
  setTimeout(() => {
    loading.value = false
  }, 250)
}

onMounted(() => {
  const tab = getInitialTab()
  currentTab.value = tab
  bookingStore.activeTab = tab
  updateQuery(tab)
  // 让 Segmented Control 先渲染出来，再显示模块，视觉更顺
  setTimeout(() => {
    loading.value = false
  }, 150)
})

watch(() => bookingStore.activeTab, (newTab) => {
  if (newTab && newTab !== currentTab.value) {
    loading.value = true
    setTimeout(() => {
      currentTab.value = newTab
      updateQuery(newTab)
      loading.value = false
    }, 250)
  }
})

watch(() => route.query.tab, (newTab) => {
  if (newTab && newTab !== currentTab.value) {
    loading.value = true
    setTimeout(() => {
      currentTab.value = newTab
      bookingStore.activeTab = newTab
      loading.value = false
    }, 250)
  }
})
</script>

<style scoped>
/* ===== 全页容器（Apple 辅色背景） ===== */
.predetermined-page {
  min-height: 100vh;
  background: #f5f5f7;
  color: #1d1d1f;
  box-sizing: border-box;
}

.page-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 22px 80px;
  box-sizing: border-box;
}

/* ===== 页面头部 ===== */
.page-header {
  text-align: center;
  margin-bottom: 48px;
}

.page-title {
  margin: 0 0 12px;
  font-size: 56px;
  line-height: 1.05;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: #1d1d1f;
}

.page-subtitle {
  margin: 0 0 32px;
  font-size: 18px;
  line-height: 1.4;
  color: #6e6e73;
}

/* ===== Segmented Control (iOS Tab) ===== */
.segmented-control {
  display: inline-flex;
  align-items: center;
  width: 280px;
  padding: 4px;
  background: rgba(0, 0, 0, 0.04);
  border-radius: 28px;
  box-sizing: border-box;
}

.segmented-item {
  flex: 1;
  height: 36px;
  border: none;
  background: transparent;
  color: #1d1d1f;
  font-size: 14px;
  font-weight: 500;
  border-radius: 24px;
  cursor: pointer;
  padding: 0 16px;
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.segmented-item:hover:not(.active) {
  color: #2997ff;
}

.segmented-item.active {
  background: #ffffff;
  color: #1d1d1f;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.04);
  font-weight: 600;
}

.segmented-item:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}

/* ===== 内容容器 ===== */
.content-wrap {
  position: relative;
  min-height: 560px;
}

/* ===== Loading（Apple 风格极简 spinner） ===== */
.loading-wrapper {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  z-index: 100;
  min-height: 560px;
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

/* ===== 模块切换淡入淡出 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== 响应式：平板（768–1199） ===== */
@media (max-width: 1199px) {
  .page-wrapper {
    padding: 40px 40px 72px;
  }

  .page-title {
    font-size: 44px;
  }

  .page-subtitle {
    font-size: 17px;
  }
}

/* ===== 响应式：手机（≤767） ===== */
@media (max-width: 767px) {
  .page-wrapper {
    padding: 32px 16px 64px;
  }

  .page-header {
    margin-bottom: 32px;
  }

  .page-title {
    font-size: 36px;
    margin-bottom: 8px;
  }

  .page-subtitle {
    font-size: 15px;
    margin-bottom: 24px;
  }

  .segmented-control {
    width: 100%;
  }

  .content-wrap,
  .loading-wrapper {
    min-height: 480px;
  }
}
</style>
