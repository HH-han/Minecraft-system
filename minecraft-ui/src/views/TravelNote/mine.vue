<template>
  <div class="note-mine">
    <header class="note-mine__hero">
      <div>
        <p class="note-mine__eyebrow">MY JOURNAL</p>
        <h2 class="note-mine__title">我的笔记</h2>
        <p class="note-mine__sub">管理你的创作、草稿与发布记录</p>
      </div>
      <el-button type="primary" round @click="router.push('/travel-note/editor')">
        <el-icon class="note-mine__mr"><EditPen /></el-icon>
        写笔记
      </el-button>
    </header>

    <div class="note-mine__filters">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="草稿箱" name="0" />
        <el-tab-pane label="待审核" name="1" />
        <el-tab-pane label="已发布" name="2" />
        <el-tab-pane label="已驳回" name="3" />
        <el-tab-pane label="私密" name="5" />
      </el-tabs>
    </div>

    <div v-loading="loading" class="note-mine__list">
      <template v-if="notes.length">
        <div v-for="note in notes" :key="note.id" class="note-mine__item" @click="goDetail(note)">
          <div class="note-mine__cover">
            <img v-if="note.coverUrl" :src="note.coverUrl" alt="" />
            <el-icon v-else :size="24"><Picture /></el-icon>
          </div>
          <div class="note-mine__info">
            <div class="note-mine__name">
              {{ note.title }}
              <el-tag v-if="note.isTop" type="danger" size="small" effect="plain" round>顶</el-tag>
              <el-tag v-if="note.isFeatured" type="warning" size="small" effect="plain" round>精</el-tag>
            </div>
            <div class="note-mine__summary">{{ note.summary || '暂无摘要' }}</div>
            <div class="note-mine__stats">
              <span><el-icon><View /></el-icon>{{ note.viewCount || 0 }}</span>
              <span><el-icon><Pointer /></el-icon>{{ note.likeCount || 0 }}</span>
              <span><el-icon><Star /></el-icon>{{ note.collectCount || 0 }}</span>
              <span><el-icon><ChatDotRound /></el-icon>{{ note.commentCount || 0 }}</span>
            </div>
          </div>
          <div class="note-mine__side">
            <el-tag :type="statusType(note.status)" size="small" round>{{ statusLabel(note.status) }}</el-tag>
            <span v-if="note.status === 3 && note.rejectReason" class="note-mine__reject">
              驳回原因：{{ note.rejectReason }}
            </span>
            <span class="note-mine__time">{{ formatTime(note.updateTime || note.createTime) }}</span>
            <div class="note-mine__ops" @click.stop>
              <el-button size="small" round @click="goEditor(note)">编辑</el-button>
              <el-popconfirm title="确定删除这篇笔记吗？" @confirm="removeNote(note)">
                <template #reference>
                  <el-button size="small" type="danger" plain round>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </template>
      <div v-else-if="!loading" class="note-mine__empty">
        <div class="note-mine__empty-icon">✎</div>
        <p class="note-mine__empty-title">这里还什么都没有</p>
        <p class="note-mine__empty-desc">记录下一次旅程，从第一篇笔记开始</p>
        <el-button type="primary" round @click="router.push('/travel-note/editor')">去写笔记</el-button>
      </div>
    </div>

    <div v-if="total > query.pageSize" class="note-mine__pagination">
      <el-pagination background layout="prev, pager, next" :total="total"
        :page-size="query.pageSize" :current-page="query.pageNum" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, EditPen, Picture, Pointer, Star, View } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { deleteNote, getMyNotes } from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const activeStatus = ref('all')
const loading = ref(false)
const notes = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10 })

const STATUS_MAP = {
  0: { label: '草稿', type: 'info' },
  1: { label: '待审核', type: 'warning' },
  2: { label: '已发布', type: 'success' },
  3: { label: '已驳回', type: 'danger' },
  4: { label: '已下架', type: 'info' },
  5: { label: '私密', type: 'info' }
}

const statusLabel = (s) => STATUS_MAP[s]?.label || '未知'
const statusType = (s) => STATUS_MAP[s]?.type || 'info'

onMounted(() => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.replace('/login')
    return
  }
  fetchNotes()
})

