import request from '@/utils/request'

/**
 * 消息通知接口 - 管理后台
 */

// 获取通知列表（分页）
export const getNotifications = (params) => {
  return request.get('/api/notification/list', { params })
}

// 获取未读数量
export const getUnreadCount = () => {
  return request.get('/api/notification/unread-count')
}

// 标记已读
export const markAsRead = (id) => {
  return request.put(`/api/notification/${id}/read`)
}

// 全部已读
export const markAllRead = () => {
  return request.put('/api/notification/read-all')
}