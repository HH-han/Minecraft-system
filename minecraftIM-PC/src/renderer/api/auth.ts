import request, { type ApiResponse } from './request';
import type { LoginParams, LoginResult, RegisterParams } from '@/types/im';

/** 登录：POST /auth/login */
export function login(params: LoginParams) {
  return request.post<unknown, ApiResponse<LoginResult>>('/auth/login', params);
}

/** 注册：POST /auth/register（成功后会话即建立，后端直接返回登录态） */
export function register(params: RegisterParams) {
  return request.post<unknown, ApiResponse<LoginResult>>('/auth/register', params);
}
