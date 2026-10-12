<template>
  <div class="announcement-page">
    <div class="page-header glass-card">
      <div class="header-hero">
        <div class="hero-icon">
          <el-icon :size="26"><Bell /></el-icon>
        </div>
        <div class="hero-text">
          <h2>系统公告</h2>
          <p class="page-subtitle">了解平台最新动态、活动信息与维护通知</p>
        </div>
        <transition name="pop">
          <div v-if="store.unreadCount > 0" class="unread-chip">
            <span class="chip-dot" />
            {{ store.unreadCount }} 条未读
          </div>
        </transition>
      </div>

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
          <el-skeleton-item variant="rect" style="height: 88px; border-radius: 12px" />
          <el-skeleton-item variant="text" style="margin-top: 14px" />
          <el-skeleton-item variant="text" style="width: 60%" />
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="records.length" class="announcement-list">
      <article
        v-for="(item, index) in records"
        :key="item.id"
        class="announcement-item glass-card"
        :class="`type-${item.type}`"
        :style="{ animationDelay: `${index * 55}ms` }"
        @click="goDetail(item.id)"
      >
        <div class="item-head">
          <span class="type-chip">
            <i class="chip-dot" />
            {{ typeText(item.type) }}
          </span>
          <span v-if="item.level > 1" class="level-chip" :class="`level-${item.level}`">
            {{ levelText(item.level) }}
          </span>
          <span v-if="!item.isRead" class="unread-dot" title="未读"></span>
        </div>
        <h3 class="item-title">
          <span v-if="item.isTop" class="top-badge">置顶</span>
          {{ item.title }}
        </h3>
        <p class="item-summary">{{ item.summary || stripHtml(item.content) }}</p>
        <div class="item-foot">
          <span class="item-time">{{ formatTime(item.publishTime) }}</span>
          <span class="item-views">
            <el-icon><View /></el-icon>
            {{ item.viewCount || 0 }} 次浏览
          </span>
        </div>
      </article>
    </div>

    <div v-else class="empty-wrap glass-card">
      <el-empty description="暂无公告" :image-size="96" />
    </div>

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
import { onMounted, ref, watch } from 'vue'
import { Bell, View } from '@element-plus/icons-vue'
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

// 关闭详情后同步消除列表红点（弹窗内已调 store.read 标记已读）
watch(detailVisible, (v) => {
  if (v) return
  const item = records.value.find((r) => r.id === detailId.value)
  if (item && !item.isRead) item.isRead = true
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
function levelText(level) {
  return { 2: '重要', 3: '紧急' }[level] || '普通'
}
function formatTime(time) {
  if (!time) return ''
  const d = new Date(time)
  if (isNaN(d.getTime())) return ''
  const now = new Date()
  const diff = now - d
  const pad = (n) => String(n).padStart(2, '0')
  const hm = `${pad(d.getHours())}:${pad(d.getMinutes())}`
  if (diff < 60_000) return '刚刚'
  if (diff < 3_600_000) return `${Math.floor(diff / 60_000)} 分钟前`
  const startToday = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const startYesterday = startToday - 86_400_000
  if (d >= startToday) return `今天 ${hm}`
  if (d >= startYesterday) return `昨天 ${hm}`
  if (diff < 7 * 86_400_000) return `${Math.floor(diff / 86_400_000)} 天前`
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}
function stripHtml(html) {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').slice(0, 100)
}
</script>

<style scoped>
.announcement-page {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 24px 16px 60px;
}
.glass-card {
  background: var(--theme-bg-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--theme-border);
  border-radius: 16px;
  box-shadow: var(--theme-shadow-md);
  transition: all 0.3s ease;
}

/* ==================== 页头 ==================== */
.page-header {
  padding: 28px 32px 24px;
  margin-bottom: 24px;
  animation: card-in 0.5s cubic-bezier(0.22, 0.61, 0.36, 1) backwards;
}
.header-hero {
  display: flex;
  align-items: center;
  gap: 16px;
}
.hero-icon {
  flex-shrink: 0;
  width: 52px;
  height: 52px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  color: #fff;
  background: var(--theme-gradient);
  box-shadow:
    0 8px 20px color-mix(in srgb, var(--theme-primary) 35%, transparent),
    inset 0 1px 0 var(--theme-bg-glass);
}
.hero-text {
  flex: 1;
  min-width: 0;
}
.page-header h2 {
  margin: 0 0 4px;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: var(--theme-text-1);
}
.page-subtitle {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-2);
}
.unread-chip {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-danger);
  background: var(--theme-danger-soft);
  border: 1px solid color-mix(in srgb, var(--theme-danger) 22%, transparent);
}
.unread-chip .chip-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--theme-danger);
  animation: pulse 1.8s ease-out infinite;
}
.pop-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.pop-leave-active {
  transition: all 0.2s ease;
}
.pop-enter-from,
.pop-leave-to {
  opacity: 0;
  transform: scale(0.85);
}

