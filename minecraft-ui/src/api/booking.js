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

// 创建酒店预订
export const createHotelBooking = (data) => {
  return request.post('/booking/hotel', data)
}

// 查询酒店预订详情
export const getHotelBookingDetail = (id) => {
  return request.get(`/booking/hotel/${id}`)
}
