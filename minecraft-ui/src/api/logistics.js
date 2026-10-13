import request from '@/utils/request'

/**
 * 物流相关接口
 */

// 查询订单下所有包裹
export const getOrderShipping = (orderId) => {
  return request.get(`/logistics/order/${orderId}`)
}

// 查询当前用户全部包裹
export const getMyShipping = () => {
  return request.get('/logistics/my')
}

// 查询运单轨迹
export const getTrack = (trackingNo, phone) => {
  return request.get(`/logistics/track/${trackingNo}`, { params: { phone } })
}

// 确认收货
export const confirmReceive = (shippingId) => {
  return request.post(`/logistics/confirm/${shippingId}`)
}

// 订阅物流推送
export const subscribeLogistics = (shippingId) => {
  return request.post(`/logistics/subscribe/${shippingId}`)
}
