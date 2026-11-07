<template>
  <div class="tn-square">
    <div class="tn-inner">
      <!-- Hero -->
      <header class="tn-hero">
        <div class="tn-hero__text">
          <p class="tn-hero__eyebrow">{{ $t('travelNote.square.eyebrow') }}</p>
          <h1 class="tn-hero__title">{{ $t('travelNote.square.title') }}</h1>
          <p class="tn-hero__sub">{{ $t('travelNote.square.subtitle') }}</p>
        </div>

        <div class="tn-hero__actions">
          <el-button class="tn-hero__btn" round @click="goMine">
            <el-icon class="tn-icon-gap"><Notebook /></el-icon>
            {{ $t('travelNote.square.mine') }}
          </el-button>
          <el-button class="tn-hero__btn tn-hero__btn--primary" type="primary" round @click="goEditor()">
            <el-icon class="tn-icon-gap"><EditPen /></el-icon>
            {{ $t('travelNote.square.write') }}
          </el-button>
        </div>
      </header>

      <!-- 工具栏：分类 + 搜索 -->
      <div class="tn-toolbar">
        <el-tabs v-model="activeTab" class="tn-toolbar__tabs" @tab-change="handleTabChange">
          <el-tab-pane v-for="tab in tabs" :key="tab.name" :label="$t(tab.labelKey)" :name="tab.name" />
        </el-tabs>

        <el-input
          v-model="keyword"
          class="tn-toolbar__search"
          :placeholder="$t('travelNote.square.searchPlaceholder')"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <!-- 热门标签 -->
      <section v-if="hotTags.length" class="tn-tags">
        <span class="tn-tags__label">
          <el-icon><PriceTag /></el-icon>
          {{ $t('travelNote.square.hotTags') }}
        </span>
        <div class="tn-tags__list">
          <button
            v-for="tag in hotTags"
            :key="tag.id"
            type="button"
            class="tn-chip"
            :class="{ 'tn-chip--active': query.tagId === tag.id }"
            :aria-pressed="query.tagId === tag.id"
            @click="handleTagClick(tag)"
          >
            <span class="tn-chip__hash">#</span>
            <span class="tn-chip__name">{{ tag.name }}</span>
            <span v-if="tag.useCount" class="tn-chip__count">{{ formatTagCount(tag.useCount) }}</span>
          </button>
        </div>
      </section>

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
          <span class="tn-empty__icon">
            <el-icon :size="30"><Notebook /></el-icon>
          </span>
          <p class="tn-empty__title">{{ $t('travelNote.square.emptyTitle') }}</p>
          <p class="tn-empty__desc">{{ $t('travelNote.square.emptyDesc') }}</p>
          <el-button type="primary" round @click="goEditor()">
            {{ $t('travelNote.square.emptyAction') }}
          </el-button>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="notes.length" class="tn-more">
        <el-button round :loading="loading" :disabled="noMore" @click="loadMore">
          {{ noMore ? $t('travelNote.square.noMore') : $t('travelNote.square.loadMore') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen, Notebook, PriceTag, Search } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import NoteCard from './components/NoteCard.vue'
import { getHotTags, getNotePage } from '@/api/travelNote'
import { useAuthStore } from '@/stores/auth'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

const tabs = [
  { name: 'recommend', labelKey: 'travelNote.square.tabs.recommend' },
  { name: 'latest', labelKey: 'travelNote.square.tabs.latest' },
  { name: 'follow', labelKey: 'travelNote.square.tabs.follow' },
  { name: 'city', labelKey: 'travelNote.square.tabs.city' }
]

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

// 数据变更后自动刷新
useAutoRefresh(fetchNotes)

async function fetchHotTags() {
  const res = await getHotTags(12)
  if (res.code === 200) {
    hotTags.value = res.data || []
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchHotTags)

function resetAndFetch() {
  query.pageNum = 1
  noMore.value = false
  fetchNotes()
}

function handleTabChange(tab) {
  query.tab = tab
  if (tab === 'follow' && !authStore.isAuthenticated) {
    ElMessage.warning(t('travelNote.square.followHint'))
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

function formatTagCount(count) {
  const num = Number(count) || 0
  if (num >= 10000) return `${(num / 10000).toFixed(1)}w`
  return String(num)
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
  --tn-line: var(--theme-border);
  --tn-shadow-sm: var(--theme-shadow-sm);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  margin-top: 70px;
  padding: 44px 20px 80px;
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

/* 内容列：唯一宽度约束，保证 Hero / 工具栏 / 标签 / 瀑布流左边界对齐 */
.tn-inner {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

/* ============ Hero ============ */
.tn-hero {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px 32px;
  margin-bottom: 22px;
  animation: tn-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.tn-hero__text {
  min-width: 0;
}

.tn-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.22em;
  color: var(--tn-accent);
}

.tn-hero__eyebrow::before {
  content: '';
  width: 20px;
  height: 2px;
  border-radius: 2px;
  background: var(--theme-gradient);
}

.tn-hero__title {
  margin: 0;
  font-size: clamp(28px, 4vw, 42px);
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.12;
  color: var(--tn-ink);
}

.tn-hero__sub {
  margin: 10px 0 0;
  font-size: 15px;
  line-height: 1.6;
  color: var(--tn-ink-2);
}

.tn-hero__actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
  padding-bottom: 4px;
}

.tn-hero__btn {
  height: 42px;
  padding: 0 20px;
  border-radius: 999px;
  font-weight: 600;
  border-color: var(--tn-line);
  background: var(--theme-bg-surface);
  color: var(--tn-ink);
  transition:
    transform 0.22s cubic-bezier(0.22, 1, 0.36, 1),
    border-color 0.22s ease,
    color 0.22s ease;
}

.tn-hero__btn:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--theme-primary) 38%, transparent);
  color: var(--tn-accent);
  background: var(--theme-bg-surface);
}

.tn-hero__btn--primary {
  border-color: transparent;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  box-shadow: 0 12px 24px -14px color-mix(in srgb, var(--theme-primary) 80%, transparent);
}

.tn-hero__btn--primary:hover {
  color: var(--theme-primary-text);
  border-color: transparent;
  background: var(--theme-gradient);
  filter: brightness(1.05);
}

.tn-icon-gap {
  margin-right: 6px;
}

/* ============ Toolbar ============ */
.tn-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 24px;
  padding: 4px 12px 4px 22px;
  border-radius: 18px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--tn-shadow-sm);
  animation: tn-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.tn-toolbar__tabs {
  flex: 1 1 300px;
  min-width: 0;
}

.tn-toolbar__tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.tn-toolbar__tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.tn-toolbar__tabs :deep(.el-tabs__item) {
  height: 46px;
  padding: 0 18px;
  font-size: 15px;
  font-weight: 500;
  color: var(--tn-ink-2);
  transition: color 0.22s ease;
}

.tn-toolbar__tabs :deep(.el-tabs__item:first-child) {
  padding-left: 0;
}

.tn-toolbar__tabs :deep(.el-tabs__item:hover) {
  color: var(--tn-ink);
}

.tn-toolbar__tabs :deep(.el-tabs__item.is-active) {
  color: var(--tn-ink);
  font-weight: 700;
}

.tn-toolbar__tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px 3px 0 0;
  background: var(--theme-gradient);
}

