import request from '@/utils/request'

/**
 * Token相关接口
 */

// 获取Token状态
export const getTokenStatus = () => {
  return request.get('/token/status')
}

// 刷新Token
export const refreshToken = () => {
  return request.post('/token/refresh')
}

export default {
  getTokenStatus,
  refreshToken
}
