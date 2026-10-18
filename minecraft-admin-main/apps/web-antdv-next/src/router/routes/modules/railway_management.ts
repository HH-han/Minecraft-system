import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:train-front',
      keepAlive: true,
      order: 950,
      title: $t('railway.title'),
    },
    name: 'Railway',
    path: '/railway',
    children: [
      {
        meta: {
          icon: 'lucide:map-pin',
          title: $t('railway.station'),
        },
        name: 'RailwayStation',
        path: 'station',
        component: () => import('#/views/railway_management/station.vue'),
      },
      {
        meta: {
          icon: 'lucide:train-track',
          title: $t('railway.train'),
        },
        name: 'RailwayTrain',
        path: 'train',
        component: () => import('#/views/railway_management/train.vue'),
      },
      {
        meta: {
          icon: 'lucide:calendar-clock',
          title: $t('railway.service'),
        },
        name: 'RailwayService',
        path: 'service',
        component: () => import('#/views/railway_management/service.vue'),
      },
      {
        meta: {
          icon: 'lucide:receipt-text',
          title: $t('railway.order'),
        },
        name: 'RailwayOrder',
        path: 'order',
        component: () => import('#/views/railway_management/order.vue'),
      },
    ],
  },
];

export default routes;
