// @ts-nocheck
import request from '#/utils/request';

// ==================== 笔记管理 ====================

// 全量分页（含草稿/待审/驳回/下架）
export const getTravelNotePage = (params: any): Promise<any> => {
  return request.get('/admin/travel-note/page', { params })
}

// 笔记详情（管理端，含未发布）
export const getTravelNoteDetail = (id: any): Promise<any> => {
  return request.get(`/admin/travel-note/${id}`)
}

// 审核：approved=true 通过 / false 驳回
export const auditTravelNote = (id: any, data: { approved: boolean; reason?: string }): Promise<any> => {
  return request.post(`/admin/travel-note/${id}/audit`, data)
}

// 置顶/取消置顶
export const topTravelNote = (id: any): Promise<any> => {
  return request.post(`/admin/travel-note/${id}/top`)
}

// 加精/取消加精
export const featureTravelNote = (id: any): Promise<any> => {
  return request.post(`/admin/travel-note/${id}/feature`)
}

// 下架
export const offlineTravelNote = (id: any): Promise<any> => {
  return request.post(`/admin/travel-note/${id}/offline`)
}

// 数据看板
export const getTravelNoteStat = (): Promise<any> => {
  return request.get('/admin/travel-note/stat/overview')
}

// ==================== 举报处理 ====================

// 举报分页
export const getTravelNoteReports = (params: any): Promise<any> => {
  return request.get('/admin/travel-note/report/page', { params })
}

// 处理举报：status 1-处理(下架笔记) 2-驳回举报
export const handleTravelNoteReport = (id: any, status: number): Promise<any> => {
  return request.post(`/admin/travel-note/report/${id}/handle`, null, { params: { status } })
}

// ==================== 标签管理 ====================

// 标签分页
export const getTravelNoteTagPage = (params: any): Promise<any> => {
  return request.get('/admin/travel-note/tag/page', { params })
}

// 新建标签
export const createTravelNoteTag = (data: { name: string; isHot?: number }): Promise<any> => {
  return request.post('/admin/travel-note/tag', data)
}

// 更新标签
export const updateTravelNoteTag = (id: any, data: any): Promise<any> => {
  return request.put(`/admin/travel-note/tag/${id}`, data)
}

// 删除标签
export const deleteTravelNoteTag = (id: any): Promise<any> => {
  return request.delete(`/admin/travel-note/tag/${id}`)
}
