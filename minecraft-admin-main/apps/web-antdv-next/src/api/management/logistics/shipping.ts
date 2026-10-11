// @ts-nocheck
import request from '#/utils/request';

/**
 * 发货管理接口
 */

// 发货单分页列表
export const getShippingPage = (params: any): Promise<any> => {
  return request.get('/admin/logistics/shipping/page', { params });
};

// 创建发货单（录入收货信息与运单）
export const createShipping = (data: any): Promise<any> => {
  return request.post('/admin/logistics/shipping', data);
};

// 批量发货
export const batchShipping = (data: any): Promise<any> => {
  return request.post('/admin/logistics/shipping/batch', data);
};

// 查询发货单轨迹
export const getShippingTrack = (id: any): Promise<any> => {
  return request.get(`/admin/logistics/shipping/${id}/track`);
};
