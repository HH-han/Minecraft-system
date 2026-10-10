/**
 * 内容站点路由模块（关于我们 / 网站介绍）
 * 独立全屏页面，不渲染 Root 布局
 */
export default [
  {
    path: '/aboutwebsite',
    name: 'AboutWebsite',
    component: () => import('@/views/Aboutwebsite/index.vue'),
    meta: { title: '关于', requiresAuth: false },
    children: [
      {
        path: '/aboutweb',
        name: 'AboutWeb',
        component: () => import('@/views/Aboutwebsite/components/Aboutweb.vue'),
        meta: {
          title: '关于我们',
          requiresAuth: false,
          nav: 'more',
          navLabelKey: 'header.nav.about',
          order: 1,
        },
      },
      {
        path: '/websiteintroduction',
        name: 'WebsiteIntroduction',
        component: () =>
          import('@/views/Aboutwebsite/components/WebsiteIntroduction.vue'),
        meta: { title: '网站介绍', requiresAuth: false },
      },
    ],
  },
];
