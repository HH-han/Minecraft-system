import request from '@/utils/request'

/**
 * 旅行卡片相关接口
 */

// 获取卡片列表（分页，支持 keyword / location / tag 筛选）
export const getCardList = (params) => {
  return request.get('/card/list', { params })
}

// 获取我的卡片列表（需登录）
export const getMyCards = (params) => {
  return request.get('/card/my', { params })
}

// 获取卡片详情（浏览次数 +1）
export const getCardDetail = (id) => {
  return request.get(`/card/${id}`)
}

// 创建卡片（需登录）
export const createCard = (card) => {
  return request.post('/card/add', card)
}

// 更新卡片（需登录，仅作者可操作）
export const updateCard = (card) => {
  return request.put('/card/update', card)
}

// 删除卡片（需登录，逻辑删除，仅作者可操作）
export const deleteCard = (id) => {
  return request.delete(`/card/${id}`)
}
