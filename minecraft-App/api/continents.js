import request from '@/utils/request'

const continentsApi = {
  // 获取大洲列表
  getContinentsList: (page = 1, size = 10) => {
    return request.get('/continents/list', {
      params: { page, size }
    });
  },

  // 获取大洲详情
  getContinentsById: (id) => {
    return request.get(`/continents/${id}`);
  },

  // 添加大洲
  addContinents: (continents) => {
    return request.post('/continents/add', continents);
  },

  // 更新大洲
  updateContinents: (continents) => {
    return request.put('/continents/update', continents);
  },

  // 删除大洲
  deleteContinents: (id) => {
    return request.delete(`/continents/${id}`);
  }
};

export default continentsApi;
