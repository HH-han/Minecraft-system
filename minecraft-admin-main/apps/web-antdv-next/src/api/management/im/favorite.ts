// @ts-nocheck
import request from '#/utils/request';

/**
 * 即时通讯-收藏（管理端）
 */

// 分页查询收藏
export const getFavoritePage = (params: any): Promise<any> => {
  return request.get('/admin/im/favorites', { params });
};

// 删除收藏
export const deleteFavorite = (id: any): Promise<any> => {
  return request.delete(`/admin/im/favorites/${id}`);
};
