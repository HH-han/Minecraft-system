import request from '@/utils/request'

/**
 * 购物车相关接口
 */

// 添加到购物车
export const addToCart = (cart) => {
  return request.post('/cart/add', cart)
}

// 更新购物车
export const updateCart = (id, quantity) => {
  return request.put('/cart/update', null, {
    params: {
      id,
      quantity
    }
  })
}

// 删除购物车商品
export const deleteFromCart = (id) => {
  return request.delete(`/cart/${id}`)
}

// 清空购物车
export const clearCart = () => {
  return request.delete('/cart/clear')
}

// 获取购物车列表
export const getCartList = (params) => {
  return request.get('/cart/list', { params })
}
