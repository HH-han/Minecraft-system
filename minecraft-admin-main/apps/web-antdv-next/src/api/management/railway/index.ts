// @ts-nocheck
import request from '#/utils/request';

/* ==================== 车站 ==================== */

export const getStationList = (params: any): Promise<any> =>
  request.get('/railway/admin/stations', { params });

export const getAllStations = (): Promise<any> =>
  request.get('/railway/admin/stations/all');

export const addStation = (data: any): Promise<any> =>
  request.post('/railway/admin/stations', data);

export const updateStation = (id: any, data: any): Promise<any> =>
  request.put(`/railway/admin/stations/${id}`, data);

export const deleteStation = (id: any): Promise<any> =>
  request.delete(`/railway/admin/stations/${id}`);

/* ==================== 车次 ==================== */

export const getTrainList = (params: any): Promise<any> =>
  request.get('/railway/admin/trains', { params });

export const addTrain = (data: any): Promise<any> =>
  request.post('/railway/admin/trains', data);

export const updateTrain = (id: any, data: any): Promise<any> =>
  request.put(`/railway/admin/trains/${id}`, data);

export const deleteTrain = (id: any): Promise<any> =>
  request.delete(`/railway/admin/trains/${id}`);

export const getTrainStops = (id: any): Promise<any> =>
  request.get(`/railway/admin/trains/${id}/stops`);

export const saveTrainStops = (id: any, data: any): Promise<any> =>
  request.put(`/railway/admin/trains/${id}/stops`, data);

/* ==================== 排班与库存 ==================== */

export const getServiceList = (params: any): Promise<any> =>
  request.get('/railway/admin/services', { params });

export const generateServices = (data: any): Promise<any> =>
  request.post('/railway/admin/services/generate', data);

export const getInventory = (params: any): Promise<any> =>
  request.get('/railway/admin/inventory', { params });

/* ==================== 订单与统计 ==================== */

export const getRailwayOrderList = (params: any): Promise<any> =>
  request.get('/railway/admin/orders', { params });

export const getRailwayStats = (): Promise<any> =>
  request.get('/railway/admin/stats');

export const getRailwayTrend = (params: any): Promise<any> =>
  request.get('/railway/admin/stats/trend', { params });
