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

/** 后端站点根（用于把相对资源路径补全为可直接访问的地址） */
export const apiOrigin = apiBaseUrl.replace(/\/api$/, '');

export function resolveMediaUrl(url?: string | null): string | undefined {
  if (!url) return undefined;
  if (/^(http|https|data|blob):/.test(url)) return url;
  if (url.startsWith('//')) return `http:${url}`;
  return `${apiOrigin}${url.startsWith('/') ? '' : '/'}${url}`;
}

export const wsBaseUrl = normalize(
  env.VITE_WS_BASE_URL ?? apiBaseUrl.replace(/^http/, 'ws').replace(/\/api$/, '')
);

export const tokenStorageKey = 'token';
export const userInfoStorageKey = 'userInfo';
