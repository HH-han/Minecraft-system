import axios from 'axios';

const citiesApi = {
  // 获取城市列表
  getCitiesList: (page = 1, size = 10) => {
    return axios.get('/api/cities/list', {
      params: { page, size }
    });
  },

  // 获取城市详情
  getCitiesById: (id) => {
    return axios.get(`/api/cities/${id}`);
  },

  // 添加城市
  addCities: (cities) => {
    return axios.post('/api/cities/add', cities);
  },

  // 更新城市
  updateCities: (cities) => {
    return axios.put('/api/cities/update', cities);
  },

  // 删除城市
  deleteCities: (id) => {
    return axios.delete(`/api/cities/${id}`);
  }
};

export default citiesApi;
