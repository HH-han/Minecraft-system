// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-通话记录（管理端）
 */

// 分页查询通话记录
export const getCallRecordPage = (params: any): Promise<any> => {
  return request.get('/admin/im/calls', { params });
};

// 删除通话记录
export const deleteCallRecord = (id: any): Promise<any> => {
  return request.delete(`/admin/im/calls/${id}`);
};
