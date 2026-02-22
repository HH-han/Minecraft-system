import request from '@/utils/request'

/**
 * 评论相关接口
 */

// 添加评论
export const addComment = (commentRequest) => {
  return request.post('/comment/add', commentRequest)
}

// 删除评论
export const deleteComment = (id) => {
  return request.delete(`/comment/${id}`)
}

// 获取评论列表
export const getCommentList = (itemType, itemId, params) => {
  return request.get('/comment/list', {
    params: {
      itemType,
      itemId,
      ...params
    }
  })
}

// 获取评论树
export const getCommentTree = (itemType, itemId) => {
  return request.get('/comment/tree', {
    params: {
      itemType,
      itemId
    }
  })
}
