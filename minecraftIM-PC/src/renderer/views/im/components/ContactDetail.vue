<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ChatDotRound,
  Close,
  Delete,
  Edit,
  Plus,
  Star,
  StarFilled,
  UserFilled
} from '@element-plus/icons-vue';
import { resolveMediaUrl } from '@/config';
import AppDialog from '@/components/dialog/AppDialog.vue';
import { confirm } from '@/components/dialog/confirm';
import { useChatStore } from '@/stores/chat';
import type { ChatGroup, FriendInfo } from '@/types/im';

const props = defineProps<{
  type: 'friend' | 'group';
  targetId: number;
}>();

const emit = defineEmits<{
  close: [];
  chat: [type: 'friend' | 'group', id: number];
  changed: [];
}>();

const { t } = useI18n();
const chatStore = useChatStore();

const remarkVisible = ref(false);
const remarkDraft = ref('');
const groupEditVisible = ref(false);
const groupForm = ref({ name: '', description: '' });
const inviteVisible = ref(false);
const inviteSelection = ref<number[]>([]);
const busy = ref(false);

const friend = computed<FriendInfo | undefined>(() =>
  props.type === 'friend' ? chatStore.findFriend(props.targetId) : undefined
);

const group = computed<ChatGroup | undefined>(() =>
  props.type === 'group' ? chatStore.findGroup(props.targetId) : undefined
);

const displayName = computed(() => {
  if (props.type === 'friend') return friend.value?.remark || friend.value?.username || '';
  return group.value?.name || '';
});

const signature = computed(() => {
  if (props.type === 'group') return group.value?.description || t('im.common.noDescription');
  return friend.value?.online ? t('im.common.online') : t('im.common.offline');
});

const members = computed(() =>
  props.type === 'group' ? chatStore.groupMembersMap[props.targetId] ?? [] : []
);

const inviteCandidates = computed<FriendInfo[]>(() => {
  const memberIds = new Set(members.value.map((item) => item.userId));
  return chatStore.friends.filter((item) => !memberIds.has(item.friendId));
});

function initialOf(name?: string): string {
  return (name || '?').trim().charAt(0).toUpperCase();
}

function memberName(userId: number): string {
  const friend = chatStore.findFriend(userId);
  return friend?.remark || friend?.username || `#${userId}`;
}

function memberAvatar(userId: number): string | undefined {
  return resolveMediaUrl(chatStore.findFriend(userId)?.avatar);
}

function openChat(): void {
  emit('chat', props.type, props.targetId);
}

function openRemark(): void {
  if (!friend.value) return;
  remarkDraft.value = friend.value.remark || '';
  remarkVisible.value = true;
}

async function saveRemark(): Promise<void> {
  if (!friend.value) return;
  busy.value = true;
  try {
    await chatStore.updateFriendRemark(friend.value.friendId, remarkDraft.value.trim());
    remarkVisible.value = false;
    emit('changed');
  } catch {
    /* 请求层已提示 */
  } finally {
    busy.value = false;
  }
}

function toggleStar(): void {
  if (!friend.value) return;
  chatStore.toggleStar(friend.value.friendId);
}

async function removeFriend(): Promise<void> {
  if (!friend.value) return;
  const confirmed = await confirm({
    title: t('im.detail.deleteFriend'),
    message: t('im.detail.deleteFriendConfirm'),
    confirmText: t('im.common.confirm'),
    cancelText: t('im.common.cancel'),
    tone: 'danger'
  });
  if (!confirmed) return;
  busy.value = true;
  try {
    await chatStore.removeFriend(friend.value.friendId);
    ElMessage.success(t('im.detail.friendDeleted'));
    emit('changed');
    emit('close');
  } catch {
    /* 请求层已提示 */
  } finally {
    busy.value = false;
  }
}

function openGroupEdit(): void {
  if (!group.value) return;
  groupForm.value = {
    name: group.value.name,
    description: group.value.description ?? ''
  };
  groupEditVisible.value = true;
}

async function saveGroup(): Promise<void> {
  if (!group.value || !groupForm.value.name.trim()) return;
  busy.value = true;
  try {
    await chatStore.editGroup({
      id: group.value.id,
      name: groupForm.value.name.trim(),
      description: groupForm.value.description.trim()
    });
    groupEditVisible.value = false;
    emit('changed');
  } catch {
    /* 请求层已提示 */
  } finally {
    busy.value = false;
  }
}

function openInvite(): void {
  inviteSelection.value = [];
  inviteVisible.value = true;
}

