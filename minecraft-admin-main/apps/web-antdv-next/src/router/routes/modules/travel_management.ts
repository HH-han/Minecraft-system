import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:plane',
      keepAlive: true,
      order: 1000,
      title: $t('travel.title'),
    },
    name: 'Travel',
    path: '/travel',
    children: [
      {
        meta: {
          icon: 'lucide:bed',
          title: $t('travel.hotel'),
        },
        name: 'Hotel',
        path: 'hotel',
        component: () => import('#/views/travel_management/hotel.vue'),
      },
      {
        meta: {
          icon: 'lucide:trees',
          title: $t('travel.scenic'),
        },
        name: 'Scenic',
        path: 'scenic',
        component: () => import('#/views/travel_management/attraction.vue'),
      },
      {
        meta: {
          icon: 'lucide:thumbs-up',
          title: $t('travel.recommend'),
        },
        name: 'Recommend',
        path: 'recommend',
        component: () => import('#/views/travel_management/recommend.vue'),
      },
      {
        meta: {
          icon: 'lucide:utensils',
          title: $t('travel.food'),
        },
        name: 'Food',
        path: 'food',
        component: () => import('#/views/travel_management/food.vue'),
      },
      {
        meta: {
          icon: 'lucide:globe',
          title: $t('travel.world'),
        },
        name: 'World',
        path: 'world',
        component: () => import('#/views/travel_management/countries.vue'),
      },
      // {
      //   meta: {
      //     icon: 'lucide:flag',
      //     title: $t('travel.strategy'),
      //   },
      //   name: 'Strategy',
      //   path: 'strategy',
      //   component: () => import('#/views/travel_management/index.vue'),
      // },
      {
        meta: {
          icon: 'lucide:users',
          title: $t('travel.tourGroup'),
        },
        name: 'TourGroup',
        path: 'tour-group',
        component: () => import('#/views/travel_management/group.vue'),
      },
      {
        meta: {
          icon: 'lucide:images',
          title: $t('travel.carousel'),
        },
        name: 'Carousel',
        path: 'carousel',
        component: () => import('#/views/travel_management/carousel.vue'),
      },
      {
        meta: {
          icon: 'lucide:flame',
          title: $t('travel.hotRecommend'),
        },
        name: 'HotRecommend',
        path: 'hot-recommend',
        component: () => import('#/views/travel_management/homeRecommendations.vue'),
      },
      {
        meta: {
          icon: 'lucide:languages',
          title: $t('travel.worldTour'),
        },
        name: 'WorldTour',
        path: 'world-tour',
        component: () => import('#/views/travel_management/worldcharacteristics.vue'),
      },
      {
        meta: {
          icon: 'lucide:newspaper',
          title: $t('travel.news'),
        },
        name: 'News',
        path: 'news',
        component: () => import('#/views/travel_management/news.vue'),
      },
      // {
      //   meta: {
      //     icon: 'lucide:map-pin',
      //     title: $t('travel.destination'),
      //   },
      //   name: 'Destination',
      //   path: 'destination',
      //   component: () => import('#/views/travel_management/index.vue'),
      // },
      {
        meta: {
          icon: 'lucide:shopping-bag',
          title: $t('travel.product'),
        },
        name: 'Product',
        path: 'product',
        component: () => import('#/views/travel_management/product.vue'),
      },
      {
        meta: {
          icon: 'lucide:users',
          title: $t('travel.community'),
        },
        name: 'Community',
        path: 'community',
        component: () => import('#/views/travel_management/community.vue'),
      },
      {
        meta: {
          icon: 'lucide:calendar',
          title: $t('travel.monthly'),
        },
        name: 'Monthly',
        path: 'monthly',
        component: () =>
          import('#/views/travel_management/monthlyRecommendations.vue'),
      },
      {
        meta: {
          icon: 'lucide:globe',
          title: $t('travel.website'),
        },
        name: 'Website',
        path: 'website',
        component: () => import('#/views/travel_management/officialwebsite.vue'),
      },
    ],
  },
];

export default routes;
