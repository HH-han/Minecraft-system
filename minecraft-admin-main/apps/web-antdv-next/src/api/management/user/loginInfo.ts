// @ts-nocheck
import request from '#/utils/request';

// 登录信息API接口
export const loginInfoApi = {
  // 获取登录信息列表
  getLoginInfoList(params: any) {
    return request({
      url: '/public/user/logininfo',
      method: 'get',
      params,
    });
  },

  // 删除登录信息
  deleteLoginInfo(id: string | number) {
    return request({
      url: `/public/user/logininfo/${id}`,
      method: 'delete',
    });
  },

  // 批量删除登录信息
  deleteBatchLoginInfo(ids: any[]) {
    return request({
      url: '/public/user/logininfo/batch',
      method: 'delete',
      params: { ids },
    });
  },
};

export default loginInfoApi;
