import request from '@/utils/request'

const countriesApi = {
  // 获取国家列表
  getCountriesList: (page = 1, size = 10) => {
    return request.get('/countries/list', {
      params: { page, size }
    });
  },

  // 获取国家详情
  getCountriesById: (id) => {
    return request.get(`/countries/${id}`);
  },

  // 添加国家
  addCountries: (countries) => {
    return request.post('/countries/add', countries);
  },

  // 更新国家
  updateCountries: (countries) => {
    return request.put('/countries/update', countries);
  },

  // 删除国家
  deleteCountries: (id) => {
    return request.delete(`/countries/${id}`);
  }
};

export default countriesApi;
