import request from '@/utils/request'

/**
 * 酒店相关接口
 */

// 获取酒店列表
export const getHotelList = (params) => {
  return request.get('/hotel/list', { params })
}

// 获取酒店详情
export const getHotelDetail = (id) => {
  return request.get(`/hotel/${id}`)
}

// 获取推荐酒店
export const getRecommendHotels = (city, limit = 10) => {
  return request.get('/hotel/recommend', {
    params: {
      city,
      limit
    }
  })
}

// 添加酒店
export const addHotel = (hotel) => {
  return request.post('/hotel/add', hotel)
}

// 更新酒店
export const updateHotel = (hotel) => {
  return request.put('/hotel/update', hotel)
}

// 删除酒店
export const deleteHotel = (id) => {
  return request.delete(`/hotel/${id}`)
}
