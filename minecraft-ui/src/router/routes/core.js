import { DEFAULT_HOME_PATH } from '../constants';

/**
 * 全局布局：顶部导航 Header + <router-view> 内容区
 * 所有主站页面都作为 Root 路由的子路由渲染在布局中
 */
const RootLayout = () => import('@/components/NavigationComponent/Header.vue');

/** 全局 404 兜底路由，匹配所有未注册路径 */
const fallbackNotFoundRoute = {
  path: '/:pathMatch(.*)*',
  name: 'NotFound',
  component: () => import('@/views/Errors/404.vue'),
  meta: { title: '页面不存在', requiresAuth: false },
};

/**
 * 根路由
 * 使用全局布局作为所有主站页面的父级容器，子级无需重复引入 Header。
 * 此路由必须存在，子路由由 routes/index.js 合并业务模块后挂载
 */
const rootRoute = {
  path: '/',
  name: 'Root',
  component: RootLayout,
  redirect: DEFAULT_HOME_PATH,
  meta: { title: 'Root' },
  children: [],
};

/** 认证相关路由（登录/注册/找回密码） */
const authRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginName.vue'),
    meta: { title: '登录/注册', requiresAuth: false },
    children: [
      {
        path: 'enrolfirst',
        name: 'EnrolFirst',
        component: () => import('@/views/login/components/EnrolFirst.vue'),
        meta: { title: '注册', requiresAuth: false },
      },
      {
        path: 'emaillogin',
        name: 'EmailLogin',
        component: () => import('@/views/login/components/EmailLogin.vue'),
        meta: { title: '邮箱登录', requiresAuth: false },
      },
      {
        path: 'ForgotPassword',
        name: 'ForgotPassword',
        component: () => import('@/views/login/components/ForgotPassword.vue'),
        meta: { title: '忘记密码', requiresAuth: false },
      },
      {
        path: 'Fanginter',
        name: 'Fanginter',
        component: () =>
          import('@/views/login/components/FanginternationalContainer.vue'),
        meta: { title: '国际登录', requiresAuth: false },
      },
    ],
  },
];

/** 错误页组件映射（静态引用，避免动态模板路径无法被 Vite 分析） */
const errorPageComponents = {
  401: () => import('@/views/Errors/401.vue'),
  403: () => import('@/views/Errors/403.vue'),
  404: () => import('@/views/Errors/404.vue'),
  500: () => import('@/views/Errors/500.vue'),
  502: () => import('@/views/Errors/502.vue'),
  503: () => import('@/views/Errors/503.vue'),
  504: () => import('@/views/Errors/504.vue'),
};

const errorPageTitles = {
  401: '未授权',
  403: '禁止访问',
  404: '页面不存在',
  500: '服务器错误',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时',
};

/** 错误页路由，按 HTTP 状态码统一生成 */
const errorRoutes = Object.keys(errorPageComponents).map((code) => ({
  path: `/${code}`,
  name: `Error${code}`,
  component: errorPageComponents[code],
  meta: { title: errorPageTitles[code], requiresAuth: false },
}));

/**
 * 收集路由树中所有路由 name，用于守卫判断核心路由
 * @param {Array} routeList 路由列表
 * @returns {Array<string>} 路由 name 列表
 */
function traverseRouteNames(routeList) {
  const names = [];
  const walk = (list) => {
    for (const route of list) {
      if (route.name) {
        names.push(route.name);
      }
      if (route.children?.length) {
        walk(route.children);
      }
    }
  };
  walk(routeList);
  return names;
}

/** 核心路由列表：这些路由不需要进入权限拦截（登录页、错误页等） */
const coreRoutes = [rootRoute, ...authRoutes, ...errorRoutes];

/** 核心路由 name 列表，守卫中对命中路由直接放行 */
const coreRouteNames = traverseRouteNames([
  ...coreRoutes,
  fallbackNotFoundRoute,
]);

export {
  authRoutes,
  coreRouteNames,
  coreRoutes,
  errorRoutes,
  fallbackNotFoundRoute,
  rootRoute,
};
