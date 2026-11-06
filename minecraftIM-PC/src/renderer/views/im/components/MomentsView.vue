<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Camera,
  ChatDotRound,
  Delete,
  EditPen,
  Location,
  Plus,
  Promotion,
  Star
} from '@element-plus/icons-vue';
import {
  addMomentComment,
  deleteMoment,
  getMomentCover,
  getMomentsFeed,
  publishMoment,
  toggleMomentLike,
  uploadImage,
  uploadMomentCover
} from '@/api/moment';
import { resolveMediaUrl } from '@/config';
import { useAuthStore } from '@/stores/auth';
import type { MomentItem } from '@/types/im';
import { formatClock, relativeTime } from '@/utils/format';

const { t, locale } = useI18n();
const authStore = useAuthStore();

const moments = ref<MomentItem[]>([]);
const loading = ref(false);
const coverUrl = ref<string | null>(null);
const page = ref(1);
const hasMore = ref(false);
const pageSize = 10;

const composeVisible = ref(false);
const composeForm = ref({ content: '', location: '', visibility: 0, images: [] as string[] });
const uploading = ref(false);
const publishing = ref(false);
const commentTarget = ref<number | null>(null);
const commentDraft = ref('');
const commenting = ref(false);

const coverFile = ref<HTMLInputElement | null>(null);
const composeFile = ref<HTMLInputElement | null>(null);

const displayName = computed(
  () => authStore.user?.nickname || authStore.user?.username || ''
);

function initialOf(name?: string): string {
  return (name || '?').trim().charAt(0).toUpperCase();
}

function momentName(moment: MomentItem): string {
  return moment.nickname || moment.username || t('im.common.member');
}

function timeOfMoment(moment: MomentItem): string {
  return relativeTime(moment.createTime, {
    justNow: t('im.time.justNow'),
    minutes: t('im.time.minutesAgo'),
    hours: t('im.time.hoursAgo'),
    days: t('im.time.daysAgo')
  });
}

function isMine(moment: MomentItem): boolean {
  return moment.userId === (authStore.userId ?? 0);
}

function imageGridClass(count: number): string {
  if (count === 1) return 'grid-one';
  if (count === 2 || count === 4) return 'grid-two';
  return 'grid-three';
}

async function loadCover(): Promise<void> {
  if (!authStore.userId) return;
  try {
    const res = await getMomentCover(authStore.userId);
    coverUrl.value = res.data ? resolveMediaUrl(res.data) ?? null : null;
  } catch {
    coverUrl.value = null;
  }
}

async function loadMoments(reset = true): Promise<void> {
  if (!authStore.userId) return;
  loading.value = true;
  if (reset) page.value = 1;
  try {
    const res = await getMomentsFeed(authStore.userId, page.value, pageSize);
    const data = res.data ?? [];
    moments.value = reset ? data : [...moments.value, ...data];
    hasMore.value = data.length >= pageSize;
  } catch {
    if (reset) moments.value = [];
    ElMessage.error(t('im.discover.loadFailed'));
  } finally {
    loading.value = false;
  }
}

function loadMore(): void {
  if (loading.value || !hasMore.value) return;
  page.value += 1;
  void loadMoments(false);
}

async function likeMoment(moment: MomentItem): Promise<void> {
  if (!authStore.userId) return;
  try {
    const res = await toggleMomentLike(moment.id, authStore.userId);
    const result = res.data ?? {};
    const liked = typeof result.liked === 'boolean' ? result.liked : !moment.liked;
    moment.liked = liked;
    moment.likeCount = Math.max(
      0,
      typeof result.likeCount === 'number'
        ? result.likeCount
        : (moment.likeCount ?? 0) + (liked ? 1 : -1)
    );
  } catch {
    /* 请求层已提示 */
  }
}

function toggleComment(moment: MomentItem): void {
  commentTarget.value = commentTarget.value === moment.id ? null : moment.id;
  commentDraft.value = '';
}

