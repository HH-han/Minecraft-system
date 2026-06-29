import request from '@/utils/request'

/**
 * 群组动态接口 - 管理后台
 */

// 获取动态列表
export const getPostList = (groupId, params) => {
  return request.get(`/api/group/${groupId}/post/list`, { params })
}

// 删除动态
export const deletePost = (groupId, postId) => {
  return request.delete(`/api/group/${groupId}/post/${postId}`)
}

// 获取评论列表
export const getCommentList = (postId) => {
  return request.get(`/api/group/post/${postId}/comment/list`)
}

// 删除评论
export const deleteComment = (commentId) => {
  return request.delete(`/api/group/post/comment/${commentId}`)
}