// @ts-nocheck
import request from '#/utils/request';

/**
 * 月份旅行推荐相关接口
 */

// 获取所有月份
export const getAllMonths = (): Promise<any> => {
  return request.get('/monthly-recommendations/months')
}

// 根据月份获取推荐内容
export const getRecommendationsByMonth = (monthName: any): Promise<any> => {
  return request.get(`/monthly-recommendations/by-month/${monthName}`)
}

// 获取推荐内容列表
export const getRecommendationsList = (): Promise<any> => {
  return request.get('/monthly-recommendations/list')
}

// 分页获取推荐内容列表
export const getRecommendationsPage = (current: any, size: any): Promise<any> => {
  return request.get('/monthly-recommendations/page', {
    params: {
      current,
      size
    }
  })
}

// 根据ID获取推荐内容详情
export const getRecommendationDetail = (id: any): Promise<any> => {
  return request.get(`/monthly-recommendations/detail/${id}`)
}

// 添加推荐内容
export const addRecommendation = (data: any): Promise<any> => {
  return request.post('/monthly-recommendations/add', data)
}

// 更新推荐内容
export const updateRecommendation = (data: any): Promise<any> => {
  return request.put('/monthly-recommendations/update', data)
}

// 删除推荐内容
export const deleteRecommendation = (id: any): Promise<any> => {
  return request.delete(`/monthly-recommendations/delete/${id}`)
}
