// @ts-nocheck
import request from '#/utils/request';

/**
 * 智能推荐管理后台接口
 * baseURL 已包含 /api，响应拦截器已解包 ApiResponse.data
 */

// ---------------- 算法参数 ----------------

// 获取全部推荐参数
export const getRecommendationConfig = (): Promise<any> => {
  return request.get('/admin/recommendations/config');
};

// 批量更新推荐参数（整体校验，任一非法全部不落库）
export const updateRecommendationConfig = (updates: any[]): Promise<any> => {
  return request.put('/admin/recommendations/config', updates);
};

// 恢复默认值（category 为空时重置全局参数）
export const resetRecommendationConfig = (category?: string): Promise<any> => {
  return request.post('/admin/recommendations/config/reset', undefined, {
    params: category ? { category } : {},
  });
};

// ---------------- 物品 / 人工干预 ----------------

// 分页查询某分类推荐行
export const getRecommendationItems = (
  category: string,
  page: number = 1,
  pageSize: number = 20,
): Promise<any> => {
  return request.get(`/admin/recommendations/items/${category}`, {
    params: { page, pageSize },
  });
};

// 人工干预：置顶 / 加权 / 上线下线
export const setRecommendationFeature = (
  category: string,
  itemId: number | string,
  payload: { featured?: boolean; featureWeight?: number; status?: number },
): Promise<any> => {
  return request.put(
    `/admin/recommendations/items/${category}/${itemId}/feature`,
    payload,
  );
};

// ---------------- 重算 / 任务 ----------------

// 触发推荐重算（category 为空时全量，同步返回结果）
export const triggerRecommendationRecalc = (category?: string): Promise<any> => {
  return request.post('/admin/recommendations/recalc', undefined, {
    params: category ? { category } : {},
  });
};

// 最近重算任务日志
export const getRecommendationJobs = (
  category?: string,
  limit: number = 20,
): Promise<any> => {
  return request.get('/admin/recommendations/jobs', {
    params: { category, limit },
  });
};

// ---------------- 分析看板 / 调度 ----------------

// 推荐效果分析看板
export const getRecommendationAnalytics = (
  category?: string,
  windowDays: number = 7,
): Promise<any> => {
  return request.get('/admin/recommendations/analytics', {
    params: { category, windowDays },
  });
};

// 定时任务状态
export const getRecommendationSchedule = (): Promise<any> => {
  return request.get('/admin/recommendations/schedule');
};

// ---------------- 人工干预规则 ----------------

// 查看某分类的干预规则
export const getRecommendationRules = (
  category: string,
  status?: number,
): Promise<any> => {
  return request.get(`/admin/recommendations/rules/${category}`, {
    params: status === undefined || status === null ? {} : { status },
  });
};

// 新建干预规则
export const createRecommendationRule = (payload: any): Promise<any> => {
  return request.post('/admin/recommendations/rules', payload);
};

// 更新干预规则
export const updateRecommendationRule = (id: number | string, payload: any): Promise<any> => {
  return request.put(`/admin/recommendations/rules/${id}`, payload);
};

// 删除干预规则
export const deleteRecommendationRule = (id: number | string): Promise<any> => {
  return request.delete(`/admin/recommendations/rules/${id}`);
};
