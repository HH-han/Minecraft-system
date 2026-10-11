import request from '@/utils/request'

/**
 * 旅游笔记相关接口
 */

// 笔记广场分页（tab: recommend-推荐 latest-最新 follow-关注 city-同城）
export const getNotePage = (params) => {
  return request.get('/travel-note/page', params)
}

// 笔记详情
export const getNoteDetail = (id) => {
  return request.get(`/travel-note/${id}`)
}

// 创建笔记（status: 0-草稿 1-提交审核 5-私密）
export const createNote = (data) => {
  return request.post('/travel-note', data)
}

// 更新笔记
export const updateNote = (id, data) => {
  return request.put(`/travel-note/${id}`, data)
}

// 删除笔记
export const deleteNote = (id) => {
  return request.del(`/travel-note/${id}`)
}

// 我的笔记
export const getMyNotes = (params) => {
  return request.get('/travel-note/mine', params)
}

// 热门标签
export const getHotTags = (limit) => {
  return request.get('/travel-note/tags/hot', { limit })
}

// 点赞 / 取消点赞
export const likeNote = (id) => request.post(`/travel-note/${id}/like`)
export const unlikeNote = (id) => request.post(`/travel-note/${id}/unlike`)

// 收藏 / 取消收藏
export const collectNote = (id) => request.post(`/travel-note/${id}/collect`)
export const uncollectNote = (id) => request.post(`/travel-note/${id}/uncollect`)

// 发表评论
export const addNoteComment = (id, data) => {
  return request.post(`/travel-note/${id}/comment`, data)
}

// 评论分页
export const getNoteComments = (id, params) => {
  return request.get(`/travel-note/${id}/comment/page`, params)
}

// 删除评论
export const deleteNoteComment = (commentId) => {
  return request.del(`/travel-note/comment/${commentId}`)
}

// 举报笔记
export const reportNote = (id, reason) => {
  return request.post(`/travel-note/${id}/report`, { reason })
}

// 一键复制为出行计划
export const copyNoteToPlan = (id) => {
  return request.post(`/travel-note/${id}/copy-to-plan`)
}

// 上传图片（uni-app 上传，返回 ApiResponse）
export const uploadNoteImage = (filePath) => {
  return request.upload('/upload/file', filePath)
}
