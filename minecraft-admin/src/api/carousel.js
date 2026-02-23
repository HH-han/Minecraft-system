import axios from 'axios';

const carouselApi = {
  // 获取指定位置的轮播图
  getCarouselsByPosition: (position) => {
    return axios.get('/api/carousel/position', {
      params: { position }
    });
  },

  // 获取指定分组的轮播图
  getCarouselsByGroup: (groupCode) => {
    return axios.get('/api/carousel/group', {
      params: { groupCode }
    });
  },

  // 获取首页顶部轮播图
  getHomeTopCarousels: () => {
    return axios.get('/api/carousel/home/top');
  },

  // 获取首页中部轮播图
  getHomeMiddleCarousels: () => {
    return axios.get('/api/carousel/home/middle');
  }
};

export default carouselApi;
