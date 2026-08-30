import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:monitor',
      keepAlive: true,
      order: 1000,
      title: $t('service.title'),
    },
    name: 'Service',
    path: '/service',
    children: [
      {
        meta: {
          icon: 'lucide:activity',
          title: $t('service.monitor'),
        },
        name: 'ServiceMonitor',
        path: 'monitor',
        component: () => import('#/views/service_management/monitoring.vue'),
      },
      {
        meta: {
          icon: 'lucide:database',
          title: $t('service.cache'),
        },
        name: 'CacheMonitor',
        path: 'cache',
        component: () => import('#/views/service_management/cache.vue'),
      },
      {
        meta: {
          icon: 'lucide:users',
          title: $t('service.online'),
        },
        name: 'OnlineUsers',
        path: 'online',
        component: () => import('#/views/service_management/onlineUsers.vue'),
      },
      {
        meta: {
          icon: 'lucide:file-text',
          title: $t('service.log'),
        },
        name: 'SystemLog',
        path: 'log',
        component: () => import('#/views/service_management/operationLog.vue'),
      },
    ],
  },
];

export default routes;
