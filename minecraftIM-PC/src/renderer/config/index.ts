/**
 * 后端接入配置。
 * 默认直连本地 Minecraft Spring Boot 服务（server.port=8080），
 * 可通过环境变量覆盖：
 *   VITE_API_BASE_URL  —— HTTP 接口根（含 /api 后缀）
 *   VITE_WS_BASE_URL   —— WebSocket 根
 */
const env = import.meta.env;

function normalize(base: string): string {
  return base.endsWith('/') ? base.slice(0, -1) : base;
}

export const apiBaseUrl = normalize(env.VITE_API_BASE_URL ?? 'http://localhost:8080/api');

export const wsBaseUrl = normalize(
  env.VITE_WS_BASE_URL ?? apiBaseUrl.replace(/^http/, 'ws').replace(/\/api$/, '')
);

export const tokenStorageKey = 'token';
export const userInfoStorageKey = 'userInfo';