async function submitInvite(): Promise<void> {
  if (!group.value || !inviteSelection.value.length) return;
  busy.value = true;
  try {
    await chatStore.inviteMembers(group.value.id, inviteSelection.value);
    ElMessage.success(t('im.detail.invited'));
    inviteVisible.value = false;
    emit('changed');
  } catch {
    /* 请求层已提示 */
  } finally {
    busy.value = false;
  }
}

async function dissolveGroup(): Promise<void> {
  if (!group.value) return;
  const confirmed = await confirm({
    title: t('im.detail.dissolve'),
    message: t('im.detail.dissolveConfirm'),
    confirmText: t('im.common.confirm'),
    cancelText: t('im.common.cancel'),
    tone: 'danger'
  });
  if (!confirmed) return;
  busy.value = true;
  try {
    await chatStore.dissolveGroup(group.value.id);
    ElMessage.success(t('im.detail.dissolved'));
    emit('changed');
    emit('close');
  } catch {
    /* 请求层已提示 */
  } finally {
    busy.value = false;
  }
}

watch(
  () => [props.type, props.targetId] as const,
  () => {
    if (props.type === 'group' && props.targetId) {
      void chatStore.loadGroupMembers(props.targetId);
    }
  },
  { immediate: true }
);
</script>

<template>
  <aside class="im-panel detail-panel">
    <header class="im-panel-head">
      <span class="im-panel-head__title">
        {{ type === 'group' ? t('im.detail.groupTitle') : t('im.detail.friendTitle') }}
      </span>
      <button class="im-icon-btn" :title="t('im.common.close')" @click="emit('close')">
        <el-icon :size="16"><Close /></el-icon>
      </button>
    </header>

    <div class="im-scroll">
      <!-- 资料头部 -->
      <div class="profile-block">
        <div class="im-avatar xl profile-avatar">
          <img v-if="resolveMediaUrl(friend?.avatar || group?.avatar)" :src="resolveMediaUrl(friend?.avatar || group?.avatar)" :alt="displayName" />
          <template v-else>{{ initialOf(displayName) }}</template>
        </div>
        <div class="profile-name-row">
          <h3 class="profile-name">{{ displayName || t('im.common.loading') }}</h3>
          <button
            v-if="type === 'friend'"
            class="im-icon-btn"
            :title="t('im.detail.editRemark')"
            @click="openRemark"
          >
            <el-icon :size="15"><Edit /></el-icon>
          </button>
          <button
            v-else
            class="im-icon-btn"
            :title="t('im.detail.editGroup')"
            @click="openGroupEdit"
          >
            <el-icon :size="15"><Edit /></el-icon>
          </button>
          <button
            v-if="type === 'friend'"
            class="im-icon-btn star-btn"
            :class="{ 'is-active': friend && chatStore.isStarred(friend.friendId) }"
            :title="t('im.detail.star')"
            @click="toggleStar"
          >
            <el-icon :size="15">
              <StarFilled v-if="friend && chatStore.isStarred(friend.friendId)" />
              <Star v-else />
            </el-icon>
          </button>
        </div>
        <p class="profile-signature">{{ signature }}</p>
      </div>

      <!-- 资料行 -->
      <div class="info-block">
        <div class="im-info-row">
          <span class="im-info-row__label">{{ t('im.detail.id') }}</span>
          <span class="im-info-row__value">{{ type === 'group' ? group?.id : friend?.friendId }}</span>
        </div>
        <template v-if="type === 'friend'">
          <div class="im-info-row">
            <span class="im-info-row__label">{{ t('im.detail.phone') }}</span>
            <span class="im-info-row__value">{{ friend?.phone || t('im.profile.emptyValue') }}</span>
          </div>
          <div class="im-info-row">
            <span class="im-info-row__label">{{ t('im.detail.email') }}</span>
            <span class="im-info-row__value">{{ friend?.email || t('im.profile.emptyValue') }}</span>
          </div>
          <div class="im-info-row">
            <span class="im-info-row__label">{{ t('im.detail.status') }}</span>
            <span class="im-info-row__value">
              <span class="status-chip" :class="{ offline: !friend?.online }">
                {{ friend?.online ? t('im.common.online') : t('im.common.offline') }}
              </span>
            </span>
          </div>
        </template>
        <template v-else>
          <div class="im-info-row">
            <span class="im-info-row__label">{{ t('im.detail.members') }}</span>
            <span class="im-info-row__value">{{ group?.memberCount ?? members.length }}</span>
          </div>
          <div class="im-info-row">
            <span class="im-info-row__label">{{ t('im.detail.status') }}</span>
            <span class="im-info-row__value">
              <span class="status-chip">{{ t('im.detail.normal') }}</span>
            </span>
          </div>
          <div class="im-info-row notice-row">
            <span class="im-info-row__label">{{ t('im.detail.notice') }}</span>
            <span class="im-info-row__value">{{ group?.description || t('im.common.noDescription') }}</span>
          </div>
        </template>
      </div>

      <!-- 群成员 -->
      <div v-if="type === 'group'" class="member-block">
        <div class="member-head">
          <span>{{ t('im.detail.memberList') }}</span>
          <span class="member-count">{{ members.length }}</span>
        </div>
        <div class="member-grid">
          <button
            v-for="member in members"
            :key="member.userId"
            class="member-cell"
            @click="emit('chat', 'friend', member.userId)"
          >
            <div class="im-avatar sm round">
              <img v-if="memberAvatar(member.userId)" :src="memberAvatar(member.userId)" :alt="memberName(member.userId)" />
              <template v-else>{{ initialOf(memberName(member.userId)) }}</template>
            </div>
            <span class="member-label">{{ memberName(member.userId) }}</span>
          </button>
          <button class="member-cell member-add" @click="openInvite">
            <div class="im-avatar sm round add-avatar">
              <el-icon :size="16"><Plus /></el-icon>
            </div>
            <span class="member-label">{{ t('im.detail.inviteShort') }}</span>
          </button>
        </div>
      </div>

      <!-- 操作区：原生按钮 + 固定宽度内容块，保证各行图标与文字左边缘严格对齐 -->
      <div class="action-block">
        <button
          type="button"
          class="action-btn action-main im-action-primary"
          :disabled="busy"
          @click="openChat"
        >
          <span class="action-inner">
            <span class="action-icon"><el-icon :size="15"><ChatDotRound /></el-icon></span>
            <span class="action-text">{{ t('im.detail.sendMessage') }}</span>
          </span>
        </button>

        <template v-if="type === 'friend'">
          <button
            type="button"
            class="action-btn action-main im-action-danger"
            :disabled="busy"
            @click="removeFriend"
          >
            <span class="action-inner">
              <span class="action-icon"><el-icon :size="15"><Delete /></el-icon></span>
              <span class="action-text">{{ t('im.detail.deleteFriend') }}</span>
            </span>
          </button>
        </template>

        <template v-else>
          <button
            type="button"
            class="action-btn action-main action-secondary"
            :disabled="busy"
            @click="openInvite"
          >
            <span class="action-inner">
              <span class="action-icon"><el-icon :size="15"><UserFilled /></el-icon></span>
              <span class="action-text">{{ t('im.detail.invite') }}</span>
            </span>
          </button>
          <button
            type="button"
            class="action-btn action-main action-secondary"
            :disabled="busy"
            @click="openGroupEdit"
          >
            <span class="action-inner">
              <span class="action-icon"><el-icon :size="15"><Edit /></el-icon></span>
              <span class="action-text">{{ t('im.detail.editGroup') }}</span>
            </span>
          </button>
          <button
            type="button"
            class="action-btn action-main im-action-danger"
            :disabled="busy"
            @click="dissolveGroup"
          >
            <span class="action-inner">
              <span class="action-icon"><el-icon :size="15"><Delete /></el-icon></span>
              <span class="action-text">{{ t('im.detail.dissolve') }}</span>
            </span>
          </button>
        </template>
      </div>
    </div>

    <!-- 备注 -->
    <AppDialog
      v-model="remarkVisible"
      :title="t('im.detail.editRemark')"
      width="400px"
      :cancel-text="t('im.common.cancel')"
      :confirm-text="t('im.common.save')"
      :confirm-loading="busy"
      @confirm="saveRemark"
    >
      <div class="dialog-form">
        <el-input
          v-model="remarkDraft"
          maxlength="24"
          :placeholder="t('im.detail.remarkPlaceholder')"
        />
      </div>
    </AppDialog>

    <!-- 编辑群资料 -->
    <AppDialog
      v-model="groupEditVisible"
      :title="t('im.detail.editGroup')"
      width="440px"
      :cancel-text="t('im.common.cancel')"
      :confirm-text="t('im.common.save')"
      :confirm-loading="busy"
      @confirm="saveGroup"
    >
      <div class="im-stack dialog-form">
        <el-input v-model="groupForm.name" maxlength="30" :placeholder="t('im.detail.groupName')" />
        <el-input
          v-model="groupForm.description"
          type="textarea"
          :rows="3"
          maxlength="120"
          :placeholder="t('im.detail.groupDesc')"
        />
      </div>
    </AppDialog>

    <!-- 邀请好友 -->
    <AppDialog
      v-model="inviteVisible"
      :title="t('im.detail.invite')"
      width="440px"
      :cancel-text="t('im.common.cancel')"
      :confirm-text="t('im.common.confirm')"
      :confirm-disabled="!inviteSelection.length"
      :confirm-loading="busy"
      @confirm="submitInvite"
    >
      <div v-if="inviteCandidates.length" class="invite-list">
        <el-checkbox-group v-model="inviteSelection" class="im-stack">
          <el-checkbox
            v-for="candidate in inviteCandidates"
            :key="candidate.friendId"
            :value="candidate.friendId"
            class="invite-item"
          >
            <span class="member-cell invite-cell">
              <div class="im-avatar xs round">
                <img
                  v-if="resolveMediaUrl(candidate.avatar)"
                  :src="resolveMediaUrl(candidate.avatar)"
                  :alt="candidate.username"
                />
                <template v-else>{{ initialOf(candidate.remark || candidate.username) }}</template>
              </div>
              <span>{{ candidate.remark || candidate.username }}</span>
            </span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <div v-else class="im-empty invite-empty">
        <p class="im-empty__title">{{ t('im.detail.noInviteCandidates') }}</p>
      </div>
    </AppDialog>
  </aside>
