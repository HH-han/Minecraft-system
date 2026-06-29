import request from '@/utils/request'

/**
 * 位置共享接口
 */

// 更新当前位置
export const updateLocation = (data) => {
  return request.post('/api/location/update', data)
}

// 获取群组成员的实时位置
export const getGroupLocations = (groupId) => {
  return request.get(`/api/location/group/${groupId}`)
}

// 获取成员位置历史
export const getUserLocationHistory = (groupId, userId) => {
  return request.get(`/api/location/history/${groupId}/${userId}`)
}