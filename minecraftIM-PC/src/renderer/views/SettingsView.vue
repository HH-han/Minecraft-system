<script setup lang="ts">
import type { Component } from 'vue';
import { useI18n } from 'vue-i18n';
import { Cpu, Moon, Sunny } from '@element-plus/icons-vue';
import { SUPPORTED_LOCALES, currentLocale, setLocale, type AppLocale } from '@/locales';
import { useAppStore } from '@/stores/app';
import { useThemeStore } from '@/stores/theme';
import type { ThemeId } from '@/stores/theme';

const { t } = useI18n();
const appStore = useAppStore();
const themeStore = useThemeStore();

const themeIcons: Record<ThemeId, Component> = {
  light: Sunny,
  dark: Moon,
  game: Cpu
};

async function changeLocale(lang: AppLocale): Promise<void> {
  await setLocale(lang);
}
</script>

<template>
  <div class="settings">
    <section class="theme-card panel">
      <h3>{{ t('settings.theme.title') }}</h3>
      <p class="panel-desc theme-text-3">{{ t('settings.theme.description') }}</p>

      <div class="option-grid">
        <button
          v-for="option in themeStore.options"
          :key="option.id"
          type="button"
          class="option-card theme-transition"
          :class="{ 'is-active': themeStore.currentTheme === option.id }"
          @click="themeStore.setTheme(option.id)"
        >
          <el-icon class="option-icon"><component :is="themeIcons[option.id]" /></el-icon>
          <span>{{ t(option.labelKey) }}</span>
        </button>
      </div>
    </section>

    <section class="theme-card panel">
      <h3>{{ t('settings.language.title') }}</h3>
      <p class="panel-desc theme-text-3">{{ t('settings.language.description') }}</p>

      <div class="option-grid option-grid--compact">
        <button
          v-for="item in SUPPORTED_LOCALES"
          :key="item.value"
          type="button"
          class="option-card theme-transition"
          :class="{ 'is-active': currentLocale === item.value }"
          @click="changeLocale(item.value)"
        >
          <span>{{ item.label }}</span>
        </button>
      </div>
    </section>

    <section class="theme-card panel">
      <h3>{{ t('settings.storage.title') }}</h3>
      <dl class="info-list">
        <div>
          <dt>{{ t('settings.storage.dataDir') }}</dt>
          <dd>{{ appStore.info?.dataDir ?? '—' }}</dd>
        </div>
        <div>
          <dt>{{ t('settings.storage.logDir') }}</dt>
          <dd>{{ appStore.info?.logDir ?? '—' }}</dd>
        </div>
      </dl>
    </section>

    <section class="theme-card panel">
      <h3>{{ t('settings.about.title') }}</h3>
      <dl class="info-list">
        <div>
          <dt>{{ t('settings.about.framework') }}</dt>
          <dd>{{ t('settings.about.frameworkValue') }}</dd>
        </div>
        <div>
          <dt>{{ t('settings.about.renderer') }}</dt>
          <dd>{{ t('settings.about.rendererValue') }}</dd>
        </div>
      </dl>

      <el-button type="danger" plain @click="appStore.quit()">
        {{ t('settings.actions.quit') }}
      </el-button>
    </section>
  </div>
</template>

<style scoped>
.settings {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 880px;
}

.panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 22px 24px;
}

.panel h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.panel-desc {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.6;
}

.option-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.option-grid--compact {
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.option-card {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 16px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
  background-color: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font: inherit;
  font-size: 13px;
  cursor: pointer;
}

.option-card:hover {
  background-color: var(--theme-bg-hover);
}

.option-card.is-active {
  border-color: color-mix(in srgb, var(--theme-primary) 42%, transparent);
  background-color: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 600;
}

.option-card:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.option-icon {
  font-size: 16px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 0;
}

.info-list > div {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
}

.info-list dt {
  flex-shrink: 0;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.info-list dd {
  margin: 0;
  overflow: hidden;
  font-size: 12.5px;
  color: var(--theme-text-1);
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
