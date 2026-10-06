export * from './core';
export * from './examples';
export { getExampleTableApi } from './examples/table';

import { requestClient } from './request';

/** demo 页面使用：获取菜单选项列表（{ id, name } 结构，供 ApiSelect 演示） */
export async function getMenuList() {
  return requestClient.get('/menu/all');
}
