<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ArrowDown, Cpu, Moon, Sunny } from '@element-plus/icons-vue';
import AppSidebar from '@/components/AppSidebar.vue';
import HomeView from '@/views/HomeView.vue';
import SettingsView from '@/views/SettingsView.vue';
import { SUPPORTED_LOCALES, currentLocale, setLocale, type AppLocale } from '@/locales';
import { useAppStore } from '@/stores/app';
import { useAuthStore } from '@/stores/auth';
import { useThemeStore } from '@/stores/theme';
import type { AppView } from '@/types/nav';

const { t } = useI18n();
const themeStore = useThemeStore();
const appStore = useAppStore();
const authStore = useAuthStore();

const activeView = ref<AppView>('home');

const themeIcon = computed(() => {
  if (themeStore.currentTheme === 'light') return Sunny;
  if (themeStore.currentTheme === 'dark') return Moon;
  return Cpu;
});

const currentLocaleLabel = computed(
  () => SUPPORTED_LOCALES.find((item) => item.value === currentLocale.value)?.label ?? currentLocale.value
);

async function handleLocale(command: string | number | object): Promise<void> {
  if (typeof command !== 'string') return;
  if (SUPPORTED_LOCALES.some((item) => item.value === command)) {
    await setLocale(command as AppLocale);
  }
}

onMounted(() => {
  void appStore.loadAppInfo();
  void authStore.refresh();
});
</script>

<template>
  <div class="app-shell">
    <AppSidebar :active="activeView" @select="activeView = $event" />

    <main class="app-main">
      <header class="app-topbar theme-transition">
        <div class="topbar-title">
          <h1>{{ t(activeView === 'home' ? 'nav.home' : 'nav.settings') }}</h1>
          <span class="theme-text-3">{{ t('app.tagline') }}</span>
        </div>

        <div class="topbar-actions">
          <el-button
            class="icon-button"
            circle
            :title="t('settings.theme.title')"
            @click="themeStore.cycleTheme()"
          >
            <el-icon><component :is="themeIcon" /></el-icon>
          </el-button>

          <el-dropdown trigger="click" @command="handleLocale">
            <el-button text class="locale-button">
              {{ currentLocaleLabel }}
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="item in SUPPORTED_LOCALES"
                  :key="item.value"
                  :command="item.value"
                >
                  {{ item.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <section class="app-content">
        <HomeView v-if="activeView === 'home'" />
        <SettingsView v-else />
      </section>
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  height: 100%;
  background-color: var(--theme-bg-app);
}

.app-main {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
}

.app-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 68px;
  padding: 0 24px;
  border-bottom: 1px solid var(--theme-hairline);
  background-color: var(--theme-bg-glass-strong);
  backdrop-filter: blur(var(--theme-blur));
}

.topbar-title {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.topbar-title h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.topbar-title span {
  font-size: 12px;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-button {
  border-color: var(--theme-border);
  background-color: var(--theme-bg-surface);
  color: var(--theme-text-2);
}

.locale-button {
  color: var(--theme-text-2);
}

.app-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
