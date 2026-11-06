<script setup lang="ts">
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { ArrowRight, Setting } from '@element-plus/icons-vue';
import { useAuthStore } from '@/stores/auth';
import { updateUserInfo } from '@/api/user';

const emit = defineEmits<{
  (e: 'open-settings'): void;
}>();

const { t } = useI18n();
const authStore = useAuthStore();

const nickname = ref('');
const signature = ref('');
const saving = ref(false);

watch(
  () => authStore.user,
  (user) => {
    nickname.value = user?.nickname ?? '';
    signature.value = user?.signature ?? '';
  },
  { immediate: true }
);

function displayName(): string {
  const user = authStore.user;
  return user?.nickname || user?.username || '';
}

async function onSave(): Promise<void> {
  if (saving.value) return;
  saving.value = true;
  try {
    await updateUserInfo({ nickname: nickname.value, signature: signature.value });
    ElMessage.success(t('im.me.saved'));
    await authStore.restore();
  } finally {
    saving.value = false;
  }
}

async function onLogout(): Promise<void> {
  await authStore.logout();
}
</script>

<template>
  <div class="me-view">
    <div class="me-scroll">
      <section class="profile-card">
        <span class="big-avatar">
          <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" :alt="displayName()" />
          <template v-else>{{ (displayName() || '?')[0] }}</template>
        </span>
        <div class="profile-meta">
          <h3 class="nickname">{{ displayName() }}</h3>
          <span class="account">{{ authStore.user?.account || authStore.user?.username }}</span>
          <span class="signature" :class="{ placeholder: !authStore.user?.signature }">
            {{ authStore.user?.signature || t('im.me.signaturePlaceholder') }}
          </span>
        </div>
      </section>

      <section class="edit-card">
        <div class="field">
          <label class="field-label">{{ t('im.me.nickname') }}</label>
          <el-input v-model="nickname" maxlength="32" />
        </div>
        <div class="field">
          <label class="field-label">{{ t('im.me.signature') }}</label>
          <el-input
            v-model="signature"
            maxlength="80"
            :placeholder="t('im.me.signaturePlaceholder')"
          />
        </div>
        <div class="edit-actions">
          <el-button type="primary" :loading="saving" @click="onSave">
            {{ t('im.me.save') }}
          </el-button>
        </div>
      </section>

      <section class="menu-card">
        <button type="button" class="menu-row" @click="emit('open-settings')">
          <el-icon :size="16"><Setting /></el-icon>
          <span class="menu-text">{{ t('im.me.settings') }}</span>
          <el-icon :size="14" class="arrow"><ArrowRight /></el-icon>
        </button>
      </section>

      <section class="logout-card">
        <el-popconfirm
          :title="t('im.me.logoutConfirm')"
          :confirm-button-text="t('im.me.logout')"
          @confirm="onLogout"
        >
          <template #reference>
            <el-button type="danger" plain class="logout-btn">
              {{ t('im.me.logout') }}
            </el-button>
          </template>
        </el-popconfirm>
      </section>
    </div>
  </div>
</template>

<style scoped>
.me-view {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-app);
}

.me-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
}

.big-avatar {
  width: 72px;
  height: 72px;
  flex-shrink: 0;
  border-radius: var(--theme-radius-lg);
  display: grid;
  place-items: center;
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  background: var(--theme-gradient);
  overflow: hidden;
}

.big-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-meta {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nickname {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.account {
  font-size: 13px;
  color: var(--theme-text-2);
}

.signature {
  font-size: 12px;
  color: var(--theme-text-2);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.signature.placeholder {
  color: var(--theme-text-3);
}

.edit-card {
  padding: 18px 20px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
}

.field {
  margin-bottom: 14px;
}

.field-label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  color: var(--theme-text-2);
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
}

.menu-card {
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
  overflow: hidden;
}

.menu-row {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  border: none;
  background: transparent;
  font-size: 14px;
  color: var(--theme-text-1);
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.menu-row:hover {
  background: var(--theme-bg-hover);
}

.menu-text {
  flex: 1;
  text-align: left;
}

.arrow {
  color: var(--theme-text-3);
}

.logout-card {
  display: flex;
  justify-content: center;
  padding-bottom: 10px;
}

.logout-btn {
  width: 60%;
}
</style>
