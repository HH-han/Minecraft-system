// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-聊天群组（管理端）
 */

// 分页查询聊天群组
export const getGroupPage = (params: any): Promise<any> => {
  return request.get('/admin/im/groups', { params });
};

// 查询群成员列表
export const getGroupMembers = (id: any): Promise<any> => {
  return request.get(`/admin/im/groups/${id}/members`);
};

// 更新群组信息
export const updateChatGroup = (id: any, data: any): Promise<any> => {
  return request.put(`/admin/im/groups/${id}`, data);
};

// 解散群组
export const deleteChatGroup = (id: any): Promise<any> => {
  return request.delete(`/admin/im/groups/${id}`);
};

// 移除群成员
export const removeGroupMember = (groupId: any, userId: any): Promise<any> => {
  return request.delete(`/admin/im/groups/${groupId}/members/${userId}`);
};
