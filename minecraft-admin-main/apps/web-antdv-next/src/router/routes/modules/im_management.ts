import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:message-circle',
      keepAlive: true,
      order: 900,
      title: $t('im.title'),
    },
    name: 'Im',
    path: '/im',
    children: [
      {
        meta: {
          icon: 'lucide:user-plus',
          title: $t('im.menu.friend'),
        },
        name: 'ImFriend',
        path: 'friends',
        component: () => import('#/views/im_management/friend.vue'),
      },
      {
        meta: {
          icon: 'lucide:users',
          title: $t('im.menu.group'),
        },
        name: 'ImGroup',
        path: 'groups',
        component: () => import('#/views/im_management/group.vue'),
      },
      {
        meta: {
          icon: 'lucide:message-square',
          title: $t('im.menu.message'),
        },
        name: 'ImMessage',
        path: 'messages',
        component: () => import('#/views/im_management/message.vue'),
      },
      {
        meta: {
          icon: 'lucide:messages-square',
          title: $t('im.menu.groupMessage'),
        },
        name: 'ImGroupMessage',
        path: 'group-messages',
        component: () => import('#/views/im_management/groupMessage.vue'),
      },
      {
        meta: {
          icon: 'lucide:contact',
          title: $t('im.menu.userGroup'),
        },
        name: 'ImUserGroup',
        path: 'user-groups',
        component: () => import('#/views/im_management/userGroup.vue'),
      },
      {
        meta: {
          icon: 'lucide:camera',
          title: $t('im.menu.moment'),
        },
        name: 'ImMoment',
        path: 'moments',
        component: () => import('#/views/im_management/moment.vue'),
      },
      {
        meta: {
          icon: 'lucide:star',
          title: $t('im.menu.favorite'),
        },
        name: 'ImFavorite',
        path: 'favorites',
        component: () => import('#/views/im_management/favorite.vue'),
      },
      {
        meta: {
          icon: 'lucide:phone',
          title: $t('im.menu.call'),
        },
        name: 'ImCall',
        path: 'calls',
        component: () => import('#/views/im_management/call.vue'),
      },
      {
        meta: {
          icon: 'lucide:smile',
          title: $t('im.menu.emoji'),
        },
        name: 'ImEmoji',
        path: 'emojis',
        component: () => import('#/views/im_management/emoji.vue'),
      },
    ],
  },
];

export default routes;
