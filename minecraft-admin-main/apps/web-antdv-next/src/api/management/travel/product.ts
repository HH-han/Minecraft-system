// @ts-nocheck
import request from '#/utils/request';

/**
 * 产品相关接口
 */

// 获取商品列表
export const getProductList = (params: any): Promise<any> => {
  return request.get('/product/list', { params })
}

// 获取商品详情
export const getProductDetail = (id: any): Promise<any> => {
  return request.get(`/product/${id}`)
}

// 获取推荐商品
export const getRecommendProducts = (city: any, limit: number = 10): Promise<any> => {
  return request.get('/product/recommend', {
    params: {
      city,
      limit
    }
  })
}

// 添加商品
export const addProduct = (product: any): Promise<any> => {
  return request.post('/product/add', product)
}

// 更新商品
export const updateProduct = (product: any): Promise<any> => {
  return request.put('/product/update', product)
}

// 删除商品
export const deleteProduct = (id: any): Promise<any> => {
  return request.delete(`/product/${id}`)
}
