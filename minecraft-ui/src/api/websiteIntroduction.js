import request from '@/utils/request';

/**
 * 网站介绍页接口（公共端，仅读取）
 * 对应后端 WebsiteIntroductionController: /api/website-introduction
 */
const websiteIntroductionApi = {
  // 聚合数据：一次获取介绍页全部内容（功能、技术栈、历程、统计）
  getIntroductionData: () => {
    return request.get('/website-introduction/data', { silent: true });
  },

  // 核心功能列表（含子项）
  getFeatureList: () => {
    return request.get('/website-introduction/features', { silent: true });
  },

  // 核心功能详情
  getFeatureDetail: (id) => {
    return request.get(`/website-introduction/features/${id}`, { silent: true });
  },

  // 根据功能ID获取子项列表
  getFeatureItems: (featureId) => {
    return request.get('/website-introduction/feature-items', {
      params: { featureId },
      silent: true
    });
  },

  // 技术分类列表（含技术项）
  getTechCategoryList: () => {
    return request.get('/website-introduction/tech-categories', { silent: true });
  },

  // 根据分类ID获取技术项列表
  getTechItems: (categoryId) => {
    return request.get('/website-introduction/tech-items', {
      params: { categoryId },
      silent: true
    });
  },

  // 发展历程列表
  getMilestoneList: () => {
    return request.get('/website-introduction/milestones', { silent: true });
  },

  // 发展历程列表（带格式化日期 yyyy.MM）
  getMilestoneListFormatted: () => {
    return request.get('/website-introduction/milestones/formatted', { silent: true });
  },

  // 平台数据统计列表
  getPlatformStatList: () => {
    return request.get('/website-introduction/platform-stats', { silent: true });
  }
};

export default websiteIntroductionApi;
