import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:file-text',
      keepAlive: true,
      order: 1000,
      title: $t('content.title'),
    },
    name: 'Content',
    path: '/content',
    children: [
      {
        meta: {
          icon: 'lucide:shield',
          title: $t('content.security'),
        },
        name: 'Security',
        path: 'security',
        component: () => import('#/views/content_management/safetytips.vue'),
      },
      {
        meta: {
          icon: 'lucide:send',
          title: $t('content.publish'),
        },
        name: 'Publish',
        path: 'publish',
        component: () => import('#/views/content_management/index.vue'),
      },
      {
        meta: {
          icon: 'lucide:pencil-line',
          title: $t('content.travelNotes'),
        },
        name: 'TravelNotes',
        path: 'travel-notes',
        component: () => import('#/views/content_management/index.vue'),
      },
      {
        meta: {
          icon: 'lucide:star',
          title: $t('content.points'),
        },
        name: 'Points',
        path: 'points',
        component: () => import('#/views/content_management/points.vue'),
      },
      {
        meta: {
          icon: 'lucide:compass',
          title: $t('content.travelPlan'),
        },
        name: 'TravelPlan',
        path: 'travel-plan',
        component: () => import('#/views/content_management/travel.vue'),
      },
      {
        meta: {
          icon: 'lucide:ticket',
          title: $t('content.ticket'),
        },
        name: 'Ticket',
        path: 'ticket',
        component: () => import('#/views/content_management/ticket.vue'),
      },
      {
        meta: {
          icon: 'lucide:globe',
          title: $t('content.websiteIntro'),
        },
        name: 'WebsiteIntro',
        path: 'website-intro',
        component: () =>
          import('#/views/content_management/websiteIntroduction.vue'),
      },
    ],
  },
];

export default routes;
