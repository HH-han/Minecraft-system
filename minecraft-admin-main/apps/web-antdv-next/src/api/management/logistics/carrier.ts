// @ts-nocheck
import request from '#/utils/request';

/**
 * 承运商管理接口
 */

// 承运商列表
export const getCarrierList = (): Promise<any> => {
  return request.get('/admin/logistics/carrier/list');
};

// 新增/编辑承运商
export const saveCarrier = (data: any): Promise<any> => {
  return request.post('/admin/logistics/carrier', data);
};

// 删除承运商
export const deleteCarrier = (id: any): Promise<any> => {
  return request.delete(`/admin/logistics/carrier/${id}`);
};
