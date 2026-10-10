/**
 * 用户中心相关路由模块
 * 独立全屏页面，不渲染 Root 布局
 */
export default [
  {
    path: '/personalcenter',
    name: 'PersonalCenter',
    component: () => import('@/views/PersonalCenter/index.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/accountsettings',
    name: 'AccountSettings',
    component: () =>
      import('@/views/PersonalCenter/components/accountsettings.vue'),
    meta: { title: '账户设置', requiresAuth: true },
  },
  {
    path: '/privatecommunity',
    name: 'PrivateCommunity',
    component: () => import('@/views/PersonalCenter/Community/index.vue'),
    meta: { title: '私有社区', requiresAuth: true },
  },
  {
    path: '/points',
    name: 'Points',
    component: () => import('@/views/Points/index.vue'),
    meta: { title: '积分商城', requiresAuth: true },
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/views/ImChat/index.vue'),
    meta: { title: '聊天', requiresAuth: true },
  },
  {
    path: '/payment',
    name: 'Payment',
    component: () => import('@/views/payment/index.vue'),
    meta: { title: '支付页面', requiresAuth: false },
  },
  {
    path: '/predetermined',
    name: 'Predetermined',
    component: () => import('@/views/Predetermined/index.vue'),
    meta: { title: '预订服务', requiresAuth: false },
  },
  {
    path: '/maps',
    name: 'Maps',
    component: () => import('@/views/Maps/index.vue'),
    meta: {
      title: '地图',
      requiresAuth: true,
      nav: 'more',
      navLabel: '世界地图',
      navLabelKey: 'header.nav.maps',
      order: 6,
    },
  },
  {
    path: '/officialwebsite',
    name: 'OfficialWebsite',
    component: () => import('@/views/OfficialWebsite/index.vue'),
    meta: { title: '官网首页', requiresAuth: true },
  },
];
