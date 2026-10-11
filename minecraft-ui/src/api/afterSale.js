import request from '@/utils/request'

/**
 * 售后相关接口
 */

// 申请售后
export const applyAfterSale = (data) => {
  return request.post('/after-sale/apply', data)
}

// 我的售后列表
export const getAfterSalePage = (params) => {
  return request.get('/after-sale/page', { params })
}

// 售后详情
export const getAfterSaleDetail = (id) => {
  return request.get(`/after-sale/${id}`)
}

// 填写退货物流
export const submitReturn = (id, data) => {
  return request.post(`/after-sale/${id}/return`, data)
}

// 撤销申请
export const cancelAfterSale = (id) => {
  return request.post(`/after-sale/${id}/cancel`)
}

// 协商留言
export const sendAfterSaleMessage = (id, data) => {
  return request.post(`/after-sale/${id}/message`, data)
}
