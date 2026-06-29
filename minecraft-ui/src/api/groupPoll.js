import request from '@/utils/request'

/**
 * 投票功能接口
 */

// 发起投票
export const createPoll = (data) => {
  return request.post('/poll/create', data)
}

// 投票
export const vote = (pollId, data) => {
  return request.post(`/poll/${pollId}/vote`, data)
}

// 获取投票结果
export const getResult = (pollId) => {
  return request.get(`/poll/${pollId}/result`)
}

// 结束投票
export const closePoll = (pollId) => {
  return request.put(`/poll/${pollId}/close`)
}

// 获取群组投票列表
export const getPollList = (groupId) => {
  return request.get(`/poll/group/${groupId}`)
}