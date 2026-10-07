import {
  getToken,
  removeToken,
  removeUserInfo,
  removeUsername,
} from '@/utils/storage';

import {
  APP_TITLE,
  CHUNK_RELOAD_KEY,
  DEFAULT_HOME_PATH,
  LOGIN_PATH,
  TOKEN_TTL,
} from './constants';
import { coreRouteNames } from './routes';

/**
 * 校验本地登录状态是否有效
 * token 缺失、为匿名标识或超过有效期（24h）时视为未登录，并清理失效凭证
 * @returns {boolean}
 */
function hasValidToken() {
  const token = getToken();
  if (!token || token === 'anonymousUser') {
    return false;
  }
  const timestamp = Number(localStorage.getItem('tokenTimestamp'));
  if (timestamp && Date.now() - timestamp > TOKEN_TTL) {
    removeToken();
    removeUsername();
    removeUserInfo();
    localStorage.removeItem('tokenTimestamp');
    return false;
  }
  return true;
}

/**
 * 通用守卫：页面加载记录与文档标题
 * @param {import('vue-router').Router} router
 */
function setupCommonGuard(router) {
  // 记录已经加载过的页面，供页面切换动画等场景判断
  const loadedPaths = new Set();

  router.beforeEach((to) => {
    to.meta.loaded = loadedPaths.has(to.path);
    return true;
  });

  router.afterEach((to) => {
    loadedPaths.add(to.path);

    // 拼接文档标题：页面标题 · 站点名
    document.title = to.meta.title
      ? `${to.meta.title} · ${APP_TITLE}`
      : APP_TITLE;

    // 导航成功后清除 chunk 加载失败的刷新标记，保证下次失败仍可自动刷新
    sessionStorage.removeItem(CHUNK_RELOAD_KEY);
  });
}

/**
 * 访问权限守卫：
 * - 核心路由（登录/错误页）直接放行；已登录访问登录页时跳回来源页或首页
 * - meta.requiresAuth 的路由校验登录态，未登录跳登录页并携带 redirect 回跳参数
 * @param {import('vue-router').Router} router
 */
function setupAccessGuard(router) {
  router.beforeEach((to) => {
    const isLoggedIn = hasValidToken();

    // 核心路由不进入权限拦截
    if (coreRouteNames.includes(to.name)) {
      if (to.path === LOGIN_PATH && isLoggedIn) {
        const redirect = to.query?.redirect;
        return redirect
          ? decodeURIComponent(redirect)
          : DEFAULT_HOME_PATH;
      }
      return true;
    }

    // 需要登录的页面对未登录用户跳转登录页
    if (to.meta.requiresAuth && !isLoggedIn) {
      return {
        path: LOGIN_PATH,
        query:
          to.fullPath === DEFAULT_HOME_PATH
            ? {}
            : { redirect: encodeURIComponent(to.fullPath) },
        replace: true,
      };
    }

    return true;
  });
}

/**
 * 创建路由守卫
 * @param {import('vue-router').Router} router
 */
function createRouterGuard(router) {
  /** 通用 */
  setupCommonGuard(router);
  /** 访问权限 */
  setupAccessGuard(router);
}

export { createRouterGuard };
