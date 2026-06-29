import request from '@/utils/request'

/**
 * 群组动态接口
 */

// 发布动态
export const createPost = (groupId, data) => {
  return request.post(`/api/group/${groupId}/post`, data)
}

// 删除动态
export const deletePost = (groupId, postId) => {
  return request.delete(`/api/group/${groupId}/post/${postId}`)
}

// 获取动态列表
export const getPostList = (groupId) => {
  return request.get(`/api/group/${groupId}/post/list`)
}

// 点赞/取消点赞
export const toggleLike = (groupId, postId) => {
  return request.post(`/api/group/${groupId}/post/${postId}/like`)
}

// 检查是否已点赞
export const checkLiked = (groupId, postId) => {
  return request.get(`/api/group/${groupId}/post/${postId}/liked`)
}

/**
 * 动态评论接口
 */

// 发表评论
export const createComment = (postId, data) => {
  return request.post(`/api/group/post/${postId}/comment`, data)
}

// 删除评论
export const deleteComment = (commentId) => {
  return request.delete(`/api/group/post/comment/${commentId}`)
}

// 获取评论列表
export const getCommentList = (postId) => {
  return request.get(`/api/group/post/${postId}/comment/list`)
}