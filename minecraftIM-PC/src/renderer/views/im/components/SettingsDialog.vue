<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  Bell,
  Cpu,
  Grid,
  InfoFilled,
  Moon,
  Setting,
  Sunny,
  SwitchButton,
  Timer,
  User
} from '@element-plus/icons-vue';
import { SUPPORTED_LOCALES, setLocale, type AppLocale } from '@/locales';
import { resolveMediaUrl } from '@/config';
import AppDialog from '@/components/dialog/AppDialog.vue';
import { confirm } from '@/components/dialog/confirm';
import { useAppStore } from '@/stores/app';
import { useAuthStore } from '@/stores/auth';
import { useThemeStore, type ThemeId } from '@/stores/theme';

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
}>();

const { t, locale } = useI18n();
const appStore = useAppStore();
const authStore = useAuthStore();
const themeStore = useThemeStore();

type SectionKey = 'account' | 'general' | 'shortcuts' | 'notifications' | 'plugins' | 'about';

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const sections = computed(() => [
  { key: 'account' as const, label: t('settings.sections.account'), icon: User },
  { key: 'general' as const, label: t('settings.sections.general'), icon: Setting },
  { key: 'shortcuts' as const, label: t('settings.sections.shortcuts'), icon: Timer },
  { key: 'notifications' as const, label: t('settings.sections.notifications'), icon: Bell },
  { key: 'plugins' as const, label: t('settings.sections.plugins'), icon: Grid },
  { key: 'about' as const, label: t('settings.sections.about'), icon: InfoFilled }
]);

const activeSection = ref<SectionKey>('account');

const themeIcons: Record<ThemeId, typeof Sunny> = {
  light: Sunny,
  dark: Moon,
  game: Cpu
};

const PREF_KEY = 'im:preferences';

interface Preferences {
  notifyMessage: boolean;
  notifySound: boolean;
  notifyDetail: boolean;
  autoStart: boolean;
  compactMode: boolean;
  enterToSend: boolean;
  pluginCloud: boolean;
  pluginTranslate: boolean;
}

const defaults: Preferences = {
  notifyMessage: true,
  notifySound: true,
  notifyDetail: false,
  autoStart: false,
  compactMode: false,
  enterToSend: true,
  pluginCloud: true,
  pluginTranslate: false
};

function readPrefs(): Preferences {
  try {
    const raw = localStorage.getItem(PREF_KEY);
    if (!raw) return { ...defaults };
    return { ...defaults, ...(JSON.parse(raw) as Partial<Preferences>) };
  } catch {
    return { ...defaults };
  }
}

const prefs = reactive<Preferences>(readPrefs());

watch(
  prefs,
  (value) => {
    localStorage.setItem(PREF_KEY, JSON.stringify(value));
  },
  { deep: true }
);

const currentLocale = computed(() => locale.value as AppLocale);

const shortcuts = computed(() => [
  { label: t('settings.shortcuts.send'), keys: ['Enter'] },
  { label: t('settings.shortcuts.newline'), keys: ['Shift', 'Enter'] },
  { label: t('settings.shortcuts.search'), keys: ['Ctrl', 'K'] },
  { label: t('settings.shortcuts.settings'), keys: ['Ctrl', ','] },
  { label: t('settings.shortcuts.closePanel'), keys: ['Esc'] }
]);

const plugins = computed(() => [
  {
    key: 'cloud' as const,
    name: t('settings.plugins.items.cloudName'),
    desc: t('settings.plugins.items.cloudDesc'),
    enabled: 'pluginCloud' as const
  },
  {
    key: 'translate' as const,
    name: t('settings.plugins.items.translateName'),
    desc: t('settings.plugins.items.translateDesc'),
    enabled: 'pluginTranslate' as const
  }
]);

async function changeLocale(next: AppLocale): Promise<void> {
  await setLocale(next);
}

async function changeDir(kind: 'data' | 'logs'): Promise<void> {
  try {
    const changed = await appStore.changeStorageDir(
      kind,
      kind === 'logs' ? t('settings.storage.pickLogs') : t('settings.storage.pickData')
    );
    if (changed) ElMessage.success(t('settings.storage.changed'));
  } catch {
    ElMessage.error(t('settings.storage.changeFailed'));
  }
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
  visible.value = false;
  await authStore.logout();
}
</script>

