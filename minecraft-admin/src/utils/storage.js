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
}

// 获取用户名
export const getUsername = () => {
  return localStorage.getItem('username')
}

// 设置用户名
export const setUsername = (username) => {
  localStorage.setItem('username', username)
}
