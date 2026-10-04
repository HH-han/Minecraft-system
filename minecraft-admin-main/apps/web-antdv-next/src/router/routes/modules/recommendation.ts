import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:sparkles',
      keepAlive: true,
      order: 950,
      title: $t('recommendation.title'),
    },
    name: 'Recommendation',
    path: '/recommendation',
    children: [
      {
        meta: {
          icon: 'lucide:layout-dashboard',
          title: $t('recommendation.dashboard'),
        },
        name: 'RecommendationDashboard',
        path: 'dashboard',
        component: () =>
          import('#/views/recommendation_management/dashboard.vue'),
      },
      {
        meta: {
          icon: 'lucide:sliders-horizontal',
          title: $t('recommendation.params'),
        },
        name: 'RecommendationParams',
        path: 'params',
        component: () =>
          import('#/views/recommendation_management/params.vue'),
      },
      {
        meta: {
          icon: 'lucide:list-checks',
          title: $t('recommendation.items'),
        },
        name: 'RecommendationItems',
        path: 'items',
        component: () =>
          import('#/views/recommendation_management/items.vue'),
      },
      {
        meta: {
          icon: 'lucide:pin',
          title: $t('recommendation.rules'),
        },
        name: 'RecommendationRules',
        path: 'rules',
        component: () =>
          import('#/views/recommendation_management/rules.vue'),
      },
      {
        meta: {
          icon: 'lucide:workflow',
          title: $t('recommendation.intervention'),
        },
        name: 'RecommendationIntervention',
        path: 'intervention',
        component: () =>
          import('#/views/recommendation_management/intervention.vue'),
      },
      {
        meta: {
          icon: 'lucide:calendar-clock',
          title: $t('recommendation.schedule'),
        },
        name: 'RecommendationSchedule',
        path: 'schedule',
        component: () =>
          import('#/views/recommendation_management/schedule.vue'),
      },
    ],
  },
];

export default routes;