<template>
  <AppDialog
    v-model="visible"
    :title="t('settings.title')"
    width="720px"
    :show-footer="false"
    body-padding="0"
  >
    <div class="settings-dialog__body">
      <nav class="settings-nav">
        <button
          v-for="section in sections"
          :key="section.key"
          class="settings-nav__item"
          :class="{ 'is-active': activeSection === section.key }"
          @click="activeSection = section.key"
        >
          <el-icon :size="15"><component :is="section.icon" /></el-icon>
          <span>{{ section.label }}</span>
        </button>
      </nav>

      <div class="im-scroll settings-panel">
        <!-- 账号与存储 -->
        <section v-if="activeSection === 'account'" class="settings-section">
          <div class="account-card">
            <span class="account-avatar">
              <img
                v-if="resolveMediaUrl(authStore.user?.avatar)"
                :src="resolveMediaUrl(authStore.user?.avatar)"
                :alt="authStore.user?.nickname ?? ''"
              />
              <template v-else>
                {{ (authStore.user?.nickname || authStore.user?.username || '?').charAt(0) }}
              </template>
            </span>
            <div class="account-meta">
              <p class="account-name">
                {{ authStore.user?.nickname || authStore.user?.username }}
              </p>
              <p class="account-sub">
                {{ t('im.profile.account') }} · {{ authStore.user?.account || '-' }}
              </p>
            </div>
          </div>

          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.storage.dataDir') }}</span>
            <span class="settings-row__value">{{ appStore.info?.dataDir ?? '-' }}</span>
            <el-button
              size="small"
              plain
              class="storage-btn"
              :disabled="!appStore.isDesktop"
              @click="changeDir('data')"
            >
              {{ t('settings.storage.change') }}
            </el-button>
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.storage.logDir') }}</span>
            <span class="settings-row__value">{{ appStore.info?.logDir ?? '-' }}</span>
            <el-button
              size="small"
              plain
              class="storage-btn"
              :disabled="!appStore.isDesktop"
              @click="changeDir('logs')"
            >
              {{ t('settings.storage.change') }}
            </el-button>
          </div>

          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.account.autoStart') }}</span>
            <el-switch v-model="prefs.autoStart" />
          </div>

          <div class="settings-actions">
            <el-button class="im-action-danger" @click="handleLogout">
              <el-icon :size="14"><SwitchButton /></el-icon>
              <span>{{ t('im.me.logout') }}</span>
            </el-button>
          </div>
        </section>

        <!-- 通用 -->
        <section v-else-if="activeSection === 'general'" class="settings-section">
          <h4 class="settings-group__title">{{ t('settings.theme.title') }}</h4>
          <div class="theme-grid">
            <button
              v-for="option in themeStore.options"
              :key="option.id"
              type="button"
              class="theme-card"
              :class="{ active: themeStore.currentTheme === option.id }"
              :data-theme="option.id"
              @click="themeStore.setTheme(option.id)"
            >
              <span class="theme-preview">
                <span class="preview-bar" />
                <span class="preview-body" />
              </span>
              <span class="theme-label">
                <el-icon :size="14"><component :is="themeIcons[option.id]" /></el-icon>
                {{ t(option.labelKey) }}
              </span>
            </button>
          </div>

          <h4 class="settings-group__title">{{ t('settings.language.title') }}</h4>
          <div class="locale-row">
            <button
              v-for="item in SUPPORTED_LOCALES"
              :key="item.value"
              type="button"
              class="locale-chip"
              :class="{ active: currentLocale === item.value }"
              @click="changeLocale(item.value)"
            >
              {{ item.label }}
            </button>
          </div>

          <h4 class="settings-group__title">{{ t('settings.general.title') }}</h4>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.general.compact') }}</span>
            <el-switch v-model="prefs.compactMode" />
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.general.enterToSend') }}</span>
            <el-switch v-model="prefs.enterToSend" />
          </div>
        </section>

        <!-- 快捷键 -->
        <section v-else-if="activeSection === 'shortcuts'" class="settings-section">
          <div v-for="item in shortcuts" :key="item.label" class="settings-row">
            <span class="settings-row__label">{{ item.label }}</span>
            <span class="shortcut-keys">
              <kbd v-for="key in item.keys" :key="key">{{ key }}</kbd>
            </span>
          </div>
        </section>

        <!-- 通知 -->
        <section v-else-if="activeSection === 'notifications'" class="settings-section">
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.notifications.message') }}</span>
            <el-switch v-model="prefs.notifyMessage" />
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.notifications.sound') }}</span>
            <el-switch v-model="prefs.notifySound" :disabled="!prefs.notifyMessage" />
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.notifications.detail') }}</span>
            <el-switch v-model="prefs.notifyDetail" :disabled="!prefs.notifyMessage" />
          </div>
        </section>

        <!-- 插件 -->
        <section v-else-if="activeSection === 'plugins'" class="settings-section">
          <div class="plugin-list">
            <div v-for="plugin in plugins" :key="plugin.key" class="plugin-row">
              <span class="plugin-icon">
                <el-icon :size="16"><Grid /></el-icon>
              </span>
              <div class="plugin-body">
                <p class="plugin-name">{{ plugin.name }}</p>
                <p class="plugin-desc">{{ plugin.desc }}</p>
              </div>
              <el-switch v-model="prefs[plugin.enabled]" />
            </div>
          </div>
        </section>

        <!-- 关于 -->
        <section v-else class="settings-section">
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.about.framework') }}</span>
            <span class="settings-row__value">{{ t('settings.about.frameworkValue') }}</span>
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.about.renderer') }}</span>
            <span class="settings-row__value">{{ t('settings.about.rendererValue') }}</span>
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.about.version') }}</span>
            <span class="settings-row__value">{{ appStore.info?.version ?? '-' }}</span>
          </div>
          <div class="settings-row">
            <span class="settings-row__label">{{ t('settings.about.platform') }}</span>
            <span class="settings-row__value">{{ appStore.info?.platform ?? '-' }}</span>
          </div>

          <div v-if="appStore.isDesktop" class="settings-actions">
            <el-button plain @click="appStore.quit()">{{ t('settings.actions.quit') }}</el-button>
          </div>
        </section>
      </div>
    </div>
  </AppDialog>
