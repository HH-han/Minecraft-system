import request from '@/utils/request'

/* ==================== 查询 ==================== */

// 车站联想（汉字 / 拼音 / 首字母）
export const getRailwayStations = (keyword) =>
  request.get('/railway/stations', { params: { keyword } })

// 车票查询（fromStation / toStation / date）
export const searchRailwayTickets = (params) =>
  request.get('/railway/tickets/search', { params })

// 运行车次详情（经停站 + 全程余票）
export const getRailwayServiceDetail = (id) =>
  request.get(`/railway/services/${id}`)

// 时刻表
export const getRailwayTimetable = (params) =>
  request.get('/railway/timetable', { params })

// 正晚点
export const getRailwayStatus = (trainNo) =>
  request.get('/railway/status', { params: { trainNo } })

/* ==================== 订单 ==================== */

export const createRailwayOrder = (data) =>
  request.post('/railway/orders', data)

export const getRailwayOrders = (params) =>
  request.get('/railway/orders', { params })

export const getRailwayOrderDetail = (id) =>
  request.get(`/railway/orders/${id}`)

export const payRailwayOrder = (id) =>
  request.post(`/railway/orders/${id}/pay`)

export const cancelRailwayOrder = (id) =>
  request.post(`/railway/orders/${id}/cancel`)

/* ==================== 乘车人 ==================== */

export const getRailwayPassengers = () =>
  request.get('/railway/passengers')

export const addRailwayPassenger = (data) =>
  request.post('/railway/passengers', data)

export const deleteRailwayPassenger = (id) =>
  request.delete(`/railway/passengers/${id}`)
