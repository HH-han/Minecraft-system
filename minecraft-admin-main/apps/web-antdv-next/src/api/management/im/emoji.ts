// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-系统表情（管理端）
 */

// 分页查询系统表情
export const getEmojiPage = (params: any): Promise<any> => {
  return request.get('/admin/im/emojis', { params });
};

// 新增系统表情
export const createEmoji = (data: any): Promise<any> => {
  return request.post('/admin/im/emojis', data);
};

// 更新系统表情
export const updateEmoji = (id: any, data: any): Promise<any> => {
  return request.put(`/admin/im/emojis/${id}`, data);
};

// 删除系统表情
export const deleteEmoji = (id: any): Promise<any> => {
  return request.delete(`/admin/im/emojis/${id}`);
};
