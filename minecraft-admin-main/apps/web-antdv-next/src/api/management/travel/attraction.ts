// @ts-nocheck
import request from '#/utils/request';

/**
 * 景点相关接口
 */

// 获取景点列表
export const getAttractionList = (params: any): Promise<any> => {
  return request.get('/attraction/list', { params })
}

// 获取景点详情
export const getAttractionDetail = (id: any): Promise<any> => {
  return request.get(`/attraction/${id}`)
}

// 添加景点
export const addAttraction = (data: any): Promise<any> => {
  return request.post('/attraction/add', data)
}

// 更新景点
export const updateAttraction = (data: any): Promise<any> => {
  return request.put('/attraction/update', data)
}

// 删除景点
export const deleteAttraction = (id: any): Promise<any> => {
  return request.delete(`/attraction/${id}`)
}

// 获取推荐景点
export const getRecommendattraction = (city: any, season: any, limit: number = 10): Promise<any> => {
  return request.get('/attraction/recommend', {
    params: {
      city,
      season,
      limit
    }
  })
}

// 获取热门景点
export const getHotattraction = (limit: number = 10): Promise<any> => {
  return request.get('/attraction/hot', {
    params: {
      limit
    }
  })
}
