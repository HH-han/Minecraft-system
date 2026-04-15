import request from '@/utils/request'

/**
 * 在线用户监控相关接口
 */

// 获取在线用户列表
export const getOnlineUsers = (params) => {
  return request.get('/online-users', { params })
}

// 获取用户统计信息
export const getUserStats = () => {
  return request.get('/online-users/stats')
}

// 强制用户下线
export const forceLogout = (userId) => {
  return request.post(`/online-users/logout/${userId}`)
}

// 获取用户活动日志
export const getUserActivity = (params) => {
  return request.get('/online-users/activity', { params })
}
