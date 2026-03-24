// 存储相关工具函数

// 获取token
export const getToken = () => {
  return uni.getStorageSync('token')
}

// 移除token
export const removeToken = () => {
  uni.removeStorageSync('token')
}

// 存储用户信息
export const setUserInfo = (userInfo) => {
  uni.setStorageSync('userInfo', userInfo)
}

// 获取用户信息
export const getUserInfo = () => {
  return uni.getStorageSync('userInfo')
}

// 清除所有存储
export const clearStorage = () => {
  uni.clearStorageSync()
}
