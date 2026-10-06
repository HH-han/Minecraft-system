import request from '@/utils/request'

// ==================== 用户端 ====================

// 分页查询已发布公告
export const getAnnouncementList = (params) => request.get('/announcements', { params })

// 公告详情
export const getAnnouncementDetail = (id) => request.get(`/announcements/${id}`)

// 首页展示公告（弹窗/轮播/横幅数据源）
export const getActiveAnnouncements = () => request.get('/announcements/active')

// 首页合并初始化（活跃公告 + 未读数）
export const getHomeInit = () => request.get('/announcements/home-init')

// 我的未读数
export const getUnreadCount = () => request.get('/announcements/unread-count')

// 我的未读公告列表
export const getMyUnread = () => request.get('/announcements/my-unread')

// 标记已读
export const markAsRead = (id) => request.post(`/announcements/${id}/read`)

// 批量标记已读
export const batchMarkAsRead = (ids) => request.post('/announcements/batch-read', ids)
