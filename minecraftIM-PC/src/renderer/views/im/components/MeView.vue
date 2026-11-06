<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  ArrowRight,
  Collection,
  Edit,
  Setting,
  SwitchButton,
  Wallet
} from '@element-plus/icons-vue';
import { resolveMediaUrl } from '@/config';
import { confirm } from '@/components/dialog/confirm';
import { useAppStore } from '@/stores/app';
import { useAuthStore } from '@/stores/auth';

const emit = defineEmits<{
  profile: [];
  services: [];
  favorites: [];
  settings: [];
}>();

const { t } = useI18n();
const authStore = useAuthStore();
const appStore = useAppStore();

const displayName = computed(
  () => authStore.user?.nickname || authStore.user?.username || ''
);

const signature = computed(
  () => authStore.user?.signature || t('im.me.defaultSignature')
);

const points = computed(() => Number(authStore.user?.points ?? 0));

function initialOf(name?: string): string {
  return (name || '?').trim().charAt(0).toUpperCase();
}

async function handleLogout(): Promise<void> {
  const confirmed = await confirm({
    title: t('im.me.logout'),
    message: t('im.me.logoutConfirm'),
    confirmText: t('im.common.confirm'),
    cancelText: t('im.common.cancel'),
    tone: 'danger'
  });
  if (!confirmed) return;
  await authStore.logout();
}
</script>

<template>
  <aside class="im-column me-column">
    <header class="im-column-head">
      <h2 class="im-column-title">{{ t('im.me.title') }}</h2>
      <div class="im-column-actions">
        <button class="im-icon-btn is-round" :title="t('im.me.editProfile')" @click="emit('profile')">
          <el-icon :size="15"><Edit /></el-icon>
        </button>
      </div>
    </header>

    <div class="im-scroll me-scroll">
      <button class="me-card" @click="emit('profile')">
        <div class="im-avatar lg round me-avatar">
          <img
            v-if="resolveMediaUrl(authStore.user?.avatar)"
            :src="resolveMediaUrl(authStore.user?.avatar)"
            :alt="displayName"
          />
          <template v-else>{{ initialOf(displayName) }}</template>
          <span class="im-status-dot" />
        </div>
        <div class="me-card__body">
          <p class="me-name">{{ displayName }}</p>
          <p class="me-signature">{{ signature }}</p>
          <div class="me-chips">
            <span class="im-chip">{{ t('im.me.points') }} {{ points }}</span>
            <span class="im-chip">{{ authStore.user?.account || t('im.profile.noAccount') }}</span>
          </div>
        </div>
        <el-icon class="me-arrow" :size="14"><ArrowRight /></el-icon>
      </button>

      <div class="me-menu">
        <button class="me-entry me-entry--feature" @click="emit('services')">
          <span class="me-entry__icon me-entry__icon--wallet">
            <el-icon :size="19"><Wallet /></el-icon>
          </span>
          <span class="me-entry__body">
            <span class="me-entry__title">{{ t('im.me.services') }}</span>
            <span class="me-entry__desc">{{ t('im.me.servicesDesc') }}</span>
          </span>
          <el-icon class="me-arrow" :size="14"><ArrowRight /></el-icon>
        </button>

        <button class="me-entry" @click="emit('favorites')">
          <span class="me-entry__icon me-entry__icon--star">
            <el-icon :size="17"><Collection /></el-icon>
          </span>
          <span class="me-entry__body">
            <span class="me-entry__title">{{ t('im.me.favorites') }}</span>
            <span class="me-entry__desc">{{ t('im.me.favoritesDesc') }}</span>
          </span>
          <el-icon class="me-arrow" :size="14"><ArrowRight /></el-icon>
        </button>

        <button class="me-entry" @click="emit('settings')">
          <span class="me-entry__icon me-entry__icon--setting">
            <el-icon :size="17"><Setting /></el-icon>
          </span>
          <span class="me-entry__body">
            <span class="me-entry__title">{{ t('im.me.settings') }}</span>
            <span class="me-entry__desc">{{ t('im.me.settingsDesc') }}</span>
          </span>
          <el-icon class="me-arrow" :size="14"><ArrowRight /></el-icon>
        </button>
      </div>

      <button class="me-logout" type="button" @click="handleLogout">
        <el-icon :size="15"><SwitchButton /></el-icon>
        <span>{{ t('im.me.logout') }}</span>
      </button>

      <p class="me-footer">
        Minecraft IM {{ appStore.info?.version ? `v${appStore.info.version}` : '' }}
        <span v-if="appStore.info">· {{ appStore.info.platform }}</span>
      </p>
    </div>
  </aside>
</template>

<style scoped>
.me-column {
  width: 304px;
  flex-shrink: 0;
  border-right: 1px solid var(--theme-hairline);
}

.me-scroll {
  padding: 4px 14px 18px;
}

.me-card {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 16px;
  border-radius: 16px;
  border: 1px solid var(--theme-hairline);
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--theme-primary) 9%, var(--theme-bg-surface)),
    var(--theme-bg-surface) 65%
  );
  cursor: pointer;
  text-align: left;
  transition: transform var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.me-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--theme-shadow-md);
}

.me-avatar {
  border: 2px solid color-mix(in srgb, var(--theme-primary) 25%, transparent);
}

.me-card__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.me-name {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.me-signature {
  margin: 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.me-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 2px;
}

.me-arrow {
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.me-menu {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.me-entry {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid transparent;
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease);
}

.me-entry:hover {
  background: var(--theme-bg-hover);
  border-color: var(--theme-hairline);
}

.me-entry--feature {
  background: var(--theme-bg-surface);
  border-color: var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
}

.me-entry__icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
  flex-shrink: 0;
}

.me-entry__icon--wallet {
  background: var(--theme-wallet-gradient);
}

.me-entry__icon--star {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
}

.me-entry__icon--setting {
  background: linear-gradient(135deg, #94a3b8, #64748b);
}

.me-entry__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.me-entry__title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.me-entry__desc {
  font-size: 11px;
  color: var(--theme-text-3);
}

.me-logout {
  width: 100%;
  height: 38px;
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: 10px;
  border: 1px solid color-mix(in srgb, var(--theme-danger) 45%, transparent);
  background: var(--theme-danger-soft);
  color: var(--theme-danger);
  font-family: inherit;
  font-size: 13px;
  cursor: pointer;
  transition:
    background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease);
}

.me-logout:hover {
  background: color-mix(in srgb, var(--theme-danger) 18%, var(--theme-bg-surface));
  border-color: color-mix(in srgb, var(--theme-danger) 65%, transparent);
}

.me-footer {
  margin: 26px 0 0;
  text-align: center;
  font-size: 10.5px;
  color: var(--theme-text-3);
}
</style>
