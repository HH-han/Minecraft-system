/**
 * 测试页面路由模块
 */
export default [
  {
    path: '/test',
    name: 'Test',
    component: () => import('@/views/Test/index.vue'),
    meta: {
      title: '测试',
      requiresAuth: false,
      nav: 'more',
      navLabel: '测试页面',
      navLabelKey: 'header.nav.test',
      order: 7,
    },
  },
];