.tn-toolbar__search {
  width: 280px;
  flex: 0 1 280px;
}

.tn-toolbar__search :deep(.el-input__wrapper) {
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  border: 1px solid transparent;
  box-shadow: none;
  padding: 2px 14px;
  transition:
    border-color 0.22s ease,
    background-color 0.22s ease,
    box-shadow 0.22s ease;
}

.tn-toolbar__search :deep(.el-input__wrapper:hover) {
  border-color: color-mix(in srgb, var(--theme-primary) 28%, transparent);
  background: var(--theme-bg-surface-2);
}

.tn-toolbar__search :deep(.el-input__wrapper.is-focus) {
  border-color: color-mix(in srgb, var(--theme-primary) 46%, transparent);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-ring);
}

/* ============ Tags ============ */
.tn-tags {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 14px;
  padding: 12px 16px;
  border-radius: 16px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--tn-shadow-sm);
  animation: tn-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.1s both;
}

.tn-tags__label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding-right: 14px;
  border-right: 1px solid var(--theme-hairline);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: var(--tn-ink-3);
  white-space: nowrap;
}

.tn-tags__list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.tn-chip {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface-2);
  color: var(--tn-ink-2);
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.35;
  white-space: nowrap;
  cursor: pointer;
  transition:
    transform 0.22s cubic-bezier(0.22, 1, 0.36, 1),
    color 0.22s ease,
    background-color 0.22s ease,
    border-color 0.22s ease,
    box-shadow 0.22s ease;
}

