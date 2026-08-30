// @ts-nocheck
import request from '#/utils/request';

/**
 * 位置共享接口 - 管理后台
 */

// 获取群组成员的实时位置
export const getGroupLocations = (groupId: any): Promise<any> => {
  return request.get(`/api/location/group/${groupId}`)
}

// 获取成员位置历史
export const getUserLocationHistory = (groupId: any, userId: any, params: any): Promise<any> => {
  return request.get(`/api/location/history/${groupId}/${userId}`, { params })
}