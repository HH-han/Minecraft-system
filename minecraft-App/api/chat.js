import request from '@/utils/request'

/**
 * 聊天相关接口
 */

// 发送消息
export const sendMessage = (message) => {
  return request.post('/chat/send', message)
}

// 获取聊天记录
export const getChatHistory = (friendId, limit = 50) => {
  return request.get('/chat/history', {
    params: {
      friendId,
      limit
    }
  })
}

// 标记消息为已读
export const markAsRead = (friendId) => {
  return request.post('/chat/read', null, {
    params: {
      friendId
    }
  })
}
