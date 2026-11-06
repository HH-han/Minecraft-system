<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ArrowLeft,
  ChatDotRound,
  Check,
  Comment,
  Plus,
  Search,
  Star,
  StarFilled,
  User,
  UserFilled
} from '@element-plus/icons-vue';
import { pinyin } from 'pinyin-pro';
import { resolveMediaUrl } from '@/config';
import AppDialog from '@/components/dialog/AppDialog.vue';
import { useChatStore } from '@/stores/chat';
import type { ChatGroup, FriendInfo, FriendRequestInfo } from '@/types/im';
import { formatListTime } from '@/utils/format';

const emit = defineEmits<{
  selectFriend: [friendId: number];
  selectGroup: [groupId: number];
  openAddFriend: [];
  openCreateGroup: [];
}>();

const { t, locale } = useI18n();
const chatStore = useChatStore();

const keyword = ref('');
const mode = ref<'contacts' | 'groups'>('contacts');
const starOnly = ref(false);
const requestVisible = ref(false);
const processingId = ref<number | null>(null);

function displayName(friend: FriendInfo): string {
  return friend.remark || friend.username || '';
}

function initialOf(name: string): string {
  return (name || '?').trim().charAt(0).toUpperCase();
}

function letterOf(name: string): string {
  const first = (name || '').trim().charAt(0);
  if (!first) return '#';
  if (/[a-zA-Z]/.test(first)) return first.toUpperCase();
  if (/[\u4e00-\u9fa5]/.test(first)) {
    const initials = pinyin(first, { pattern: 'first', toneType: 'none', type: 'array' });
    const letter = String(initials[0] ?? '').trim().charAt(0).toUpperCase();
    return /[A-Z]/.test(letter) ? letter : '#';
  }
  return '#';
}

const filteredFriends = computed<FriendInfo[]>(() => {
  const kw = keyword.value.trim().toLowerCase();
  return chatStore.friends.filter((friend) => {
    if (starOnly.value && !chatStore.isStarred(friend.friendId)) return false;
    if (!kw) return true;
    return displayName(friend).toLowerCase().includes(kw);
  });
});

const groupedFriends = computed<Array<{ letter: string; friends: FriendInfo[] }>>(() => {
  const map = new Map<string, FriendInfo[]>();
  for (const friend of [...filteredFriends.value].sort((a, b) =>
    displayName(a).localeCompare(displayName(b), 'zh-CN')
  )) {
    const letter = letterOf(displayName(friend));
    const list = map.get(letter);
    if (list) list.push(friend);
    else map.set(letter, [friend]);
  }
  return Array.from(map.entries())
    .sort((a, b) => {
      if (a[0] === '#') return 1;
      if (b[0] === '#') return -1;
      return a[0].localeCompare(b[0]);
    })
    .map(([letter, friends]) => ({ letter, friends }));
});

const filteredGroups = computed<ChatGroup[]>(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return chatStore.groups;
  return chatStore.groups.filter((group) => group.name.toLowerCase().includes(kw));
});

const starredFriends = computed<FriendInfo[]>(() =>
  chatStore.friends.filter((friend) => chatStore.isStarred(friend.friendId))
);

const requestCount = computed(() => chatStore.pendingRequestCount);

function requestName(request: FriendRequestInfo): string {
  return request.nickname || request.username || t('im.common.member');
}

function requestIdOf(request: FriendRequestInfo): number {
  return Number(request.id ?? request.requestId ?? 0);
}

function openRequests(): void {
  if (!chatStore.friendRequests.length && requestCount.value === 0) {
    ElMessage.info(t('im.contacts.noRequests'));
  }
  requestVisible.value = true;
}

async function handleRequest(request: FriendRequestInfo, accept: boolean): Promise<void> {
  const id = requestIdOf(request);
  if (!id || processingId.value) return;
  processingId.value = id;
  try {
    await chatStore.resolveFriendRequest(id, accept);
    ElMessage.success(accept ? t('im.contacts.accepted') : t('im.contacts.rejected'));
  } catch {
    /* 请求层已提示 */
  } finally {
    processingId.value = null;
  }
}

function toggleStarOnly(): void {
  starOnly.value = !starOnly.value;
  if (starOnly.value && !starredFriends.value.length) {
    ElMessage.info(t('im.contacts.noStarred'));
  }
}

function notReady(): void {
  ElMessage.info(t('im.common.comingSoon'));
}

function switchMode(next: 'contacts' | 'groups'): void {
  mode.value = next;
  starOnly.value = false;
  keyword.value = '';
}

function handleCommand(command: string): void {
  if (command === 'add-friend') emit('openAddFriend');
  if (command === 'create-group') emit('openCreateGroup');
}
</script>