.tn-chip__hash {
  font-weight: 700;
  color: var(--tn-accent);
}

.tn-chip__count {
  margin-left: 2px;
  font-size: 11px;
  color: var(--tn-ink-3);
}

.tn-chip:hover {
  transform: translateY(-1px);
  color: var(--tn-accent);
  background: var(--theme-primary-soft);
  border-color: color-mix(in srgb, var(--theme-primary) 32%, transparent);
}

.tn-chip:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.tn-chip--active,
.tn-chip--active:hover {
  color: var(--theme-primary-text);
  background: var(--theme-gradient);
  border-color: transparent;
  box-shadow: 0 8px 18px -10px color-mix(in srgb, var(--theme-primary) 80%, transparent);
}

.tn-chip--active .tn-chip__hash,
.tn-chip--active .tn-chip__count {
  color: inherit;
  opacity: 0.82;
}

/* ============ Waterfall ============ */
.tn-waterfall {
  margin-top: 22px;
  columns: 4 250px;
  column-gap: 20px;
  min-height: 280px;
}

.tn-waterfall__item {
  break-inside: avoid;
  margin-bottom: 20px;
  animation: tn-rise 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* ============ Empty ============ */
.tn-empty {
  column-span: all;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 76px 20px;
  text-align: center;
}

.tn-empty__icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 6px;
  border-radius: 22px;
  background: var(--theme-primary-soft);
  color: var(--tn-accent);
}

.tn-empty__title {
  margin: 4px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--tn-ink);
}

.tn-empty__desc {
  margin: 0 0 16px;
  font-size: 14px;
  color: var(--tn-ink-2);
}

/* ============ More ============ */
.tn-more {
  margin-top: 6px;
  text-align: center;
}

.tn-more :deep(.el-button) {
  min-width: 168px;
  height: 40px;
  border-radius: 999px;
  border-color: var(--tn-line);
  background: var(--theme-bg-surface);
  color: var(--tn-ink-2);
}

.tn-more :deep(.el-button:hover) {
  color: var(--tn-accent);
  border-color: color-mix(in srgb, var(--theme-primary) 38%, transparent);
  background: var(--theme-bg-surface);
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

@media (max-width: 900px) {
  .tn-hero {
    align-items: flex-start;
  }
  .tn-hero__actions {
    width: 100%;
    padding-bottom: 0;
  }
  .tn-hero__btn {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .tn-square {
    margin-top: 84px;
    padding: 28px 14px 56px;
  }
  .tn-toolbar {
    padding: 4px 12px 12px;
    border-radius: 16px;
    gap: 4px;
  }
  .tn-toolbar__tabs {
    flex-basis: 100%;
  }
  .tn-toolbar__tabs :deep(.el-tabs__item) {
    height: 44px;
    padding: 0 14px;
    font-size: 14px;
  }
  .tn-toolbar__search {
    width: 100%;
    flex-basis: 100%;
  }
  .tn-tags {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
    padding: 12px;
  }
  .tn-tags__label {
    padding-right: 0;
    border-right: none;
  }
  .tn-tags__list {
    flex-wrap: nowrap;
    overflow-x: auto;
    padding-bottom: 2px;
    scrollbar-width: none;
    mask-image: linear-gradient(90deg, #000 calc(100% - 26px), transparent);
    -webkit-mask-image: linear-gradient(90deg, #000 calc(100% - 26px), transparent);
  }
  .tn-tags__list::-webkit-scrollbar {
    display: none;
  }
  .tn-waterfall {
    columns: 2 150px;
    column-gap: 12px;
  }
  .tn-waterfall__item {
    margin-bottom: 12px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .tn-hero,
  .tn-toolbar,
  .tn-tags,
  .tn-waterfall__item {
    animation: none;
  }
}
</style>
