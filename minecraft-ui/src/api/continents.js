import axios from 'axios';

const continentsApi = {
  // 获取大洲列表
  getContinentsList: (page = 1, size = 10) => {
    return axios.get('/api/continents/list', {
      params: { page, size }
    });
  },

  // 获取大洲详情
  getContinentsById: (id) => {
    return axios.get(`/api/continents/${id}`);
  },

  // 添加大洲
  addContinents: (continents) => {
    return axios.post('/api/continents/add', continents);
  },

  // 更新大洲
  updateContinents: (continents) => {
    return axios.put('/api/continents/update', continents);
  },

  // 删除大洲
  deleteContinents: (id) => {
    return axios.delete(`/api/continents/${id}`);
  }
};

export default continentsApi;
