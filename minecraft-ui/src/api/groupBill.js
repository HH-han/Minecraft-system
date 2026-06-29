import request from '@/utils/request'

/**
 * AA账单接口
 */

// 添加账单
export const addBill = (groupId, data) => {
  return request.post(`/api/group/${groupId}/bill`, data)
}

// 修改账单
export const updateBill = (groupId, billId, data) => {
  return request.put(`/api/group/${groupId}/bill/${billId}`, data)
}

// 删除账单
export const deleteBill = (groupId, billId) => {
  return request.delete(`/api/group/${groupId}/bill/${billId}`)
}

// 获取账单列表
export const getBillList = (groupId) => {
  return request.get(`/api/group/${groupId}/bill/list`)
}

// 获取结算汇总
export const getSettlement = (groupId) => {
  return request.get(`/api/group/${groupId}/bill/settlement`)
}

// 标记账单已结清
export const settleBill = (groupId, billId) => {
  return request.put(`/api/group/${groupId}/bill/${billId}/settle`)
}