// @ts-nocheck
import request from '#/utils/request';

/**
 * 旅行群组管理接口 - 管理后台
 */

// 获取群组列表（分页）
export const getGroupList = (params: any): Promise<any> => {
  return request.get('/api/group/list', { params })
}

// 获取群组详情
export const getGroupDetail = (groupId: any): Promise<any> => {
  return request.get(`/api/group/${groupId}`)
}

// 创建群组
export const createGroup = (data: any): Promise<any> => {
  return request.post('/api/group/create', data)
}

// 更新群组信息
export const updateGroup = (groupId: any, data: any): Promise<any> => {
  return request.put(`/api/group/${groupId}`, data)
}

// 解散群组
export const dissolveGroup = (groupId: any): Promise<any> => {
  return request.delete(`/api/group/${groupId}`)
}

// 搜索公开群组
export const searchPublicGroups = (keyword: any): Promise<any> => {
  return request.get('/api/group/search', { params: { keyword } })
}

// 转让群主
export const transferOwner = (groupId: any, newOwnerId: any): Promise<any> => {
  return request.post(`/api/group/${groupId}/transfer`, null, { params: { newOwnerId } })
}

// 获取群组成员列表
export const getMembers = (groupId: any): Promise<any> => {
  return request.get(`/api/group/${groupId}/member/list`)
}

// 获取成员信息
export const getMemberInfo = (groupId: any, userId: any): Promise<any> => {
  return request.get(`/api/group/${groupId}/member/${userId}`)
}

// 移除成员
export const removeMember = (groupId: any, userId: any): Promise<any> => {
  return request.delete(`/api/group/${groupId}/member/${userId}`)
}

// 修改成员角色
export const changeRole = (groupId: any, userId: any, role: any): Promise<any> => {
  return request.put(`/api/group/${groupId}/member/${userId}/role`, null, { params: { role } })
}