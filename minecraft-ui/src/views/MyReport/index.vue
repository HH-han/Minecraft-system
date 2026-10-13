<template>
  <div class="my-report">
    <!-- 顶部 -->
    <header class="my-report__hero">
      <div>
        <p class="my-report__eyebrow">MY REPORTS</p>
        <h2 class="my-report__title">我的举报</h2>
        <p class="my-report__sub">查看你提交的举报记录与处理进度</p>
      </div>
      <el-button round @click="router.push('/travel-note')">去笔记广场</el-button>
    </header>

    <!-- 状态筛选 -->
    <div class="my-report__filters">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待处理" name="0" />
        <el-tab-pane label="已处理" name="1" />
        <el-tab-pane label="已驳回" name="2" />
      </el-tabs>
    </div>

    <!-- 列表 -->
    <div v-loading="loading" class="my-report__list">
      <template v-if="reports.length">
        <div v-for="item in reports" :key="item.id" class="my-report__item">
          <div class="my-report__icon">
            <el-icon :size="20"><Flag /></el-icon>
          </div>
          <div class="my-report__info">
            <div class="my-report__name">{{ item.noteTitle || '笔记已删除' }}</div>
            <div class="my-report__reason">举报原因：{{ item.reason || '违规内容' }}</div>
            <div class="my-report__times">
              <span>提交于 {{ formatTime(item.createTime) }}</span>
              <span v-if="item.handleTime">处理于 {{ formatTime(item.handleTime) }}</span>
            </div>
          </div>
          <div class="my-report__side">
            <el-tag :type="statusType(item.status)" size="small" round>{{ statusLabel(item.status) }}</el-tag>
          </div>
        </div>
      </template>
      <div v-else-if="!loading" class="my-report__empty">
        <div class="my-report__empty-icon">⚑</div>
        <p class="my-report__empty-title">暂无举报记录</p>
        <p class="my-report__empty-desc">发现违规内容时，可在笔记详情页发起举报</p>
        <el-button type="primary" round @click="router.push('/travel-note')">浏览笔记</el-button>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total > query.pageSize" class="my-report__pagination">
      <el-pagination background layout="prev, pager, next" :total="total"
        :page-size="query.pageSize" :current-page="query.pageNum" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Flag } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { getMyReports } from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const router = useRouter()
const authStore = useAuthStore()

const activeStatus = ref('all')
const loading = ref(false)
const reports = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const STATUS_MAP = {
  0: { label: '待处理', type: 'warning' },
  1: { label: '已处理', type: 'success' },
  2: { label: '已驳回', type: 'info' }
}

const statusLabel = (s) => STATUS_MAP[s]?.label || '未知'
const statusType = (s) => STATUS_MAP[s]?.type || 'info'

onMounted(() => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.replace('/login')
    return
  }
  fetchReports()
})

async function fetchReports() {
  loading.value = true
  try {
    const params = { ...query }
    if (activeStatus.value !== 'all') {
      params.status = Number(activeStatus.value)
    }
    const res = await getMyReports(params)
    if (res.code === 200) {
      reports.value = res.data?.records || []
      total.value = Number(res.data?.total || 0)
    }
  } finally {
    loading.value = false
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchReports)

function handleTabChange() {
  query.pageNum = 1
  fetchReports()
}

function handlePageChange(page) {
  query.pageNum = page
  fetchReports()
}

function formatTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.my-report {
  --mr-ink: var(--theme-text-1);
  --mr-ink-2: var(--theme-text-2);
  --mr-ink-3: var(--theme-text-3);
  --mr-accent: var(--theme-primary);
  --mr-accent-2: var(--theme-accent-2);
  --mr-line: var(--theme-border);
  --mr-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 960px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 20px 72px;
  font-family: var(--mr-font);
  color: var(--mr-ink);
}

.my-report::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(900px 460px at 6% -8%, var(--theme-primary-soft), transparent 60%),
    radial-gradient(760px 420px at 100% 2%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    var(--theme-bg-surface-2);
}

/* Hero */
.my-report__hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
  animation: mr-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.my-report__eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--mr-accent);
}

.my-report__title {
  margin: 0;
  font-size: clamp(24px, 3.4vw, 34px);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.my-report__sub {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--mr-ink-2);
}

/* Filters */
.my-report__filters {
  padding: 6px 18px 0;
  border-radius: 18px;
  background: var(--theme-bg-glass);
  backdrop-filter: saturate(180%) blur(16px);
  -webkit-backdrop-filter: saturate(180%) blur(16px);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  margin-bottom: 18px;
  animation: mr-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.my-report__filters :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.my-report__filters :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.my-report__filters :deep(.el-tabs__item) {
  height: 46px;
  font-weight: 500;
  color: var(--mr-ink-2);
}

.my-report__filters :deep(.el-tabs__item.is-active) {
  color: var(--mr-ink);
  font-weight: 700;
}

.my-report__filters :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, var(--mr-accent), var(--mr-accent-2));
}

/* List */
.my-report__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 220px;
}

.my-report__item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  border-radius: 18px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--mr-line);
  box-shadow: var(--theme-shadow-sm);
  transition:
    transform 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.3s ease;
  animation: mr-rise 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.my-report__item:hover {
  transform: translateY(-3px);
  border-color: color-mix(in srgb, var(--theme-primary) 24%, transparent);
  box-shadow: var(--theme-shadow-lg);
}

.my-report__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: var(--theme-primary-soft);
  color: var(--mr-accent);
}

.my-report__info {
  flex: 1;
  min-width: 0;
}

.my-report__name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--mr-ink);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.my-report__reason {
  margin-top: 6px;
  font-size: 13px;
  line-height: 1.55;
  color: var(--mr-ink-2);
  word-break: break-word;
}

.my-report__times {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  font-size: 12px;
  color: var(--mr-ink-3);
}

.my-report__side {
  flex-shrink: 0;
}

/* Empty */
.my-report__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 72px 20px;
  text-align: center;
}

.my-report__empty-icon {
  font-size: 42px;
  color: var(--mr-accent);
  opacity: 0.5;
}

.my-report__empty-title {
  margin: 6px 0 0;
  font-size: 18px;
  font-weight: 700;
}

.my-report__empty-desc {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--mr-ink-2);
}

.my-report__pagination {
  margin-top: 22px;
  display: flex;
  justify-content: center;
}

@keyframes mr-rise {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 720px) {
  .my-report {
    margin-top: 84px;
    padding: 24px 14px 56px;
  }
}
</style>
