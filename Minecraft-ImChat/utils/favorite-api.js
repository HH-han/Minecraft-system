/**
 * 收藏接口
 */

import { get, post, put, del } from './request.js'
import { API_CONFIG } from './config.js'

const { FAVORITE } = API_CONFIG
const BASE = FAVORITE.BASE
const E = FAVORITE.ENDPOINTS

/**
 * 新增收藏
 * @param {Object} data { userId, type, title, content, cover, url, tags, source }
 */
export const addFavorite = (data) => post(BASE + E.ADD, data)

/**
 * 编辑收藏
 */
export const updateFavorite = (id, data) => put(BASE + E.UPDATE.replace('{id}', id), data)

/**
 * 分页查询收藏
 * @param {Object} params { userId, type, tag, keyword, page, size }
 */
export const getFavoriteList = (params) => get(BASE + E.LIST, params)

/**
 * 最近使用
 */
export const getRecentFavorites = (userId, limit = 20) =>
  get(BASE + E.RECENT, { userId, limit })

/**
 * 分类统计
 */
export const getFavoriteStats = (userId) => get(BASE + E.STATS, { userId })

/**
 * 标签列表
 */
export const getFavoriteTags = (userId) => get(BASE + E.TAGS, { userId })

/**
 * 收藏详情
 */
export const getFavoriteDetail = (id, userId) => get(BASE + E.DETAIL.replace('{id}', id), { userId })

/**
 * 更新最近使用时间
 */
export const touchFavorite = (id, userId) =>
  post(BASE + E.TOUCH.replace('{id}', id) + `?userId=${userId}`)

/**
 * 删除收藏
 */
export const deleteFavorite = (id, userId) => del(BASE + E.DELETE.replace('{id}', id), { userId })

/**
 * 清空收藏（type 为空表示全部）
 */
export const clearFavorites = (userId, type) =>
  del(BASE + E.CLEAR, type ? { userId, type } : { userId })

export default {
  addFavorite,
  updateFavorite,
  getFavoriteList,
  getRecentFavorites,
  getFavoriteStats,
  getFavoriteTags,
  getFavoriteDetail,
  touchFavorite,
  deleteFavorite,
  clearFavorites
}
