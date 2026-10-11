import request from '@/utils/request'

/**
 * 预订相关接口（景点门票预订 / 酒店预订）
 */

// 创建景点门票预订
export const createTicketBooking = (data) => {
  return request.post('/booking/ticket', data)
}

// 查询景点门票预订详情
export const getTicketBookingDetail = (id) => {
  return request.get(`/booking/ticket/${id}`)
}

// 按联系电话查询门票预订列表
export const getTicketBookingList = (phone) => {
  return request.get('/booking/ticket/list', { params: { phone } })
}

// 取消门票预订
export const cancelTicketBooking = (id) => {
  return request.put(`/booking/ticket/${id}/cancel`)
}

// 完成门票预订（支付成功后）
export const completeTicketBooking = (id) => {
  return request.put(`/booking/ticket/${id}/complete`)
}

// 删除门票预订
export const deleteTicketBooking = (id) => {
  return request.delete(`/booking/ticket/${id}`)
}

// 创建酒店预订
export const createHotelBooking = (data) => {
  return request.post('/booking/hotel', data)
}

// 查询酒店预订详情
export const getHotelBookingDetail = (id) => {
  return request.get(`/booking/hotel/${id}`)
}

// 按联系电话查询酒店预订列表
export const getHotelBookingList = (phone) => {
  return request.get('/booking/hotel/list', { params: { phone } })
}

// 取消酒店预订
export const cancelHotelBooking = (id) => {
  return request.put(`/booking/hotel/${id}/cancel`)
}

// 完成酒店预订（支付成功后）
export const completeHotelBooking = (id) => {
  return request.put(`/booking/hotel/${id}/complete`)
}

// 删除酒店预订
export const deleteHotelBooking = (id) => {
  return request.delete(`/booking/hotel/${id}`)
}
