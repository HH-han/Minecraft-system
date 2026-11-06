import request, { type ApiResponse } from './request';
import type { FavoriteItem, FavoriteStat } from '../types/im';

export interface FavoriteQuery {
  userId: number;
  type?: string;
  tag?: string;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface FavoritePayload {
  userId: number;
  type: string;
  title?: string;
  content?: string;
  cover?: string;
  url?: string;
  fileName?: string;
  fileSize?: number;
  tags?: string[];
  source?: string;
}

export function getFavoriteList(params: FavoriteQuery) {
  return request.get<unknown, ApiResponse<FavoriteItem[]>>('/public/favorite/list', { params });
}

export function getRecentFavorites(userId: number, limit = 20) {
  return request.get<unknown, ApiResponse<FavoriteItem[]>>('/public/favorite/recent', {
    params: { userId, limit }
  });
}

export function getFavoriteStats(userId: number) {
  return request.get<unknown, ApiResponse<FavoriteStat>>('/public/favorite/stats', {
    params: { userId }
  });
}

export function getFavoriteTags(userId: number) {
  return request.get<unknown, ApiResponse<string[]>>('/public/favorite/tags', {
    params: { userId }
  });
}

export function getFavoriteDetail(userId: number, id: number) {
  return request.get<unknown, ApiResponse<FavoriteItem>>(`/public/favorite/${id}`, {
    params: { userId }
  });
}

export function addFavorite(payload: FavoritePayload) {
  return request.post<unknown, ApiResponse<FavoriteItem>>('/public/favorite/add', payload);
}

export function updateFavorite(id: number, payload: FavoritePayload) {
  return request.put<unknown, ApiResponse<FavoriteItem>>(`/public/favorite/${id}`, payload);
}

export function touchFavorite(userId: number, id: number) {
  return request.post<unknown, ApiResponse<void>>(`/public/favorite/${id}/touch`, null, {
    params: { userId }
  });
}

export function deleteFavorite(userId: number, id: number) {
  return request.delete<unknown, ApiResponse<void>>(`/public/favorite/${id}`, {
    params: { userId }
  });
}

export function clearFavorites(userId: number, type?: string) {
  return request.delete<unknown, ApiResponse<void>>('/public/favorite/clear', {
    params: { userId, type }
  });
}
