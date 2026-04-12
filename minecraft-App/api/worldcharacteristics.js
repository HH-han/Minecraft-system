import request from '@/utils/request'

/**
 * 世界特征相关接口
 */

// 获取世界特征列表
export const getWorldCharacteristicsList = () => {
  return request.get('/world-characteristics/list')
}

// 分页获取世界特征列表
export const getWorldCharacteristicsPage = (current, size) => {
  return request.get('/world-characteristics/page', {
    params: {
      current,
      size
    }
  })
}

// 根据ID获取世界特征详情
export const getWorldCharacteristicsDetail = (id) => {
  return request.get(`/world-characteristics/detail/${id}`)
}

// 根据国家获取世界特征
export const getWorldCharacteristicsByCountry = (country) => {
  return request.get(`/world-characteristics/by-country/${country}`)
}

// 根据分类获取世界特征
export const getWorldCharacteristicsByCategory = (category) => {
  return request.get(`/world-characteristics/by-category/${category}`)
}

// 添加世界特征
export const addWorldCharacteristics = (data) => {
  return request.post('/world-characteristics/add', data)
}

// 更新世界特征
export const updateWorldCharacteristics = (data) => {
  return request.put('/world-characteristics/update', data)
}

// 删除世界特征
export const deleteWorldCharacteristics = (id) => {
  return request.delete(`/world-characteristics/delete/${id}`)
}
