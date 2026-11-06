import request, { type ApiResponse } from './request';
import type { LoginParams, LoginResult, RegisterParams } from '@/types/im';

/** 登录：POST /auth/login */
export function login(params: LoginParams) {
  return request.post<unknown, ApiResponse<LoginResult>>('/auth/login', params);
}

/** 注册：POST /auth/register（后端返回新账号，需再登录获取登录态） */
export function register(params: RegisterParams) {
  return request.post<unknown, ApiResponse<string>>('/auth/register', params);
}