</template>

<style scoped>
.detail-panel {
  width: 360px;
}

.profile-block {
  padding: 26px 20px 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 10px;
  background: linear-gradient(
    180deg,
    color-mix(in srgb, var(--theme-primary) 8%, transparent),
    transparent
  );
}

.profile-avatar {
  box-shadow: 0 18px 34px -22px color-mix(in srgb, var(--theme-primary) 70%, transparent);
}

.profile-name-row {
  display: flex;
  align-items: center;
  gap: 4px;
}

.profile-name {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-text-1);
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-signature {
  margin: 0;
  font-size: 12px;
  color: var(--theme-text-3);
  max-width: 260px;
  line-height: 1.5;
}

.star-btn.is-active {
  color: #f5b301;
  background: rgba(245, 179, 1, 0.12);
}

.info-block {
  border-top: 1px solid var(--theme-hairline);
}

.notice-row .im-info-row__value {
  line-height: 1.6;
  text-align: right;
  font-size: 12.5px;
  color: var(--theme-text-2);
}

.status-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 2px 9px;
  border-radius: 99px;
  font-size: 11px;
  color: var(--theme-online);
  background: color-mix(in srgb, var(--theme-online) 12%, transparent);
}

.status-chip::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-chip.offline {
  color: var(--theme-text-3);
  background: var(--theme-bg-muted);
}