async function submitComment(moment: MomentItem): Promise<void> {
  const content = commentDraft.value.trim();
  if (!content || !authStore.userId || commenting.value) return;
  commenting.value = true;
  try {
    const res = await addMomentComment(moment.id, {
      userId: authStore.userId,
      content
    });
    const comment = res.data;
    if (comment) {
      moment.comments = [...(moment.comments ?? []), comment];
      moment.commentCount = (moment.commentCount ?? 0) + 1;
    }
    commentDraft.value = '';
    commentTarget.value = null;
    ElMessage.success(t('im.discover.commentSuccess'));
  } catch {
    /* 请求层已提示 */
  } finally {
    commenting.value = false;
  }
}

async function removeMoment(moment: MomentItem): Promise<void> {
  if (!authStore.userId) return;
  try {
    await ElMessageBox.confirm(t('im.discover.deleteConfirm'), t('im.common.delete'), {
      type: 'warning',
      confirmButtonText: t('im.common.confirm'),
      cancelButtonText: t('im.common.cancel')
    });
  } catch {
    return;
  }
  try {
    await deleteMoment(moment.id, authStore.userId);
    moments.value = moments.value.filter((item) => item.id !== moment.id);
    ElMessage.success(t('im.discover.deleted'));
  } catch {
    /* 请求层已提示 */
  }
}

function openCompose(): void {
  composeForm.value = { content: '', location: '', visibility: 0, images: [] };
  composeVisible.value = true;
}

async function handleCoverFile(event: Event): Promise<void> {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file || !authStore.userId) return;
  uploading.value = true;
  try {
    const res = await uploadMomentCover(authStore.userId, file);
    coverUrl.value = res.data ? resolveMediaUrl(res.data) ?? null : coverUrl.value;
    ElMessage.success(t('im.discover.coverUpdated'));
  } catch {
    /* 请求层已提示 */
  } finally {
    uploading.value = false;
  }
}

async function handleComposeImage(event: Event): Promise<void> {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file) return;
  if (composeForm.value.images.length >= 9) {
    ElMessage.warning(t('im.discover.maxImages'));
    return;
  }
  uploading.value = true;
  try {
    const res = await uploadImage(file);
    if (res.data) composeForm.value.images.push(res.data);
  } catch {
    /* 请求层已提示 */
  } finally {
    uploading.value = false;
  }
}

function removeComposeImage(index: number): void {
  composeForm.value.images.splice(index, 1);
}

async function submitCompose(): Promise<void> {
  if (!authStore.userId || publishing.value) return;
  const content = composeForm.value.content.trim();
  if (!content && !composeForm.value.images.length) {
    ElMessage.warning(t('im.discover.contentRequired'));
    return;
  }
  publishing.value = true;
  try {
    const res = await publishMoment({
      userId: authStore.userId,
      content,
      images: composeForm.value.images.length ? composeForm.value.images : undefined,
      location: composeForm.value.location.trim() || undefined,
      visibility: composeForm.value.visibility
    });
    if (res.data) moments.value = [res.data, ...moments.value];
    composeVisible.value = false;
    ElMessage.success(t('im.discover.published'));
  } catch {
    /* 请求层已提示 */
  } finally {
    publishing.value = false;
  }
}

function openImage(url: string): void {
  const resolved = resolveMediaUrl(url);
  if (resolved) window.open(resolved, '_blank');
}

onMounted(async () => {
  await Promise.all([loadCover(), loadMoments(true)]);
});
</script>

