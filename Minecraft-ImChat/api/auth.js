/**
 * 认证管理接口
 *
 * 参照 minecraft-App/api/auth.js 的代码结构
 * 统一从 utils/auth.js 导入
 */

import { authApi } from '../utils/auth.js'

// 登录
export const login = (data) => {
  return authApi.login(data)
}

// 注册
export const register = (data) => {
  return authApi.register(data)
}

// 退出登录
export const logout = () => {
  return authApi.logout()
}

export default {
  login,
  register,
  logout
}
