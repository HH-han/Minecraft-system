import request from '@/utils/request'

// 登录信息API接口
export default {
  // 获取登录信息列表
  getLoginInfoList(params) {
    return request({
      url: '/public/user/logininfo',
      method: 'get',
      params
    })
  },
  
  // 删除登录信息
  deleteLoginInfo(id) {
    return request({
      url: `/public/user/logininfo/${id}`,
      method: 'delete'
    })
  },
  
  // 批量删除登录信息
  deleteBatchLoginInfo(ids) {
    return request({
      url: '/public/user/logininfo/batch',
      method: 'delete',
      params: { ids }
    })
  }
}