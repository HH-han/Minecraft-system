/**
 * 存储工具模块
 */

// 存储键名常量
const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  REMEMBERED_ACCOUNT: 'rememberedAccount',
  FRIENDS_LIST: 'friendsList',
  GROUPS_LIST: 'groupsList',
  CHAT_HISTORY: 'chatHistory',
  SETTINGS: 'settings',
  LOGIN_STATUS: 'loginStatus'
}

/**
 * 设置 Token
 */
export const setToken = (token) => {
  uni.setStorageSync(STORAGE_KEYS.TOKEN, token)
}

/**
 * 获取 Token
 */
export const getToken = () => {
  return uni.getStorageSync(STORAGE_KEYS.TOKEN) || ''
}

/**
 * 移除 Token
 */
export const removeToken = () => {
  uni.removeStorageSync(STORAGE_KEYS.TOKEN)
}

/**
 * 设置用户信息
 */
export const setUserInfo = (userInfo) => {
  uni.setStorageSync(STORAGE_KEYS.USER_INFO, userInfo)
  uni.setStorageSync(STORAGE_KEYS.LOGIN_STATUS, true)
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return uni.getStorageSync(STORAGE_KEYS.USER_INFO) || null
}

/**
 * 移除用户信息
 */
export const removeUserInfo = () => {
  uni.removeStorageSync(STORAGE_KEYS.USER_INFO)
  uni.removeStorageSync(STORAGE_KEYS.LOGIN_STATUS)
}

/**
 * 清除用户信息（removeUserInfo 的别名）
 */
export const clearUserInfo = () => {
  removeUserInfo()
}

/**
 * 检查是否已登录
 */
export const isLoggedIn = () => {
  const token = getToken()
  const userInfo = getUserInfo()
  return !!(token && userInfo && userInfo.id)
}

/**
 * 设置记住的账号
 */
export const setRememberedAccount = (account) => {
  uni.setStorageSync(STORAGE_KEYS.REMEMBERED_ACCOUNT, account)
}

/**
 * 获取记住的账号
 */
export const getRememberedAccount = () => {
  return uni.getStorageSync(STORAGE_KEYS.REMEMBERED_ACCOUNT) || ''
}

/**
 * 移除记住的账号
 */
export const removeRememberedAccount = () => {
  uni.removeStorageSync(STORAGE_KEYS.REMEMBERED_ACCOUNT)
}

/**
 * 退出登录 - 清除所有登录信息
 */
export const logout = () => {
  removeToken()
  removeUserInfo()
  // 保留记住的账号，方便下次快速登录
}

/**
 * 设置好友列表缓存
 */
export const setFriendsList = (friends) => {
  uni.setStorageSync(STORAGE_KEYS.FRIENDS_LIST, friends)
}

/**
 * 获取好友列表缓存
 */
export const getFriendsList = () => {
  return uni.getStorageSync(STORAGE_KEYS.FRIENDS_LIST) || []
}

/**
 * 设置群组列表缓存
 */
export const setGroupsList = (groups) => {
  uni.setStorageSync(STORAGE_KEYS.GROUPS_LIST, groups)
}

/**
 * 获取群组列表缓存
 */
export const getGroupsList = () => {
  return uni.getStorageSync(STORAGE_KEYS.GROUPS_LIST) || []
}

/**
 * 保存聊天历史记录
 */
export const setChatHistory = (key, messages) => {
  const historyKey = `${STORAGE_KEYS.CHAT_HISTORY}_${key}`
  uni.setStorageSync(historyKey, messages)
}

/**
 * 获取聊天历史记录
 */
export const getChatHistory = (key) => {
  const historyKey = `${STORAGE_KEYS.CHAT_HISTORY}_${key}`
  return uni.getStorageSync(historyKey) || []
}

/**
 * 清除聊天历史记录
 */
export const clearChatHistory = (key) => {
  const historyKey = `${STORAGE_KEYS.CHAT_HISTORY}_${key}`
  uni.removeStorageSync(historyKey)
}

/**
 * 设置应用设置
 */
export const setSettings = (settings) => {
  uni.setStorageSync(STORAGE_KEYS.SETTINGS, settings)
}

/**
 * 获取应用设置
 */
export const getSettings = () => {
  return uni.getStorageSync(STORAGE_KEYS.SETTINGS) || {
    notification: true,
    sound: true,
    vibration: true,
    theme: 'light'
  }
}

/**
 * 清除所有存储数据
 */
export const clearAll = () => {
  uni.clearStorageSync()
}

export default {
  STORAGE_KEYS,
  setToken,
  getToken,
  removeToken,
  setUserInfo,
  getUserInfo,
  removeUserInfo,
  clearUserInfo,
  isLoggedIn,
  setRememberedAccount,
  getRememberedAccount,
  removeRememberedAccount,
  logout,
  setFriendsList,
  getFriendsList,
  setGroupsList,
  getGroupsList,
  setChatHistory,
  getChatHistory,
  clearChatHistory,
  setSettings,
  getSettings,
  clearAll
}
