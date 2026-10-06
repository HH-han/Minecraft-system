import request from '@/utils/request'

// ==================== 公告管理端 ====================

// 分页查询公告（含草稿/已下架）
export const adminPageAnnouncements = (params) => request.get('/admin/announcements', { params })

// 新建公告（status=1 立即发布；指定未来 publishTime 为定时发布）
export const adminSaveAnnouncement = (data) => request.post('/admin/announcements', data)

// 修改公告
export const adminUpdateAnnouncement = (id, data) => request.put(`/admin/announcements/${id}`, data)

// 删除公告（逻辑删除）
export const adminDeleteAnnouncement = (id) => request.delete(`/admin/announcements/${id}`)

// 发布公告
export const adminPublishAnnouncement = (id) => request.post(`/admin/announcements/${id}/publish`)

// 下架公告
export const adminOfflineAnnouncement = (id) => request.post(`/admin/announcements/${id}/offline`)

// 置顶/取消置顶
export const adminTopAnnouncement = (id, isTop) =>
  request.post(`/admin/announcements/${id}/top?isTop=${isTop}`)

// 阅读统计
export const adminReadStat = (id) => request.get(`/admin/announcements/${id}/read-stat`)
