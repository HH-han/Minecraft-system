import request from '@/utils/request'

/**
 * 聚合支付接口
 */

// 创建支付（返回 payNo / flowNo / payParams）
export const createPayment = (data) => {
  return request.post('/pay/create', data)
}

// 查询支付详情（config 可传 silent 等请求配置）
export const queryPayment = (payNo, config = {}) => {
  return request.get(`/pay/query/${payNo}`, config)
}

// 模拟支付确认
export const mockConfirmPayment = (flowNo) => {
  return request.post(`/pay/mock/confirm/${flowNo}`)
}

// 获取支付详情（兼容接口）
export const getPaymentDetail = (id) => {
  return request.get(`/payment/${id}`)
}

// 取消支付（兼容接口）
export const cancelPayment = (id) => {
  return request.put(`/payment/${id}/cancel`)
}

// 发起退款（管理员）
export const refundPayment = (data) => {
  return request.post('/pay/refund', data)
}

/**
 * 轮询支付结果（真实渠道异步回调场景）
 */
export const waitForPayment = async (payNo, timeoutMs = 120000) => {
  const deadline = Date.now() + timeoutMs
  while (Date.now() < deadline) {
    const res = await queryPayment(payNo)
    const status = res?.data?.status
    if (status === 'SUCCESS') return res.data
    if (['FAILED', 'CLOSED'].includes(status)) {
      throw new Error('支付失败或支付单已关闭')
    }
    await new Promise((resolve) => setTimeout(resolve, 1500))
  }
  throw new Error('支付确认超时，请稍后在订单中查看')
}
