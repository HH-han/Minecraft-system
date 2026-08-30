// @ts-nocheck
import request from '#/utils/request';

/**
 * 系统日志管理接口
 * 对应后端 SystemLogController: /api/system-logs
 */

// 获取系统日志列表(分页)
export const getSystemLogList = (params: any): Promise<any> => {
  return request.get('/system-logs/list', { params });
};

// 获取系统日志详情
export const getSystemLogDetail = (id: any): Promise<any> => {
  return request.get(`/system-logs/${id}`);
};

// 记录系统日志
export const saveSystemLog = (data: any): Promise<any> => {
  return request.post('/system-logs/add', data);
};

// 删除系统日志
export const deleteSystemLog = (id: any): Promise<any> => {
  return request.delete(`/system-logs/${id}`);
};

// 删除指定时间之前的系统日志（time: yyyy-MM-dd HH:mm:ss）
export const deleteLogsBeforeTime = (time: string): Promise<any> => {
  return request.delete('/system-logs/before', {
    params: { time },
  });
};
