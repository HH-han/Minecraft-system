<template>
  <div class="predetermined-page">
    <div class="page-wrapper">
      <!-- 页面头部：标题 + 描述 -->
      <header class="page-header">
        <span class="page-eyebrow">{{ $t('predetermined.page.eyebrow') }}</span>
        <h1 class="page-title">{{ $t('predetermined.page.title') }}</h1>
        <p class="page-subtitle">{{ $t('predetermined.page.subtitle') }}</p>
      </header>

      <!-- 主内容区 -->
      <main class="content-wrap">
        <div v-if="loading" class="loading-wrapper" role="status" aria-live="polite">
          <div class="loading-spinner"></div>
          <span class="loading-text">{{ $t('predetermined.page.loading') }}</span>
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

onMounted(() => {
  const tab = getInitialTab()
  currentTab.value = tab
  bookingStore.activeTab = tab
  updateQuery(tab)
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
  background: var(--theme-bg-app);
  color: var(--theme-text-1);
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

.page-eyebrow {
  display: inline-block;
  margin-bottom: 16px;
  padding: 4px 12px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.page-title {
  margin: 0 0 12px;
  font-size: 56px;
  line-height: 1.05;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--theme-text-1);
}

.page-subtitle {
  margin: 0;
  font-size: 18px;
  line-height: 1.4;
  color: var(--theme-text-2);
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
  }

  .content-wrap,
  .loading-wrapper {
    min-height: 480px;
  }
}
</style>
