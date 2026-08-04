import request from '@/utils/request';

/**
 * 网站介绍页接口（管理端，完整 CRUD）
 * 对应后端 WebsiteIntroductionController: /api/website-introduction
 */
const websiteIntroductionApi = {
  // ============ 聚合数据 ============
  getIntroductionData: () => {
    return request.get('/website-introduction/data');
  },

  // ============ 核心功能 (features) ============
  getFeatureList: () => {
    return request.get('/website-introduction/features');
  },

  getFeatureDetail: (id) => {
    return request.get(`/website-introduction/features/${id}`);
  },

  saveFeature: (data) => {
    return request.post('/website-introduction/features', data);
  },

  deleteFeature: (id) => {
    return request.delete(`/website-introduction/features/${id}`);
  },

  // ============ 功能子项 (feature_items) ============
  getFeatureItems: (featureId) => {
    return request.get('/website-introduction/feature-items', {
      params: { featureId }
    });
  },

  saveFeatureItem: (data) => {
    return request.post('/website-introduction/feature-items', data);
  },

  deleteFeatureItem: (id) => {
    return request.delete(`/website-introduction/feature-items/${id}`);
  },

  // ============ 技术分类 (tech_categories) ============
  getTechCategoryList: () => {
    return request.get('/website-introduction/tech-categories');
  },

  saveTechCategory: (data) => {
    return request.post('/website-introduction/tech-categories', data);
  },

  deleteTechCategory: (id) => {
    return request.delete(`/website-introduction/tech-categories/${id}`);
  },

  // ============ 技术项 (tech_items) ============
  getTechItems: (categoryId) => {
    return request.get('/website-introduction/tech-items', {
      params: { categoryId }
    });
  },

  saveTechItem: (data) => {
    return request.post('/website-introduction/tech-items', data);
  },

  deleteTechItem: (id) => {
    return request.delete(`/website-introduction/tech-items/${id}`);
  },

  // ============ 发展历程 (milestones) ============
  getMilestoneList: () => {
    return request.get('/website-introduction/milestones');
  },

  getMilestoneListFormatted: () => {
    return request.get('/website-introduction/milestones/formatted');
  },

  saveMilestone: (data) => {
    return request.post('/website-introduction/milestones', data);
  },

  deleteMilestone: (id) => {
    return request.delete(`/website-introduction/milestones/${id}`);
  },

  // ============ 平台数据统计 (platform_stats) ============
  getPlatformStatList: () => {
    return request.get('/website-introduction/platform-stats');
  },

  savePlatformStat: (data) => {
    return request.post('/website-introduction/platform-stats', data);
  },

  deletePlatformStat: (id) => {
    return request.delete(`/website-introduction/platform-stats/${id}`);
  }
};

export default websiteIntroductionApi;
