import request, { type ApiResponse } from './request';
import type { UserInfo } from '@/types/im';

/** 当前用户信息：GET /user/info */
export function getUserInfo() {
  return request.get<unknown, ApiResponse<UserInfo>>('/user/info');
}

/** 更新用户信息：PUT /user/update */
export function updateUserInfo(data: Partial<UserInfo>) {
  return request.put<unknown, ApiResponse<UserInfo>>('/user/update', data);
}

/** 退出登录：POST /user/logout */
export function logout() {
  return request.post<unknown, ApiResponse<null>>('/user/logout');
}

/** 按账号查找用户（添加好友前置）：GET /user/by-account */
export function getUserByAccount(account: string) {
  return request.get<unknown, ApiResponse<UserInfo>>('/user/by-account', {
    params: { account }
  });
}
