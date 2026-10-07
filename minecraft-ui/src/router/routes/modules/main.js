/**
 * 主站页面路由模块
 * 全部渲染在 Root 布局（Header + <router-view>）中。
 *
 * meta 约定：
 * - title: 页面标题（用于 document.title）
 * - requiresAuth: 是否需要登录访问
 * - nav: 'main' | 'more'，声明后自动出现在顶部导航 / 更多下拉菜单
 * - navLabel: 导航菜单显示文案（缺省时使用 title）
 * - order: 导航菜单排序，越小越靠前
 */
export default [
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/index/index.vue'),
    meta: { title: '首页', requiresAuth: false, nav: 'main', order: 1 },
  },
  {
    path: '/worldtravel',
    name: 'WorldTravel',
    component: () => import('@/views/WorldTravel/index.vue'),
    meta: {
      title: '世界旅行',
      requiresAuth: true,
      nav: 'main',
      navLabel: '目的地',
      order: 2,
    },
  },
  {
    path: '/scenicspot',
    name: 'ScenicSpot',
    component: () => import('@/views/Scenicspot/index.vue'),
    meta: { title: '景点', requiresAuth: false, nav: 'main', order: 3 },
  },
  {
    path: '/hotel',
    name: 'Hotel',
    component: () => import('@/views/Hotel/index.vue'),
    meta: { title: '酒店', requiresAuth: false, nav: 'main', order: 4 },
  },
  {
    path: '/food',
    name: 'Food',
    component: () => import('@/views/Food/index.vue'),
    meta: { title: '美食', requiresAuth: false, nav: 'main', order: 5 },
  },
  {
    path: '/souvenir',
    name: 'Souvenir',
    component: () => import('@/views/Souvenir/index.vue'),
    meta: {
      title: '纪念品',
      requiresAuth: false,
      nav: 'main',
      navLabel: '小物件',
      order: 6,
    },
  },
  {
    path: '/strategy',
    name: 'Strategy',
    component: () => import('@/views/Strategy/index.vue'),
    meta: {
      title: '攻略',
      requiresAuth: false,
      nav: 'main',
      navLabel: '攻略群',
      order: 7,
    },
  },
  {
    path: '/cards',
    name: 'Cards',
    component: () => import('@/views/Cards/index.vue'),
    meta: { title: '旅行卡片', requiresAuth: false, nav: 'more', order: 2 },
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('@/views/Community/index.vue'),
    meta: {
      title: '社区',
      requiresAuth: true,
      nav: 'more',
      navLabel: '旅行社区',
      order: 3,
    },
    children: [
      {
        path: '',
        name: 'CommunityMain',
        component: () => import('@/views/Community/components/main.vue'),
        meta: { title: '社区', requiresAuth: true },
      },
      {
        path: 'detail/:id',
        name: 'CommunityDetail',
        component: () => import('@/views/Community/components/details.vue'),
        meta: { title: '帖子详情', requiresAuth: true },
      },
    ],
  },
  {
    path: '/forum',
    name: 'Forum',
    component: () => import('@/views/Forum/index.vue'),
    meta: { title: '论坛', requiresAuth: true },
  },
  {
    path: '/ticket',
    name: 'Ticket',
    component: () => import('@/views/Ticket/index.vue'),
    meta: {
      title: '机票',
      requiresAuth: false,
      nav: 'more',
      navLabel: '购票服务',
      order: 4,
    },
  },
  {
    path: '/travel',
    name: 'Travel',
    component: () => import('@/views/Travel/index.vue'),
    meta: {
      title: '旅行计划',
      requiresAuth: false,
      nav: 'more',
      navLabel: '出行计划',
      order: 5,
    },
  },
  {
    path: '/announcement',
    name: 'AnnouncementList',
    component: () => import('@/views/Announcement/index.vue'),
    meta: { title: '系统公告', requiresAuth: false },
  },
  {
    path: '/announcement/:id',
    name: 'AnnouncementDetail',
    component: () => import('@/views/Announcement/Detail.vue'),
    meta: { title: '公告详情', requiresAuth: false },
  },
];
