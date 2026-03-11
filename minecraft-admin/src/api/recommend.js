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

// 添加推荐
export const addRecommend = (data) => {
  return request.post('/recommend/add', data)
}

// 更新推荐
export const updateRecommend = (data) => {
  return request.put('/recommend/update', data)
}

// 删除推荐
export const deleteRecommend = (id) => {
  return request.delete(`/recommend/${id}`)
}
