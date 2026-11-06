import request from '@/utils/request'

/**
 * IM「服务」页相关接口（服务项 + 钱包）
 */

// 服务项列表
export const getServiceItems = () => {
  return request.get('/public/service-item/list')
}

// 钱包信息（不存在自动开户）
export const getWalletInfo = (userId) => {
  return request.get('/public/wallet/info', { params: { userId } })
}

// 钱包流水
export const getWalletTransactions = (params) => {
  return request.get('/public/wallet/transactions', { params })
}

// 充值
export const rechargeWallet = (data) => {
  return request.post('/public/wallet/recharge', data)
}

// 提现
export const withdrawWallet = (data) => {
  return request.post('/public/wallet/withdraw', data)
}

// 转账给好友
export const transferWallet = (data) => {
  return request.post('/public/wallet/transfer', data)
}

// 设置/修改支付密码
export const setPayPassword = (data) => {
  return request.post('/public/wallet/pay-password', data)
}
