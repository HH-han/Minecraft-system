import request from '@/utils/request'

/**
 * 景点相关接口
 */

// 获取景点列表
export const getAttractionList = (params) => {
  return request.get('/attraction/list', { params })
}

// 获取景点详情
export const getAttractionDetail = (id) => {
  return request.get(`/attraction/${id}`)
}

// 获取推荐景点
export const getRecommendAttractions = (city, season, limit = 10) => {
  return request.get('/attraction/recommend', {
    params: {
      city,
      season,
      limit
    }
  })
}

// 获取热门景点
export const getHotAttractions = (limit = 10) => {
  return request.get('/attraction/hot', {
    params: {
      limit
    }
  })
}
