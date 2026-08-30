// @ts-nocheck
import request from '#/utils/request';

// 登录日志API接口
export const loginLogApi = {
  // 获取登录日志列表
  getLoginLogs(params: any) {
    return request({
      url: '/login-log/list',
      method: 'get',
      params,
    });
  },

  // 获取登录日志详情
  getLoginLogDetail(id: string | number) {
    return request({
      url: `/login-log/${id}`,
      method: 'get',
    });
  },

  // 封禁IP地址
  blockIPAddress(data: any) {
    return request({
      url: '/login-log/block-ip',
      method: 'post',
      params: data,
    });
  },
};

export default loginLogApi;
