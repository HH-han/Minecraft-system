// @ts-nocheck
import request from '#/utils/request';

/**
 * 运费模板接口
 */

// 运费模板列表
export const getFreightList = (): Promise<any> => {
  return request.get('/admin/logistics/freight/list');
};

// 新增/编辑运费模板
export const saveFreight = (data: any): Promise<any> => {
  return request.post('/admin/logistics/freight', data);
};

// 删除运费模板
export const deleteFreight = (id: any): Promise<any> => {
  return request.delete(`/admin/logistics/freight/${id}`);
};
