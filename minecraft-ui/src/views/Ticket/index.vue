<template>
  <div class="railway-shell theme-transition">
    <RailwayHeader />
    <main class="railway-main">
      <router-view />
    </main>
    <BottomPage />
  </div>
</template>

<script setup>
import RailwayHeader from './components/RailwayHeader.vue'
import BottomPage from '@/components/DisplayBox/BottomPage.vue'
</script>

<style scoped>
.railway-shell {
  /* 铁路票务局部变量统一别名到全站主题令牌，跟随系统主题切换 */
  --rw-blue: var(--theme-primary);
  --rw-orange: var(--theme-warning);
  --rw-orange-hover: color-mix(in srgb, var(--theme-warning) 82%, #000);
  --rw-bg: var(--theme-bg-app);
  --rw-card: var(--theme-bg-elevated);
  --rw-text: var(--theme-text-1);
  --rw-text-2: var(--theme-text-2);
  --rw-text-3: var(--theme-text-3);
  --rw-border: var(--theme-border);
  --rw-soft: var(--theme-primary-soft);
  --rw-soft-2: var(--theme-bg-surface-2);
  --rw-danger: var(--theme-danger);
  --rw-success: var(--theme-success);
  --rw-shadow: var(--theme-shadow-lg);

  min-height: 100vh;
  display: flex;
  flex-direction: column;
  /* 为全站固定顶部导航留出高度 */
  padding-top: 70px;
  background: var(--rw-bg);
  color: var(--rw-text);
  font-family: 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.railway-main {
  flex: 1;
  width: 100%;
}
</style>