.member-block {
  padding: 14px 20px 4px;
  border-bottom: 1px solid var(--theme-hairline);
}

.member-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  color: var(--theme-text-2);
  margin-bottom: 12px;
}

.member-count {
  font-size: 11px;
  color: var(--theme-text-3);
}

.member-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px 6px;
  margin-bottom: 14px;
}

.member-cell {
  border: none;
  background: transparent;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  min-width: 0;
}

.member-label {
  font-size: 10.5px;
  color: var(--theme-text-3);
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-cell:hover .member-label {
  color: var(--theme-text-1);
}

.add-avatar {
  border: 1px dashed var(--theme-border);
  background: var(--theme-bg-muted);
  color: var(--theme-text-3);
  box-shadow: none;
}

.action-block {
  padding: 16px 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  width: 100%;
  height: 38px;
  padding: 0;
  box-sizing: border-box;
  border: 1px solid transparent;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: inherit;
  font-size: 13px;
  line-height: 1;
  cursor: pointer;
  border-radius: 10px;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.action-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.action-main {
  width: 100%;
  height: 38px;
  border-radius: 10px;
  font-size: 13px;
}

/* 图标 + 文字作为整体在按钮内居中；文字槽定宽并居中，各行视觉中心一致 */
.action-inner {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  white-space: nowrap;
}

/* 固定图标槽，避免不同 el-icon 尺寸造成基线漂移 */
.action-icon {
  width: 16px;
  height: 16px;
  flex: 0 0 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.action-icon :deep(.el-icon) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.action-text {
  min-width: 72px;
  margin-left: 0;
  text-align: center;
  line-height: 1;
}

.action-secondary {
  border-radius: 10px;
  border-color: var(--theme-border);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
}

.action-secondary:hover:not(:disabled) {
  background: var(--theme-bg-muted);
  color: var(--theme-text-1);
}

.dialog-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-bottom: 6px;
}

.invite-list {
  max-height: 320px;
  overflow-y: auto;
}

.invite-item {
  display: flex;
  align-items: center;
  height: auto;
  padding: 4px 0;
}

.invite-cell {
  flex-direction: row;
  gap: 10px;
}

.invite-empty {
  min-height: 120px;
}
</style>
