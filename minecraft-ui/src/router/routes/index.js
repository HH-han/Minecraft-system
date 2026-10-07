import {
  authRoutes,
  coreRouteNames,
  errorRoutes,
  fallbackNotFoundRoute,
  rootRoute,
} from './core';
import contentRoutes from './modules/content';
import mainRoutes from './modules/main';
import miscRoutes from './modules/misc';
import userRoutes from './modules/user';

/** 渲染在 Root 布局中的主站页面路由 */
const layoutRoutes = [...mainRoutes];

/**
 * 完整路由表
 * 由根布局路由、认证路由、错误页路由、各业务模块路由和 404 兜底路由组成
 */
const routes = [
  { ...rootRoute, children: [...layoutRoutes] },
  ...authRoutes,
  ...errorRoutes,
  ...contentRoutes,
  ...userRoutes,
  ...miscRoutes,
  fallbackNotFoundRoute,
];

/**
 * 拼接完整路由路径（子路由可能是相对路径）
 * @param {string} parentPath 父级完整路径
 * @param {string} path 当前路由路径
 * @returns {string} 完整路径
 */
function resolveFullPath(parentPath, path) {
  if (path.startsWith('/')) {
    return path;
  }
  return `${parentPath.replace(/\/$/, '')}/${path}`;
}

/**
 * 从路由表收集导航菜单项（meta.nav: 'main' | 'more'）
 * @param {Array} routeList 路由列表
 * @param {string} parentPath 父级完整路径
 * @returns {Array<{ path: string, label: string, group: string, order: number }>}
 */
function collectNavMenus(routeList, parentPath = '') {
  const menus = [];
  const walk = (list, parent) => {
    for (const route of list) {
      const fullPath = resolveFullPath(parent, route.path);
      const { meta } = route;
      if (meta?.nav) {
        menus.push({
          path: fullPath,
          label: meta.navLabel || meta.title,
          group: meta.nav,
          order: meta.order ?? 99,
        });
      }
      if (route.children?.length) {
        walk(route.children, fullPath);
      }
    }
  };
  walk(routeList, parentPath);
  return menus;
};

const byOrder = (a, b) => a.order - b.order;

const navMenus = collectNavMenus(routes);

/** 顶部主导航菜单项（从路由 meta 派生，路由即导航的唯一数据源） */
const mainNavMenus = navMenus
  .filter((menu) => menu.group === 'main')
  .sort(byOrder);

/** 「更多」下拉菜单项 */
const moreNavMenus = navMenus
  .filter((menu) => menu.group === 'more')
  .sort(byOrder);

export { coreRouteNames, mainNavMenus, moreNavMenus, routes };
