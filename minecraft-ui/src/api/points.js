import request from '@/utils/request'

/**
 * 积分系统相关接口
 */

// 获取用户积分
export const getUserPoints = () => {
  return request.get('/user/points')
}

// 获取积分记录
export const getPointsRecords = (page = 1, size = 10) => {
  return request.get('/user/points/records', {
    params: {
      page,
      size
    }
  })
}

// 获取商品列表
export const getProducts = () => {
  return request.get('/points/products')
}

// 兑换商品
export const exchangeProduct = (productId) => {
  return request.post('/exchange', {
    productId
  })
}

// 获取兑换订单
export const getExchangeOrders = (page = 1, size = 10) => {
  return request.get('/exchange/orders', {
    params: {
      page,
      size
    }
  })
}
