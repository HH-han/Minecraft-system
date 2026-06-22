/**
 * 聊天模块 API
 */

const BASE_URL = 'https://api.mc-system.com'

/**
 * 通用请求封装
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({ url: '/pages/login/login' })
          reject(new Error('未授权'))
        } else {
          reject(new Error(res.data?.message || '请求失败'))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络请求失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

/**
 * ==================== 认证相关 ====================
 */

/**
 * 登录
 * @param {Object} data - 登录参数 { account, password }
 * @returns {Promise} 登录响应 { code, data, message }
 */
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'POST',
    data: data
  })
}

/**
 * 注册
 * @param {Object} data - 注册参数 { username, password, email, phone, avatar }
 * @returns {Promise} 注册响应
 */
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'POST',
    data: data
  })
}

/**
 * 退出登录
 * @returns {Promise} 退出登录响应
 */
export const logout = () => {
  return request({
    url: '/user/logout',
    method: 'POST'
  })
}

/**
 * 根据账号获取用户信息（用于登录时获取头像）
 * @param {String} account - 用户账号
 * @returns {Promise} 用户信息
 */
export const getUserByAccount = (account) => {
  return request({
    url: '/public/user/account/' + account,
    method: 'GET'
  })
}

/**
 * 忘记密码 - 发送验证码
 * @param {String} phone - 手机号
 * @returns {Promise} 发送结果
 */
export const sendResetCode = (phone) => {
  return request({
    url: '/auth/reset-password/code',
    method: 'POST',
    data: { phone }
  })
}

/**
 * 忘记密码 - 重置密码
 * @param {Object} data - { phone, code, newPassword }
 * @returns {Promise} 重置结果
 */
export const resetPassword = (data) => {
  return request({
    url: '/auth/reset-password',
    method: 'POST',
    data: data
  })
}

/**
 * ==================== 聊天相关 ====================
 */

/**
 * 发送私聊消息
 */
export const sendSingleMessage = (message) => {
  return request({
    url: '/public/im/single/send',
    method: 'POST',
    data: message
  })
}

/**
 * 获取私聊历史记录
 */
export const getSingleChatHistory = (userId1, userId2, limit = 50) => {
  return request({
    url: '/public/im/single/history',
    method: 'GET',
    data: { userId1, userId2, limit }
  })
}

/**
 * 发送群聊消息
 */
export const sendGroupMessage = (message) => {
  return request({
    url: '/public/im/group/send',
    method: 'POST',
    data: message
  })
}

/**
 * 获取群聊历史记录
 */
export const getGroupChatHistory = (groupId, limit = 50) => {
  return request({
    url: '/public/im/group/history',
    method: 'GET',
    data: { groupId, limit }
  })
}

/**
 * 标记消息已读
 */
export const markAsRead = (friendId) => {
  return request({
    url: '/chat/read',
    method: 'POST',
    data: { friendId }
  })
}

/**
 * 获取好友列表
 */
export const getFriendList = (userId) => {
  return request({
    url: '/public/im/friend/list',
    method: 'GET',
    data: { userId }
  })
}

/**
 * 获取好友详细信息列表
 */
export const getFriendInfoList = (userId) => {
  return request({
    url: '/public/im/friend/info/list',
    method: 'GET',
    data: { userId }
  })
}

/**
 * 发送好友请求
 */
export const sendFriendRequest = (data) => {
  return request({
    url: '/public/im/friend/request',
    method: 'POST',
    data: data
  })
}

/**
 * 获取待处理好友请求
 */
export const getPendingFriendRequests = (userId) => {
  return request({
    url: '/public/im/friend/request/pending',
    method: 'GET',
    data: { userId }
  })
}

/**
 * 接受好友请求
 */
export const acceptFriendRequest = (requestId) => {
  return request({
    url: '/public/im/friend/request/accept',
    method: 'PUT',
    data: { requestId }
  })
}

