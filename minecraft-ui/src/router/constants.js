/**
 * 路由系统全局常量
 */

/** 登录页路径 */
export const LOGIN_PATH = '/login';

/** 默认首页路径（根路径 / 会重定向到该路径） */
export const DEFAULT_HOME_PATH = '/home';

/** 站点名称，用于拼接文档标题 */
export const APP_TITLE = '博览旅行';

/** 本地 token 有效期（毫秒），与登录逻辑保持一致的 24 小时 */
export const TOKEN_TTL = 24 * 60 * 60 * 1000;

/** 异步 chunk 加载失败后的自动刷新标记（sessionStorage key，防止刷新死循环） */
export const CHUNK_RELOAD_KEY = 'router:chunk-load-reloaded';
