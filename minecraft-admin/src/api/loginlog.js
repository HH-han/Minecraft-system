import request from '@/utils/request'

// 登录日志API接口
export default {
  // 获取登录日志列表
  getLoginLogs(params) {
    return request({
      url: '/login-log/list',
      method: 'get',
      params
    })
  },
  
  // 获取登录日志详情
  getLoginLogDetail(id) {
    return request({
      url: `/login-log/${id}`,
      method: 'get'
    })
  },
  
  // 封禁IP地址
  blockIPAddress(data) {
    return request({
      url: '/login-log/block-ip',
      method: 'post',
      params: data
    })
  }
}