// @ts-nocheck
import request from '#/utils/request';

/**
 * 社区相关接口
 */

// 获取帖子列表
export const getPostList = (params: any): Promise<any> => {
  return request.get('/community/list', { params })
}

// 获取帖子详情
export const getPostDetail = (id: any): Promise<any> => {
  return request.get(`/community/${id}`)
}

// 创建帖子
export const createPost = (post: any): Promise<any> => {
  return request.post('/community/create', post)
}

// 更新帖子
export const updatePost = (post: any): Promise<any> => {
  return request.put('/community/update', post)
}

// 删除帖子
export const deletePost = (id: any): Promise<any> => {
  return request.delete(`/community/${id}`)
}
