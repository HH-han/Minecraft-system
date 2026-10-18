// @ts-nocheck
import request from '#/utils/request';

/**
 * 聚合支付管理接口（管理端）
 */

// 支付主单分页
export const getPayOrderPage = (params: any): Promise<any> => {
  return request.get('/admin/pay/orders', { params });
};

// 支付流水分页
export const getPayFlowPage = (params: any): Promise<any> => {
  return request.get('/admin/pay/flows', { params });
};

// 支付回调日志分页
export const getPayNotifyLogPage = (params: any): Promise<any> => {
  return request.get('/admin/pay/notify-logs', { params });
};

// 按支付单发起退款
export const refundPayOrder = (payNo: string, params: any): Promise<any> => {
  return request.post(`/admin/pay/${payNo}/refund`, null, { params });
};

/**
 * 兼容旧调用：创建支付 / 查询 / 取消
 */
export const createPayment = (params: any): Promise<any> => {
  return request.post('/payment/create', null, { params });
};

export const getPaymentDetail = (id: any): Promise<any> => {
  return request.get(`/payment/${id}`);
};

export const cancelPayment = (id: any): Promise<any> => {
  return request.put(`/payment/${id}/cancel`);
};
