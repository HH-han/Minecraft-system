import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:truck',
      keepAlive: true,
      order: 1100,
      title: $t('logistics.title'),
    },
    name: 'Logistics',
    path: '/logistics',
    children: [
      {
        meta: {
          icon: 'lucide:package',
          title: $t('logistics.shipping'),
        },
        name: 'LogisticsShipping',
        path: 'shipping',
        component: () => import('#/views/logistics_management/shipping.vue'),
      },
      {
        meta: {
          icon: 'lucide:settings',
          title: $t('logistics.carrier'),
        },
        name: 'LogisticsCarrier',
        path: 'carrier',
        component: () => import('#/views/logistics_management/carrier.vue'),
      },
      {
        meta: {
          icon: 'lucide:list',
          title: $t('logistics.freight'),
        },
        name: 'LogisticsFreight',
        path: 'freight',
        component: () => import('#/views/logistics_management/freight.vue'),
      },
      {
        meta: {
          icon: 'lucide:undo-2',
          title: $t('logistics.after_sale'),
        },
        name: 'LogisticsAfterSale',
        path: 'after-sale',
        component: () => import('#/views/logistics_management/afterSale.vue'),
      },
      {
        meta: {
          icon: 'lucide:circle-check',
          title: $t('logistics.refund'),
        },
        name: 'LogisticsRefund',
        path: 'refund',
        component: () => import('#/views/logistics_management/refund.vue'),
      },
    ],
  },
];

export default routes;
