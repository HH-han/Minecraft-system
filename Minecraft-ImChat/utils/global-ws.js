/**
 * 全局 WebSocket 消息路由
 *
 * 在 App 启动时注册一次，负责把实时消息写入全局会话摘要（store），
 * 并维护未读角标与待处理好友申请数。业务页面（聊天/通讯录）可再额外监听做局部刷新。
 */

import wsService from './websocket.js'
import store, { bumpUnread, setSummary, currentUserId } from '../store/index.js'
import { getPendingFriendRequests } from './chat-api.js'

let registered = false

function sameId(a, b) {
  return a != null && b != null && String(a) === String(b)
}

function previewOf(data) {
  const type = (data.messageType || 'text').toLowerCase()
  if (type === 'image') return '[图片]'
  if (type === 'file') return '[文件]'
  if (type === 'emoji') return '[表情]'
  if (type === 'voice') return '[语音]'
  if (type === 'video') return '[视频]'
  return data.content || ''
}

function handlePrivateMessage(data) {
  if (!data) return
  // 收到私聊消息时，对方即 senderId
  const otherId = sameId(data.senderId, currentUserId()) ? data.receiverId : data.senderId
  if (!otherId) return
  setSummary('single', otherId, {
    lastMessage: previewOf(data),
    lastTime: Date.now()
  })
  bumpUnread('single', otherId)
}

function handleGroupMessage(data) {
  if (!data || !data.groupId) return
  setSummary('group', data.groupId, {
    lastMessage: previewOf(data),
    lastTime: Date.now()
  })
  bumpUnread('group', data.groupId)
}

async function refreshPendingCount() {
  const userId = currentUserId()
  if (!userId) return
  try {
    const res = await getPendingFriendRequests(userId)
    if (res && res.code === 200 && Array.isArray(res.data)) {
      store.pendingRequestCount = res.data.length
    }
  } catch (e) {
    // ignore
  }
}

export function initGlobalWs() {
  if (registered) return
  registered = true

  wsService.on('private-message', handlePrivateMessage)
  wsService.on('group-message', handleGroupMessage)
  wsService.on('friend-request', refreshPendingCount)
  wsService.on('group-invite', refreshPendingCount)
}

export { refreshPendingCount }
