// @ts-nocheck
import request from '#/utils/request';

/**
 * 缓存监控相关接口
 */

// 获取缓存统计信息
export const getCacheStats = (): Promise<any> => {
  return request.get('/cache/stats')
}

// 获取缓存键列表
export const getCacheKeys = (params: any): Promise<any> => {
  return request.get('/cache/keys', { params })
}

// 清除指定缓存（键名含 ":" 等特殊字符，需 URL 编码）
export const clearCache = (key: any): Promise<any> => {
  return request.delete(`/cache/keys/${encodeURIComponent(key)}`)
}

// 清除所有缓存
export const clearAllCache = (): Promise<any> => {
  return request.delete('/cache/keys')
}

// 获取缓存详情
export const getCacheDetail = (key: any): Promise<any> => {
  return request.get(`/cache/keys/${encodeURIComponent(key)}`)
}
