import request from '@/utils/request'

/**
 * 投票功能接口 - 管理后台
 */

// 获取群组投票列表
export const getPollList = (groupId, params) => {
  return request.get(`/api/poll/group/${groupId}`, { params })
}

// 获取投票详情
export const getPollDetail = (pollId) => {
  return request.get(`/api/poll/${pollId}`)
}

// 获取投票结果
export const getResult = (pollId) => {
  return request.get(`/api/poll/${pollId}/result`)
}

// 结束投票
export const closePoll = (pollId) => {
  return request.put(`/api/poll/${pollId}/close`)
}

// 删除投票
export const deletePoll = (pollId) => {
  return request.delete(`/api/poll/${pollId}`)
}