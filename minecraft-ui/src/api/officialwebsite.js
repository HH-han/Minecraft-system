import request from '@/utils/request';

const officialwebsiteApi = {
  getHomeData: () => {
    return request.get('/officialwebsite/home', { silent: true });
  },

  getNavigation: () => {
    return request.get('/officialwebsite/navigation', { silent: true });
  },

  getHero: () => {
    return request.get('/officialwebsite/hero', { silent: true });
  },

  getDestinations: () => {
    return request.get('/officialwebsite/destinations', { silent: true });
  },

  getFeaturedDestinations: () => {
    return request.get('/officialwebsite/destinations/featured', { silent: true });
  },

  getExperiences: () => {
    return request.get('/officialwebsite/experiences', { silent: true });
  },

  getNarrative: () => {
    return request.get('/officialwebsite/narrative', { silent: true });
  },

  getStories: () => {
    return request.get('/officialwebsite/stories', { silent: true });
  },

  getFeaturedStories: () => {
    return request.get('/officialwebsite/stories/featured', { silent: true });
  },

  getImmersion: () => {
    return request.get('/officialwebsite/immersion', { silent: true });
  },

  getCta: () => {
    return request.get('/officialwebsite/cta', { silent: true });
  },

  getFooter: () => {
    return request.get('/officialwebsite/footer', { silent: true });
  },

  getFooterLinks: (category) => {
    return request.get('/officialwebsite/footer/links', {
      params: category ? { category } : {},
      silent: true
    });
  },

  getSettings: () => {
    return request.get('/officialwebsite/settings', { silent: true });
  }
};

export default officialwebsiteApi;