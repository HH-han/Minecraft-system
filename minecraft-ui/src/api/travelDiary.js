import request from '@/utils/request'

/**
 * 旅行日记接口
 */

// 生成旅行日记
export const generateDiary = (groupId) => {
  return request.get(`/diary/${groupId}/generate`)
}

// 获取日记
export const getDiary = (groupId) => {
  return request.get(`/diary/${groupId}`)
}

// 导出日记
export const exportDiary = (diaryId, format = 'json') => {
  return request.post(`/diary/${diaryId}/export`, null, { params: { format } })
}