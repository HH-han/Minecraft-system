import request, { type ApiResponse } from './request';
import type { FavoriteItem } from '@/types/im';

/** 收藏列表：GET /public/favorite/list */
export function getFavoriteList(userId: number) {
  return request.get<unknown, ApiResponse<FavoriteItem[]>>('/public/favorite/list', {
    params: { userId }
  });
}

export function deleteFavorite(id: number) {
  return request.delete<unknown, ApiResponse<unknown>>(`/public/favorite/${id}`);
}
