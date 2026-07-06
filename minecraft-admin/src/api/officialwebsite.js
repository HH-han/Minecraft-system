import request from '@/utils/request';

const officialwebsiteApi = {
  getHomeData: () => {
    return request.get('/officialwebsite/home');
  },

  getNavigation: () => {
    return request.get('/officialwebsite/navigation');
  },

  getHero: () => {
    return request.get('/officialwebsite/hero');
  },

  getDestinations: () => {
    return request.get('/officialwebsite/destinations');
  },

  getFeaturedDestinations: () => {
    return request.get('/officialwebsite/destinations/featured');
  },

  getExperiences: () => {
    return request.get('/officialwebsite/experiences');
  },

  getNarrative: () => {
    return request.get('/officialwebsite/narrative');
  },

  getStories: () => {
    return request.get('/officialwebsite/stories');
  },

  getFeaturedStories: () => {
    return request.get('/officialwebsite/stories/featured');
  },

  getImmersion: () => {
    return request.get('/officialwebsite/immersion');
  },

  getCta: () => {
    return request.get('/officialwebsite/cta');
  },

  getFooter: () => {
    return request.get('/officialwebsite/footer');
  },

  getFooterLinks: (category) => {
    return request.get('/officialwebsite/footer/links', {
      params: category ? { category } : {}
    });
  },

  getSettings: () => {
    return request.get('/officialwebsite/settings');
  }
};

export default officialwebsiteApi;