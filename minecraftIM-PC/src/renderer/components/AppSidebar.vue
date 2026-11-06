<script setup lang="ts">
import { computed } from 'vue';
import type { Component } from 'vue';
import { useI18n } from 'vue-i18n';
import { ChatDotRound, Setting } from '@element-plus/icons-vue';
import { useAppStore } from '@/stores/app';
import type { AppView } from '@/types/nav';

const props = defineProps<{ active: AppView }>();
const emit = defineEmits<{ (event: 'select', view: AppView): void }>();

const { t } = useI18n();
const appStore = useAppStore();

const navItems: Array<{ id: AppView; labelKey: string; icon: Component }> = [
  { id: 'home', labelKey: 'nav.home', icon: ChatDotRound },
  { id: 'settings', labelKey: 'nav.settings', icon: Setting }
];

const statusLabel = computed(() =>
  appStore.isDesktop ? t('status.desktop') : t('status.browser')
);
</script>

<template>
  <aside class="app-sidebar">
    <div class="brand">
      <div class="brand-logo">MC</div>
      <div class="brand-text">
        <strong>{{ t('app.name') }}</strong>
        <span>{{ t('app.tagline') }}</span>
      </div>
    </div>

    <nav class="nav">
      <button
        v-for="item in navItems"
        :key="item.id"
        type="button"
        class="nav-item theme-transition"
        :class="{ 'is-active': props.active === item.id }"
        @click="emit('select', item.id)"
      >
        <el-icon><component :is="item.icon" /></el-icon>
        <span>{{ t(item.labelKey) }}</span>
      </button>
    </nav>

    <div class="sidebar-footer">
      <span class="status-dot" :class="{ 'is-desktop': appStore.isDesktop }" />
      <span class="status-text">{{ statusLabel }}</span>
    </div>
  </aside>
</template>

<style scoped>
.app-sidebar {
  display: flex;
  width: 248px;
  flex-shrink: 0;
  flex-direction: column;
  border-right: 1px solid var(--theme-hairline);
  background-color: var(--theme-bg-surface);
  transition: background-color var(--theme-duration-slow) var(--theme-ease);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 22px 20px;
}

.brand-logo {
  display: grid;
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  place-items: center;
  border-radius: var(--theme-radius-md);
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 0.5px;
  box-shadow: var(--theme-shadow-sm);
}

.brand-text {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 2px;
}

.brand-text strong {
  font-size: 14px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.brand-text span {
  overflow: hidden;
  font-size: 11px;
  color: var(--theme-text-3);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 6px;
  padding: 8px 12px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  border: 1px solid transparent;
  border-radius: var(--theme-radius-md);
  background: transparent;
  color: var(--theme-text-2);
  font: inherit;
  font-size: 14px;
  cursor: pointer;
  text-align: left;
}

.nav-item:hover {
  background-color: var(--theme-bg-hover);
}

.nav-item.is-active {
  border-color: color-mix(in srgb, var(--theme-primary) 24%, transparent);
  background-color: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 600;
}

.nav-item:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.sidebar-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-top: 1px solid var(--theme-hairline);
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background-color: var(--theme-warning);
}

.status-dot.is-desktop {
  background-color: var(--theme-success);
}

.status-text {
  font-size: 12px;
  color: var(--theme-text-3);
}
</style>
