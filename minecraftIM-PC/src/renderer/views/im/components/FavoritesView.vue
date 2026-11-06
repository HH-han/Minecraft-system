<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ArrowLeft,
  ChatDotRound,
  Clock,
  Delete,
  Document,
  EditPen,
  Folder,
  Grid,
  Link,
  Location,
  Microphone,
  Picture,
  Plus,
  Search,
  Star
} from '@element-plus/icons-vue';
import {
  addFavorite,
  deleteFavorite,
  getFavoriteList,
  getFavoriteStats,
  getRecentFavorites,
  touchFavorite
} from '@/api/favorite';
import { resolveMediaUrl } from '@/config';
import AppDialog from '@/components/dialog/AppDialog.vue';
import { confirm } from '@/components/dialog/confirm';
import { useAuthStore } from '@/stores/auth';
import type { FavoriteItem, FavoriteStat } from '@/types/im';
import { formatFullTime, formatFileSize } from '@/utils/format';

type CategoryKey =
  | 'all'
  | 'recent'
  | 'note'
  | 'link'
  | 'image'
  | 'file'
  | 'chat'
  | 'voice'
  | 'location'
  | 'miniapp';

const { t, locale } = useI18n();
const authStore = useAuthStore();

const categories: Array<{ key: CategoryKey; icon: typeof Star; type?: string }> = [
  { key: 'all', icon: Star },
  { key: 'recent', icon: Clock },
  { key: 'note', icon: EditPen, type: 'note' },
  { key: 'link', icon: Link, type: 'link' },
  { key: 'image', icon: Picture, type: 'image' },
  { key: 'file', icon: Folder, type: 'file' },
  { key: 'chat', icon: ChatDotRound, type: 'chat' },
  { key: 'voice', icon: Microphone, type: 'voice' },
  { key: 'location', icon: Location, type: 'location' },
  { key: 'miniapp', icon: Grid, type: 'miniapp' }
];

const activeCategory = ref<CategoryKey>('all');
const keyword = ref('');
const items = ref<FavoriteItem[]>([]);
const stats = ref<FavoriteStat>({ total: 0, types: {} });
const selected = ref<FavoriteItem | null>(null);
const loading = ref(false);
const page = ref(1);
const hasMore = ref(false);
const pageSize = 20;

const noteVisible = ref(false);
const noteForm = ref({ title: '', content: '' });
const linkVisible = ref(false);
const linkForm = ref({ title: '', url: '' });
const saving = ref(false);
const deleting = ref(false);

let searchTimer: ReturnType<typeof setTimeout> | undefined;

const categoryLabel = (key: CategoryKey): string => t(`im.favorites.categories.${key}`);

const categoryCount = (key: CategoryKey): number | null => {
  if (key === 'all') return Number(stats.value.total ?? 0);
  if (key === 'recent') return null;
  const type = categories.find((item) => item.key === key)?.type ?? key;
  return Number(stats.value.types?.[type] ?? 0);
};

const activeLabel = computed(() => categoryLabel(activeCategory.value));

const isEmpty = computed(() => !loading.value && items.value.length === 0);

function itemIcon(item: FavoriteItem): string {
  const icons: Record<string, string> = {
    note: '📝',
    link: '🔗',
    image: '🖼️',
    video: '🎬',
    file: '📁',
    chat: '💬',
    voice: '🎙️',
    location: '📍',
    miniapp: '🧩'
  };
  return icons[item.type] ?? '⭐';
}

function itemTitle(item: FavoriteItem): string {
  return item.title || item.content || item.fileName || item.url || t('im.favorites.untitled');
}

function itemPreview(item: FavoriteItem): string {
  if (item.content && item.content !== item.title) return item.content;
  if (item.url) return item.url;
  if (item.fileName) return item.fileName;
  return t('im.common.noDescription');
}

function itemTime(item: FavoriteItem): string {
  return formatFullTime(item.updateTime || item.createTime, locale.value);
}

function coverOf(item: FavoriteItem): string | undefined {
  return resolveMediaUrl(item.cover || (item.type === 'image' ? item.url : undefined));
}

async function loadStats(): Promise<void> {
  if (!authStore.userId) return;
  try {
    const res = await getFavoriteStats(authStore.userId);
    stats.value = res.data ?? { total: 0, types: {} };
  } catch {
    /* 忽略统计失败 */
  }
}

