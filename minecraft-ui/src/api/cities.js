import request from '@/utils/request'

const citiesApi = {
  // 获取城市列表
  getCitiesList: (page = 1, size = 10) => {
    return request.get('/cities/list', {
      params: { page, size }
    });
  },

  // 获取城市详情
  getCitiesById: (id) => {
    return request.get(`/cities/${id}`);
  },

  // 添加城市
  addCities: (cities) => {
    return request.post('/cities/add', cities);
  },

  // 更新城市
  updateCities: (cities) => {
    return request.put('/cities/update', cities);
  },

  // 删除城市
  deleteCities: (id) => {
    return request.delete(`/cities/${id}`);
  }
};

export default citiesApi;
