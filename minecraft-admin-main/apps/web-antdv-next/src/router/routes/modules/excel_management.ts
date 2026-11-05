import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:file-spreadsheet',
      keepAlive: true,
      order: 1150,
      title: $t('excel.title'),
    },
    name: 'ExcelTools',
    path: '/excel',
    children: [
      {
        meta: {
          icon: 'lucide:table',
          title: $t('excel.importExport'),
        },
        name: 'ExcelToolsImportExport',
        path: 'import-export',
        component: () => import('#/views/excel_management/excel.vue'),
      },
    ],
  },
];

export default routes;
