// @ts-nocheck
import request from '#/utils/request';

/**
 * Token相关接口
 */

// 获取Token状态
export const getTokenStatus = (): Promise<any> => {
  return request.get('/token/status')
}

// 刷新Token
export const refreshToken = (): Promise<any> => {
  return request.post('/token/refresh')
}

const api = {
  getTokenStatus,
  refreshToken
};

export default api;
