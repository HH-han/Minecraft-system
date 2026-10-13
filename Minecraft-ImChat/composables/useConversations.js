/**
 * 会话聚合（对齐 PC 端 SidebarList 的“好友 + 群”合并排序思路）
 */

import { convKey } from '../store/index.js'

/**
 * 把好友、群与全局会话摘要合并为统一会话列表
 * @param {Array} friends 好友（{ id, name, avatar, online }）
 * @param {Array} groups 群（{ id, name, avatar, memberCount }）
 * @param {Object} summaries 全局会话摘要，形如 { 'single-1': { lastMessage, lastTime, unread, isTop } }
 */
export function buildConversations(friends = [], groups = [], summaries = {}) {
  const merged = [
    ...friends.map((f) => ({
      type: 'single',
      id: f.id,
      name: f.name,
      avatar: f.avatar || '',
      online: !!f.online,
      memberCount: 0
    })),
    ...groups.map((g) => ({
      type: 'group',
      id: g.id,
      name: g.name,
      avatar: g.avatar || '',
      online: false,
      memberCount: g.memberCount || 0
    }))
  ]

  return merged.map((conv) => {
    const summary = summaries[convKey(conv.type, conv.id)] || {}
    return {
      ...conv,
      lastMessage: summary.lastMessage || '',
      lastTime: summary.lastTime || 0,
      unread: summary.unread || 0,
      isTop: !!summary.isTop
    }
  })
}

/**
 * 排序：置顶优先，其次按最后消息时间倒序
 */
export function sortConversations(list = []) {
  return [...list].sort((a, b) => {
    if (a.isTop !== b.isTop) return a.isTop ? -1 : 1
    return (b.lastTime || 0) - (a.lastTime || 0)
  })
}
