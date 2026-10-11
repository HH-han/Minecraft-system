// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-聊天消息（管理端）
 */

// 分页查询单聊消息
export const getSingleMessagePage = (params: any): Promise<any> => {
  return request.get('/admin/im/messages/single', { params });
};

// 分页查询群聊消息
export const getGroupMessagePage = (params: any): Promise<any> => {
  return request.get('/admin/im/messages/group', { params });
};
