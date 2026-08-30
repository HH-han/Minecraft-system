// @ts-nocheck
import request from '#/utils/request';

// 获取出行计划列表
export const getTravelPlanList = (params: any): Promise<any> => {
  return request.get('/travel/list', { params })
}

// 获取出行计划详情
export const getTravelPlanDetail = (id: any): Promise<any> => {
  return request.get(`/travel/${id}`)
}

// 创建出行计划
export const createTravelPlan = (data: any): Promise<any> => {
  return request.post('/travel/create', data)
}

// 更新出行计划
export const updateTravelPlan = (data: any): Promise<any> => {
  return request.put('/travel/update', data)
}

// 删除出行计划
export const deleteTravelPlan = (id: any): Promise<any> => {
  return request.delete(`/travel/${id}`)
}
