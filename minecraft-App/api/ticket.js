import request from '@/utils/request'

// 获取票务列表
export const getTicketList = (params) => {
  return request.get('/ticket/list', { params })
}

// 获取票务详情
export const getTicketDetail = (id) => {
  return request.get(`/ticket/${id}`)
}

// 添加票务
export const addTicket = (data) => {
  return request.post('/ticket/add', data)
}

// 更新票务
export const updateTicket = (data) => {
  return request.put('/ticket/update', data)
}

// 删除票务
export const deleteTicket = (id) => {
  return request.delete(`/ticket/${id}`)
}
