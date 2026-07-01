import request from '@/utils/request'

/**
 * 跟团游相关接口
 */

// 获取旅行团列表
export const getGroupList = (params) => {
  return request.get('/grouptravel/list', { params })
}

// 获取旅行团详情
export const getGroupDetail = (id) => {
  return request.get(`/grouptravel/${id}`)
}

// 创建旅行团
export const createGroup = (group) => {
  return request.post('/grouptravel/create', group)
}

// 加入旅行团
export const joinGroup = (groupId) => {
  return request.post('/grouptravel/join', null, {
    params: {
      groupId
    }
  })
}

// 退出旅行团
export const leaveGroup = (groupId) => {
  return request.post('/grouptravel/leave', null, {
    params: {
      groupId
    }
  })
}