<template>
  <aside class="im-column contacts-column">
    <header class="im-column-head">
      <button
        v-if="mode === 'groups'"
        class="im-icon-btn"
        :title="t('im.common.back')"
        @click="switchMode('contacts')"
      >
        <el-icon :size="16"><ArrowLeft /></el-icon>
      </button>
      <h2 class="im-column-title">
        {{ mode === 'groups' ? t('im.contacts.groups') : t('im.contacts.title') }}
      </h2>
      <div class="im-column-actions">
        <el-dropdown trigger="click" placement="bottom-end" @command="handleCommand">
          <button class="im-icon-btn is-round" :title="t('im.common.more')">
            <el-icon :size="16"><Plus /></el-icon>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="add-friend">
                <el-icon><User /></el-icon>
                {{ t('im.contacts.addFriend') }}
              </el-dropdown-item>
              <el-dropdown-item command="create-group">
                <el-icon><ChatDotRound /></el-icon>
                {{ t('im.contacts.createGroup') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="im-search-row">
      <el-input
        v-model="keyword"
        :placeholder="mode === 'groups' ? t('im.contacts.searchGroups') : t('im.contacts.search')"
        clearable
      >
        <template #prefix>
          <el-icon :size="14"><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <div class="im-scroll">
      <!-- 通讯录入口 -->
      <div v-if="mode === 'contacts'" class="entry-list">
        <button class="entry" @click="openRequests">
          <span class="entry__icon entry__icon--new">
            <el-icon :size="17"><User /></el-icon>
          </span>
          <span class="entry__label">{{ t('im.contacts.newFriends') }}</span>
          <span v-if="requestCount" class="im-badge">{{ requestCount }}</span>
          <el-icon class="entry__arrow" :size="13"><ArrowLeft /></el-icon>
        </button>

        <button class="entry" @click="switchMode('groups')">
          <span class="entry__icon entry__icon--group">
            <el-icon :size="17"><ChatDotRound /></el-icon>
          </span>
          <span class="entry__label">{{ t('im.contacts.groups') }}</span>
          <span class="entry__count">{{ chatStore.groups.length }}</span>
          <el-icon class="entry__arrow" :size="13"><ArrowLeft /></el-icon>
        </button>

        <button class="entry" @click="notReady">
          <span class="entry__icon entry__icon--tag">
            <el-icon :size="17"><Comment /></el-icon>
          </span>
          <span class="entry__label">{{ t('im.contacts.tags') }}</span>
          <el-icon class="entry__arrow" :size="13"><ArrowLeft /></el-icon>
        </button>

        <button class="entry" @click="notReady">
          <span class="entry__icon entry__icon--official">
            <el-icon :size="17"><UserFilled /></el-icon>
          </span>
          <span class="entry__label">{{ t('im.contacts.official') }}</span>
          <el-icon class="entry__arrow" :size="13"><ArrowLeft /></el-icon>
        </button>
      </div>

      <!-- 星标朋友 -->
      <template v-if="mode === 'contacts'">
        <button class="star-row" :class="{ 'is-active': starOnly }" @click="toggleStarOnly">
          <el-icon :size="14">
            <StarFilled v-if="starOnly" />
            <Star v-else />
          </el-icon>
          <span>{{ t('im.contacts.starFriends') }}</span>
          <span class="entry__count">{{ starredFriends.length }}</span>
        </button>

        <template v-if="groupedFriends.length">
          <div v-for="group in groupedFriends" :key="group.letter">
            <p class="im-group-title">{{ group.letter }}</p>
            <div class="im-list">
              <div
                v-for="friend in group.friends"
                :key="friend.friendId"
                class="im-list-item contact-item"
                @click="emit('selectFriend', friend.friendId)"
              >
                <div class="im-avatar">
                  <img
                    v-if="resolveMediaUrl(friend.avatar)"
                    :src="resolveMediaUrl(friend.avatar)"
                    :alt="displayName(friend)"
                  />
                  <template v-else>{{ initialOf(displayName(friend)) }}</template>
                  <span class="im-status-dot" :class="{ offline: !friend.online }" />
                </div>
                <div class="im-list-item__body">
                  <div class="im-list-item__top">
                    <span class="im-list-item__name">{{ displayName(friend) }}</span>
                    <el-icon v-if="chatStore.isStarred(friend.friendId)" class="star-mark" :size="12">
                      <StarFilled />
                    </el-icon>
                  </div>
                  <div class="im-list-item__bottom">
                    <span class="im-list-item__preview">
                      {{ friend.online ? t('im.common.online') : t('im.common.offline') }}
                    </span>
                    <span class="im-list-item__time">
                      {{ formatListTime(friend.lastMessageTime, locale, t('im.time.yesterday')) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="im-empty">
          <div class="im-empty__icon">
            <el-icon :size="28"><UserFilled /></el-icon>
          </div>
          <p class="im-empty__title">{{ keyword ? t('im.conversation.noResult') : t('im.contacts.empty') }}</p>
          <p class="im-empty__desc">{{ t('im.contacts.emptyDesc') }}</p>
        </div>
      </template>

      <!-- 群聊列表 -->
      <template v-else>
        <div v-if="filteredGroups.length" class="im-list group-list">
          <div
            v-for="group in filteredGroups"
            :key="group.id"
            class="im-list-item"
            @click="emit('selectGroup', group.id)"
          >
            <div class="im-avatar">
              <img
                v-if="resolveMediaUrl(group.avatar)"
                :src="resolveMediaUrl(group.avatar)"
                :alt="group.name"
              />
              <template v-else>{{ initialOf(group.name) }}</template>
              <span class="im-group-badge">{{ group.memberCount ?? 0 }}</span>
            </div>
            <div class="im-list-item__body">
              <div class="im-list-item__top">
                <span class="im-list-item__name">{{ group.name }}</span>
              </div>
              <div class="im-list-item__bottom">
                <span class="im-list-item__preview">{{ group.description || t('im.common.noDescription') }}</span>
                <span class="im-chat-stub">{{ t('im.common.group') }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="im-empty">
          <div class="im-empty__icon">
            <el-icon :size="28"><ChatDotRound /></el-icon>
          </div>
          <p class="im-empty__title">{{ keyword ? t('im.conversation.noResult') : t('im.contacts.noGroups') }}</p>
          <p class="im-empty__desc">{{ t('im.contacts.noGroupsDesc') }}</p>
        </div>
      </template>
    </div>

    <!-- 新的朋友 -->
    <AppDialog
      v-model="requestVisible"
      :title="t('im.contacts.newFriends')"
      width="460px"
      :show-footer="false"
    >
      <div v-if="chatStore.friendRequests.length" class="request-list">
        <div
          v-for="request in chatStore.friendRequests"
          :key="requestIdOf(request)"
          class="request-item"
        >
          <div class="im-avatar sm round">
            <img
              v-if="resolveMediaUrl(request.avatar)"
              :src="resolveMediaUrl(request.avatar)"
              :alt="requestName(request)"
            />
            <template v-else>{{ initialOf(requestName(request)) }}</template>
          </div>
          <div class="request-main">
            <p class="request-name">{{ requestName(request) }}</p>
            <p class="request-message">{{ request.message || t('im.contacts.requestMessage') }}</p>
          </div>
          <div class="request-actions">
            <el-button
              size="small"
              class="im-action-primary"
              :loading="processingId === requestIdOf(request)"
              @click="handleRequest(request, true)"
            >
              <el-icon><Check /></el-icon>
              <span>{{ t('im.contacts.accept') }}</span>
            </el-button>
            <el-button
              size="small"
              class="im-action-danger"
              :disabled="processingId === requestIdOf(request)"
              @click="handleRequest(request, false)"
            >
              {{ t('im.contacts.reject') }}
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="im-empty request-empty">
        <div class="im-empty__icon">
          <el-icon :size="30"><User /></el-icon>
        </div>
        <p class="im-empty__title">{{ t('im.contacts.noRequests') }}</p>
        <p class="im-empty__desc">{{ t('im.contacts.noRequestsDesc') }}</p>
      </div>
    </AppDialog>
  </aside>
</template>

<style scoped>
.contacts-column {
  width: 292px;
  flex-shrink: 0;
  border-right: 1px solid var(--theme-hairline);
}

.entry-list {
  padding: 2px 10px 8px;
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.entry {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border: none;
  border-radius: 12px;
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.entry:hover {
  background: var(--theme-bg-hover);
}

.entry__icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
  flex-shrink: 0;
  box-shadow: var(--theme-shadow-sm);
}

.entry__icon--new {
  background: linear-gradient(135deg, #ffb347, #f97316);
}

.entry__icon--group {
  background: linear-gradient(135deg, #59b8ff, #4a6cf7);
}

.entry__icon--tag {
  background: linear-gradient(135deg, #4ade80, #16a34a);
}

.entry__icon--official {
  background: linear-gradient(135deg, #c084fc, #7c3aed);
}

.entry__label {
  flex: 1;
  min-width: 0;
  font-size: 13.5px;
  font-weight: 500;
  color: var(--theme-text-1);
}

.entry__count {
  font-size: 11px;
  color: var(--theme-text-3);
  font-variant-numeric: tabular-nums;
}

.entry__arrow {
  color: var(--theme-text-3);
  transform: rotate(180deg);
  opacity: 0.5;
}

.star-row {
  margin: 8px 10px 0;
  width: calc(100% - 20px);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 12px;
  border-radius: 10px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12.5px;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.star-row:hover {
  background: var(--theme-bg-hover);
}

.star-row.is-active {
  color: var(--theme-primary);
  border-color: color-mix(in srgb, var(--theme-primary) 30%, transparent);
  background: var(--theme-primary-soft);
}

.star-row span:first-of-type {
  flex: 1;
  text-align: left;
}

.contact-item {
  padding: 9px 12px;
}

.star-mark {
  color: #f5b301;
  flex-shrink: 0;
}

.group-list {
  padding-top: 6px;
}

.im-chat-stub {
  flex-shrink: 0;
  font-size: 11px;
  color: var(--theme-primary);
}

.request-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 380px;
  overflow-y: auto;
}

.request-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface-2);
}

.request-main {
  flex: 1;
  min-width: 0;
}

.request-name {
  margin: 0;
  font-size: 13.5px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.request-message {
  margin: 3px 0 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.request-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.request-actions :deep(.el-button span) {
  margin-left: 4px;
}

.request-empty {
  min-height: 220px;
}
</style>
