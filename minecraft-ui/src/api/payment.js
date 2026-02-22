import request from '@/utils/request'

// 创建支付
export const createPayment = (params) => {
  return request.post('/payment/create', null, { params })
}

// 获取支付详情
export const getPaymentDetail = (id) => {
  return request.get(`/payment/${id}`)
}

// 取消支付
export const cancelPayment = (id) => {
  return request.put(`/payment/${id}/cancel`)
}