/**
 * 拒绝好友请求
 */
export const rejectFriendRequest = (requestId) => {
  return request({
    url: '/public/im/friend/request/reject',
    method: 'PUT',
    data: { requestId }
  })
}

/**
 * 删除好友
 */
export const deleteFriend = (userId, friendId) => {
  return request({
    url: '/public/im/friend/delete',
    method: 'DELETE',
    data: { userId, friendId }
  })
}

/**
 * 修改好友备注
 */
export const updateFriendRemark = (userId, friendId, remark) => {
  return request({
    url: '/public/im/friend/remark',
    method: 'PUT',
    data: { userId, friendId, remark }
  })
}

/**
 * 获取群组列表（按用户ID）
 */
export const getGroupsByUserId = (userId) => {
  return request({
    url: '/public/chat-group/user/' + userId,
    method: 'GET'
  })
}

/**
 * 获取群组详情
 */
export const getGroupById = (groupId) => {
  return request({
    url: '/public/chat-group/' + groupId,
    method: 'GET'
  })
}

/**
 * 创建群组
 */
export const createGroup = (group) => {
  return request({
    url: '/public/chat-group',
    method: 'POST',
    data: group
  })
}

/**
 * 创建群组（包含成员）
 */
export const createGroupWithMembers = (groupData) => {
  return request({
    url: '/public/chat-group/with-members',
    method: 'POST',
    data: groupData
  })
}

/**
 * 更新群组信息
 */
export const updateGroup = (groupData) => {
  return request({
    url: '/public/chat-group/update',
    method: 'POST',
    data: groupData,
    header: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 解散群组
 */
export const deleteGroup = (groupId) => {
  return request({
    url: '/public/chat-group/' + groupId,
    method: 'DELETE'
  })
}

/**
 * 获取群组成员列表
 */
export const getGroupMembers = (groupId) => {
  return request({
    url: '/public/chat-group/' + groupId + '/members',
    method: 'GET'
  })
}

/**
 * 邀请好友加入群组
 */
export const inviteFriendsToGroup = (groupId, friendIds) => {
  return request({
    url: '/public/chat-group/' + groupId + '/invite',
    method: 'POST',
    data: friendIds
  })
}

/**
 * 移除群组成员
 */
export const removeGroupMember = (groupId, userId) => {
  return request({
    url: '/public/chat-group/' + groupId + '/members/' + userId,
    method: 'DELETE'
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'GET'
  })
}

/**
 * 上传文件
 */
export const uploadFile = (filePath, formData = {}) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')

    uni.uploadFile({
      url: BASE_URL + '/upload',
      filePath: filePath,
      name: 'file',
      formData: formData,
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data)
        } else {
          uni.showToast({ title: data.message || '上传失败', icon: 'none' })
          reject(new Error(data.message))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

/**
 * 获取表情包列表
 */
export const getEmojiList = () => {
  return request({
    url: '/emoji/list',
    method: 'GET'
  })
}

/**
 * 获取系统表情
 */
export const getSystemEmojis = () => {
  return request({
    url: '/emojis',
    method: 'GET'
  })
}

export default {
  // 认证相关
  login,
  register,
  logout,
  sendResetCode,
  resetPassword,
  // 聊天相关
  sendSingleMessage,
  getSingleChatHistory,
  sendGroupMessage,
  getGroupChatHistory,
  markAsRead,
  getFriendList,
  getFriendInfoList,
  sendFriendRequest,
  getPendingFriendRequests,
  acceptFriendRequest,
  rejectFriendRequest,
  deleteFriend,
  updateFriendRemark,
  getGroupsByUserId,
  getGroupById,
  createGroup,
  createGroupWithMembers,
  updateGroup,
  deleteGroup,
  getGroupMembers,
  inviteFriendsToGroup,
  removeGroupMember,
  getUserByAccount,
  getUserInfo,
  uploadFile,
  getEmojiList,
  getSystemEmojis
}