<template>
  <section class="moments-view">
    <div class="im-scroll moments-scroll">
      <!-- 朋友圈封面 -->
      <header class="cover" :class="{ 'has-image': !!coverUrl }">
        <div v-if="coverUrl" class="cover-image" :style="{ backgroundImage: `url(${coverUrl})` }" />
        <div v-else class="cover-fallback">
          <span class="cover-grid" />
        </div>
        <div class="cover-shade" />

        <div class="cover-actions">
          <button class="cover-btn" :disabled="uploading" @click="coverFile?.click()">
            <el-icon :size="14"><Camera /></el-icon>
            <span>{{ t('im.discover.changeCover') }}</span>
          </button>
          <button class="cover-btn cover-btn--primary" @click="openCompose">
            <el-icon :size="14"><EditPen /></el-icon>
            <span>{{ t('im.discover.publish') }}</span>
          </button>
        </div>

        <div class="cover-user">
          <div class="cover-user__meta">
            <span class="cover-user__name">{{ displayName }}</span>
            <span class="cover-user__sign">{{ t('im.discover.slogan') }}</span>
          </div>
          <div class="im-avatar lg round cover-avatar">
            <img v-if="resolveMediaUrl(authStore.user?.avatar)" :src="resolveMediaUrl(authStore.user?.avatar)" :alt="displayName" />
            <template v-else>{{ initialOf(displayName) }}</template>
          </div>
        </div>

        <input ref="coverFile" class="hidden-input" type="file" accept="image/*" @change="handleCoverFile" />
      </header>

      <!-- 动态流 -->
      <div class="feed">
        <article v-for="moment in moments" :key="moment.id" class="moment-card">
          <div class="im-avatar sm round moment-avatar">
            <img v-if="resolveMediaUrl(moment.avatar)" :src="resolveMediaUrl(moment.avatar)" :alt="momentName(moment)" />
            <template v-else>{{ initialOf(momentName(moment)) }}</template>
          </div>

          <div class="moment-main">
            <header class="moment-head">
              <div class="moment-who">
                <span class="moment-name">{{ momentName(moment) }}</span>
                <span class="moment-time">{{ timeOfMoment(moment) }}</span>
              </div>
              <button
                v-if="isMine(moment)"
                class="im-icon-btn moment-delete"
                :title="t('im.common.delete')"
                @click="removeMoment(moment)"
              >
                <el-icon :size="14"><Delete /></el-icon>
              </button>
            </header>

            <p v-if="moment.content" class="moment-content">{{ moment.content }}</p>

            <div v-if="moment.images?.length" class="moment-images" :class="imageGridClass(moment.images.length)">
              <button
                v-for="image in moment.images"
                :key="image"
                class="moment-image"
                @click="openImage(image)"
              >
                <img :src="resolveMediaUrl(image)" alt="moment" />
              </button>
            </div>

            <p v-if="moment.location" class="moment-location">
              <el-icon :size="12"><Location /></el-icon>
              <span>{{ moment.location }}</span>
            </p>

            <div class="moment-actions">
              <button class="moment-action" :class="{ 'is-liked': moment.liked }" @click="likeMoment(moment)">
                <el-icon :size="15"><Star /></el-icon>
                <span>{{ moment.liked ? t('im.discover.liked') : t('im.discover.like') }}</span>
                <span v-if="moment.likeCount" class="action-count">{{ moment.likeCount }}</span>
              </button>
              <button class="moment-action" @click="toggleComment(moment)">
                <el-icon :size="15"><ChatDotRound /></el-icon>
                <span>{{ t('im.discover.comment') }}</span>
                <span v-if="moment.commentCount" class="action-count">{{ moment.commentCount }}</span>
              </button>
            </div>

            <div v-if="moment.likes?.length || moment.comments?.length" class="moment-social">
              <div v-if="moment.likes?.length" class="likes-row">
                <el-icon :size="13" class="likes-icon"><Star /></el-icon>
                <span v-for="(like, index) in moment.likes" :key="like.userId" class="like-name">
                  {{ like.nickname || like.username || '#' + like.userId }}<template v-if="index < moment.likes.length - 1">、</template>
                </span>
              </div>

              <div v-if="moment.comments?.length" class="comment-list">
                <div v-for="comment in moment.comments" :key="comment.id" class="comment-row">
                  <span class="comment-name">{{ comment.nickname || comment.username || '#' + comment.userId }}</span>
                  <span v-if="comment.replyToNickname" class="comment-reply">
                    {{ t('im.discover.reply') }} {{ comment.replyToNickname }}
                  </span>
                  <span class="comment-text">{{ comment.content }}</span>
                  <span class="comment-time">{{ formatClock(comment.createTime, locale) }}</span>
                </div>
              </div>
            </div>

            <div v-if="commentTarget === moment.id" class="comment-compose">
              <el-input
                v-model="commentDraft"
                :placeholder="t('im.discover.writeComment')"
                maxlength="300"
                @keyup.enter="submitComment(moment)"
              />
              <el-button
                size="small"
                class="im-action-primary"
                :loading="commenting"
                :disabled="!commentDraft.trim()"
                @click="submitComment(moment)"
              >
                {{ t('im.common.send') }}
              </el-button>
            </div>
          </div>
        </article>

        <button v-if="hasMore" class="load-more" :disabled="loading" @click="loadMore">
          {{ loading ? t('im.common.loading') : t('im.common.loadMore') }}
        </button>

        <div v-if="!moments.length" class="im-empty moments-empty">
          <div class="im-empty__icon">
            <el-icon :size="30"><Promotion /></el-icon>
          </div>
          <p class="im-empty__title">{{ loading ? t('im.common.loading') : t('im.discover.empty') }}</p>
          <p class="im-empty__desc">{{ t('im.discover.emptyDesc') }}</p>
        </div>
      </div>
    </div>

    <!-- 发布动态 -->
    <el-dialog v-model="composeVisible" :title="t('im.discover.publish')" width="520px" append-to-body>
      <div class="compose-form">
        <el-input
          v-model="composeForm.content"
          type="textarea"
          :rows="5"
          maxlength="2000"
          show-word-limit
          :placeholder="t('im.discover.publishPlaceholder')"
        />

        <div class="compose-images">
          <div v-for="(image, index) in composeForm.images" :key="image" class="compose-image">
            <img :src="resolveMediaUrl(image)" alt="upload" />
            <button class="remove-image" @click="removeComposeImage(index)">
              <el-icon :size="11"><Delete /></el-icon>
            </button>
          </div>
          <button v-if="composeForm.images.length < 9" class="add-image" :disabled="uploading" @click="composeFile?.click()">
            <el-icon :size="18"><Plus /></el-icon>
            <span>{{ uploading ? t('im.discover.uploading') : t('im.discover.addImage') }}</span>
          </button>
        </div>
        <input ref="composeFile" class="hidden-input" type="file" accept="image/*" @change="handleComposeImage" />

        <div class="compose-meta">
          <el-input v-model="composeForm.location" :placeholder="t('im.discover.locationPlaceholder')" clearable>
            <template #prefix>
              <el-icon><Location /></el-icon>
            </template>
          </el-input>
          <el-select v-model="composeForm.visibility" class="visibility-select">
            <el-option :value="0" :label="t('im.discover.visibilityPublic')" />
            <el-option :value="1" :label="t('im.discover.visibilityFriends')" />
            <el-option :value="2" :label="t('im.discover.visibilityPrivate')" />
          </el-select>
        </div>
      </div>

      <template #footer>
        <el-button @click="composeVisible = false">{{ t('im.common.cancel') }}</el-button>
        <el-button class="im-action-primary" :loading="publishing" @click="submitCompose">
          <el-icon><Promotion /></el-icon>
          <span class="publish-text">{{ t('im.discover.publish') }}</span>
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.moments-view {
  flex: 1;
  min-width: 0;
  height: 100%;
  background: var(--theme-bg-chat);
  display: flex;
  flex-direction: column;
}

