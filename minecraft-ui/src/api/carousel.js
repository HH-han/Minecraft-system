import request from '@/utils/request'

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
  }
};

export default carouselApi;
