<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { resolveMediaUrl } from '@/config';
import AppDialog from '@/components/dialog/AppDialog.vue';
import { useAuthStore } from '@/stores/auth';
import { useChatStore } from '@/stores/chat';
import type { FriendInfo } from '@/types/im';

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  created: [];
}>();

const { t } = useI18n();
const authStore = useAuthStore();
const chatStore = useChatStore();

const name = ref('');
const memberIds = ref<number[]>([]);
const creating = ref(false);

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function friendId(friend: FriendInfo): number {
  return Number(friend.friendId);
}

function friendName(friend: FriendInfo): string {
  return String(friend.remark || friend.username || '');
}

const canSubmit = computed(
  () => Boolean(name.value.trim()) && memberIds.value.length > 0 && !creating.value
);

function reset(): void {
  name.value = '';
  memberIds.value = [];
}

function close(): void {
  visible.value = false;
  reset();
}

async function submit(): Promise<void> {
  const creatorId = authStore.userId;
  if (!creatorId || !canSubmit.value) return;
  creating.value = true;
  try {
    await chatStore.createGroup({
      name: name.value.trim(),
      creatorId,
      memberIds: memberIds.value
    });
    ElMessage.success(t('im.createGroup.created'));
    emit('created');
    close();
  } catch {
    /* 请求层已提示错误 */
  } finally {
    creating.value = false;
  }
}
</script>

<template>
  <AppDialog
    v-model="visible"
    :title="t('im.createGroup.title')"
    width="440px"
    :cancel-text="t('im.contacts.reject')"
    :confirm-text="t('im.createGroup.submit')"
    :confirm-disabled="!canSubmit"
    :confirm-loading="creating"
    @confirm="submit"
    @closed="reset"
  >
    <div class="form">
      <el-input
        v-model="name"
        :placeholder="t('im.createGroup.namePlaceholder')"
        maxlength="30"
      />

      <div class="members-block">
        <p class="members-label">{{ t('im.createGroup.membersLabel') }}</p>
        <el-empty
          v-if="!chatStore.friends.length"
          :description="t('im.contacts.empty')"
          :image-size="70"
        />
        <el-checkbox-group v-else v-model="memberIds" class="member-list">
          <el-checkbox
            v-for="friend in chatStore.friends"
            :key="friendId(friend)"
            :value="friendId(friend)"
            class="member-item"
          >
            <span class="member-row">
              <span class="avatar">
                <img
                  v-if="resolveMediaUrl(friend.avatar)"
                  :src="resolveMediaUrl(friend.avatar)"
                  :alt="friendName(friend)"
                />
                <template v-else>{{ (friendName(friend) || '?')[0] }}</template>
              </span>
              <span class="member-name">{{ friendName(friend) }}</span>
            </span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
  </AppDialog>
</template>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding-bottom: 6px;
}

.members-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.members-label {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-2);
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  max-height: 260px;
  overflow-y: auto;
  padding: 4px;
  border: 1px solid var(--theme-hairline);
  border-radius: var(--theme-radius-md);
  background: var(--theme-bg-surface);
}

.member-item {
  width: 100%;
  height: auto;
  margin-right: 0;
  padding: 6px 8px;
  border-radius: var(--theme-radius-sm);
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.member-item:hover {
  background: var(--theme-bg-hover);
}

.member-item :deep(.el-checkbox__label) {
  flex: 1;
  min-width: 0;
}

.member-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 30px;
  height: 30px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 12px;
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

.member-name {
  font-size: 13px;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
