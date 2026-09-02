// @ts-nocheck
import request from '#/utils/request';

/**
 * 积分系统相关接口
 */

// 获取用户积分
export const getUserPoints = (): Promise<any> => {
  return request.get('/user/points')
}

// 获取积分记录
export const getPointsRecords = (page: number = 1, size: number = 10): Promise<any> => {
  return request.get('/user/points/records', {
    params: {
      page,
      size
    }
  })
}

// 获取商品列表
export const getProducts = (): Promise<any> => {
  return request.get('/points/products')
}

// 兑换商品
export const exchangeProduct = (productId: any): Promise<any> => {
  return request.post('/exchange', {
    productId
  })
}

// 获取兑换订单
export const getExchangeOrders = (page: number = 1, size: number = 10): Promise<any> => {
  return request.get('/exchange/orders', {
    params: {
      page,
      size
    }
  })
}

// 新增商品
export const addProduct = (data: any): Promise<any> => {
  return request.post('/points/products', data)
}

// 修改商品
export const updateProduct = (id: any, data: any): Promise<any> => {
  return request.put(`/points/products/${id}`, data)
}

// 删除商品
export const deleteProduct = (id: any): Promise<any> => {
  return request.delete(`/points/products/${id}`)
}

// 上传积分商品封面图（Multipart：字段名 file，返回 URL/相对路径字符串）
export const uploadProductImage = (file: File): Promise<any> => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/points/products/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
