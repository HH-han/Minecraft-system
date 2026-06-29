import request from '@/utils/request'

/**
 * 旅行日记接口 - 管理后台
 */

// 获取日记列表
export const getDiaryList = (params) => {
  return request.get('/api/diary/list', { params })
}

// 获取日记详情
export const getDiary = (groupId) => {
  return request.get(`/api/diary/${groupId}`)
}

// 生成旅行日记
export const generateDiary = (groupId) => {
  return request.get(`/api/diary/${groupId}/generate`)
}

// 导出日记
export const exportDiary = (diaryId, format = 'json') => {
  return request.post(`/api/diary/${diaryId}/export`, null, { params: { format } })
}

// 删除日记
export const deleteDiary = (diaryId) => {
  return request.delete(`/api/diary/${diaryId}`)
}