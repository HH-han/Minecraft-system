import request, { type ApiResponse } from './request';
import type { MomentItem } from '@/types/im';

/** 朋友圈 feed：GET /public/moment/feed */
export function getMomentFeed(userId: number, page = 1, size = 20) {
  return request.get<unknown, ApiResponse<MomentItem[]>>('/public/moment/feed', {
    params: { userId, page, size }
  });
}

export function publishMoment(data: { userId: number; content: string; images?: string[] }) {
  return request.post<unknown, ApiResponse<MomentItem>>('/public/moment/publish', data);
}

export function toggleMomentLike(momentId: number, userId: number) {
  return request.post<unknown, ApiResponse<unknown>>('/public/moment/like/toggle', null, {
    params: { momentId, userId }
  });
}