/* iOS 风格分段筛选 */
.filter-bar {
  margin-top: 18px;
}
.filter-bar :deep(.el-radio-group) {
  display: inline-flex;
  gap: 2px;
  max-width: 100%;
  padding: 4px;
  overflow-x: auto;
  scrollbar-width: none;
  background: var(--theme-bg-muted);
  border: 1px solid var(--theme-border);
  border-radius: 999px;
  box-shadow: inset 0 1px 3px var(--theme-hairline);
}
.filter-bar :deep(.el-radio-group)::-webkit-scrollbar {
  display: none;
}
.filter-bar :deep(.el-radio-button__inner) {
  border: none !important;
  border-radius: 999px !important;
  background: transparent;
  padding: 7px 18px;
  font-size: 13px;
  color: var(--theme-text-2);
  white-space: nowrap;
  transition: all 0.25s ease;
}
.filter-bar :deep(.el-radio-button__inner:hover) {
  color: var(--theme-text-1);
}
.filter-bar :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  color: var(--theme-primary-text);
  background: var(--theme-primary);
  box-shadow: var(--theme-shadow-sm) !important;
}

/* ==================== 公告卡片 ==================== */
.announcement-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.announcement-item {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 20px;
  cursor: pointer;
  overflow: hidden;
  animation: card-in 0.5s cubic-bezier(0.22, 0.61, 0.36, 1) backwards;
}
/* 类型强调色 */
.type-1 {
  --accent: var(--theme-primary);
  --accent-soft: var(--theme-primary-soft);
}
.type-2 {
  --accent: var(--theme-warning);
  --accent-soft: var(--theme-warning-soft);
}
.type-3 {
  --accent: var(--theme-danger);
  --accent-soft: var(--theme-danger-soft);
}
.type-4 {
  --accent: var(--theme-success);
  --accent-soft: var(--theme-success-soft);
}
/* 悬停时右上角浮现类型色光晕 */
.announcement-item::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(240px 150px at 100% 0%, var(--accent-soft), transparent 70%);
  opacity: 0;
  transition: opacity 0.35s ease;
  pointer-events: none;
}
/* 悬停时底部浮现类型色渐进线 */
.announcement-item::after {
  content: '';
  position: absolute;
  left: 20px;
  right: 20px;
  bottom: 0;
  height: 3px;
  border-radius: 3px 3px 0 0;
  background: linear-gradient(90deg, var(--accent), transparent);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.35s cubic-bezier(0.22, 0.61, 0.36, 1);
}
.announcement-item:hover {
  box-shadow: var(--theme-shadow-md);
  transform: translateY(-4px);
  border-color: var(--theme-border-strong);
}
.announcement-item:hover::before {
  opacity: 1;
}
.announcement-item:hover::after {
  transform: scaleX(1);
}

.item-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.type-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  color: var(--accent, var(--theme-text-2));
  background: var(--accent-soft, var(--theme-bg-muted));
}
.type-chip .chip-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}
.level-chip {
  padding: 2px 9px;
  border-radius: 999px;
  font-size: 12px;
}
.level-2 {
  color: var(--theme-warning);
  background: var(--theme-warning-soft);
  border: 1px solid color-mix(in srgb, var(--theme-warning) 30%, transparent);
}
.level-3 {
  color: var(--theme-danger);
  background: var(--theme-danger-soft);
  border: 1px solid color-mix(in srgb, var(--theme-danger) 32%, transparent);
}
.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--theme-danger);
  margin-left: auto;
  animation: pulse 1.8s ease-out infinite;
}
.item-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  transition: color 0.25s ease;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.announcement-item:hover .item-title {
  color: var(--accent);
}
.top-badge {
  display: inline-block;
  margin-right: 6px;
  padding: 1px 7px;
  border-radius: 5px;
  background: linear-gradient(135deg, var(--theme-warning), var(--theme-danger));
  color: var(--theme-primary-text);
  font-size: 12px;
  font-weight: normal;
  vertical-align: 2px;
  box-shadow: 0 3px 8px color-mix(in srgb, var(--theme-danger) 30%, transparent);
}
.item-summary {
  margin: 0 0 14px;
  font-size: 13px;
  color: var(--theme-text-2);
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
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid var(--theme-hairline);
  font-size: 12px;
  color: var(--theme-text-3);
}
.item-views {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* ==================== 骨架屏 / 空态 / 分页 ==================== */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.skeleton-card {
  padding: 20px;
}
.empty-wrap {
  padding: 48px 20px;
  animation: card-in 0.5s cubic-bezier(0.22, 0.61, 0.36, 1) backwards;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 28px;
  animation: card-in 0.5s cubic-bezier(0.22, 0.61, 0.36, 1) backwards;
}
.pagination-wrap :deep(.el-pagination) {
  padding: 7px 14px;
  background: var(--theme-bg-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid var(--theme-border);
  border-radius: 999px;
  box-shadow: var(--theme-shadow-sm);
}
.pagination-wrap :deep(.el-pager li),
.pagination-wrap :deep(.btn-prev),
.pagination-wrap :deep(.btn-next) {
  border-radius: 8px;
  background: transparent;
}
.pagination-wrap :deep(.el-pager li.is-active) {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: none;
  }
}
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 color-mix(in srgb, var(--theme-danger) 45%, transparent);
  }
  70% {
    box-shadow: 0 0 0 7px transparent;
  }
  100% {
    box-shadow: 0 0 0 0 transparent;
  }
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
  .page-header {
    padding: 22px 18px 18px;
  }
  .header-hero {
    flex-wrap: wrap;
  }
  .hero-icon {
    width: 44px;
    height: 44px;
    border-radius: 13px;
  }
  .filter-bar :deep(.el-radio-button__inner) {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>
