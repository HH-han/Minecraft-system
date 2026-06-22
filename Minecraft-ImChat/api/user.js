/**
 * 用户管理接口
 */

import { userApi } from '../utils/auth.js'

// 获取当前用户信息
export const getUserInfo = () => {
  return userApi.getUserInfo()
}

// 根据账号获取用户信息
export const getUserByAccount = (account) => {
  return userApi.getUserByAccount(account)
}

export default {
  getUserInfo,
  getUserByAccount
}
