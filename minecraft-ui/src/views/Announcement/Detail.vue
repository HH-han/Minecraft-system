<template>
  <div class="announcement-detail">
    <div v-if="loading" class="detail-card glass-card">
      <el-skeleton animated :rows="8" />
    </div>

    <div v-else-if="detail" class="detail-card glass-card">
      <div class="detail-head">
        <el-tag :type="typeTagType(detail.type)" size="small">{{ typeText(detail.type) }}</el-tag>
        <el-tag v-if="detail.level > 1" :type="detail.level === 3 ? 'danger' : 'warning'" size="small" effect="plain">
          {{ levelText(detail.level) }}
        </el-tag>
        <span v-if="detail.isTop" class="top-badge">置顶</span>
      </div>
      <h1 class="detail-title">{{ detail.title }}</h1>
      <div class="detail-meta">
        <span>{{ detail.creatorName || '系统' }}</span>
        <span>{{ formatTime(detail.publishTime) }}</span>
        <span>{{ detail.viewCount || 0 }} 次浏览</span>
      </div>
      <el-divider />
      <div class="detail-content" v-html="detail.content || detail.summary" />
      <div class="detail-footer">
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </div>

    <el-empty v-else description="公告不存在或已下架">
      <el-button type="primary" @click="goBack">返回列表</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnnouncementDetail } from '@/api/announcement'
import { useAnnouncementStore } from '@/stores/announcementStore'

const route = useRoute()
const router = useRouter()
const store = useAnnouncementStore()
const loading = ref(true)
const detail = ref(null)

onMounted(async () => {
  try {
    const res = await getAnnouncementDetail(route.params.id)
    if (res.code === 200) {
      detail.value = res.data
      // 同步标记已读
      if (!store.readIds.has(detail.value.id)) {
        await store.read(detail.value.id)
      }
    }
  } finally {
    loading.value = false
  }
})

function goBack() {
  router.push('/announcement')
}
function typeText(type) {
  return { 1: '系统公告', 2: '活动通知', 3: '维护通知', 4: '版本更新' }[type] || '公告'
}
function typeTagType(type) {
  return { 1: 'primary', 2: 'warning', 3: 'danger', 4: 'success' }[type] || 'info'
}
function levelText(level) {
  return { 2: '重要', 3: '紧急' }[level] || '普通'
}
function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return isNaN(d.getTime()) ? '' : d.toLocaleString('zh-CN', { hour12: false })
}
</script>

<style scoped>
.announcement-detail {
  max-width: 860px;
  margin: 0 auto;
  padding: 24px 16px 60px;
}
.glass-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
}
.detail-card {
  padding: 36px 40px;
}
.detail-head {
  display: flex;
  align-items: center;
  gap: 8px;
}
.top-badge {
  padding: 1px 6px;
  border-radius: 4px;
  background: linear-gradient(135deg, #f97316, #ef4444);
  color: #fff;
  font-size: 12px;
}
.detail-title {
  margin: 16px 0 10px;
  font-size: 24px;
  color: #1d1d1f;
  line-height: 1.4;
}
.detail-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}
.detail-content {
  line-height: 1.9;
  color: #333;
  font-size: 15px;
  word-break: break-word;
}
.detail-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
.detail-footer {
  margin-top: 32px;
  text-align: center;
}
@media (max-width: 767px) {
  .detail-card {
    padding: 24px 18px;
  }
}
</style>
