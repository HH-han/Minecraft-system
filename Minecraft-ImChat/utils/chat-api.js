/**
 * 即时通讯模块统一接口层
 *
 * 全项目唯一的接口来源（原 api/ 目录已废弃合并到此处）。
 * 页面统一使用 `import { xxx } from '@/utils/chat-api.js'` 或相对路径引用。
 */

import { get, post, put, del, upload } from './request.js'
import { API_CONFIG, UPLOAD_CONFIG } from './config.js'
import { authApi, userApi, auth } from './auth.js'

const { CHAT, FRIEND, CHAT_GROUP, EMOJI, USER } = API_CONFIG

// ==================== 认证相关 ====================

export const login = authApi.login
export const register = authApi.register
export const logout = authApi.logout
export const sendCode = authApi.sendCode
export const emailLogin = authApi.emailLogin
export const sendResetCode = authApi.sendResetCode
export const resetPassword = authApi.resetPassword

// ==================== 用户相关 ====================

export const getUserInfo = userApi.getUserInfo
export const getUserByAccount = userApi.getUserByAccount

/**
 * 更新当前用户信息
 */
export const updateUserInfo = (data) => post(USER.BASE + USER.ENDPOINTS.UPDATE, data)

// ==================== 聊天相关 ====================

/**
 * 发送私聊消息
 */
export const sendSingleMessage = (message) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.SEND_SINGLE, message)
}

/**
 * 获取私聊历史记录
 */
export const getSingleChatHistory = (userId1, userId2, limit = 50) => {
  return get(CHAT.BASE + CHAT.ENDPOINTS.HISTORY_SINGLE, { userId1, userId2, limit })
}

/**
 * 发送群聊消息
 */
export const sendGroupMessage = (message) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.SEND_GROUP, message)
}

/**
 * 获取群聊历史记录
 */
export const getGroupChatHistory = (groupId, limit = 50) => {
  return get(CHAT.BASE + CHAT.ENDPOINTS.HISTORY_GROUP, { groupId, limit })
}

/**
 * 标记消息已读
 */
export const markAsRead = (friendId) => {
  return post(CHAT.BASE + CHAT.ENDPOINTS.MARK_READ, { friendId })
}

/**
 * 获取未读消息数量
 */
export const getUnreadMessageCount = () => {
  return get(CHAT.BASE + CHAT.ENDPOINTS.UNREAD_COUNT)
}

/**
 * 文件上传
 */
export const uploadFile = (filePath, formData = {}) => {
  return upload(UPLOAD_CONFIG.UPLOAD_URL, filePath, formData)
}

// ==================== 好友相关 ====================

export const getFriendList = (userId) => {
  return get(FRIEND.BASE + FRIEND.ENDPOINTS.LIST, { userId })
}

export const getFriendInfoList = (userId) => {
  return get(FRIEND.BASE + FRIEND.ENDPOINTS.INFO_LIST, { userId })
}

export const sendFriendRequest = (data) => {
  return post(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST, data)
}

export const getPendingFriendRequests = (userId) => {
  return get(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST_PENDING, { userId })
}

export const acceptFriendRequest = (requestId) => {
  return put(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST_ACCEPT, { requestId })
}

export const rejectFriendRequest = (requestId) => {
  return put(FRIEND.BASE + FRIEND.ENDPOINTS.REQUEST_REJECT, { requestId })
}

export const deleteFriend = (userId, friendId) => {
  return del(FRIEND.BASE + FRIEND.ENDPOINTS.DELETE, { userId, friendId })
}

export const updateFriendRemark = (userId, friendId, remark) => {
  return put(FRIEND.BASE + FRIEND.ENDPOINTS.REMARK, { userId, friendId, remark })
}

// ==================== 群组相关 ====================

export const getGroupsByUserId = (userId) => {
  return get(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.USER_GROUPS.replace('{userId}', userId))
}

export const getGroupById = (groupId) => {
  return get(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.DETAIL.replace('{id}', groupId))
}

export const createGroup = (group) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.CREATE, group)
}

export const createGroupWithMembers = (groupData) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.CREATE_WITH_MEMBERS, groupData)
}

const GROUP_UPDATE_URL = CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.UPDATE

/**
 * 更新群资料
 * @param {Object} groupData 群字段（id/name/description/maxMembers/remark 等）
 * @param {String} avatarPath 群头像本地路径（可选，存在时走 uni.uploadFile）
 */
export const updateGroup = (groupData, avatarPath = '') => {
  if (avatarPath) {
    const formData = {}
    Object.keys(groupData).forEach((key) => {
      if (groupData[key] !== undefined && groupData[key] !== null) {
        formData[key] = String(groupData[key])
      }
    })
    return upload(GROUP_UPDATE_URL, avatarPath, formData)
  }
  return post(GROUP_UPDATE_URL, groupData)
}

export const deleteGroup = (groupId) => {
  return del(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.DELETE.replace('{id}', groupId))
}

export const getGroupMembers = (groupId) => {
  return get(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.MEMBERS.replace('{id}', groupId))
}

export const inviteFriendsToGroup = (groupId, friendIds) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.INVITE.replace('{id}', groupId), friendIds)
}

export const removeGroupMember = (groupId, userId) => {
  return del(
    CHAT_GROUP.BASE +
    CHAT_GROUP.ENDPOINTS.REMOVE_MEMBER
      .replace('{id}', groupId)
      .replace('{userId}', userId)
  )
}

// ==================== 表情包相关 ====================

export const getEmojiList = () => {
  return get(EMOJI.BASE + EMOJI.ENDPOINTS.LIST)
}

export const getSystemEmojis = () => {
  return get(EMOJI.BASE + EMOJI.ENDPOINTS.SYSTEM)
}

/**
 * 按分类获取系统表情
 */
export const getGroupedSystemEmojis = () => {
  return get(EMOJI.BASE + EMOJI.ENDPOINTS.GROUPED)
}

// ==================== 默认导出 ====================

export default {
  // 认证
  login,
  register,
  logout,
  sendCode,
  emailLogin,
  sendResetCode,
  resetPassword,
  // 用户
  getUserInfo,
  getUserByAccount,
  updateUserInfo,
  // 聊天
  sendSingleMessage,
  getSingleChatHistory,
  sendGroupMessage,
  getGroupChatHistory,
  markAsRead,
  getUnreadMessageCount,
  uploadFile,
  // 好友
  getFriendList,
  getFriendInfoList,
  sendFriendRequest,
  getPendingFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest,
  deleteFriend,
  updateFriendRemark,
  // 群组
  getGroupsByUserId,
  getGroupById,
  createGroup,
  createGroupWithMembers,
  updateGroup,
  deleteGroup,
  getGroupMembers,
  inviteFriendsToGroup,
  removeGroupMember,
  // 表情
  getEmojiList,
  getSystemEmojis,
  getGroupedSystemEmojis,
  // 业务封装
  auth
}
