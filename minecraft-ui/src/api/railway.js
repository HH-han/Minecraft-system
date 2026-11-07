import request from '@/utils/request'

/**
 * 统一拆包：后端业务异常以 HTTP 200 + code!=200 返回，
 * 这里转为 rejected Promise，便于页面 catch 后展示 message。
 */
const call = async (promise) => {
  const res = await promise
  if (res && typeof res.code === 'number' && res.code !== 200) {
    const error = new Error(res.message || 'Request failed')
    error.code = res.code
    throw error
  }
  return res
}

/* ==================== 查询 ==================== */

// 车站联想（汉字 / 拼音 / 首字母）
export const getRailwayStations = (keyword) =>
  call(request.get('/railway/stations', { params: { keyword } }))

// 车票查询（fromStation / toStation / date）
export const searchRailwayTickets = (params) =>
  call(request.get('/railway/tickets/search', { params }))

// 运行车次详情（经停站 + 全程余票）
export const getRailwayServiceDetail = (id) =>
  call(request.get(`/railway/services/${id}`))

// 时刻表
export const getRailwayTimetable = (params) =>
  call(request.get('/railway/timetable', { params }))

// 正晚点
export const getRailwayStatus = (trainNo) =>
  call(request.get('/railway/status', { params: { trainNo } }))

/* ==================== 订单 ==================== */

export const createRailwayOrder = (data) =>
  call(request.post('/railway/orders', data))

export const getRailwayOrders = (params) =>
  call(request.get('/railway/orders', { params }))

export const getRailwayOrderDetail = (id) =>
  call(request.get(`/railway/orders/${id}`))

export const cancelRailwayOrder = (id) =>
  call(request.post(`/railway/orders/${id}/cancel`))

/* ==================== 乘车人 ==================== */

export const getRailwayPassengers = () =>
  call(request.get('/railway/passengers'))

export const addRailwayPassenger = (data) =>
  call(request.post('/railway/passengers', data))

export const deleteRailwayPassenger = (id) =>
  call(request.delete(`/railway/passengers/${id}`))

/* ==================== 信息查询 ==================== */

// 车站大屏（type: DEPARTURE / ARRIVAL）
export const getRailwayBoard = (params) =>
  call(request.get('/railway/info/board', { params }))

// 区间票价查询
export const getRailwayFares = (params) =>
  call(request.get('/railway/info/fares', { params }))

/* ==================== 团购服务 ==================== */

export const quoteRailwayGroup = (params) =>
  call(request.get('/railway/group/quote', { params }))

export const createRailwayGroupBooking = (data) =>
  call(request.post('/railway/group/bookings', data))

export const getRailwayGroupBookings = (params) =>
  call(request.get('/railway/group/bookings', { params }))

export const cancelRailwayGroupBooking = (id) =>
  call(request.post(`/railway/group/bookings/${id}/cancel`))

/* ==================== 会员服务 ==================== */

export const getRailwayMemberOverview = () =>
  call(request.get('/railway/member/overview'))

export const getRailwayPointLogs = (params) =>
  call(request.get('/railway/member/points', { params }))

export const getRailwayMemberGifts = () =>
  call(request.get('/railway/member/gifts'))

export const redeemRailwayGift = (data) =>
  call(request.post('/railway/member/redeem', data))

/* ==================== 站车服务 ==================== */

export const getRailwayStationServices = () =>
  call(request.get('/railway/station-services'))

export const createRailwayServiceRequest = (data) =>
  call(request.post('/railway/station-services/requests', data))

export const getRailwayServiceRequests = (params) =>
  call(request.get('/railway/station-services/requests', { params }))

export const cancelRailwayServiceRequest = (id) =>
  call(request.post(`/railway/station-services/requests/${id}/cancel`))

/* ==================== 商旅服务 ==================== */

export const getRailwayBusinessAccount = () =>
  call(request.get('/railway/business/account'))

export const saveRailwayBusinessAccount = (data) =>
  call(request.put('/railway/business/account', data))

export const getRailwayBusinessSummary = () =>
  call(request.get('/railway/business/summary'))

export const createRailwayBusinessTrip = (data) =>
  call(request.post('/railway/business/trips', data))

export const getRailwayBusinessTrips = (params) =>
  call(request.get('/railway/business/trips', { params }))

export const cancelRailwayBusinessTrip = (id) =>
  call(request.post(`/railway/business/trips/${id}/cancel`))

/* ==================== 出行指南 ==================== */

export const getRailwayGuides = (params) =>
  call(request.get('/railway/guides', { params }))

export const getRailwayGuideDetail = (id) =>
  call(request.get(`/railway/guides/${id}`))

export const getRailwayGuideRelated = (id, limit = 3) =>
  call(request.get(`/railway/guides/${id}/related`, { params: { limit } }))
