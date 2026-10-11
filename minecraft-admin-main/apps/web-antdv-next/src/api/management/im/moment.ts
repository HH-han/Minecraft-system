// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-朋友圈（管理端）
 */

// 分页查询朋友圈动态
export const getMomentPage = (params: any): Promise<any> => {
  return request.get('/admin/im/moments', { params });
};

// 查询动态评论列表
export const getMomentComments = (id: any): Promise<any> => {
  return request.get(`/admin/im/moments/${id}/comments`);
};

// 查询动态点赞用户列表
export const getMomentLikes = (id: any): Promise<any> => {
  return request.get(`/admin/im/moments/${id}/likes`);
};

// 删除动态
export const deleteMoment = (id: any): Promise<any> => {
  return request.delete(`/admin/im/moments/${id}`);
};