async function loadItems(reset = true): Promise<void> {
  if (!authStore.userId) return;
  loading.value = true;
  if (reset) {
    page.value = 1;
    selected.value = null;
  }
  try {
    let data: FavoriteItem[] = [];
    if (activeCategory.value === 'recent') {
      const res = await getRecentFavorites(authStore.userId, pageSize);
      data = res.data ?? [];
      hasMore.value = false;
    } else {
      const type = categories.find((item) => item.key === activeCategory.value)?.type;
      const res = await getFavoriteList({
        userId: authStore.userId,
        type,
        keyword: keyword.value.trim() || undefined,
        page: page.value,
        size: pageSize
      });
      data = res.data ?? [];
      hasMore.value = data.length >= pageSize;
    }
    items.value = reset ? data : [...items.value, ...data];
  } catch {
    if (reset) items.value = [];
    ElMessage.error(t('im.favorites.loadFailed'));
  } finally {
    loading.value = false;
  }
}

function selectCategory(key: CategoryKey): void {
  if (activeCategory.value === key) return;
  activeCategory.value = key;
  void loadItems(true);
}

function selectItem(item: FavoriteItem): void {
  selected.value = item;
  if (authStore.userId) {
    void touchFavorite(authStore.userId, item.id).catch(() => undefined);
  }
}

function loadMore(): void {
  if (loading.value || !hasMore.value) return;
  page.value += 1;
  void loadItems(false);
}

watch(keyword, () => {
  if (searchTimer) clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    void loadItems(true);
  }, 350);
});

function openNoteDialog(): void {
  noteForm.value = { title: '', content: '' };
  noteVisible.value = true;
}

async function saveNote(): Promise<void> {
  if (!authStore.userId || !noteForm.value.content.trim()) {
    ElMessage.warning(t('im.favorites.contentRequired'));
    return;
  }
  saving.value = true;
  try {
    await addFavorite({
      userId: authStore.userId,
      type: 'note',
      title: noteForm.value.title.trim() || undefined,
      content: noteForm.value.content.trim()
    });
    noteVisible.value = false;
    ElMessage.success(t('im.favorites.saved'));
    await Promise.all([loadItems(true), loadStats()]);
  } catch {
    /* 请求层已提示 */
  } finally {
    saving.value = false;
  }
}

function openLinkDialog(): void {
  linkForm.value = { title: '', url: '' };
  linkVisible.value = true;
}

async function saveLink(): Promise<void> {
  const url = linkForm.value.url.trim();
  if (!authStore.userId || !/^(https?:\/\/|www\.)/i.test(url)) {
    ElMessage.warning(t('im.favorites.linkRequired'));
    return;
  }
  saving.value = true;
  try {
    const normalized = url.startsWith('www.') ? `https://${url}` : url;
    await addFavorite({
      userId: authStore.userId,
      type: 'link',
      title: linkForm.value.title.trim() || normalized,
      url: normalized,
      source: 'manual'
    });
    linkVisible.value = false;
    ElMessage.success(t('im.favorites.saved'));
    await Promise.all([loadItems(true), loadStats()]);
  } catch {
    /* 请求层已提示 */
  } finally {
    saving.value = false;
  }
}

async function removeSelected(): Promise<void> {
  if (!selected.value || !authStore.userId) return;
  const confirmed = await confirm({
    title: t('im.common.delete'),
    message: t('im.favorites.deleteConfirm'),
    confirmText: t('im.common.confirm'),
    cancelText: t('im.common.cancel'),
    tone: 'danger'
  });
  if (!confirmed) return;
  deleting.value = true;
  try {
    await deleteFavorite(authStore.userId, selected.value.id);
    selected.value = null;
    ElMessage.success(t('im.favorites.deleted'));
    await Promise.all([loadItems(true), loadStats()]);
  } catch {
    /* 请求层已提示 */
  } finally {
    deleting.value = false;
  }
}

function openUrl(url?: string): void {
  if (!url) return;
  window.open(url, '_blank');
}

onMounted(async () => {
  await Promise.all([loadStats(), loadItems(true)]);
});
</script>

