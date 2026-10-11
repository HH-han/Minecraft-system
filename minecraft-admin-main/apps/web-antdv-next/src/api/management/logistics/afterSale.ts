// @ts-nocheck
import request from '#/utils/request';

/**
 * 售后管理接口（管理端）
 */

// 售后列表
export const getAfterSalePage = (params: any): Promise<any> => {
  return request.get('/admin/after-sale/page', { params });
};

// 售后详情
export const getAfterSaleDetail = (id: any): Promise<any> => {
  return request.get(`/admin/after-sale/${id}`);
};

// 审核
export const auditAfterSale = (id: any, data: any): Promise<any> => {
  return request.post(`/admin/after-sale/${id}/audit`, data);
};

// 确认收货
export const receiveAfterSale = (id: any): Promise<any> => {
  return request.post(`/admin/after-sale/${id}/receive`);
};

// 发起退款
export const refundAfterSale = (id: any): Promise<any> => {
  return request.post(`/admin/after-sale/${id}/refund`);
};

// 关闭售后
export const closeAfterSale = (id: any, reason?: string): Promise<any> => {
  return request.post(`/admin/after-sale/${id}/close`, null, {
    params: { reason },
  });
};
