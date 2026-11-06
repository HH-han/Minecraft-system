<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { Sunny, Moon, Cpu, SwitchButton } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/app';
import { useThemeStore, type ThemeId } from '@/stores/theme';
import { SUPPORTED_LOCALES, setLocale, type AppLocale } from '@/locales';

const { t, locale } = useI18n();
const appStore = useAppStore();
const themeStore = useThemeStore();

const themeIcons: Record<ThemeId, typeof Sunny> = {
  light: Sunny,
  dark: Moon,
  game: Cpu
};

const currentLocale = computed(() => locale.value as AppLocale);

async function changeLocale(next: AppLocale): Promise<void> {
  await setLocale(next);
}
</script>

<template>
  <div class="settings-view">
    <header class="settings-header">
      <h2>{{ t('settings.title') }}</h2>
      <p>{{ t('settings.description') }}</p>
    </header>

    <section class="settings-card">
      <h3>{{ t('settings.theme.title') }}</h3>
      <p class="card-desc">{{ t('settings.theme.description') }}</p>
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
            <el-icon :size="15"><component :is="themeIcons[option.id]" /></el-icon>
            {{ t(option.labelKey) }}
          </span>
        </button>
      </div>
    </section>

    <section class="settings-card">
      <h3>{{ t('settings.language.title') }}</h3>
      <p class="card-desc">{{ t('settings.language.description') }}</p>
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
    </section>

    <section class="settings-card">
      <h3>{{ t('settings.storage.title') }}</h3>
      <div class="kv-row">
        <span class="kv-key">{{ t('settings.storage.dataDir') }}</span>
        <span class="kv-value">{{ appStore.info?.dataDir ?? '-' }}</span>
      </div>
      <div class="kv-row">
        <span class="kv-key">{{ t('settings.storage.logDir') }}</span>
        <span class="kv-value">{{ appStore.info?.logDir ?? '-' }}</span>
      </div>
    </section>

    <section class="settings-card">
      <h3>{{ t('settings.about.title') }}</h3>
      <div class="kv-row">
        <span class="kv-key">{{ t('settings.about.framework') }}</span>
        <span class="kv-value">{{ t('settings.about.frameworkValue') }}</span>
      </div>
      <div class="kv-row">
        <span class="kv-key">{{ t('settings.about.renderer') }}</span>
        <span class="kv-value">{{ t('settings.about.rendererValue') }}</span>
      </div>
      <div class="kv-row">
        <span class="kv-key">{{ t('home.runtime.version') }}</span>
        <span class="kv-value">{{ appStore.info?.version ?? '-' }}</span>
      </div>
    </section>

    <section v-if="appStore.isDesktop" class="settings-card danger-card">
      <el-button type="danger" plain :icon="SwitchButton" @click="appStore.quit()">
        {{ t('settings.actions.quit') }}
      </el-button>
    </section>
  </div>
</template>

<style scoped>
.settings-view {
  flex: 1;
  overflow-y: auto;
  padding: 28px 32px 40px;
  background: var(--theme-bg-app);
}

.settings-header h2 {
  margin: 0;
  font-size: 22px;
  color: var(--theme-text-1);
}

.settings-header p {
  margin: 6px 0 20px;
  font-size: 13px;
  color: var(--theme-text-3);
}

.settings-card {
  max-width: 640px;
  margin-bottom: 18px;
  padding: 20px 22px;
  border-radius: var(--theme-radius-md);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-sm);
}

.settings-card h3 {
  margin: 0 0 4px;
  font-size: 15px;
  color: var(--theme-text-1);
}

.card-desc {
  margin: 0 0 14px;
  font-size: 12px;
  color: var(--theme-text-3);
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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
  height: 52px;
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
  height: 14px;
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
  padding: 8px 18px;
  border-radius: var(--theme-radius-pill);
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  font-size: 13px;
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

.kv-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 8px 0;
  font-size: 13px;
}

.kv-key {
  color: var(--theme-text-3);
  flex-shrink: 0;
}

.kv-value {
  color: var(--theme-text-1);
  word-break: break-all;
  text-align: right;
}

.danger-card {
  display: flex;
  justify-content: flex-end;
}
</style>
