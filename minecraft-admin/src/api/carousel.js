import request from '@/utils/request';

const carouselApi = {
  // 获取指定位置的轮播图
  getCarouselsByPosition: (position) => {
    return request.get('/carousel/position', {
      params: { position }
    });
  },

  // 获取指定分组的轮播图
  getCarouselsByGroup: (groupCode) => {
    return request.get('/carousel/group', {
      params: { groupCode }
    });
  },

  // 获取首页顶部轮播图
  getHomeTopCarousels: () => {
    return request.get('/carousel/home/top');
  },

  // 获取首页中部轮播图
  getHomeMiddleCarousels: () => {
    return request.get('/carousel/home/middle');
  },

  // 获取目的地轮播图
  getDestinationCarousels: () => {
    return request.get('/carousel/destination');
  },

  // 获取景点轮播图
  getAttractionCarousels: () => {
    return request.get('/carousel/attraction');
  },

  // 获取酒店轮播图
  getHotelCarousels: () => {
    return request.get('/carousel/hotel');
  },

  // 获取美食轮播图
  getFoodCarousels: () => {
    return request.get('/carousel/food');
  },

  // 获取纪念品轮播图
  getSouvenirCarousels: () => {
    return request.get('/carousel/souvenir');
  },

  // 获取攻略群轮播图
  getStrategyCarousels: () => {
    return request.get('/carousel/strategy');
  },

  // 获取社区轮播图
  getCommunityCarousels: () => {
    return request.get('/carousel/community');
  },

  // 获取所有轮播图
  getAllCarousels: () => {
    return request.get('/carousel/categories');
  },

  // 获取轮播图详情
  getCarouselById: (id) => {
    return request.get(`/carousel/${id}`);
  },

  // 新增轮播图
  addCarousel: (carousel) => {
    return request.post('/carousel', carousel);
  },

  // 更新轮播图
  updateCarousel: (carousel) => {
    return request.put('/carousel', carousel);
  },

  // 删除轮播图
  deleteCarousel: (id) => {
    return request.delete(`/carousel/${id}`);
  }
};

export default carouselApi;
