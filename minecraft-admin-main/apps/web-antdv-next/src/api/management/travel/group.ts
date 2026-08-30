// @ts-nocheck
import request from '#/utils/request';

/**
 * 跟团游相关接口
 */

// 获取旅行团列表
export const getGroupList = (params: any): Promise<any> => {
  return request.get('/group/list', { params })
}

// 获取旅行团详情
export const getGroupDetail = (id: any): Promise<any> => {
  return request.get(`/group/${id}`)
}

// 创建旅行团
export const createGroup = (group: any): Promise<any> => {
  return request.post('/group/create', group)
}

// 加入旅行团
export const joinGroup = (groupId: any): Promise<any> => {
  return request.post('/group/join', null, {
    params: {
      groupId
    }
  })
}

// 退出旅行团
export const leaveGroup = (groupId: any): Promise<any> => {
  return request.post('/group/leave', null, {
    params: {
      groupId
    }
  })
}

// 修改旅行团
export const updateGroup = (group: any): Promise<any> => {
  return request.put('/group/update', group)
}

// 删除旅行团
export const deleteGroup = (id: any): Promise<any> => {
  return request.delete(`/group/${id}`)
}
