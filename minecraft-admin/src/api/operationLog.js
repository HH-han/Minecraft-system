import request from '@/utils/request';

/**
 * 操作日志接口（管理端）
 * 对应后端 OperationLogController: /api/operation-logs
 */
const operationLogApi = {
  // 获取操作日志列表（分页，支持 keyword 关键字）
  getOperationLogList: (params) => {
    return request.get('/operation-logs/list', { params });
  },

  // 获取操作日志详情
  getOperationLogDetail: (id) => {
    return request.get(`/operation-logs/${id}`);
  },

  // 根据用户ID获取操作日志
  getLogsByUserId: (userId) => {
    return request.get(`/operation-logs/user/${userId}`);
  },

  // 记录操作日志
  saveOperationLog: (data) => {
    return request.post('/operation-logs/add', data);
  },

  // 删除操作日志
  deleteOperationLog: (id) => {
    return request.delete(`/operation-logs/${id}`);
  }
};

export default operationLogApi;
