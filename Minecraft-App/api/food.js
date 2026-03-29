import request from '@/utils/request'

/**
 * 美食相关接口
 */

// 获取美食列表
export const getFoodList = (params) => {
  return request.get('/food/list', { params })
}

// 获取美食详情
export const getFoodDetail = (id) => {
  return request.get(`/food/${id}`)
}

// 获取推荐美食
export const getRecommendFoods = (city, limit = 10) => {
  return request.get('/food/recommend', {
    params: {
      city,
      limit
    }
  })
}

// 添加美食
export const addFood = (food) => {
  return request.post('/food/add', food)
}

// 更新美食
export const updateFood = (food) => {
  return request.put('/food/update', food)
}

// 删除美食
export const deleteFood = (id) => {
  return request.delete(`/food/${id}`)
}
