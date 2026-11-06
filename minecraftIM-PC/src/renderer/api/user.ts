import request, { type ApiResponse } from './request';
import type { PointsRecord, UserInfo } from '../types/im';

export function getUserInfo() {
  return request.get<unknown, ApiResponse<UserInfo>>('/user/info');
}

export function updateUserInfo(payload: Partial<UserInfo> & { id?: number }) {
  return request.put<unknown, ApiResponse<void>>('/user/update', payload);
}

export function updatePassword(oldPassword: string, newPassword: string) {
  return request.put<unknown, ApiResponse<void>>('/user/password', null, {
    params: { oldPassword, newPassword }
  });
}

export function uploadAvatar(file: File) {
  const form = new FormData();
  form.append('avatar', file);
  return request.post<unknown, ApiResponse<string>>('/user/avatar', form);
}

export function uploadUserImage(file: File) {
  const form = new FormData();
  form.append('file', file);
  return request.post<unknown, ApiResponse<string>>('/user/upload', form);
}

export function logout() {
  return request.post<unknown, ApiResponse<void>>('/user/logout');
}

export function getUserByAccount(account: string, options?: { silent?: boolean }) {
  return request.get<unknown, ApiResponse<UserInfo>>('/user/by-account', {
    params: { account },
    silent: options?.silent
  });
}

export function getUserPoints() {
  return request.get<unknown, ApiResponse<number>>('/user/points');
}

export function getPointsRecords() {
  return request.get<unknown, ApiResponse<PointsRecord[]>>('/user/points/records');
}
