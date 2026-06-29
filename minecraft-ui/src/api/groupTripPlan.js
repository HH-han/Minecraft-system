import request from '@/utils/request'

/**
 * 行程计划接口
 */

// 添加行程项
export const addTripItem = (groupId, data) => {
  return request.post(`/group/${groupId}/trip`, data)
}

// 更新行程项
export const updateTripItem = (groupId, tripId, data) => {
  return request.put(`/group/${groupId}/trip/${tripId}`, data)
}

// 删除行程项
export const deleteTripItem = (groupId, tripId) => {
  return request.delete(`/group/${groupId}/trip/${tripId}`)
}

// 获取完整行程（按天分组）
export const getTripPlanByDay = (groupId) => {
  return request.get(`/group/${groupId}/trip/list`)
}

// 获取行程列表
export const getTripPlanList = (groupId) => {
  return request.get(`/group/${groupId}/trip/all`)
}