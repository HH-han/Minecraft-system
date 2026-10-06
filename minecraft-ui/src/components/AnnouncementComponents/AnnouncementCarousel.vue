<template>
  <div v-if="carouselList.length" class="glass-card announcement-carousel">
    <el-carousel :interval="5000" height="120px" arrow="hover" indicator-position="outside">
      <el-carousel-item v-for="item in carouselList" :key="item.id">
        <div class="carousel-item" @click="goDetail(item.id)">
          <div class="carousel-head">
            <el-tag type="warning" size="small">活动</el-tag>
            <span class="carousel-title">{{ item.title }}</span>
          </div>
          <p class="carousel-summary">{{ item.summary || stripHtml(item.content) }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from './AnnouncementDetailModal.vue'

const store = useAnnouncementStore()

const detailVisible = ref(false)
const detailId = ref(null)

const carouselList = computed(() =>
  (store.activeList || []).filter((a) => a.displayMode === 3).slice(0, 5)
)

function goDetail(id) {
  detailId.value = id
  detailVisible.value = true
}

function stripHtml(html) {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').slice(0, 80)
}
</script>

<style scoped>
.announcement-carousel {
  margin: 12px auto;
  max-width: 1200px;
  padding: 8px 16px 18px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
}
.carousel-item {
  cursor: pointer;
  padding: 12px 8px;
}
.carousel-head {
  display: flex;
  align-items: center;
  gap: 10px;
}
.carousel-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.carousel-summary {
  margin-top: 10px;
  font-size: 13px;
  color: #666;
  line-height: 1.7;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
