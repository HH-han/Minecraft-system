import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:crown',
      keepAlive: true,
      order: 1050,
      title: $t('member.title'),
    },
    name: 'Member',
    path: '/member',
    children: [
      {
        meta: {
          icon: 'lucide:layers',
          title: $t('member.level'),
        },
        name: 'MemberLevel',
        path: 'level',
        component: () => import('#/views/member_management/level.vue'),
      },
      {
        meta: {
          icon: 'lucide:package',
          title: $t('member.package'),
        },
        name: 'MemberPackage',
        path: 'package',
        component: () => import('#/views/member_management/package.vue'),
      },
      {
        meta: {
          icon: 'lucide:ticket',
          title: $t('member.order'),
        },
        name: 'MemberOrder',
        path: 'order',
        component: () => import('#/views/member_management/order.vue'),
      },
      {
        meta: {
          icon: 'lucide:users',
          title: $t('member.user'),
        },
        name: 'MemberUser',
        path: 'user',
        component: () => import('#/views/member_management/user.vue'),
      },
      {
        meta: {
          icon: 'lucide:trending-up',
          title: $t('member.record'),
        },
        name: 'MemberRecord',
        path: 'record',
        component: () => import('#/views/member_management/record.vue'),
      },
    ],
  },
];

export default routes;