.moments-scroll {
  padding-bottom: 24px;
}

.cover {
  position: relative;
  height: 240px;
  overflow: hidden;
  background: var(--theme-accent-gradient);
}

.cover-image {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  transform: scale(1.03);
}

.cover-fallback {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 18% 22%, rgba(255, 255, 255, 0.34), transparent 42%),
    radial-gradient(circle at 82% 74%, rgba(255, 255, 255, 0.18), transparent 46%),
    var(--theme-accent-gradient);
}

.cover-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.08) 1px, transparent 1px);
  background-size: 44px 44px;
  mask-image: linear-gradient(180deg, #000 30%, transparent 100%);
}

.cover-shade {
  position: absolute;
  inset: 0;
  background: var(--theme-cover-overlay);
}

.cover-actions {
  position: absolute;
  top: 16px;
  right: 18px;
  display: flex;
  gap: 8px;
}

.cover-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  border-radius: 99px;
  border: 1px solid rgba(255, 255, 255, 0.28);
  background: rgba(15, 23, 42, 0.32);
  backdrop-filter: blur(8px);
  color: #fff;
  font-size: 11.5px;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    transform var(--theme-duration-fast) var(--theme-ease);
}

.cover-btn:hover {
  background: rgba(15, 23, 42, 0.46);
  transform: translateY(-1px);
}

