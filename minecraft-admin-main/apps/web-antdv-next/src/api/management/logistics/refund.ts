// @ts-nocheck
import request from '#/utils/request';

/**
 * 退款管理接口（管理端）
 */

// 退款单列表
export const getRefundPage = (params: any): Promise<any> => {
  return request.get('/admin/refund/page', { params });
};

// 模拟渠道回调（标记成功/失败）
// status: { success: boolean, channelRefundNo?: string, failReason?: string }
export const notifyRefund = (id: any, status: any): Promise<any> => {
  const { success, channelRefundNo, failReason } = status || {};
  return request.post(`/admin/refund/${id}/notify`, null, {
    params: { success, channelRefundNo, failReason },
  });
};