</template>

<style scoped>
.settings-dialog__body {
  display: flex;
  height: 460px;
  min-height: 0;
  border-top: 1px solid var(--theme-hairline);
}

.settings-nav {
  width: 184px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 14px 10px;
  border-right: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface-2);
}

.settings-nav__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border: none;
  border-radius: 9px;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 13px;
  text-align: left;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.settings-nav__item:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.settings-nav__item.is-active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 600;
}

.settings-panel {
  flex: 1;
  min-width: 0;
  padding: 18px 24px 24px;
}

.settings-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.settings-group__title {
  margin: 6px 0 0;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.4px;
  color: var(--theme-text-2);
}

.account-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 14px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.account-avatar {
  width: 52px;
  height: 52px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  overflow: hidden;
  background: var(--theme-accent-gradient);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.account-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.account-meta {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.account-name {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.account-sub {
  margin: 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
}

.settings-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 11px 16px;
  border-radius: 11px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.settings-row__label {
  flex-shrink: 0;
  min-width: 72px;
  font-size: 13px;
  color: var(--theme-text-2);
}

.settings-row__value {
  flex: 1;
  min-width: 0;
  font-size: 12px;
  color: var(--theme-text-3);
  text-align: right;
  word-break: break-all;
}

.storage-btn {
  flex-shrink: 0;
}

.settings-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 6px;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.theme-card {
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-sm);
  padding: 10px;
  background: var(--theme-bg-surface);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 8px;
  transition: all var(--theme-duration-fast) var(--theme-ease);
}

.theme-card:hover {
  border-color: var(--theme-primary);
}

.theme-card.active {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.theme-preview {
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface-2);
}

.theme-card[data-theme='dark'] .theme-preview {
  background: #0f172a;
}

.theme-card[data-theme='game'] .theme-preview {
  background: #14532d;
}

.preview-bar {
  height: 13px;
  background: var(--theme-gradient);
}

.preview-body {
  flex: 1;
}

.theme-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--theme-text-2);
}

.locale-row {
  display: flex;
  gap: 10px;
}

.locale-chip {
  padding: 7px 18px;
  border-radius: var(--theme-radius-pill);
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font-size: 12.5px;
  cursor: pointer;
  transition: all var(--theme-duration-fast) var(--theme-ease);
}

.locale-chip:hover {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
}

.locale-chip.active {
  background: var(--theme-primary);
  border-color: var(--theme-primary);
  color: var(--theme-primary-text);
}

.shortcut-keys {
  display: inline-flex;
  gap: 6px;
}

kbd {
  min-width: 26px;
  padding: 3px 7px;
  border-radius: 6px;
  border: 1px solid var(--theme-border);
  border-bottom-width: 2px;
  background: var(--theme-bg-surface-2);
  font-size: 11px;
  font-family: inherit;
  color: var(--theme-text-2);
  text-align: center;
}

.plugin-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plugin-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.plugin-icon {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: #fff;
  background: var(--theme-accent-gradient);
}

.plugin-body {
  flex: 1;
  min-width: 0;
}

.plugin-name {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.plugin-desc {
  margin: 2px 0 0;
  font-size: 11.5px;
  color: var(--theme-text-3);
}
</style>
