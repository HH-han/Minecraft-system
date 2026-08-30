import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:settings',
      keepAlive: true,
      order: 1000,
      title: $t('system.title'),
    },
    name: 'System',
    path: '/system',
    children: [
      {
        meta: {
          icon: 'lucide:layout-dashboard',
          title: $t('system.console'),
        },
        name: 'Console',
        path: 'console',
        component: () => import('#/views/system_management/index.vue'),
      },
      {
        meta: {
          icon: 'lucide:list',
          title: $t('system.operation_log'),
        },
        name: 'OperationLog',
        path: 'operation-log',
        component: () => import('#/views/system_management/systemlog.vue'),
      },
      {
        meta: {
          icon: 'lucide:lock',
          title: $t('system.login_logs'),
        },
        name: 'LoginLogs',
        path: 'login-logs',
        component: () => import('#/views/system_management/index.vue'),
      },
      {
        meta: {
          icon: 'lucide:plug',
          title: $t('system.SwaggerUI'),
        },
        name: 'SwaggerUI',
        path: 'swagger-ui',
        component: () => import('#/views/system_management/swaggerui.vue'),
      },
      {
        meta: {
          icon: 'lucide:braces',
          title: $t('system.OpenAPIJSON'),
        },
        name: 'OpenAPIJSON',
        path: 'open-api-json',
        component: () => import('#/views/system_management/openapijson.vue'),
      },
    ],
  },
];

export default routes;
