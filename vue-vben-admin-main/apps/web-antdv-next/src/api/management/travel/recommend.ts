// @ts-nocheck
import request from '#/utils/request';

/**
 * 推荐相关接口
 */

// 获取推荐列表
export const getRecommendList = (): Promise<any> => {
  return request.get('/recommend/list')
}

// 获取所有推荐列表（管理用）
export const getAllRecommendList = (): Promise<any> => {
  return request.get('/recommend/list/all')
}

// 根据状态获取推荐列表
export const getRecommendListByState = (state: any): Promise<any> => {
  return request.get('/recommend/list/state', { params: { state } })
}

// 获取推荐详情
export const getRecommendById = (id: any): Promise<any> => {
  return request.get(`/recommend/${id}`)
}

// 添加推荐
export const addRecommend = (data: any): Promise<any> => {
  return request.post('/recommend/add', data)
}

// 更新推荐
export const updateRecommend = (data: any): Promise<any> => {
  return request.put('/recommend/update', data)
}

// 删除推荐
export const deleteRecommend = (id: any): Promise<any> => {
  return request.delete(`/recommend/${id}`)
}
