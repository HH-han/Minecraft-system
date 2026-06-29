import request from '@/utils/request'

/**
 * 旅行群组管理接口
 */

// 创建群组
export const createGroup = (data) => {
  return request.post('/api/group/create', data)
}

// 更新群组信息
export const updateGroup = (groupId, data) => {
  return request.put(`/api/group/${groupId}`, data)
}

// 解散群组
export const dissolveGroup = (groupId) => {
  return request.delete(`/api/group/${groupId}`)
}

// 获取群组详情
export const getGroupDetail = (groupId) => {
  return request.get(`/api/group/${groupId}`)
}

// 获取我的群组列表
export const getMyGroups = () => {
  return request.get('/api/group/list')
}

// 搜索公开群组
export const searchPublicGroups = (keyword) => {
  return request.get('/api/group/search', { params: { keyword } })
}

// 转让群主
export const transferOwner = (groupId, newOwnerId) => {
  return request.post(`/api/group/${groupId}/transfer`, null, { params: { newOwnerId } })
}

/**
 * 群组成员管理接口
 */

// 邀请成员
export const inviteMembers = (groupId, data) => {
  return request.post(`/api/group/${groupId}/member/invite`, data)
}

// 申请加入群组
export const applyToJoin = (groupId) => {
  return request.post(`/api/group/${groupId}/member/apply`)
}

// 审批入群申请
export const approveJoin = (groupId, userId) => {
  return request.put(`/api/group/${groupId}/member/${userId}/approve`)
}

// 拒绝入群申请
export const rejectJoin = (groupId, userId) => {
  return request.put(`/api/group/${groupId}/member/${userId}/reject`)
}

// 移除成员
export const removeMember = (groupId, userId) => {
  return request.delete(`/api/group/${groupId}/member/${userId}`)
}

// 退出群组
export const exitGroup = (groupId) => {
  return request.post(`/api/group/${groupId}/member/exit`)
}

// 修改成员角色
export const changeRole = (groupId, userId, role) => {
  return request.put(`/api/group/${groupId}/member/${userId}/role`, null, { params: { role } })
}

// 获取成员列表
export const getMembers = (groupId) => {
  return request.get(`/api/group/${groupId}/member/list`)
}

// 获取成员信息
export const getMemberInfo = (groupId, userId) => {
  return request.get(`/api/group/${groupId}/member/${userId}`)
}