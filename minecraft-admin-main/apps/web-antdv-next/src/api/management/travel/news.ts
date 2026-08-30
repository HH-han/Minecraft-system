// @ts-nocheck
import request from '#/utils/request';

// 获取新闻列表
export const getNewsList = (params: any): Promise<any> => {
  return request.get('/news/list', { params })
}

// 获取新闻详情
export const getNewsDetail = (id: any): Promise<any> => {
  return request.get(`/news/${id}`)
}

// 获取热门新闻
export const getHotNews = (params: any): Promise<any> => {
  return request.get('/news/hot', { params })
}

// 添加新闻
export const addNews = (data: any): Promise<any> => {
  return request.post('/news/add', data)
}

// 更新新闻
export const updateNews = (data: any): Promise<any> => {
  return request.put('/news/update', data)
}

// 删除新闻
export const deleteNews = (id: any): Promise<any> => {
  return request.delete(`/news/${id}`)
}
