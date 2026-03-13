import request from '@/utils/request'

// 登录
export const login = (data) => {
  return request.post('/auth/login', data)
}

// 注册
export const register = (data) => {
  return request.post('/auth/register', data)
}

// 退出登录
export const logout = (userId) => {
  return request.post('/auth/logout', { userId })
}
