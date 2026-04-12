import request from '@/utils/request'

/**
 * 产品相关接口
 */

// 获取商品列表
export const getProductList = (params) => {
  return request.get('/product/list', { params })
}

// 获取商品详情
export const getProductDetail = (id) => {
  return request.get(`/product/${id}`)
}

// 获取推荐商品
export const getRecommendProducts = (city, limit = 10) => {
  return request.get('/product/recommend', {
    params: {
      city,
      limit
    }
  })
}

// 添加商品
export const addProduct = (product) => {
  return request.post('/product/add', product)
}

// 更新商品
export const updateProduct = (product) => {
  return request.put('/product/update', product)
}

// 删除商品
export const deleteProduct = (id) => {
  return request.delete(`/product/${id}`)
}
