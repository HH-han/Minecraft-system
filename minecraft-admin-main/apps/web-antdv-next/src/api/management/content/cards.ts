// @ts-nocheck
import request from '#/utils/request';

/**
 * 旅行卡片接口 - 管理后台
 */

// 获取卡片列表（分页，支持关键字 / 地点 / 标签筛选）
export const getCardList = (params: any): Promise<any> => {
  return request.get('/card/list', { params })
}

// 获取卡片详情（浏览次数 +1）
export const getCardDetail = (id: any): Promise<any> => {
  return request.get(`/card/${id}`)
}

// 新增卡片
export const addCard = (data: any): Promise<any> => {
  return request.post('/card/add', data)
}

// 更新卡片
export const updateCard = (data: any): Promise<any> => {
  return request.put('/card/update', data)
}

// 删除卡片（逻辑删除）
export const deleteCard = (id: any): Promise<any> => {
  return request.delete(`/card/${id}`)
}
