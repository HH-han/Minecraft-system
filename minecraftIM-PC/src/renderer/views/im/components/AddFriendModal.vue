<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { sendFriendRequest } from '@/api/chat';
import { getUserByAccount } from '@/api/user';
import { resolveMediaUrl } from '@/config';
import AppDialog from '@/components/dialog/AppDialog.vue';
import { useAuthStore } from '@/stores/auth';
import type { UserInfo } from '@/types/im';

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  sent: [];
}>();

const { t } = useI18n();
const authStore = useAuthStore();

const account = ref('');
const message = ref('');
const found = ref<UserInfo | null>(null);
const searching = ref(false);
const sending = ref(false);
const searched = ref(false);

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const foundId = computed(() => {
  if (!found.value) return null;
  const id = found.value.userId ?? found.value.id;
  return typeof id === 'number' ? id : null;
});

const foundName = computed(() => {
  if (!found.value) return '';
  return String(found.value.nickname || found.value.username || '');
});

async function search(): Promise<void> {
  const kw = account.value.trim();
  if (!kw || searching.value) return;
  searching.value = true;
  searched.value = false;
  found.value = null;
  try {
    const res = await getUserByAccount(kw);
    found.value = res.data ?? null;
  } catch {
    found.value = null;
  } finally {
    searching.value = false;
    searched.value = true;
  }
}

function reset(): void {
  account.value = '';
  message.value = '';
  found.value = null;
  searched.value = false;
}

function close(): void {
  visible.value = false;
  reset();
}

async function submit(): Promise<void> {
  const senderId = authStore.userId;
  const receiverId = foundId.value;
  if (!senderId || !receiverId || sending.value) return;
  sending.value = true;
  try {
    await sendFriendRequest({
      senderId,
      receiverId,
      message: message.value.trim() || undefined
    });
    ElMessage.success(t('im.addFriend.sent'));
    emit('sent');
    close();
  } catch {
    /* 请求层已提示错误 */
  } finally {
    sending.value = false;
  }
}
</script>

<template>
  <AppDialog
    v-model="visible"
    :title="t('im.addFriend.title')"
    width="430px"
    :cancel-text="t('im.contacts.reject')"
    :confirm-text="t('im.addFriend.submit')"
    :confirm-disabled="!foundId"
    :confirm-loading="sending"
    @confirm="submit"
    @closed="reset"
  >
    <div class="form">
      <div class="search-row">
        <el-input
          v-model="account"
          :placeholder="t('im.addFriend.accountPlaceholder')"
          clearable
          @keyup.enter="search"
        />
        <button
          type="button"
          class="search-btn im-action-primary"
          :disabled="searching"
          @click="search"
        >
          <el-icon :size="14"><Search /></el-icon>
          <span>{{ t('im.addFriend.search') }}</span>
        </button>
      </div>

      <div v-if="found" class="user-card">
        <span class="avatar">
          <img
            v-if="resolveMediaUrl(found.avatar)"
            :src="resolveMediaUrl(found.avatar)"
            :alt="foundName"
          />
          <template v-else>{{ (foundName || '?')[0] }}</template>
        </span>
        <span class="user-meta">
          <span class="user-name">{{ foundName }}</span>
          <span class="user-account">{{ found.account ?? found.username ?? '' }}</span>
        </span>
      </div>
      <p v-else-if="searched" class="not-found">{{ t('im.addFriend.notFound') }}</p>

      <el-input
        v-model="message"
        :placeholder="t('im.addFriend.messagePlaceholder')"
        maxlength="100"
      />
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

.search-row {
  display: flex;
  gap: 8px;
}

.search-btn {
  height: 32px;
  padding: 0 14px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 13px;
  cursor: pointer;
}

.search-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
  background: var(--theme-bg-surface);
}

.avatar {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  font-size: 16px;
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

.user-meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.user-account {
  font-size: 12px;
  color: var(--theme-text-3);
}

.not-found {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-3);
  text-align: center;
}
</style>
