<template>
  <transition name="banner-slide">
    <div v-if="banner" class="announcement-banner" :class="bannerClass">
      <span class="banner-icon">📢</span>
      <span class="banner-text" @click="goDetail">{{ banner.title }}</span>
      <button class="banner-close" @click="dismiss">×</button>
    </div>
  </transition>

  <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()
const dismissed = ref(false)
const detailVisible = ref(false)
const detailId = ref(null)

const banner = computed(() =>
  dismissed.value ? null : (store.activeList || []).find((a) => a.displayMode === 4)
)
const bannerClass = computed(() => ({
  urgent: banner.value?.level === 3,
  important: banner.value?.level === 2
}))

function goDetail() {
  detailId.value = banner.value?.id
  detailVisible.value = true
}
function dismiss() {
  dismissed.value = true
}
</script>

<style scoped>
.announcement-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: var(--theme-bg-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--theme-border);
  border-bottom: 1px solid var(--theme-border);
  color: var(--theme-text-1);
}
.announcement-banner.important {
  background: var(--theme-warning-soft);
}
.announcement-banner.urgent {
  background: var(--theme-danger-soft);
}
.banner-icon {
  font-size: 16px;
}
.banner-text {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.banner-close {
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
  color: var(--theme-text-3);
  line-height: 1;
}
.banner-slide-enter-active,
.banner-slide-leave-active {
  transition: all 0.3s ease;
}
.banner-slide-enter-from,
.banner-slide-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}
</style>
