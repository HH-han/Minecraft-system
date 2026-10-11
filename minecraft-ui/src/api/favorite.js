import request from '@/utils/request'

/**
 * 收藏相关接口（微信风格「收藏」）
 */

// 新增收藏
export const addFavorite = (data) => {
  return request.post('/public/favorite/add', data)
}

// 编辑收藏
export const updateFavorite = (id, data) => {
  return request.put(`/public/favorite/${id}`, data)
}

// 分页查询收藏
export const getFavoriteList = (params) => {
  return request.get('/public/favorite/list', { params })
}

// 最近使用的收藏
export const getRecentFavorites = (userId, limit = 20) => {
  return request.get('/public/favorite/recent', { params: { userId, limit } })
}

// 分类统计
export const getFavoriteStats = (userId) => {
  return request.get('/public/favorite/stats', { params: { userId } })
}

// 标签列表
export const getFavoriteTags = (userId) => {
  return request.get('/public/favorite/tags', { params: { userId } })
}

// 收藏详情
export const getFavoriteDetail = (id, userId) => {
  return request.get(`/public/favorite/${id}`, { params: { userId } })
}

// 更新最近使用时间
export const touchFavorite = (id, userId) => {
  return request.post(`/public/favorite/${id}/touch`, null, { params: { userId } })
}

// 删除收藏
export const deleteFavorite = (id, userId) => {
  return request.delete(`/public/favorite/${id}`, { params: { userId } })
}

// 清空收藏（type 为空表示全部）
export const clearFavorites = (userId, type) => {
  return request.delete('/public/favorite/clear', { params: { userId, type } })
}
