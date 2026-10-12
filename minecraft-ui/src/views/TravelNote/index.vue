<template>
  <div class="tn-square">
    <!-- Hero -->
    <header class="tn-hero">
      <p class="tn-hero__eyebrow">TRAVEL JOURNAL</p>
      <h1 class="tn-hero__title">旅行笔记广场</h1>
      <p class="tn-hero__sub">发现真实旅行者的足迹、攻略与灵感</p>
    </header>

    <!-- 工具栏 -->
    <div class="tn-toolbar">
      <el-tabs v-model="activeTab" class="tn-toolbar__tabs" @tab-change="handleTabChange">
        <el-tab-pane label="推荐" name="recommend" />
        <el-tab-pane label="最新" name="latest" />
        <el-tab-pane label="关注" name="follow" />
        <el-tab-pane label="同城" name="city" />
      </el-tabs>

      <div class="tn-toolbar__actions">
        <el-input
          v-model="keyword"
          class="tn-toolbar__search"
          placeholder="搜索目的地 / 标题 / 关键词"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" round @click="goEditor()">
          <el-icon class="tn-mr"><EditPen /></el-icon>
          写笔记
        </el-button>
        <el-button round @click="goMine">我的笔记</el-button>
      </div>
    </div>

    <!-- 热门标签 -->
    <div v-if="hotTags.length" class="tn-tags">
      <button
        v-for="tag in hotTags"
        :key="tag.id"
        type="button"
        class="tn-chip"
        :class="{ 'tn-chip--active': query.tagId === tag.id }"
        @click="handleTagClick(tag)"
      >
        # {{ tag.name }}
      </button>
    </div>

    <!-- 瀑布流 -->
    <div v-loading="loading" class="tn-waterfall">
      <template v-if="notes.length">
        <div
          v-for="(note, i) in notes"
          :key="note.id"
          class="tn-waterfall__item"
          :style="{ animationDelay: `${Math.min(i, 11) * 45}ms` }"
        >
          <NoteCard :note="note" @click="goDetail" />
        </div>
      </template>
      <div v-else-if="!loading" class="tn-empty">
        <div class="tn-empty__icon">◇</div>
        <p class="tn-empty__title">还没有笔记</p>
        <p class="tn-empty__desc">成为第一个分享旅程的人吧</p>
        <el-button type="primary" round @click="goEditor()">立即写笔记</el-button>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="notes.length" class="tn-more">
      <el-button round :loading="loading" :disabled="noMore" @click="loadMore">
        {{ noMore ? '没有更多了' : '加载更多' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen, Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import NoteCard from './components/NoteCard.vue'
import { getHotTags, getNotePage } from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('recommend')
const keyword = ref('')
const loading = ref(false)
const notes = ref([])
const hotTags = ref([])
const noMore = ref(false)

const query = reactive({
  pageNum: 1,
  pageSize: 12,
  tab: 'recommend',
  keyword: '',
  tagId: null,
  cityId: null
})

onMounted(() => {
  fetchNotes()
  fetchHotTags()
})

async function fetchNotes() {
  loading.value = true
  try {
    const res = await getNotePage({ ...query })
    if (res.code === 200) {
      const records = res.data?.records || []
      if (query.pageNum === 1) {
        notes.value = records
      } else {
        notes.value = [...notes.value, ...records]
      }
      noMore.value = notes.value.length >= Number(res.data?.total || 0)
    }
  } finally {
    loading.value = false
  }
}

async function fetchHotTags() {
  const res = await getHotTags(12)
  if (res.code === 200) {
    hotTags.value = res.data || []
  }
}

function resetAndFetch() {
  query.pageNum = 1
  noMore.value = false
  fetchNotes()
}

function handleTabChange(tab) {
  query.tab = tab
  if (tab === 'follow' && !authStore.isAuthenticated) {
    ElMessage.warning('登录后可查看关注作者的笔记')
  }
  resetAndFetch()
}

function handleSearch() {
  query.keyword = keyword.value.trim()
  resetAndFetch()
}

function handleTagClick(tag) {
  query.tagId = query.tagId === tag.id ? null : tag.id
  resetAndFetch()
}

function loadMore() {
  query.pageNum += 1
  fetchNotes()
}

function goDetail(note) {
  router.push(`/travel-note/detail/${note.id}`)
}

function goEditor() {
  router.push('/travel-note/editor')
}

function goMine() {
  router.push('/travel-note/mine')
}
</script>

<style scoped>
/* ============ Design tokens ============ */
.tn-square {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-accent-2: var(--theme-accent-2);
  --tn-surface: var(--theme-bg-glass);
  --tn-line: var(--theme-border);
  --tn-shadow-sm: var(--theme-shadow-sm);
  --tn-shadow-md: var(--theme-shadow-md);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  margin-top: 70px;
  padding: 40px 20px 72px;
  font-family: var(--tn-font);
  color: var(--tn-ink);
  background:
    radial-gradient(1100px 560px at 8% -12%, var(--theme-primary-soft), transparent 60%),
    radial-gradient(900px 520px at 102% -4%, color-mix(in srgb, var(--theme-accent-2) 10%, transparent), transparent 58%),
    radial-gradient(800px 620px at 50% 118%, var(--theme-success-soft), transparent 62%),
    var(--theme-bg-surface-2);
}

.tn-square::before {
  content: '';
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image: radial-gradient(color-mix(in srgb, var(--theme-text-1) 6%, transparent) 1px, transparent 1px);
  background-size: 26px 26px;
  mask-image: linear-gradient(180deg, var(--theme-text-1) 0%, transparent 42%);
}

.tn-square > * {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

/* ============ Hero ============ */
.tn-hero {
  margin-bottom: 24px;
  animation: tn-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.tn-hero__eyebrow {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--tn-accent);
}

.tn-hero__title {
  margin: 0;
  font-size: clamp(28px, 4vw, 42px);
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.1;
  color: var(--tn-ink);
}

.tn-hero__sub {
  margin: 10px 0 0;
  font-size: 15px;
  color: var(--tn-ink-2);
}

/* ============ Toolbar ============ */
.tn-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  border-radius: 20px;
  background: var(--tn-surface);
  backdrop-filter: saturate(180%) blur(18px);
  -webkit-backdrop-filter: saturate(180%) blur(18px);
  border: 1px solid var(--theme-border);
  box-shadow: var(--tn-shadow-sm);
  animation: tn-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.tn-toolbar__tabs {
  flex: 1;
  min-width: 260px;
}

.tn-toolbar__tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.tn-toolbar__tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.tn-toolbar__tabs :deep(.el-tabs__item) {
  height: 40px;
  font-size: 15px;
  font-weight: 500;
  color: var(--tn-ink-2);
}

.tn-toolbar__tabs :deep(.el-tabs__item.is-active) {
  color: var(--tn-ink);
  font-weight: 700;
}

.tn-toolbar__tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
  background: linear-gradient(90deg, var(--tn-accent), var(--tn-accent-2));
}

.tn-toolbar__actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tn-toolbar__search {
  width: 250px;
}

.tn-toolbar__search :deep(.el-input__wrapper) {
  border-radius: 999px;
  background: var(--theme-bg-surface);
  box-shadow: 0 0 0 1px var(--tn-line) inset;
  padding-left: 14px;
}

.tn-mr {
  margin-right: 4px;
}

/* ============ Tags ============ */
.tn-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 18px 0 6px;
  animation: tn-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.1s both;
}

.tn-chip {
  padding: 6px 14px;
  border-radius: 999px;
  border: 1px solid var(--tn-line);
  background: var(--theme-bg-glass);
  color: var(--tn-ink-2);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.22s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: inherit;
}

.tn-chip:hover {
  transform: translateY(-1px);
  color: var(--tn-accent);
  border-color: color-mix(in srgb, var(--theme-primary) 35%, transparent);
  box-shadow: var(--tn-shadow-sm);
}

.tn-chip--active {
  color: var(--theme-primary-text);
  border-color: transparent;
  background: var(--theme-gradient);
  box-shadow: 0 8px 18px -8px color-mix(in srgb, var(--theme-primary) 70%, transparent);
}

/* ============ Waterfall ============ */
.tn-waterfall {
  margin-top: 18px;
  columns: 4 240px;
  column-gap: 18px;
  min-height: 260px;
}

.tn-waterfall__item {
  break-inside: avoid;
  margin-bottom: 18px;
  animation: tn-rise 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* ============ Empty ============ */
.tn-empty {
  column-span: all;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 72px 20px;
  text-align: center;
}

.tn-empty__icon {
  font-size: 44px;
  color: var(--tn-accent);
  opacity: 0.5;
}

.tn-empty__title {
  margin: 6px 0 0;
  font-size: 18px;
  font-weight: 700;
}

.tn-empty__desc {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--tn-ink-2);
}

/* ============ More ============ */
.tn-more {
  margin-top: 26px;
  text-align: center;
}

@keyframes tn-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .tn-square {
    margin-top: 84px;
    padding: 28px 14px 56px;
  }
  .tn-toolbar__search {
    width: 100%;
  }
  .tn-toolbar__actions {
    width: 100%;
  }
  .tn-waterfall {
    columns: 2 150px;
    column-gap: 12px;
  }
  .tn-waterfall__item {
    margin-bottom: 12px;
  }
}
</style>
