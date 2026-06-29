import request from '@/utils/request'

/**
 * AI行程推荐接口
 */

// 生成AI行程方案
export const generateTrip = (data) => {
  return request.post('/api/ai/trip/generate', data)
}

// 采用方案到群组行程
export const adoptTrip = (id) => {
  return request.post(`/api/ai/trip/adopt/${id}`)
}

// 获取历史推荐记录
export const getHistory = () => {
  return request.get('/api/ai/trip/history')
}