/**
 * 聊天管理接口
 */

import { API_CONFIG } from '../utils/config.js'
import { get, post } from '../utils/request.js'

const { CHAT } = API_CONFIG

/**
 * 发送私聊消息
 * @param {Object} message 消息内容
 * @returns {Promise} 发送结果
 */
export const sendSingleMessage = (message) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.SEND_SINGLE, message)
}

/**
 * 获取私聊历史记录
 * @param {String|Number} userId1 用户1 ID
 * @param {String|Number} userId2 用户2 ID
 * @param {Number} limit 数量限制
 * @returns {Promise} 历史记录
 */
export const getSingleChatHistory = (userId1, userId2, limit = 50) => {
  return get(CHAT.BASE + CHAT.ENDPOINTS.HISTORY_SINGLE, { userId1, userId2, limit })
}

/**
 * 发送群聊消息
 * @param {Object} message 消息内容
 * @returns {Promise} 发送结果
 */
export const sendGroupMessage = (message) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.SEND_GROUP, message)
}

/**
 * 获取群聊历史记录
 * @param {String|Number} groupId 群组 ID
 * @param {Number} limit 数量限制
 * @returns {Promise} 历史记录
 */
export const getGroupChatHistory = (groupId, limit = 50) => {
  return get(CHAT.BASE + CHAT.ENDPOINTS.HISTORY_GROUP, { groupId, limit })
}

/**
 * 标记消息已读
 * @param {String|Number} friendId 好友 ID
 * @returns {Promise} 标记结果
 */
export const markAsRead = (friendId) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.MARK_READ, { friendId })
}

/**
 * 文件上传
 * @param {String} filePath 文件路径
 * @param {Object} formData 附加数据
 * @returns {Promise} 上传结果
 */
export const uploadFile = (filePath, formData = {}) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.UPLOAD, { filePath, ...formData })
}

export default {
  sendSingleMessage,
  getSingleChatHistory,
  sendGroupMessage,
  getGroupChatHistory,
  markAsRead,
  uploadFile
}
