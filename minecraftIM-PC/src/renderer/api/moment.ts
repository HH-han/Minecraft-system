import request, { type ApiResponse } from './request';
import type {
  LikeResult,
  MomentComment,
  MomentItem,
  MomentUser
} from '../types/im';

export interface PublishMomentPayload {
  userId: number;
  content?: string;
  images?: string[];
  location?: string;
  visibility?: number;
}

export interface MomentCommentPayload {
  userId: number;
  content: string;
  replyToUserId?: number;
}

export function getMomentsFeed(userId: number, page = 1, size = 10) {
  return request.get<unknown, ApiResponse<MomentItem[]>>('/public/moment/feed', {
    params: { userId, page, size }
  });
}

export function getUserMoments(userId: number, targetUserId: number, page = 1, size = 10) {
  return request.get<unknown, ApiResponse<MomentItem[]>>('/public/moment/user', {
    params: { userId, targetUserId, page, size }
  });
}

export function publishMoment(payload: PublishMomentPayload) {
  return request.post<unknown, ApiResponse<MomentItem>>('/public/moment/publish', payload);
}

export function deleteMoment(momentId: number, userId: number) {
  return request.delete<unknown, ApiResponse<void>>(`/public/moment/${momentId}`, {
    params: { userId }
  });
}

export function toggleMomentLike(momentId: number, userId: number) {
  return request.post<unknown, ApiResponse<LikeResult>>(`/public/moment/${momentId}/like`, null, {
    params: { userId }
  });
}

export function getMomentLikes(momentId: number) {
  return request.get<unknown, ApiResponse<MomentUser[]>>(`/public/moment/${momentId}/likes`);
}

export function addMomentComment(momentId: number, payload: MomentCommentPayload) {
  return request.post<unknown, ApiResponse<MomentComment>>(
    `/public/moment/${momentId}/comment`,
    payload
  );
}

export function getMomentComments(momentId: number) {
  return request.get<unknown, ApiResponse<MomentComment[]>>(
    `/public/moment/${momentId}/comments`
  );
}

export function deleteMomentComment(commentId: number, userId: number) {
  return request.delete<unknown, ApiResponse<void>>(`/public/moment/comment/${commentId}`, {
    params: { userId }
  });
}

/** 上传朋友圈封面背景图 */
export function uploadMomentCover(userId: number, file: File) {
  const form = new FormData();
  form.append('file', file);
  return request.post<unknown, ApiResponse<string>>('/public/moment/cover', form, {
    params: { userId }
  });
}

export function getMomentCover(userId: number) {
  return request.get<unknown, ApiResponse<string>>('/public/moment/cover', { params: { userId } });
}

/** 通用图片上传（聊天图片、动态配图复用） */
export function uploadImage(file: File) {
  const form = new FormData();
  form.append('file', file);
  return request.post<unknown, ApiResponse<string>>('/public/im/upload', form);
}
