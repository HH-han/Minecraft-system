import request from '@/utils/request'

/**
 * 聚合支付接口
 */

// 创建支付（返回 payNo / flowNo / payParams）
export const createPayment = (data) => {
  return request.post('/pay/create', data)
}

// 商城/预订订单支付（兼容接口，内部走聚合支付网关）
export const processPayment = (params) => {
  return request.post('/payment/process', null, { params })
}

// 查询支付详情
export const queryPayment = (payNo) => {
  return request.get(`/pay/query/${payNo}`)
}

// 模拟支付确认
export const mockConfirmPayment = (flowNo) => {
  return request.post(`/pay/mock/confirm/${flowNo}`)
}

// 获取支付详情（兼容接口）
export const getPaymentDetail = (id) => {
  return request.get(`/payment/${id}`)
}

// 取消支付（兼容接口）
export const cancelPayment = (id) => {
  return request.put(`/payment/${id}/cancel`)
}

// 发起退款（管理员）
export const refundPayment = (data) => {
  return request.post('/pay/refund', data)
}
