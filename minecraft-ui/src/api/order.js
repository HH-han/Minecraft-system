import request from '@/utils/request'

/**
 * 订单相关接口
 */

// 创建订单
export const createOrder = (orderRequest) => {
  return request.post('/order/create', orderRequest)
}

// 获取订单列表
export const getOrderList = (params) => {
  return request.get('/order/list', { params })
}

// 获取订单详情
export const getOrderDetail = (id) => {
  return request.get(`/order/${id}`)
}

// 取消订单
export const cancelOrder = (id) => {
  return request.put(`/order/${id}/cancel`)
}

// 删除订单
export const deleteOrder = (id) => {
  return request.delete(`/order/${id}`)
}
