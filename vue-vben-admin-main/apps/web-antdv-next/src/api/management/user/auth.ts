// @ts-nocheck
import request from '#/utils/request';

// 登录
export const login = (data: any): Promise<any> => {
  return request.post('/auth/login', data)
}

// 注册
export const register = (data: any): Promise<any> => {
  return request.post('/auth/register', data)
}

// 退出登录
export const logout = (): Promise<any> => {
  return request.post('/user/logout')
}
