// 存储相关工具函数

// 获取token
export const getToken = () => {
  return localStorage.getItem('token')
}

// 设置token
export const setToken = (token) => {
  localStorage.setItem('token', token)
}

// 移除token
export const removeToken = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('userInfo')
}

// 获取用户名
export const getUsername = () => {
  return localStorage.getItem('username')
}

// 设置用户名
export const setUsername = (username) => {
  localStorage.setItem('username', username)
}

// 移除用户名
export const removeUsername = () => {
  localStorage.removeItem('username')
}

// 获取用户信息
export const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  try {
    return userInfoStr ? JSON.parse(userInfoStr) : null
  } catch {
    return null
  }
}

// 设置用户信息
export const setUserInfo = (userInfo) => {
  if (userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  }
}

// 移除用户信息
export const removeUserInfo = () => {
  localStorage.removeItem('userInfo')
}
