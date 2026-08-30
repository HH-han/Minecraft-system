// @ts-nocheck
import request from '#/utils/request';

/**
 * 国家相关接口
 */

// 获取国家列表（MyBatis-Plus 分页：current / size）
export const getCountriesList = (params: any): Promise<any> => {
  return request.get('/countries/list', { params })
}

// 获取国家详情
export const getCountriesById = (id: any): Promise<any> => {
  return request.get(`/countries/${id}`)
}

// 添加国家
export const addCountries = (countries: any): Promise<any> => {
  return request.post('/countries/add', countries)
}

// 更新国家
export const updateCountries = (countries: any): Promise<any> => {
  return request.put('/countries/update', countries)
}

// 删除国家
export const deleteCountries = (id: any): Promise<any> => {
  return request.delete(`/countries/${id}`)
}
