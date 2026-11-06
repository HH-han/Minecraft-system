<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { Star, StarFilled } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';
import { getMomentFeed, publishMoment, toggleMomentLike } from '@/api/moment';
import type { MomentItem } from '@/types/im';

const { t } = useI18n();
const authStore = useAuthStore();

const draft = ref('');
const publishing = ref(false);
const loading = ref(false);
const feed = ref<MomentItem[]>([]);

function imagesOf(item: MomentItem): string[] {
  const raw = item.images;
  if (Array.isArray(raw)) return raw.filter(Boolean);
  if (typeof raw === 'string') {
    return raw
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean);
  }
  return [];
}

function formatTime(value?: string): string {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} ${String(
    date.getHours()
  ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
}

async function loadFeed(): Promise<void> {
  const userId = authStore.userId;
  if (!userId) return;
  loading.value = true;
  try {
    const res = await getMomentFeed(userId);
    feed.value = res.data ?? [];
  } catch {
    ElMessage.error(t('im.discover.loadFailed'));
  } finally {
    loading.value = false;
  }
}

async function onPublish(): Promise<void> {
  const userId = authStore.userId;
  const content = draft.value.trim();
  if (!userId || !content || publishing.value) return;
  publishing.value = true;
  try {
    await publishMoment({ userId, content });
    draft.value = '';
    ElMessage.success(t('im.discover.published'));
    await loadFeed();
  } catch {
    ElMessage.error(t('im.discover.loadFailed'));
  } finally {
    publishing.value = false;
  }
}

async function onLike(item: MomentItem): Promise<void> {
  const userId = authStore.userId;
  if (!userId || item.id == null) return;
  try {
    await toggleMomentLike(item.id, userId);
    item.liked = !item.liked;
    item.likeCount = Math.max(0, (item.likeCount ?? 0) + (item.liked ? 1 : -1));
  } catch {
    ElMessage.error(t('im.discover.loadFailed'));
  }
}

onMounted(loadFeed);
</script>

<template>
  <div class="moments-view">
    <div class="feed-scroll">
      <div class="publish-box">
        <el-input
          v-model="draft"
          type="textarea"
          :rows="3"
          resize="none"
          :placeholder="t('im.discover.publishPlaceholder')"
        />
        <div class="publish-actions">
          <el-button
            type="primary"
            :loading="publishing"
            :disabled="!draft.trim()"
            @click="onPublish"
          >
            {{ t('im.discover.publish') }}
          </el-button>
        </div>
      </div>

      <el-skeleton v-if="loading" :rows="5" animated class="skeleton" />
      <el-empty
        v-else-if="!feed.length"
        :description="t('im.discover.empty')"
        :image-size="90"
      />
      <article v-for="(item, index) in feed" v-else :key="item.id ?? index" class="moment-item">
        <span class="avatar">
          <img v-if="item.avatar" :src="item.avatar" :alt="item.nickname" />
          <template v-else>{{ (item.nickname || '?')[0] }}</template>
        </span>
        <div class="moment-body">
          <span class="nickname">{{ item.nickname }}</span>
          <p class="content">{{ item.content }}</p>
          <div
            v-if="imagesOf(item).length"
            class="image-grid"
            :class="{ single: imagesOf(item).length === 1 }"
          >
            <img
              v-for="(src, i) in imagesOf(item)"
              :key="i"
              :src="src"
              class="grid-image"
              alt=""
            />
          </div>
          <div class="footer-row">
            <span class="time">{{ formatTime(item.createTime) }}</span>
            <button type="button" class="like-btn" :class="{ liked: item.liked }" @click="onLike(item)">
              <el-icon :size="14">
                <StarFilled v-if="item.liked" />
                <Star v-else />
              </el-icon>
              <span>{{ item.likeCount ?? 0 }}</span>
            </button>
          </div>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
.moments-view {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-app);
}

.feed-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.publish-box {
  padding: 14px;
  margin-bottom: 16px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
}

.publish-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.skeleton {
  padding: 8px;
}

.moment-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
  transition: box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.moment-item:hover {
  box-shadow: var(--theme-shadow-md);
}

.avatar {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  border-radius: var(--theme-radius-md);
  display: grid;
  place-items: center;
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-gradient);
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.moment-body {
  flex: 1;
  min-width: 0;
}

.nickname {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-primary-text);
}

.content {
  margin: 6px 0 0;
  font-size: 14px;
  line-height: 1.6;
  color: var(--theme-text-1);
  white-space: pre-wrap;
  word-break: break-word;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
  margin-top: 10px;
  max-width: 360px;
}

.image-grid.single {
  grid-template-columns: minmax(0, 220px);
}

.grid-image {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: var(--theme-radius-sm);
  background: var(--theme-bg-hover);
}

.image-grid.single .grid-image {
  aspect-ratio: auto;
}

.footer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.time {
  font-size: 12px;
  color: var(--theme-text-3);
}

.like-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border: none;
  border-radius: var(--theme-radius-sm);
  background: var(--theme-bg-hover);
  color: var(--theme-text-2);
  font-size: 12px;
  cursor: pointer;
  transition:
    background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.like-btn:hover {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.like-btn.liked {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}
</style>
