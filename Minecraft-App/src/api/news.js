import request from '@/utils/request'

// 获取新闻列表
export const getNewsList = (params) => {
  return request.get('/news/list', { params })
}

// 获取新闻详情
export const getNewsDetail = (id) => {
  return request.get(`/news/${id}`)
}

// 获取热门新闻
export const getHotNews = (params) => {
  return request.get('/news/hot', { params })
}

// 添加新闻
export const addNews = (data) => {
  return request.post('/news/add', data)
}

// 更新新闻
export const updateNews = (data) => {
  return request.put('/news/update', data)
}

// 删除新闻
export const deleteNews = (id) => {
  return request.delete(`/news/${id}`)
}
