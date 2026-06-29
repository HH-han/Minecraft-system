import request from '@/utils/request'

/**
 * AI行程推荐接口 - 管理后台
 */

// 获取历史推荐记录
export const getHistory = (params) => {
  return request.get('/api/ai/trip/history', { params })
}

// 生成AI行程方案
export const generateTrip = (data) => {
  return request.post('/api/ai/trip/generate', data)
}

// 采用方案到群组行程
export const adoptTrip = (id) => {
  return request.post(`/api/ai/trip/adopt/${id}`)
}

// 删除推荐记录
export const deleteRecommendation = (id) => {
  return request.delete(`/api/ai/trip/${id}`)
}