async function fetchNotes() {
  loading.value = true
  try {
    const params = { ...query }
    if (activeStatus.value !== 'all') {
      params.status = Number(activeStatus.value)
    }
    const res = await getMyNotes(params)
    if (res.code === 200) {
      notes.value = res.data?.records || []
      total.value = Number(res.data?.total || 0)
    }
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  query.pageNum = 1
  fetchNotes()
}

function handlePageChange(page) {
  query.pageNum = page
  fetchNotes()
}

function goDetail(note) {
  if (note.status === 2) {
    router.push(`/travel-note/detail/${note.id}`)
  }
}

function goEditor(note) {
  router.push(`/travel-note/editor?id=${note.id}`)
}

async function removeNote(note) {
  const res = await deleteNote(note.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    fetchNotes()
  } else {
    ElMessage.error(res.message || '删除失败')
  }
}

function formatTime(value) {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.note-mine {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-accent-2: var(--theme-accent-2);
  --tn-line: var(--theme-border);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 960px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 20px 72px;
  font-family: var(--tn-font);
  color: var(--tn-ink);
}

.note-mine::before {
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
.note-mine__hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
  animation: nm-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.note-mine__eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--tn-accent);
}

.note-mine__title {
  margin: 0;
  font-size: clamp(24px, 3.4vw, 34px);
  font-weight: 800;
  letter-spacing: -0.02em;
}

.note-mine__sub {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--tn-ink-2);
}

.note-mine__mr {
  margin-right: 4px;
}

/* Filters */
.note-mine__filters {
  padding: 6px 18px 0;
  border-radius: 18px;
  background: var(--theme-bg-glass);
  backdrop-filter: saturate(180%) blur(16px);
  -webkit-backdrop-filter: saturate(180%) blur(16px);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  margin-bottom: 18px;
  animation: nm-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.note-mine__filters :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.note-mine__filters :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.note-mine__filters :deep(.el-tabs__item) {
  height: 46px;
  font-weight: 500;
  color: var(--tn-ink-2);
}

.note-mine__filters :deep(.el-tabs__item.is-active) {
  color: var(--tn-ink);
  font-weight: 700;
}

.note-mine__filters :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, var(--tn-accent), var(--tn-accent-2));
}

/* List */
.note-mine__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 220px;
}

.note-mine__item {
  display: flex;
  gap: 14px;
  padding: 14px;
  border-radius: 18px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-sm);
  cursor: pointer;
  transition:
    transform 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.3s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.3s ease;
  animation: nm-rise 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.note-mine__item:hover {
  transform: translateY(-3px);
  border-color: color-mix(in srgb, var(--theme-primary) 24%, transparent);
  box-shadow: var(--theme-shadow-lg);
}

.note-mine__cover {
  width: 132px;
  height: 90px;
  border-radius: 12px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.note-mine__cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.note-mine__info {
  flex: 1;
  min-width: 0;
}

.note-mine__name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--tn-ink);
  display: flex;
  align-items: center;
  gap: 6px;
}

.note-mine__summary {
  margin-top: 6px;
  font-size: 13px;
  line-height: 1.55;
  color: var(--tn-ink-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.note-mine__stats {
  margin-top: 10px;
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: var(--tn-ink-3);
}

.note-mine__stats span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.note-mine__side {
  width: 200px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.note-mine__reject {
  font-size: 12px;
  color: var(--theme-danger);
  text-align: right;
}

.note-mine__time {
  font-size: 12px;
  color: var(--tn-ink-3);
}

.note-mine__ops {
  display: flex;
  gap: 6px;
  margin-top: 2px;
}

/* Empty */
.note-mine__empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 72px 20px;
  text-align: center;
}

.note-mine__empty-icon {
  font-size: 42px;
  color: var(--tn-accent);
  opacity: 0.5;
}

.note-mine__empty-title {
  margin: 6px 0 0;
  font-size: 18px;
  font-weight: 700;
}

.note-mine__empty-desc {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--tn-ink-2);
}

.note-mine__pagination {
  margin-top: 22px;
  display: flex;
  justify-content: center;
}

@keyframes nm-rise {
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
  .note-mine {
    margin-top: 84px;
    padding: 24px 14px 56px;
  }
  .note-mine__item {
    flex-wrap: wrap;
  }
  .note-mine__side {
    width: 100%;
    align-items: flex-start;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
  }
}
</style>
