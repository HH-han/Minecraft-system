/**
 * 登录会话读取（页面统一入口，避免各处重复 getStorageSync）
 */

import { getUserInfo } from './storage.js'

/**
 * 当前登录用户 id
 */
export function getUserId() {
  const user = getUserInfo()
  return user ? user.id : null
}

/**
 * 当前登录用户对象
 */
export function getCurrentUser() {
  return getUserInfo() || null
}

/**
 * 登录用户展示信息 { id, name, avatar, account }
 */
export function getCurrentProfile() {
  const user = getUserInfo()
  if (!user) return { id: null, name: '', avatar: '', account: '' }
  return {
    id: user.id,
    name: user.nickname || user.username || '',
    avatar: user.avatar || '',
    account: user.account || ''
  }
}
