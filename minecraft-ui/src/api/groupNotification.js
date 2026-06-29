import request from '@/utils/request'

/**
 * 消息通知接口
 */

// 获取通知列表
export const getNotifications = () => {
  return request.get('/notification/list')
}

// 标记已读
export const markAsRead = (id) => {
  return request.put(`/notification/${id}/read`)
}

// 全部已读
export const markAllRead = () => {
  return request.put('/notification/read-all')
}

// 获取未读数量
export const getUnreadCount = () => {
  return request.get('/notification/unread-count')
}