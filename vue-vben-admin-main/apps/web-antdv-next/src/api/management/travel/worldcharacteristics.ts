// @ts-nocheck
import request from '#/utils/request';

/**
 * 世界特征相关接口
 */

// 获取世界特征列表
export const getWorldCharacteristicsList = (): Promise<any> => {
  return request.get('/world-characteristics/list')
}

// 分页获取世界特征列表
export const getWorldCharacteristicsPage = (current: any, size: any): Promise<any> => {
  return request.get('/world-characteristics/page', {
    params: {
      current,
      size
    }
  })
}

// 根据ID获取世界特征详情
export const getWorldCharacteristicsDetail = (id: any): Promise<any> => {
  return request.get(`/world-characteristics/detail/${id}`)
}

// 根据国家获取世界特征
export const getWorldCharacteristicsByCountry = (country: any): Promise<any> => {
  return request.get(`/world-characteristics/by-country/${country}`)
}

// 根据分类获取世界特征
export const getWorldCharacteristicsByCategory = (category: any): Promise<any> => {
  return request.get(`/world-characteristics/by-category/${category}`)
}

// 添加世界特征
export const addWorldCharacteristics = (data: any): Promise<any> => {
  return request.post('/world-characteristics/add', data)
}

// 更新世界特征
export const updateWorldCharacteristics = (data: any): Promise<any> => {
  return request.put('/world-characteristics/update', data)
}

// 删除世界特征
export const deleteWorldCharacteristics = (id: any): Promise<any> => {
  return request.delete(`/world-characteristics/delete/${id}`)
}
