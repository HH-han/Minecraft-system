<template>
  <div class="announcement-page">
    <div class="page-header glass-card">
      <h2>系统公告</h2>
      <p class="page-subtitle">了解平台最新动态、活动信息与维护通知</p>
      <div class="filter-bar">
        <el-radio-group v-model="filterType" @change="handleFilter">
          <el-radio-button :value="null">全部</el-radio-button>
          <el-radio-button v-for="t in TYPE_OPTIONS" :key="t.value" :value="t.value">
            {{ t.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div v-if="loading" class="skeleton-grid">
      <el-skeleton v-for="i in 6" :key="i" class="skeleton-card glass-card" animated>
        <template #template>
          <el-skeleton-item variant="rect" style="height: 90px; border-radius: 12px" />
          <el-skeleton-item variant="text" style="margin-top: 12px" />
          <el-skeleton-item variant="text" style="width: 60%" />
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="records.length" class="announcement-list">
      <div
        v-for="item in records"
        :key="item.id"
        class="announcement-item glass-card"
        @click="goDetail(item.id)"
      >
        <div class="item-head">
          <el-tag :type="typeTagType(item.type)" size="small">{{ typeText(item.type) }}</el-tag>
          <el-tag v-if="item.level > 1" :type="item.level === 3 ? 'danger' : 'warning'" size="small" effect="plain">
            {{ levelText(item.level) }}
          </el-tag>
          <span v-if="!item.isRead" class="unread-dot" title="未读"></span>
        </div>
        <h3 class="item-title">
          <span v-if="item.isTop" class="top-badge">置顶</span>
          {{ item.title }}
        </h3>
        <p class="item-summary">{{ item.summary || stripHtml(item.content) }}</p>
        <div class="item-foot">
          <span class="item-time">{{ formatTime(item.publishTime) }}</span>
          <span class="item-views">{{ item.viewCount || 0 }} 次浏览</span>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无公告" />

    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, jumper"
        @current-change="fetchList"
      />
    </div>

    <AnnouncementDetailModal v-model:visible="detailVisible" :id="detailId" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getAnnouncementList } from '@/api/announcement'
import { useAnnouncementStore } from '@/stores/announcementStore'
import AnnouncementDetailModal from '@/components/AnnouncementComponents/AnnouncementDetailModal.vue'

const TYPE_OPTIONS = [
  { value: 1, label: '系统公告' },
  { value: 2, label: '活动通知' },
  { value: 3, label: '维护通知' },
  { value: 4, label: '版本更新' }
]

const store = useAnnouncementStore()
const loading = ref(true)
const records = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(9)
const filterType = ref(null)
const detailVisible = ref(false)
const detailId = ref(null)

onMounted(async () => {
  await fetchList()
  // 回到列表页时刷新未读数
  store.fetchUnread()
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getAnnouncementList({
      page: page.value,
      size: pageSize.value,
      type: filterType.value
    })
    if (res.code === 200) {
      records.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  page.value = 1
  fetchList()
}

function goDetail(id) {
  // 模态框打开时自动标记已读（消除红点）
  detailId.value = id
  detailVisible.value = true
}

function typeText(type) {
  return TYPE_OPTIONS.find((t) => t.value === type)?.label || '公告'
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
function stripHtml(html) {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').slice(0, 100)
}
</script>

<style scoped>
.announcement-page {
  max-width: 1200px;
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
  transition: all 0.3s ease;
}
.page-header {
  padding: 28px 32px;
  margin-bottom: 24px;
}
.page-header h2 {
  margin: 0 0 6px;
  color: #1d1d1f;
}
.page-subtitle {
  margin: 0 0 16px;
  font-size: 14px;
  color: #888;
}
.filter-bar {
  margin-top: 8px;
}
.announcement-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.announcement-item {
  padding: 20px;
  cursor: pointer;
}
.announcement-item:hover {
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.25);
  transform: translateY(-2px);
}
.item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f56c6c;
  margin-left: auto;
}
.item-title {
  margin: 0 0 8px;
  font-size: 16px;
  color: #1d1d1f;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.top-badge {
  display: inline-block;
  margin-right: 6px;
  padding: 1px 6px;
  border-radius: 4px;
  background: linear-gradient(135deg, #f97316, #ef4444);
  color: #fff;
  font-size: 12px;
  font-weight: normal;
  vertical-align: 2px;
}
.item-summary {
  margin: 0 0 14px;
  font-size: 13px;
  color: #666;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 44px;
}
.item-foot {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.skeleton-card {
  padding: 20px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 28px;
}
@media (min-width: 768px) and (max-width: 1199px) {
  .announcement-list,
  .skeleton-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 767px) {
  .announcement-list,
  .skeleton-grid {
    grid-template-columns: 1fr;
  }
}
</style>
