import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { invoke, hasNativeBridge } from '@/bridge';
import { IPC_CHANNELS } from '@shared/constants';
import type { AppInfo } from '@shared/types/ipc';

export const useAppStore = defineStore('app', () => {
  const info = ref<AppInfo | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  const isDesktop = computed(() => hasNativeBridge());

  async function loadAppInfo(): Promise<void> {
    loading.value = true;
    error.value = null;
    try {
      info.value = await invoke(IPC_CHANNELS.appGetInfo);
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err);
    } finally {
      loading.value = false;
    }
  }

  async function quit(): Promise<void> {
    await invoke(IPC_CHANNELS.appQuit);
  }

  return {
    info,
    loading,
    error,
    isDesktop,
    loadAppInfo,
    quit
  };
});
