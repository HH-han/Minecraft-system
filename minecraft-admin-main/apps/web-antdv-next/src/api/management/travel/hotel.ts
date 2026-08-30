// @ts-nocheck
import request from '#/utils/request';

/**
 * 酒店相关接口
 */

// 获取酒店列表
export const getHotelList = (params: any): Promise<any> => {
  return request.get('/hotel/list', { params })
}

// 获取酒店详情
export const getHotelDetail = (id: any): Promise<any> => {
  return request.get(`/hotel/${id}`)
}

// 获取推荐酒店
export const getRecommendHotels = (city: any, limit: number = 10): Promise<any> => {
  return request.get('/hotel/recommend', {
    params: {
      city,
      limit
    }
  })
}

// 添加酒店
export const addHotel = (hotel: any): Promise<any> => {
  return request.post('/hotel/add', hotel)
}

// 更新酒店
export const updateHotel = (hotel: any): Promise<any> => {
  return request.put('/hotel/update', hotel)
}

// 删除酒店
export const deleteHotel = (id: any): Promise<any> => {
  return request.delete(`/hotel/${id}`)
}
