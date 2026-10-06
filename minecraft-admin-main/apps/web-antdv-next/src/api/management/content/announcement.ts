// @ts-nocheck
import request from '#/utils/request';

// 公告分页查询（含草稿/已下架）
export const getAnnouncementPage = (params: any): Promise<any> => {
  return request.get('/admin/announcements', { params });
};

// 新建公告（status=1 立即发布；指定未来 publishTime 为定时发布）
export const addAnnouncement = (data: any): Promise<any> => {
  return request.post('/admin/announcements', data);
};

// 修改公告
export const updateAnnouncement = (id: any, data: any): Promise<any> => {
  return request.put(`/admin/announcements/${id}`, data);
};

// 删除公告（逻辑删除）
export const deleteAnnouncement = (id: any): Promise<any> => {
  return request.delete(`/admin/announcements/${id}`);
};

// 发布公告
export const publishAnnouncement = (id: any): Promise<any> => {
  return request.post(`/admin/announcements/${id}/publish`);
};

// 下架公告
export const offlineAnnouncement = (id: any): Promise<any> => {
  return request.post(`/admin/announcements/${id}/offline`);
};

// 置顶/取消置顶
export const topAnnouncement = (id: any, isTop: number): Promise<any> => {
  return request.post(`/admin/announcements/${id}/top`, null, {
    params: { isTop },
  });
};

// 阅读统计（浏览量/已读数/阅读率）
export const getReadStat = (id: any): Promise<any> => {
  return request.get(`/admin/announcements/${id}/read-stat`);
};
