import { defineStore } from 'pinia';
import { ref } from 'vue';
import { invoke, onPush } from '@/bridge';
import { IPC_CHANNELS, IPC_EVENTS } from '@shared/constants';
import type { AuthState, LoginRequest } from '@shared/types/ipc';

export const useAuthStore = defineStore('auth', () => {
  const state = ref<AuthState>({ authenticated: false, user: null });
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function refresh(): Promise<void> {
    try {
      state.value = await invoke(IPC_CHANNELS.authGetState);
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err);
    }
  }

  async function login(payload: LoginRequest): Promise<boolean> {
    loading.value = true;
    error.value = null;
    try {
      const result = await invoke(IPC_CHANNELS.authLogin, payload);
      state.value = { authenticated: true, user: result.user };
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err);
      return false;
    } finally {
      loading.value = false;
    }
  }

  async function logout(): Promise<void> {
    try {
      state.value = await invoke(IPC_CHANNELS.authLogout);
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err);
    }
  }

  // 主进程推送的会话变化（多窗口/后台刷新时保持同步）
  onPush(IPC_EVENTS.sessionChanged, (next) => {
    state.value = next;
  });

  return {
    state,
    loading,
    error,
    refresh,
    login,
    logout
  };
});
