/**
 * 朋友圈接口
 */

import { get, post, del, upload } from './request.js'
import { API_CONFIG } from './config.js'

const { MOMENT } = API_CONFIG
const BASE = MOMENT.BASE
const E = MOMENT.ENDPOINTS

/**
 * 发布动态
 * @param {Object} data { userId, content, images, location, visibility }
 */
export const publishMoment = (data) => post(BASE + E.PUBLISH, data)

/**
 * 获取信息流（自己 + 好友）
 */
export const getMomentFeed = (userId, page = 1, size = 10) =>
  get(BASE + E.FEED, { userId, page, size })

/**
 * 获取指定用户的动态
 */
export const getUserMoments = (userId, targetUserId, page = 1, size = 10) =>
  get(BASE + E.USER, { userId, targetUserId, page, size })

/**
 * 删除动态
 */
export const deleteMoment = (momentId, userId) =>
  del(BASE + E.DELETE.replace('{id}', momentId), { userId })

/**
 * 点赞 / 取消点赞
 */
export const toggleMomentLike = (momentId, userId) =>
  post(BASE + E.LIKE.replace('{id}', momentId) + `?userId=${userId}`)

/**
 * 点赞用户列表
 */
export const getMomentLikes = (momentId) => get(BASE + E.LIKES.replace('{id}', momentId))

/**
 * 发表评论
 */
export const addMomentComment = (momentId, data) =>
  post(BASE + E.COMMENT.replace('{id}', momentId), data)

/**
 * 评论列表
 */
export const getMomentComments = (momentId) => get(BASE + E.COMMENTS.replace('{id}', momentId))

/**
 * 删除评论
 */
export const deleteMomentComment = (commentId, userId) =>
  del(BASE + E.DELETE_COMMENT.replace('{id}', commentId), { userId })

/**
 * 获取封面
 */
export const getMomentCover = (userId) => get(BASE + E.COVER, { userId })

/**
 * 上传封面
 */
export const uploadMomentCover = (userId, filePath) =>
  upload(BASE + E.COVER, filePath, { userId })

export default {
  publishMoment,
  getMomentFeed,
  getUserMoments,
  deleteMoment,
  toggleMomentLike,
  getMomentLikes,
  addMomentComment,
  getMomentComments,
  deleteMomentComment,
  getMomentCover,
  uploadMomentCover
}
