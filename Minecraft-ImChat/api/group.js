/**
 * 群组管理接口
 */

import { API_CONFIG } from '../utils/config.js'
import { get, post, del } from '../utils/request.js'

const { CHAT_GROUP } = API_CONFIG

/**
 * 获取群组列表（按用户ID）
 * @param {String|Number} userId 用户 ID
 * @returns {Promise} 群组列表
 */
export const getGroupsByUserId = (userId) => {
  return get(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.USER_GROUPS.replace('{userId}', userId))
}

/**
 * 获取群组详情
 * @param {String|Number} groupId 群组 ID
 * @returns {Promise} 群组详情
 */
export const getGroupById = (groupId) => {
  return get(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.DETAIL.replace('{id}', groupId))
}

/**
 * 创建群组
 * @param {Object} group 群组数据
 * @returns {Promise} 创建结果
 */
export const createGroup = (group) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.CREATE, group)
}

/**
 * 创建群组（包含成员）
 * @param {Object} groupData 群组数据
 * @returns {Promise} 创建结果
 */
export const createGroupWithMembers = (groupData) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.CREATE_WITH_MEMBERS, groupData)
}

/**
 * 更新群组信息
 * @param {Object} groupData 群组数据
 * @returns {Promise} 更新结果
 */
export const updateGroup = (groupData) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.UPDATE, groupData, {
    header: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 解散群组
 * @param {String|Number} groupId 群组 ID
 * @returns {Promise} 删除结果
 */
export const deleteGroup = (groupId) => {
  return del(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.DELETE.replace('{id}', groupId))
}

/**
 * 获取群组成员列表
 * @param {String|Number} groupId 群组 ID
 * @returns {Promise} 成员列表
 */
export const getGroupMembers = (groupId) => {
  return get(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.MEMBERS.replace('{id}', groupId))
}

/**
 * 邀请好友加入群组
 * @param {String|Number} groupId 群组 ID
 * @param {Array} friendIds 好友 ID 列表
 * @returns {Promise} 邀请结果
 */
export const inviteFriendsToGroup = (groupId, friendIds) => {
  return post(CHAT_GROUP.BASE + CHAT_GROUP.ENDPOINTS.INVITE.replace('{id}', groupId), friendIds)
}

/**
 * 移除群组成员
 * @param {String|Number} groupId 群组 ID
 * @param {String|Number} userId 用户 ID
 * @returns {Promise} 移除结果
 */
export const removeGroupMember = (groupId, userId) => {
  return del(
    CHAT_GROUP.BASE +
    CHAT_GROUP.ENDPOINTS.REMOVE_MEMBER
      .replace('{id}', groupId)
      .replace('{userId}', userId)
  )
}

export default {
  getGroupsByUserId,
  getGroupById,
  createGroup,
  createGroupWithMembers,
  updateGroup,
  deleteGroup,
  getGroupMembers,
  inviteFriendsToGroup,
  removeGroupMember
}
