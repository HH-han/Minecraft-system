import axios from 'axios';

const countriesApi = {
  // 获取国家列表
  getCountriesList: (page = 1, size = 10) => {
    return axios.get('/api/countries/list', {
      params: { page, size }
    });
  },

  // 获取国家详情
  getCountriesById: (id) => {
    return axios.get(`/api/countries/${id}`);
  },

  // 添加国家
  addCountries: (countries) => {
    return axios.post('/api/countries/add', countries);
  },

  // 更新国家
  updateCountries: (countries) => {
    return axios.put('/api/countries/update', countries);
  },

  // 删除国家
  deleteCountries: (id) => {
    return axios.delete(`/api/countries/${id}`);
  }
};

export default countriesApi;
