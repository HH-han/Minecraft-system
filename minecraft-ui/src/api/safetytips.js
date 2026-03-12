import request from '@/utils/request';

const safetyTipsApi = {
  // 获取所有激活的安全知识提示
  getActiveSafetyTips: () => {
    return request.get('/safety-tips');
  },

  // 根据分类获取安全知识提示
  getSafetyTipsByCategory: (categoryId) => {
    return request.get(`/safety-tips/category/${categoryId}`);
  },

  // 获取安全知识提示详情
  getSafetyTipById: (id) => {
    return request.get(`/safety-tips/${id}`);
  }
};

export default safetyTipsApi;