<template>
  <div class="favorites-view">
    <!-- 分类导航 -->
    <aside class="favorites-nav">
      <header class="favorites-nav__head">
        <h2 class="im-column-title">{{ t('im.favorites.title') }}</h2>
        <button class="im-icon-btn is-round" :title="t('im.favorites.writeNote')" @click="openNoteDialog">
          <el-icon :size="15"><EditPen /></el-icon>
        </button>
      </header>

      <div class="im-search-row">
        <el-input v-model="keyword" :placeholder="t('im.favorites.search')" clearable>
          <template #prefix>
            <el-icon :size="14"><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="im-scroll nav-scroll">
        <button
          v-for="category in categories"
          :key="category.key"
          class="nav-item"
          :class="{ 'is-active': activeCategory === category.key }"
          @click="selectCategory(category.key)"
        >
          <span class="nav-icon">
            <el-icon :size="15"><component :is="category.icon" /></el-icon>
          </span>
          <span class="nav-label">{{ categoryLabel(category.key) }}</span>
          <span v-if="categoryCount(category.key) !== null" class="nav-count">
            {{ categoryCount(category.key) }}
          </span>
        </button>
      </div>
    </aside>

    <!-- 收藏列表 -->
    <section class="favorites-list">
      <header class="list-head">
        <div class="list-head__title">
          <h3>{{ activeLabel }}</h3>
          <span class="list-count">{{ items.length }}</span>
        </div>
        <div class="list-head__actions">
          <el-button size="small" text @click="openLinkDialog">
            <el-icon><Plus /></el-icon>
            <span class="btn-text">{{ t('im.favorites.addLink') }}</span>
          </el-button>
          <el-button size="small" text @click="openNoteDialog">
            <el-icon><EditPen /></el-icon>
            <span class="btn-text">{{ t('im.favorites.writeNote') }}</span>
          </el-button>
        </div>
      </header>

      <div class="im-scroll list-scroll">
        <div v-if="items.length" class="favorite-cards">
          <article
            v-for="item in items"
            :key="item.id"
            class="favorite-card"
            :class="{ 'is-active': selected?.id === item.id }"
            @click="selectItem(item)"
          >
            <div class="card-thumb" :class="{ 'has-cover': !!coverOf(item) }">
              <img v-if="coverOf(item)" :src="coverOf(item)" :alt="itemTitle(item)" />
              <span v-else class="thumb-emoji">{{ itemIcon(item) }}</span>
            </div>
            <div class="card-body">
              <p class="card-title">{{ itemTitle(item) }}</p>
              <p class="card-preview">{{ itemPreview(item) }}</p>
              <div class="card-meta">
                <span class="im-tag">{{ t(`im.favorites.categories.${item.type}` as string) }}</span>
                <span class="card-time">{{ itemTime(item) }}</span>
              </div>
            </div>
          </article>

          <button v-if="hasMore" class="load-more" :disabled="loading" @click="loadMore">
            {{ loading ? t('im.common.loading') : t('im.common.loadMore') }}
          </button>
        </div>

        <div v-else class="im-empty">
          <div class="im-empty__icon">
            <el-icon :size="30"><Star /></el-icon>
          </div>
          <p class="im-empty__title">
            {{ loading ? t('im.common.loading') : t('im.favorites.empty') }}
          </p>
          <p class="im-empty__desc">{{ t('im.favorites.emptyDesc') }}</p>
        </div>
      </div>
    </section>

    <!-- 收藏详情 -->
    <section class="favorites-detail">
      <template v-if="selected">
        <header class="detail-head">
          <span class="im-tag">{{ t(`im.favorites.categories.${selected.type}` as string) }}</span>
          <button class="im-icon-btn" :title="t('im.common.delete')" :disabled="deleting" @click="removeSelected">
            <el-icon :size="16"><Delete /></el-icon>
          </button>
        </header>

        <div class="im-scroll detail-scroll">
          <h2 class="detail-title">{{ itemTitle(selected) }}</h2>
          <p class="detail-time">{{ itemTime(selected) }}</p>

          <div v-if="coverOf(selected)" class="detail-cover">
            <img :src="coverOf(selected)" :alt="itemTitle(selected)" />
          </div>

          <div v-if="selected.content" class="detail-content">{{ selected.content }}</div>

          <a
            v-if="selected.url"
            class="detail-link"
            :href="selected.url"
            target="_blank"
            rel="noreferrer"
            @click.prevent="openUrl(selected.url)"
          >
            <el-icon><Link /></el-icon>
            <span>{{ selected.url }}</span>
          </a>

          <div v-if="selected.fileName" class="detail-file">
            <el-icon><Document /></el-icon>
            <span class="file-name">{{ selected.fileName }}</span>
            <span class="file-size">{{ formatFileSize(selected.fileSize) }}</span>
          </div>

          <div v-if="selected.tags?.length" class="detail-tags">
            <span v-for="tag in selected.tags" :key="tag" class="im-chip">#{{ tag }}</span>
          </div>
        </div>
      </template>

      <div v-else class="im-empty detail-empty">
        <div class="im-empty__icon">
          <el-icon :size="28"><ArrowLeft /></el-icon>
        </div>
        <p class="im-empty__title">{{ t('im.favorites.detailEmpty') }}</p>
      </div>
    </section>

    <!-- 写笔记 -->
    <AppDialog
      v-model="noteVisible"
      :title="t('im.favorites.writeNote')"
      width="460px"
      :cancel-text="t('im.common.cancel')"
      :confirm-text="t('im.common.save')"
      :confirm-loading="saving"
      @confirm="saveNote"
    >
      <div class="im-stack dialog-form">
        <el-input v-model="noteForm.title" maxlength="80" :placeholder="t('im.favorites.noteTitle')" />
        <el-input
          v-model="noteForm.content"
          type="textarea"
          :rows="6"
          maxlength="2000"
          show-word-limit
          :placeholder="t('im.favorites.noteContent')"
        />
      </div>
    </AppDialog>

    <!-- 添加链接 -->
    <AppDialog
      v-model="linkVisible"
      :title="t('im.favorites.addLink')"
      width="460px"
      :cancel-text="t('im.common.cancel')"
      :confirm-text="t('im.common.save')"
      :confirm-loading="saving"
      @confirm="saveLink"
    >
      <div class="im-stack dialog-form">
        <el-input v-model="linkForm.title" maxlength="80" :placeholder="t('im.favorites.linkTitle')" />
        <el-input v-model="linkForm.url" :placeholder="t('im.favorites.linkUrl')" />
      </div>
    </AppDialog>
  </div>
