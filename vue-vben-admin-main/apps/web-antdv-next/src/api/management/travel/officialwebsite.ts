// @ts-nocheck
import request from '#/utils/request';

const officialwebsiteApi = {
  getHomeData: () => {
    return request.get('/officialwebsite/home');
  },

  getNavigation: () => {
    return request.get('/officialwebsite/navigation');
  },

  saveNavigation: (data) => {
    return request.post('/officialwebsite/navigation', data);
  },

  deleteNavigation: (id) => {
    return request.delete(`/officialwebsite/navigation/${id}`);
  },

  getHero: () => {
    return request.get('/officialwebsite/hero');
  },

  saveHero: (data) => {
    return request.post('/officialwebsite/hero', data);
  },

  getDestinations: () => {
    return request.get('/officialwebsite/destinations');
  },

  getFeaturedDestinations: () => {
    return request.get('/officialwebsite/destinations/featured');
  },

  saveDestination: (data) => {
    return request.post('/officialwebsite/destinations', data);
  },

  deleteDestination: (id) => {
    return request.delete(`/officialwebsite/destinations/${id}`);
  },

  getExperiences: () => {
    return request.get('/officialwebsite/experiences');
  },

  saveExperience: (data) => {
    return request.post('/officialwebsite/experiences', data);
  },

  deleteExperience: (id) => {
    return request.delete(`/officialwebsite/experiences/${id}`);
  },

  getNarrative: () => {
    return request.get('/officialwebsite/narrative');
  },

  saveNarrative: (data) => {
    return request.post('/officialwebsite/narrative', data);
  },

  deleteNarrative: (id) => {
    return request.delete(`/officialwebsite/narrative/${id}`);
  },

  getStories: () => {
    return request.get('/officialwebsite/stories');
  },

  getFeaturedStories: () => {
    return request.get('/officialwebsite/stories/featured');
  },

  saveStory: (data) => {
    return request.post('/officialwebsite/stories', data);
  },

  deleteStory: (id) => {
    return request.delete(`/officialwebsite/stories/${id}`);
  },

  getImmersion: () => {
    return request.get('/officialwebsite/immersion');
  },

  saveImmersion: (data) => {
    return request.post('/officialwebsite/immersion', data);
  },

  deleteImmersion: (id) => {
    return request.delete(`/officialwebsite/immersion/${id}`);
  },

  getCta: () => {
    return request.get('/officialwebsite/cta');
  },

  saveCta: (data) => {
    return request.post('/officialwebsite/cta', data);
  },

  getFooter: () => {
    return request.get('/officialwebsite/footer');
  },

  saveFooter: (data) => {
    return request.post('/officialwebsite/footer', data);
  },

  getFooterLinks: (category) => {
    return request.get('/officialwebsite/footer/links', {
      params: category ? { category } : {}
    });
  },

  saveFooterLinks: (data) => {
    return request.post('/officialwebsite/footer/links', data);
  },

  deleteFooterLink: (id) => {
    return request.delete(`/officialwebsite/footer/links/${id}`);
  },

  getSettings: () => {
    return request.get('/officialwebsite/settings');
  },

  saveSettings: (data) => {
    return request.post('/officialwebsite/settings', data);
  }
};

export default officialwebsiteApi;
