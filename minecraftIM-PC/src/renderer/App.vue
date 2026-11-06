<script setup lang="ts">
import { onMounted } from 'vue';
import { useAppStore } from '@/stores/app';
import { useAuthStore } from '@/stores/auth';
import AuthView from '@/views/auth/AuthView.vue';
import ImLayout from '@/views/im/ImLayout.vue';

const appStore = useAppStore();
const authStore = useAuthStore();

onMounted(async () => {
  await appStore.loadAppInfo();
  // 有本地 token 时向后端校验并拉取最新用户信息
  await authStore.restore();
});
</script>

<template>
  <div class="app-shell theme-transition">
    <ImLayout v-if="authStore.authenticated" />
    <AuthView v-else />
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style>
