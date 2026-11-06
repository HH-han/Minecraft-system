<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { useChatStore } from '@/stores/chat';
import type { ChatGroup, FriendInfo, FriendRequestInfo } from '@/types/im';

interface ContactSelection {
  type: 'friend' | 'group';
  id: number;
}

const props = defineProps<{
  selected: ContactSelection | null;
}>();

const emit = defineEmits<{
  select: [payload: ContactSelection];
  'open-requests': [];
}>();

const { t } = useI18n();
const chatStore = useChatStore();

const keyword = ref('');
const showRequests = ref(false);
const handlingId = ref<number | null>(null);

function friendId(friend: FriendInfo): number {
  return Number(friend.friendId ?? friend.userId);
}

function friendName(friend: FriendInfo): string {
  return String(friend.remark || friend.nickname || friend.username || `用户 ${friendId(friend)}`);
}

function groupName(group: ChatGroup): string {
  return String(group.name ?? group.groupName ?? `群聊 ${group.id}`);
}

const filteredFriends = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return chatStore.friends;
  return chatStore.friends.filter((friend) => friendName(friend).toLowerCase().includes(kw));
});

const filteredGroups = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return chatStore.groups;
  return chatStore.groups.filter((group) => groupName(group).toLowerCase().includes(kw));
});

const isEmpty = computed(() => !filteredFriends.value.length && !filteredGroups.value.length);

function isSelected(type: 'friend' | 'group', id: number): boolean {
  return props.selected?.type === type && props.selected?.id === id;
}

function openRequests(): void {
  showRequests.value = true;
  emit('open-requests');
}

function requestIdOf(request: FriendRequestInfo): number {
  return Number(request.id ?? request.requestId);
}

function requestName(request: FriendRequestInfo): string {
  return String(request.senderNickname || `用户 ${request.senderId ?? ''}`);
}

async function handleRequest(request: FriendRequestInfo, accept: boolean): Promise<void> {
  const id = requestIdOf(request);
  if (!Number.isFinite(id) || handlingId.value !== null) return;
  handlingId.value = id;
  try {
    await chatStore.handleFriendRequest(id, accept);
    ElMessage.success(accept ? t('im.contacts.accepted') : t('im.contacts.rejected'));
    if (!chatStore.friendRequests.length) showRequests.value = false;
  } catch {
    /* 静默：操作失败保留在列表中 */
  } finally {
    handlingId.value = null;
  }
}
</script>

<template>
  <div class="contacts-column">
    <div class="search-box">
      <el-input
        v-model="keyword"
        :placeholder="t('im.contacts.search')"
        size="small"
        clearable
      />
    </div>

    <div class="list-scroll">
      <button type="button" class="requests-entry" @click="openRequests">
        <span class="requests-icon">
          <span v-if="chatStore.friendRequests.length" class="requests-badge">
            {{ chatStore.friendRequests.length > 99 ? '99+' : chatStore.friendRequests.length }}
          </span>
          +
        </span>
        <span class="requests-label">{{ t('im.contacts.requests') }}</span>
      </button>

      <el-empty
        v-if="isEmpty"
        :description="t('im.contacts.empty')"
        :image-size="90"
      />

      <template v-else>
        <section v-if="filteredGroups.length" class="group-section">
          <p class="section-title">{{ t('im.contacts.groups') }}</p>
          <button
            v-for="group in filteredGroups"
            :key="`group-${group.id}`"
            type="button"
            class="contact-item"
            :class="{ active: isSelected('group', group.id) }"
            @click="emit('select', { type: 'group', id: group.id })"
          >
            <span class="avatar group">
              <img v-if="group.avatar" :src="group.avatar" :alt="groupName(group)" />
              <template v-else>{{ groupName(group)[0] }}</template>
            </span>
            <span class="name">{{ groupName(group) }}</span>
          </button>
        </section>

        <section v-if="filteredFriends.length" class="group-section">
          <p class="section-title">{{ t('im.contacts.friends') }}</p>
          <button
            v-for="friend in filteredFriends"
            :key="`friend-${friendId(friend)}`"
            type="button"
            class="contact-item"
            :class="{ active: isSelected('friend', friendId(friend)) }"
            @click="emit('select', { type: 'friend', id: friendId(friend) })"
          >
            <span class="avatar">
              <img v-if="friend.avatar" :src="friend.avatar" :alt="friendName(friend)" />
              <template v-else>{{ friendName(friend)[0] }}</template>
            </span>
            <span class="name">{{ friendName(friend) }}</span>
          </button>
        </section>
      </template>
    </div>

    <el-dialog
      v-model="showRequests"
      :title="t('im.contacts.requests')"
      width="380px"
      append-to-body
    >
      <el-empty
        v-if="!chatStore.friendRequests.length"
        :description="t('im.contacts.noRequests')"
        :image-size="80"
      />
      <ul v-else class="request-list">
        <li v-for="request in chatStore.friendRequests" :key="requestIdOf(request)" class="request-item">
          <span class="avatar small">
            <img v-if="request.senderAvatar" :src="request.senderAvatar" :alt="requestName(request)" />
            <template v-else>{{ requestName(request)[0] }}</template>
          </span>
          <span class="request-meta">
            <span class="request-name">{{ requestName(request) }}</span>
            <span class="request-message">{{ request.message || '…' }}</span>
          </span>
          <span class="request-actions">
            <el-button
              type="primary"
              size="small"
              :loading="handlingId === requestIdOf(request)"
              @click="handleRequest(request, true)"
            >
              {{ t('im.contacts.accept') }}
            </el-button>
            <el-button
              size="small"
              :disabled="handlingId === requestIdOf(request)"
              @click="handleRequest(request, false)"
            >
              {{ t('im.contacts.reject') }}
            </el-button>
          </span>
        </li>
      </ul>
    </el-dialog>
  </div>
</template>

<style scoped>
.contacts-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-box {
  padding: 0 14px 10px;
}

.list-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0 8px 10px;
}

.requests-entry {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border: none;
  border-radius: var(--theme-radius-sm);
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.requests-entry:hover {
  background: var(--theme-bg-hover);
}

.requests-icon {
  position: relative;
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 20px;
  font-weight: 700;
  color: var(--theme-primary-text);
  background: var(--theme-gradient);
}

.requests-badge {
  position: absolute;
  top: -4px;
  right: -6px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: var(--theme-danger);
  color: #fff;
  font-size: 11px;
  line-height: 18px;
  text-align: center;
  box-shadow: var(--theme-shadow-sm);
}

.requests-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.group-section {
  margin-top: 12px;
}

.section-title {
  margin: 0 0 4px;
  padding: 0 10px;
  font-size: 12px;
  color: var(--theme-text-3);
}

.contact-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px;
  border: none;
  border-radius: var(--theme-radius-sm);
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.contact-item:hover {
  background: var(--theme-bg-hover);
}

.contact-item.active {
  background: var(--theme-primary-soft);
}

.avatar {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-gradient);
  overflow: hidden;
}

.avatar.group {
  border-radius: 10px;
  background: linear-gradient(135deg, var(--theme-accent-3), var(--theme-primary));
}

.avatar.small {
  width: 36px;
  height: 36px;
  font-size: 14px;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.name {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.request-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 320px;
  overflow-y: auto;
}

.request-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: var(--theme-radius-sm);
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.request-item:hover {
  background: var(--theme-bg-hover);
}

.request-meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.request-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.request-message {
  font-size: 12px;
  color: var(--theme-text-3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.request-actions {
  flex-shrink: 0;
  display: flex;
  gap: 6px;
}
</style>
