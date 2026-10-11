import request from '@/utils/request'

// 发布朋友圈动态
export const publishMoment = (data) => {
  return request.post('/public/moment/publish', data)
}

// 获取朋友圈信息流（自己 + 好友）
export const getMomentFeed = (userId, page = 1, size = 10) => {
  return request.get('/public/moment/feed', {
    params: { userId, page, size }
  })
}

// 获取指定用户的动态列表
export const getUserMoments = (userId, targetUserId, page = 1, size = 10) => {
  return request.get('/public/moment/user', {
    params: { userId, targetUserId, page, size }
  })
}

// 删除动态
export const deleteMoment = (momentId, userId) => {
  return request.delete(`/public/moment/${momentId}`, {
    params: { userId }
  })
}

// 点赞 / 取消点赞
export const toggleMomentLike = (momentId, userId) => {
  return request.post(`/public/moment/${momentId}/like`, null, {
    params: { userId }
  })
}

// 获取动态点赞用户列表
export const getMomentLikes = (momentId) => {
  return request.get(`/public/moment/${momentId}/likes`)
}

// 发表评论
export const addMomentComment = (momentId, data) => {
  return request.post(`/public/moment/${momentId}/comment`, data)
}

// 获取动态评论列表
export const getMomentComments = (momentId) => {
  return request.get(`/public/moment/${momentId}/comments`)
}

// 删除评论
export const deleteMomentComment = (commentId, userId) => {
  return request.delete(`/public/moment/comment/${commentId}`, {
    params: { userId }
  })
}

// 上传朋友圈封面背景图
export const uploadMomentCover = (userId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/public/moment/cover', formData, {
    params: { userId },
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取朋友圈封面背景图
export const getMomentCover = (userId) => {
  return request.get('/public/moment/cover', {
    params: { userId }
  })
}
