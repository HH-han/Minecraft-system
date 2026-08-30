// @ts-nocheck
import request from '#/utils/request';

/**
 * 轮播图相关接口
 */

// 获取所有轮播图（管理列表用）
export const getAllCarousels = (): Promise<any> => {
  return request.get('/carousel/categories');
}

// 获取指定位置的轮播图
export const getCarouselsByPosition = (position: any): Promise<any> => {
  return request.get('/carousel/position', {
    params: { position }
  });
}

// 获取指定分组的轮播图
export const getCarouselsByGroup = (groupCode: any): Promise<any> => {
  return request.get('/carousel/group', {
    params: { groupCode }
  });
}

// 获取首页顶部轮播图
export const getHomeTopCarousels = (): Promise<any> => {
  return request.get('/carousel/home/top');
}

// 获取首页中部轮播图
export const getHomeMiddleCarousels = (): Promise<any> => {
  return request.get('/carousel/home/middle');
}

// 获取目的地轮播图
export const getDestinationCarousels = (): Promise<any> => {
  return request.get('/carousel/destination');
}

// 获取景点轮播图
export const getAttractionCarousels = (): Promise<any> => {
  return request.get('/carousel/attraction');
}

// 获取酒店轮播图
export const getHotelCarousels = (): Promise<any> => {
  return request.get('/carousel/hotel');
}

// 获取美食轮播图
export const getFoodCarousels = (): Promise<any> => {
  return request.get('/carousel/food');
}

// 获取纪念品轮播图
export const getSouvenirCarousels = (): Promise<any> => {
  return request.get('/carousel/souvenir');
}

// 获取攻略群轮播图
export const getStrategyCarousels = (): Promise<any> => {
  return request.get('/carousel/strategy');
}

// 获取社区轮播图
export const getCommunityCarousels = (): Promise<any> => {
  return request.get('/carousel/community');
}

// 获取轮播图详情
export const getCarouselById = (id: any): Promise<any> => {
  return request.get(`/carousel/${id}`);
}

// 新增轮播图
export const addCarousel = (carousel: any): Promise<any> => {
  return request.post('/carousel', carousel);
}

// 更新轮播图
export const updateCarousel = (carousel: any): Promise<any> => {
  return request.put('/carousel', carousel);
}

// 删除轮播图
export const deleteCarousel = (id: any): Promise<any> => {
  return request.delete(`/carousel/${id}`);
}
