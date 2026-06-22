/**
 * 好友管理接口
 */

import { API_CONFIG } from '../utils/config.js'
import { get, post, put, del } from '../utils/request.js'

const { FRIEND } = API_CONFIG

/**
 * 获取好友列表
 * @param {String|Number} userId 用户 ID
 * @returns {Promise} 好友列表
 */
export const getFriendList = (userId) => {
  return get(FRIEND.BASE + FRIEND.ENDPOINTS.LIST, { userId })
}

/**
 * 获取好友详细信息列表
 * @param {String|Number} userId 用户 ID
 * @returns {Promise} 好友详细信息列表
 */
export const getFriendInfoList = (userId) => {
  return get(FRIEND.BASE + FRIEND.ENDPOINTS.INFO_LIST, { userId })
}

/**
 * 发送好友请求
 * @param {Object} data 请求数据
 * @returns {Promise} 发送结果
 */
export const sendFriendRequest = (data) => {
  return post(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST, data)
}

/**
 * 获取待处理好友请求
 * @param {String|Number} userId 用户 ID
 * @returns {Promise} 待处理请求列表
 */
export const getPendingFriendRequests = (userId) => {
  return get(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST_PENDING, { userId })
}

/**
 * 接受好友请求
 * @param {String|Number} requestId 请求 ID
 * @returns {Promise} 处理结果
 */
export const acceptFriendRequest = (requestId) => {
  return put(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST_ACCEPT, { requestId })
}

/**
 * 拒绝好友请求
 * @param {String|Number} requestId 请求 ID
 * @returns {Promise} 处理结果
 */
export const rejectFriendRequest = (requestId) => {
  return put(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST_REJECT, { requestId })
}

/**
 * 删除好友
 * @param {String|Number} userId 用户 ID
 * @param {String|Number} friendId 好友 ID
 * @returns {Promise} 删除结果
 */
export const deleteFriend = (userId, friendId) => {
  return del(FRIEND.BASE + FRIEND.ENDPOINTS.DELETE, { userId, friendId })
}

/**
 * 修改好友备注
 * @param {String|Number} userId 用户 ID
 * @param {String|Number} friendId 好友 ID
 * @param {String} remark 备注
 * @returns {Promise} 修改结果
 */
export const updateFriendRemark = (userId, friendId, remark) => {
  return put(FRIEND.BASE + FRIEND.ENDPOINTS.REMARK, { userId, friendId, remark })
}

export default {
  getFriendList,
  getFriendInfoList,
  sendFriendRequest,
  getPendingFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest,
  deleteFriend,
  updateFriendRemark
}
