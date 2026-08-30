import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:square-user-round',
      keepAlive: true,
      order: 1000,
      title: $t('user.title'),   
    },
    name: 'User',
    path: '/user',
    children: [
      {
        meta: {
          icon: 'lucide:users',
          title: $t('user.user_list'),
        },
        name: 'user_list',
        path: 'user_list',
        component: () => import('#/views/user_management/userList.vue'),
      },
      {
        meta: {
          icon: 'lucide:user',
          title: $t('user.user_info'),
        },
        name: 'user_info',
        path: 'ant-design-next',
        component: () => import('#/views/user_management/user.vue'),
      },
      {
        meta: {
          icon: 'lucide:receipt',
          title: $t('user.order'),
        },
        name: 'order',
        path: 'order',
        component: () => import('#/views/user_management/index.vue'),
      },
      {
        meta: {
          icon: 'lucide:log-in',
          title: $t('user.login'),
        },
        name: 'login',
        path: 'login',
        component: () => import('#/views/user_management/index.vue'),
      }
    ],
  },
];

export default routes;
