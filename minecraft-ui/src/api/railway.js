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

export const payRailwayOrder = (id) =>
  call(request.post(`/railway/orders/${id}/pay`))

export const cancelRailwayOrder = (id) =>
  call(request.post(`/railway/orders/${id}/cancel`))

/* ==================== 乘车人 ==================== */

export const getRailwayPassengers = () =>
  call(request.get('/railway/passengers'))

export const addRailwayPassenger = (data) =>
  call(request.post('/railway/passengers', data))

export const deleteRailwayPassenger = (id) =>
  call(request.delete(`/railway/passengers/${id}`))
