// @ts-nocheck
import request from '#/utils/request';

/**
 * 评论相关接口
 */

// 添加评论
export const addComment = (commentRequest: any): Promise<any> => {
  return request.post('/comment/add', commentRequest)
}

// 删除评论
export const deleteComment = (id: any): Promise<any> => {
  return request.delete(`/comment/${id}`)
}

// 获取评论列表
export const getCommentList = (itemType: any, itemId: any, params: any): Promise<any> => {
  return request.get('/comment/list', {
    params: {
      itemType,
      itemId,
      ...params
    }
  })
}

// 获取评论树
export const getCommentTree = (itemType: any, itemId: any): Promise<any> => {
  return request.get('/comment/tree', {
    params: {
      itemType,
      itemId
    }
  })
}
