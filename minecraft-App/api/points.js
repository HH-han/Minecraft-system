import request from '@/utils/request'

/**
 * 积分系统相关接口
 */

// 获取积分兑换商品列表
export const getPointsProducts = (params) => {
  return request.get('/products', { params })
}

// 获取积分兑换商品详情
export const getPointsProductDetail = (id) => {
  return request.get(`/products/${id}`)
}

// 积分兑换商品
export const exchangeProduct = (data) => {
  return request.post('/exchange/product', data)
}

// 获取兑换订单列表
export const getExchangeOrders = (params) => {
  return request.get('/exchange/orders', { params })
}

// 获取积分记录
export const getPointsRecords = () => {
  return request.get('/user/points/records')
}
