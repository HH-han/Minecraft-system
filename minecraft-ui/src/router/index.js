import {
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';

import { CHUNK_RELOAD_KEY } from './constants';
import { createRouterGuard } from './guard';
import { routes } from './routes';

/**
 * 创建 vue-router 实例
 * - history 模式可通过环境变量 VITE_ROUTER_HISTORY=hash 切换为 hash 模式
 * - scrollBehavior：前进后退恢复原滚动位置；锚点平滑滚动；其余回到顶部
 */
const router = createRouter({
  history:
    import.meta.env.VITE_ROUTER_HISTORY === 'hash'
      ? createWebHashHistory(import.meta.env.BASE_URL)
      : createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: (to, _from, savedPosition) => {
    if (savedPosition) {
      return savedPosition;
    }
    return to.hash
      ? { behavior: 'smooth', el: to.hash }
      : { left: 0, top: 0 };
  },
});

/**
 * 路由错误处理：
 * 异步页面 chunk 加载失败（常见于发版后旧资源失效）时自动刷新一次页面；
 * 通过 sessionStorage 标记防止刷新死循环，成功导航后由守卫清除标记
 */
router.onError((error, to) => {
  const message = error?.message || '';
  const isChunkLoadError =
    /dynamically imported module|Failed to fetch|Importing a module script failed|error loading/i.test(
      message,
    );
  if (isChunkLoadError && !sessionStorage.getItem(CHUNK_RELOAD_KEY)) {
    sessionStorage.setItem(CHUNK_RELOAD_KEY, '1');
    window.location.assign(to?.fullPath || '/');
    return;
  }
  console.error('[router] 路由导航异常:', error);
});

/** 重置路由表为初始静态路由（如退出登录后调用） */
const resetRoutes = () => {
  for (const route of router.getRoutes()) {
    if (route.name) {
      router.removeRoute(route.name);
    }
  }
  for (const route of routes) {
    router.addRoute(route);
  }
};

// 创建路由守卫
createRouterGuard(router);

export { resetRoutes, router };
export default router;
