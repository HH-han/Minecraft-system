import request from '@/utils/request'

/**
 * 跟团游相关接口
 */

// 获取旅行团列表
export const getGroupList = (params) => {
  return request.get('/group/list', { params })
}

// 获取旅行团详情
export const getGroupDetail = (id) => {
  return request.get(`/group/${id}`)
}

// 创建旅行团
export const createGroup = (group) => {
  return request.post('/group/create', group)
}

// 加入旅行团
export const joinGroup = (groupId) => {
  return request.post('/group/join', null, {
    params: {
      groupId
    }
  })
}

// 退出旅行团
export const leaveGroup = (groupId) => {
  return request.post('/group/leave', null, {
    params: {
      groupId
    }
  })
}

// 修改旅行团
export const updateGroup = (group) => {
  return request.put('/group/update', group)
}

// 删除旅行团
export const deleteGroup = (id) => {
  return request.delete(`/group/${id}`)
}
