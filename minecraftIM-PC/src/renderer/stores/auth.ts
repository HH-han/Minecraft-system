import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { login as loginApi, register as registerApi } from '@/api/auth';
import { getUserInfo, logout as logoutApi } from '@/api/user';
import { wsService } from '@/api/ws';
import { tokenStorageKey, userInfoStorageKey } from '@/config';
import type { LoginParams, LoginResult, RegisterParams, UserInfo } from '@/types/im';

function readStoredUser(): UserInfo | null {
  try {
    const raw = localStorage.getItem(userInfoStorageKey);
    return raw ? (JSON.parse(raw) as UserInfo) : null;
  } catch {
    return null;
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(tokenStorageKey));
  const user = ref<UserInfo | null>(readStoredUser());
  const loading = ref(false);
  const error = ref<string | null>(null);

  const authenticated = computed(() => Boolean(token.value));
  const userId = computed<number | null>(() => {
    const u = user.value;
    if (!u) return null;
    const id = u.userId ?? u.id;
    return typeof id === 'number' ? id : null;
  });

  function applyLoginResult(data: LoginResult): void {
    token.value = data.token;
    localStorage.setItem(tokenStorageKey, data.token);
    const { token: _ignored, ...profile } = data;
    user.value = { ...profile };
    localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
  }

  async function login(payload: LoginParams): Promise<boolean> {
    loading.value = true;
    error.value = null;
    try {
      const res = await loginApi(payload);
      applyLoginResult(res.data);
      connectWs();
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err);
      return false;
    } finally {
      loading.value = false;
    }
  }

  async function register(payload: RegisterParams): Promise<boolean> {
    loading.value = true;
    error.value = null;
    try {
      const res = await registerApi(payload);
      applyLoginResult(res.data);
      connectWs();
      return true;
    } catch (err) {
      error.value = err instanceof Error ? err.message : String(err);
      return false;
    } finally {
      loading.value = false;
    }
  }

  /** 启动时用本地 token 拉取最新用户信息；失败（token 失效）则清空登录态 */
  async function restore(): Promise<void> {
    if (!token.value) return;
    try {
      const res = await getUserInfo();
      if (res.data) {
        user.value = { ...user.value, ...res.data };
        localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
      }
      connectWs();
    } catch {
      clearSession();
    }
  }

  function connectWs(): void {
    const id = userId.value;
    if (id) wsService.connect(id);
  }

  /** 重新拉取用户资料（个人资料保存/头像更新后调用） */
  async function refreshUser(): Promise<void> {
    if (!token.value) return;
    try {
      const res = await getUserInfo();
      if (res.data) {
        user.value = { ...user.value, ...res.data };
        localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
      }
    } catch {
      /* 保持现有资料 */
    }
  }

  /** 本地合并资料字段（无需等待接口） */
  function patchUser(patch: Partial<UserInfo>): void {
    if (!user.value) return;
    user.value = { ...user.value, ...patch };
    localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
  }

  function clearSession(): void {
    token.value = null;
    user.value = null;
    localStorage.removeItem(tokenStorageKey);
    localStorage.removeItem(userInfoStorageKey);
    wsService.disconnect();
  }

  async function logout(): Promise<void> {
    try {
      await logoutApi();
    } catch {
      /* 后端不可达也允许本地退出 */
    }
    clearSession();
  }

  // 请求层检测到 401 时广播的登出事件
  window.addEventListener('app:unauthorized', () => {
    clearSession();
  });

  return {
    token,
    user,
    userId,
    authenticated,
    loading,
    error,
    login,
    register,
    restore,
    refreshUser,
    patchUser,
    logout
  };
});
