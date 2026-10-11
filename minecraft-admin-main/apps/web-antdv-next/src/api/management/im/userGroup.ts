// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-用户所在群（管理端）
 */

// 查询用户所在的群组列表
export const getUserGroups = (userId: any): Promise<any> => {
  return request.get(`/admin/im/users/${userId}/groups`);
};

// 分页查询群聊消息（按群组筛选）
export const getGroupMessages = (params: any): Promise<any> => {
  return request.get('/admin/im/messages/group', { params });
};
