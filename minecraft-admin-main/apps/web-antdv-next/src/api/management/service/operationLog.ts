// @ts-nocheck
import request from '#/utils/request';

/**
 * 操作日志接口（管理端）
 * 对应后端 OperationLogController: /api/operation-logs
 */

// 获取操作日志列表（分页，支持 keyword 关键字）
export const getOperationLogList = (params: any): Promise<any> => {
  return request.get('/operation-logs/list', { params });
};

// 获取操作日志详情
export const getOperationLogDetail = (id: any): Promise<any> => {
  return request.get(`/operation-logs/${id}`);
};

// 根据用户ID获取操作日志
export const getLogsByUserId = (userId: any): Promise<any> => {
  return request.get(`/operation-logs/user/${userId}`);
};

// 记录操作日志
export const saveOperationLog = (data: any): Promise<any> => {
  return request.post('/operation-logs/add', data);
};

// 删除操作日志
export const deleteOperationLog = (id: any): Promise<any> => {
  return request.delete(`/operation-logs/${id}`);
};
