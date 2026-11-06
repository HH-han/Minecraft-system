<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { getGroupMembers } from '@/api/chat';
import { useChatStore } from '@/stores/chat';
import type { ChatGroup, FriendInfo } from '@/types/im';

const props = defineProps<{
  type: 'friend' | 'group';
  targetId: number;
}>();

const emit = defineEmits<{
  'send-message': [payload: { type: 'friend' | 'group'; id: number }];
}>();

const { t } = useI18n();
const chatStore = useChatStore();

const members = ref<FriendInfo[]>([]);

const friend = computed<FriendInfo | null>(() => {
  if (props.type !== 'friend') return null;
  return (
    chatStore.friends.find(
      (item) => Number(item.friendId ?? item.userId) === props.targetId
    ) ?? null
  );
});

const group = computed<ChatGroup | null>(() => {
  if (props.type !== 'group') return null;
  return chatStore.groups.find((item) => item.id === props.targetId) ?? null;
});

const target = computed(() => friend.value ?? group.value);

const displayName = computed(() => {
  if (friend.value) {
    return String(
      friend.value.remark || friend.value.nickname || friend.value.username || `用户 ${props.targetId}`
    );
  }
  if (group.value) {
    return String(group.value.name ?? group.value.groupName ?? `群聊 ${group.value.id}`);
  }
  return '';
});

const account = computed(() => {
  if (!friend.value) return '';
  return String(friend.value.username ?? '');
});

const signature = computed(() => {
  if (!friend.value) return '';
  return String(friend.value.signature ?? '');
});

function memberName(member: FriendInfo): string {
  return String(member.nickname || member.username || '');
}

async function loadMembers(): Promise<void> {
  if (props.type !== 'group') {
    members.value = [];
    return;
  }
  try {
    const res = await getGroupMembers(props.targetId);
    members.value = res.data ?? [];
  } catch {
    members.value = [];
  }
}

onMounted(loadMembers);
watch(() => [props.type, props.targetId], loadMembers);
</script>

<template>
  <div class="contact-detail">
    <el-empty v-if="!target" :description="t('im.contacts.empty')" :image-size="110" />

    <template v-else>
      <div class="detail-scroll">
        <div class="profile">
          <span class="big-avatar" :class="{ group: type === 'group' }">
            <img v-if="target.avatar" :src="target.avatar" :alt="displayName" />
            <template v-else>{{ displayName[0] }}</template>
          </span>
          <h2 class="display-name">{{ displayName }}</h2>
        </div>

        <div v-if="friend" class="info-rows">
          <div class="info-row">
            <span class="info-label">{{ t('im.detail.account') }}</span>
            <span class="info-value">{{ account || '—' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">{{ t('im.detail.signature') }}</span>
            <span class="info-value">{{ signature || '—' }}</span>
          </div>
        </div>

        <div v-else-if="group" class="info-rows">
          <div class="info-row">
            <span class="info-label">{{ t('im.detail.members') }}</span>
            <span class="info-value">{{ group.memberCount ?? members.length }}</span>
          </div>
          <div v-if="members.length" class="member-grid">
            <span
              v-for="member in members"
              :key="Number(member.userId ?? member.friendId)"
              class="member-avatar"
              :title="memberName(member)"
            >
              <img v-if="member.avatar" :src="member.avatar" :alt="memberName(member)" />
              <template v-else>{{ (memberName(member) || '?')[0] }}</template>
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">{{ t('im.detail.notice') }}</span>
            <span class="info-value">{{ group.notice || '—' }}</span>
          </div>
        </div>
      </div>

      <footer class="detail-footer">
        <el-button
          type="primary"
          class="send-btn"
          @click="emit('send-message', { type, id: targetId })"
        >
          {{ t('im.detail.sendMessage') }}
        </el-button>
      </footer>
    </template>
  </div>
</template>

<style scoped>
.contact-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-app);
}

.detail-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 32px 24px;
}

.profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.big-avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-gradient);
  overflow: hidden;
  box-shadow: var(--theme-shadow-sm);
}

.big-avatar.group {
  border-radius: var(--theme-radius-md);
  background: linear-gradient(135deg, var(--theme-accent-3), var(--theme-primary));
}

.big-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.display-name {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.info-rows {
  width: 100%;
  max-width: 360px;
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 10px 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  width: 64px;
  flex-shrink: 0;
  font-size: 13px;
  color: var(--theme-text-3);
}

.info-value {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  color: var(--theme-text-2);
  word-break: break-all;
}

.member-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 0 8px 80px;
}

.member-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-gradient);
  overflow: hidden;
}

.member-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-footer {
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  padding: 16px 32px 28px;
  border-top: 1px solid var(--theme-hairline);
}

.send-btn {
  min-width: 180px;
}
</style>
