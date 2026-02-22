import request from '@/utils/request'

// 获取出行计划列表
export const getTravelPlanList = (params) => {
  return request.get('/travel/list', { params })
}

// 获取出行计划详情
export const getTravelPlanDetail = (id) => {
  return request.get(`/travel/${id}`)
}

// 创建出行计划
export const createTravelPlan = (data) => {
  return request.post('/travel/create', data)
}

// 更新出行计划
export const updateTravelPlan = (data) => {
  return request.put('/travel/update', data)
}

// 删除出行计划
export const deleteTravelPlan = (id) => {
  return request.delete(`/travel/${id}`)
}
