// @ts-nocheck
import request from '#/utils/request';

// 点赞
export const like = (data: any): Promise<any> => {
  return request.post('/like/add', data)
}

// 取消点赞
export const unlike = (data: any): Promise<any> => {
  return request.post('/like/cancel', data)
}

// 检查是否已点赞
export const isLiked = (params: any): Promise<any> => {
  return request.get('/like/check', { params })
}
