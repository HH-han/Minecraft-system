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

/** 读取本地 token，顺带清理历史遗留的非法值（如 "undefined"） */
function readStoredToken(): string | null {
  const raw = localStorage.getItem(tokenStorageKey);
  if (!raw || raw === 'undefined' || raw === 'null') {
    localStorage.removeItem(tokenStorageKey);
    return null;
  }
  return raw;
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(readStoredToken());
  const user = ref<UserInfo | null>(readStoredUser());
  const loading = ref(false);
  const error = ref<string | null>(null);

  // token 与用户信息同时存在才算已登录，避免脏数据进入 IM 后 userId 缺失
  const authenticated = computed(() => Boolean(token.value && user.value));
  const userId = computed<number | null>(() => {
    const u = user.value;
    if (!u) return null;
    const id = u.userId ?? u.id;
    return typeof id === 'number' ? id : null;
  });

  function applyLoginResult(data: LoginResult): void {
    if (!data?.token) throw new Error('登录响应异常，请重试');
    token.value = data.token;
    localStorage.setItem(tokenStorageKey, data.token);
    const { token: _ignored, ...profile } = data;
    user.value = { ...profile };
    localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
  }

  /**
   * 拉取完整用户资料（登录响应只含基础字段）。
   * 仅当 token 未变化时写入，避免切换账号时旧请求的响应覆盖新账号信息。
   */
  async function syncProfile(expectToken: string | null): Promise<void> {
    if (!expectToken) return;
    try {
      const res = await getUserInfo();
      if (token.value !== expectToken) return;
      if (res.data) {
        user.value = { ...user.value, ...res.data };
        localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
      }
    } catch {
      /* 拉取失败时保留登录响应中的资料 */
    }
  }

  async function login(payload: LoginParams): Promise<boolean> {
    loading.value = true;
    error.value = null;
    try {
      const res = await loginApi(payload);
      applyLoginResult(res.data);
      // 登录后重新拉取完整资料，保证切换账号后资料是最新的
      await syncProfile(res.data.token);
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
      // 后端注册接口只返回新账号，需要再登录一次换取 token 与用户信息
      const account = typeof res.data === 'string' && res.data ? res.data : payload.username;
      const loginRes = await loginApi({ account, password: payload.password });
      applyLoginResult(loginRes.data);
      await syncProfile(loginRes.data.token);
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
    const expectToken = token.value;
    if (!expectToken) return;
    try {
      const res = await getUserInfo();
      // 请求期间已切换账号（或已退出），丢弃旧响应
      if (token.value !== expectToken) return;
      if (res.data) {
        user.value = { ...user.value, ...res.data };
        localStorage.setItem(userInfoStorageKey, JSON.stringify(user.value));
      }
      connectWs();
    } catch {
      if (token.value === expectToken) clearSession();
    }
  }

  function connectWs(): void {
    const id = userId.value;
    if (id) wsService.connect(id);
  }

  /** 重新拉取用户资料（个人资料保存/头像更新后调用） */
  async function refreshUser(): Promise<void> {
    await syncProfile(token.value);
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
    // 通知各业务 store 清理上一个账号的数据
    window.dispatchEvent(new CustomEvent('app:session-cleared'));
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