</template>

<style scoped>
.dialog-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-bottom: 6px;
}

.favorites-view {
  flex: 1;
  min-width: 0;
  display: flex;
  height: 100%;
  background: var(--theme-bg-surface);
}

.favorites-nav {
  width: 214px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--theme-hairline);
  background: var(--theme-list-bg);
}

.favorites-nav__head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 24px 16px 12px 20px;
}

.favorites-nav__head .im-column-title {
  flex: 1;
}

.nav-scroll {
  padding: 6px 10px 14px;
}

.nav-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: var(--theme-text-3);
  cursor: pointer;
  font-size: 12.5px;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.nav-item:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.nav-item.is-active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 600;
}

.nav-icon {
  width: 22px;
  height: 22px;
  display: grid;
  place-items: center;
  border-radius: 6px;
  background: color-mix(in srgb, var(--theme-text-1) 5%, transparent);
}

.nav-item.is-active .nav-icon {
  background: color-mix(in srgb, var(--theme-primary) 16%, transparent);
}

.nav-label {
  flex: 1;
  text-align: left;
}

.nav-count {
  font-size: 10.5px;
  color: var(--theme-text-3);
  font-variant-numeric: tabular-nums;
}

.favorites-list {
  width: 348px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--theme-hairline);
  background: var(--theme-bg-app);
}

.list-head {
  padding: 22px 18px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.list-head__title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.list-head__title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.list-count {
  font-size: 11px;
  color: var(--theme-text-3);
  background: var(--theme-bg-muted);
  padding: 1px 7px;
  border-radius: 99px;
}

.list-head__actions {
  display: flex;
  align-items: center;
  gap: 2px;
}

.btn-text {
  margin-left: 4px;
  font-size: 12px;
}

.list-scroll {
  padding: 0 12px 16px;
}

.favorite-cards {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.favorite-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
  cursor: pointer;
  transition: border-color var(--theme-duration-fast) var(--theme-ease),
    background var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.favorite-card:hover {
  transform: translateY(-1px);
  border-color: color-mix(in srgb, var(--theme-primary) 24%, transparent);
}

.favorite-card.is-active {
  border-color: color-mix(in srgb, var(--theme-primary) 45%, transparent);
  background: var(--theme-primary-soft);
}

.card-thumb {
  width: 62px;
  height: 62px;
  flex-shrink: 0;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-size: 24px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  overflow: hidden;
}

.card-thumb.has-cover {
  padding: 0;
}

.card-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  justify-content: center;
}

.card-title {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-preview {
  margin: 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-time {
  margin-left: auto;
  font-size: 10.5px;
  color: var(--theme-text-3);
}

.load-more {
  margin: 8px auto 0;
  padding: 8px 18px;
  border-radius: 99px;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font-size: 12px;
  cursor: pointer;
}

.favorites-detail {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-chat);
}

.detail-head {
  height: 56px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid var(--theme-hairline);
}

.detail-scroll {
  padding: 26px 30px 32px;
}

.detail-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--theme-text-1);
  line-height: 1.45;
}

.detail-time {
  margin: 8px 0 18px;
  font-size: 11.5px;
  color: var(--theme-text-3);
}

.detail-cover {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--theme-hairline);
  margin-bottom: 18px;
}

.detail-cover img {
  display: block;
  max-width: 100%;
  max-height: 420px;
  object-fit: contain;
}

.detail-content {
  font-size: 13.5px;
  line-height: 1.85;
  color: var(--theme-text-2);
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-top: 18px;
  padding: 10px 14px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  color: var(--theme-primary);
  font-size: 12.5px;
  word-break: break-all;
}

.detail-link:hover {
  border-color: color-mix(in srgb, var(--theme-primary) 40%, transparent);
}

.detail-file {
  margin-top: 18px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 10px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  color: var(--theme-text-2);
  font-size: 12.5px;
}

.file-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: var(--theme-text-3);
  font-size: 11px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 18px;
}

.detail-empty {
  height: 100%;
}
</style>
