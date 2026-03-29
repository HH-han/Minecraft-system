import request from '@/utils/request'

/**
 * 推荐相关接口
 */

// 获取推荐列表
export const getRecommendList = () => {
  return request.get('/recommend/list')
}

// 获取推荐详情
export const getRecommendById = (id) => {
  return request.get(`/recommend/${id}`)
}
