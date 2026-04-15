import request from '@/utils/request'

/**
 * 缓存监控相关接口
 */

// 获取缓存统计信息
export const getCacheStats = () => {
  return request.get('/cache/stats')
}

// 获取缓存键列表
export const getCacheKeys = (params) => {
  return request.get('/cache/keys', { params })
}

// 清除指定缓存
export const clearCache = (key) => {
  return request.delete(`/cache/keys/${key}`)
}

// 清除所有缓存
export const clearAllCache = () => {
  return request.delete('/cache/keys')
}

// 获取缓存详情
export const getCacheDetail = (key) => {
  return request.get(`/cache/keys/${key}`)
}
