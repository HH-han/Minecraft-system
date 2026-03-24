import request from '@/utils/request'

/**
 * 首页推荐相关接口
 */

// 获取首页推荐列表
export const getHomeRecommendationsList = (params) => {
  return request.get('/home-recommendations/list', { params })
}

// 获取首页推荐详情
export const getHomeRecommendationById = (id) => {
  return request.get(`/home-recommendations/${id}`)
}

// 获取热门推荐
export const getHotRecommendations = (limit = 10) => {
  return request.get('/home-recommendations/hot', {
    params: {
      limit
    }
  })
}

// 获取新晋推荐
export const getNewRecommendations = (limit = 10) => {
  return request.get('/home-recommendations/new', {
    params: {
      limit
    }
  })
}

// 按分类获取推荐
export const getRecommendationsByCategory = (categoryId, limit = 10) => {
  return request.get(`/home-recommendations/category/${categoryId}`, {
    params: {
      limit
    }
  })
}

// 按城市获取推荐
export const getRecommendationsByCity = (city, limit = 10) => {
  return request.get(`/home-recommendations/city/${city}`, {
    params: {
      limit
    }
  })
}
