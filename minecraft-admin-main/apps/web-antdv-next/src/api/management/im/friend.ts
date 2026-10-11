// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-好友关系（管理端）
 */

// 分页查询好友关系/好友申请
export const getFriendPage = (params: any): Promise<any> => {
  return request.get('/admin/im/friends', { params });
};

// 删除好友关系（双向）
export const deleteFriendRelation = (id: any): Promise<any> => {
  return request.delete(`/admin/im/friends/${id}`);
};

// 通过好友申请
export const approveFriendRequest = (id: any): Promise<any> => {
  return request.post(`/admin/im/friends/${id}/approve`);
};

// 拒绝好友申请
export const rejectFriendRequest = (id: any): Promise<any> => {
  return request.post(`/admin/im/friends/${id}/reject`);
};