.cover-btn--primary {
  border-color: transparent;
  background: rgba(255, 255, 255, 0.92);
  color: #1c2b3a;
  font-weight: 600;
}

.cover-user {
  position: absolute;
  right: 22px;
  bottom: 20px;
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.cover-user__meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
}

.cover-user__name {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.cover-user__sign {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.78);
}

.cover-avatar {
  border: 2px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 14px 34px -18px rgba(0, 0, 0, 0.7);
}

.feed {
  max-width: 760px;
  margin: 0 auto;
  padding: 18px 24px 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.moment-card {
  display: flex;
  gap: 12px;
  padding: 18px;
  border-radius: 16px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
  animation: im-fade-up 320ms var(--theme-ease) both;
}

.moment-main {
  flex: 1;
  min-width: 0;
}

.moment-head {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.moment-who {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.moment-name {
  font-size: 13.5px;
  font-weight: 700;
  color: var(--theme-primary);
}

.moment-time {
  font-size: 10.5px;
  color: var(--theme-text-3);
}

.moment-delete {
  opacity: 0;
  transition: opacity var(--theme-duration-fast) var(--theme-ease);
}

.moment-card:hover .moment-delete {
  opacity: 1;
}

.moment-content {
  margin: 10px 0 0;
  font-size: 13.5px;
  line-height: 1.75;
  color: var(--theme-text-1);
  white-space: pre-wrap;
  word-break: break-word;
}

.moment-images {
  display: grid;
  gap: 5px;
  margin-top: 12px;
  max-width: 480px;
}

.moment-images.grid-one {
  grid-template-columns: 1fr;
  max-width: 300px;
}

.moment-images.grid-two {
  grid-template-columns: repeat(2, 1fr);
}

.moment-images.grid-three {
  grid-template-columns: repeat(3, 1fr);
}

.moment-image {
  padding: 0;
  border: none;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 1 / 1;
  cursor: pointer;
  background: var(--theme-bg-muted);
}

.moment-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--theme-duration-fast) var(--theme-ease);
}

.moment-image:hover img {
  transform: scale(1.04);
}

.moment-location {
  margin: 10px 0 0;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: var(--theme-primary);
}

.moment-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
}

.moment-action {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: var(--theme-text-3);
  font-size: 12px;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.moment-action:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.moment-action.is-liked {
  color: #f5b301;
}

.action-count {
  font-size: 10.5px;
  opacity: 0.75;
}

.moment-social {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.likes-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 2px;
  font-size: 11.5px;
  color: var(--theme-text-2);
}

.likes-icon {
  color: #f5b301;
  margin-right: 2px;
}

.like-name {
  color: var(--theme-primary);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  border-top: 1px solid var(--theme-hairline);
  padding-top: 6px;
}

.comment-row {
  font-size: 11.5px;
  line-height: 1.6;
  color: var(--theme-text-2);
  word-break: break-word;
}

.comment-name {
  color: var(--theme-primary);
  font-weight: 600;
  margin-right: 4px;
}

.comment-reply {
  color: var(--theme-text-3);
  margin-right: 4px;
}

.comment-time {
  margin-left: 6px;
  font-size: 10px;
  color: var(--theme-text-3);
}

.comment-compose {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.load-more {
  align-self: center;
  margin: 4px auto 0;
  padding: 8px 20px;
  border-radius: 99px;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font-size: 12px;
  cursor: pointer;
}

.moments-empty {
  min-height: 240px;
}

.compose-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.compose-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.compose-image {
  position: relative;
  width: 76px;
  height: 76px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--theme-hairline);
}

.compose-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 3px;
  right: 3px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: none;
  background: rgba(15, 23, 42, 0.55);
  color: #fff;
  display: grid;
  place-items: center;
  cursor: pointer;
}

.add-image {
  width: 76px;
  height: 76px;
  border-radius: 8px;
  border: 1px dashed var(--theme-border);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  font-size: 10px;
  cursor: pointer;
}

.compose-meta {
  display: flex;
  gap: 10px;
}

.visibility-select {
  width: 150px;
  flex-shrink: 0;
}

.publish-text {
  margin-left: 4px;
}

.hidden-input {
  display: none;
}
</style>
