import request from '#/utils/request';

// ==================== 会员等级 ====================

export const getMemberLevels = (): Promise<any> =>
  request.get('/admin/member/levels');

export const createMemberLevel = (data: any): Promise<any> =>
  request.post('/admin/member/levels', data);

export const updateMemberLevel = (id: any, data: any): Promise<any> =>
  request.put(`/admin/member/levels/${id}`, data);

export const deleteMemberLevel = (id: any): Promise<any> =>
  request.delete(`/admin/member/levels/${id}`);

// ==================== 会员套餐 ====================

export const getMemberPackages = (): Promise<any> =>
  request.get('/admin/member/packages');

export const createMemberPackage = (data: any): Promise<any> =>
  request.post('/admin/member/packages', data);

export const updateMemberPackage = (id: any, data: any): Promise<any> =>
  request.put(`/admin/member/packages/${id}`, data);

export const deleteMemberPackage = (id: any): Promise<any> =>
  request.delete(`/admin/member/packages/${id}`);

// ==================== 会员订单 ====================

export const getMemberOrderPage = (params: any): Promise<any> =>
  request.get('/admin/member/orders/page', { params });

export const refundMemberOrder = (id: any): Promise<any> =>
  request.post(`/admin/member/orders/${id}/refund`);

// ==================== 会员用户 ====================

export const getMemberUserPage = (params: any): Promise<any> =>
  request.get('/admin/member/users/page', { params });

export const adjustMemberGrowth = (userId: any, data: any): Promise<any> =>
  request.post(`/admin/member/users/${userId}/growth`, data);

export const setMemberLevel = (userId: any, data: any): Promise<any> =>
  request.post(`/admin/member/users/${userId}/level`, data);

export const grantMemberBenefit = (userId: any, data: any): Promise<any> =>
  request.post(`/admin/member/users/${userId}/benefit`, data);

// ==================== 流水记录 ====================

export const getMemberGrowthPage = (params: any): Promise<any> =>
  request.get('/admin/member/growth/page', { params });

export const getMemberSignPage = (params: any): Promise<any> =>
  request.get('/admin/member/sign/page', { params });
