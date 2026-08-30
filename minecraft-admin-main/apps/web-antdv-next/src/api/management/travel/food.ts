// @ts-nocheck
import request from '#/utils/request';

/**
 * 美食相关接口
 */

// 获取美食列表
export const getFoodList = (params: any): Promise<any> => {
  return request.get('/food/list', { params })
}

// 获取美食详情
export const getFoodDetail = (id: any): Promise<any> => {
  return request.get(`/food/${id}`)
}

// 获取推荐美食
export const getRecommendFoods = (city: any, limit: number = 10): Promise<any> => {
  return request.get('/food/recommend', {
    params: {
      city,
      limit
    }
  })
}

// 添加美食
export const addFood = (food: any): Promise<any> => {
  return request.post('/food/add', food)
}

// 更新美食
export const updateFood = (food: any): Promise<any> => {
  return request.put('/food/update', food)
}

// 删除美食
export const deleteFood = (id: any): Promise<any> => {
  return request.delete(`/food/${id}`)
}
