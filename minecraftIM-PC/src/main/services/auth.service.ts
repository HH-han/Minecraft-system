import type { AuthState, LoginRequest, LoginResponse } from '../../shared/types/ipc';

/**
 * 认证服务：当前为本地占位实现，后续接入后端时只替换本文件，
 * IPC handler 与渲染进程契约保持不变。
 */
let session: AuthState = {
  authenticated: false,
  user: null
};

export function getAuthState(): AuthState {
  return session;
}

export async function login(payload: LoginRequest): Promise<LoginResponse> {
  const username = payload.username?.trim() ?? '';
  const password = payload.password ?? '';

  if (!username || !password) {
    throw new Error('用户名和密码不能为空');
  }

  const user = {
    id: `local-${username}`,
    username,
    nickname: username,
    role: 'member' as const
  };

  session = { authenticated: true, user };

  return {
    user,
    token: `local-token-${Date.now()}`
  };
}

export function logout(): AuthState {
  session = { authenticated: false, user: null };
  return session;
}
