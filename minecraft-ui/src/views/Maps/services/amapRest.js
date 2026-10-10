/**
 * 高德「Web服务」REST API 统一封装（第二套 Key：AMAP_WEB_KEY）
 *
 * 与 JS API（AMAP_KEY）职责分离：
 * - 本文件只调用 restapi.amap.com / tsapi.amap.com 的 HTTP 服务
 * - 覆盖 Web服务 Key 的全部可使用服务：
 *   静态地图 / 地理编码 / 逆地理编码 / 关键字搜索 / 周边搜索 / 多边形搜索 /
 *   ID查询 / 输入提示 / 路径规划 / 坐标转换 / 行政区划 / IP定位 / 天气查询 /
 *   矩形·圆形·指定线路交通态势 / 地理围栏 / 猎鹰 / GeoHUB
 */
import { AMAP_WEB_KEY } from '../config.js'

const REST_BASE = 'https://restapi.amap.com'
const TSAPI_BASE = 'https://tsapi.amap.com'

/** v3 常见 infocode 释义（便于前端直接给出可读错误） */
const V3_ERR_HINT = {
  10001: 'Key 不正确或错误',
  10003: '访问超出日配额限制',
  10009: 'Key 类型不符（请确认使用「Web服务」Key）',
  10044: '该服务未开通此 Key 的权限',
  20000: '请求参数非法',
  20003: '请求参数不存在',
  20800: '起终点距离超长',
  30001: '城市/区县编码错误',
  30003: '天气服务：请使用区县级 adcode 查询'
}

/* ================= 基础请求层 ================= */

function buildQuery(params) {
  const qs = new URLSearchParams()
  qs.set('key', AMAP_WEB_KEY)
  for (const [k, v] of Object.entries(params || {})) {
    if (v === undefined || v === null || v === '') continue
    qs.set(k, String(v))
  }
  return qs.toString()
}

async function httpGet(url) {
  const res = await fetch(url, { method: 'GET' })
  if (!res.ok) throw new Error(`网络请求失败（HTTP ${res.status}）`)
  try {
    return await res.json()
  } catch {
    throw new Error('服务响应不是有效 JSON，请检查请求参数')
  }
}

async function httpPost(url, bodyParams) {
  const res = await fetch(url, { method: 'POST' })
  // 高德 v4/猎鹰 的 POST 接口参数可直接拼在 query 上，body 留空
  if (!res.ok) throw new Error(`网络请求失败（HTTP ${res.status}）`)
  try {
    return await res.json()
  } catch {
    throw new Error('服务响应不是有效 JSON，请检查请求参数')
  }
}

/** v3 标准响应校验：{status:'1'|'0', info, infocode} */
function assertV3(data) {
  if (data?.status === '0') {
    const hint = V3_ERR_HINT[data.infocode] ? `（${V3_ERR_HINT[data.infocode]}）` : ''
    throw new Error(`${data.info || '请求失败'}${hint}`)
  }
  return data
}

/** v4 标准响应校验：{errcode:0, errmsg, data} */
function assertV4(data) {
  if (typeof data?.errcode === 'number' && data.errcode !== 0) {
    throw new Error(`${data.errmsg || data.errdetail || '请求失败'}（errcode:${data.errcode}）`)
  }
  return data
}

/** 猎鹰响应校验（兼容 errno / errcode 两种风格） */
function assertTrack(data) {
  if (data?.errno != null && data.errno !== 0) throw new Error(data.errmsg || `猎鹰服务错误 errno:${data.errno}`)
  if (data?.errcode != null && data.errcode !== 0) throw new Error(data.errmsg || `猎鹰服务错误 errcode:${data.errcode}`)
  if (data?.status === '0') throw new Error(data.info || '猎鹰服务请求失败')
  return data
}

/** v3 GET */
async function get(path, params) {
  return assertV3(await httpGet(`${REST_BASE}${path}?${buildQuery(params)}`))
}

/* ================= 服务实现 ================= */

export const amapRest = {
  /* —— 静态地图API：返回可直接用于 <img src> 的 URL —— */
  staticMapUrl(params) {
    return `${REST_BASE}/v3/staticmap?${buildQuery(params)}`
  },

  /* —— 地理编码API：地址 → 坐标 —— */
  async geocode(params) {
    const d = await get('/v3/geocode/geo', params)
    return d
  },

  /* —— 逆地理编码API：坐标 → 地址 —— */
  async regeo(params) {
    const d = await get('/v3/geocode/regeo', params)
    return d.regeocode || {}
  },

  /* —— 关键字搜索API —— */
  async placeText(params) {
    const d = await get('/v3/place/text', params)
    return { pois: d.pois || [], suggests: d.suggests || [], count: d.count }
  },

  /* —— 周边搜索API —— */
  async placeAround(params) {
    const d = await get('/v3/place/around', params)
    return { pois: d.pois || [], count: d.count }
  },

  /* —— 多边形搜索API —— */
  async placePolygon(params) {
    const d = await get('/v3/place/polygon', params)
    return { pois: d.pois || [], count: d.count }
  },

  /* —— ID查询API（POI 详情） —— */
  async placeDetail(id) {
    const d = await get('/v3/place/detail', { id })
    return (d.pois && d.pois[0]) || null
  },

  /* —— 输入提示API —— */
  async inputtips(params) {
    const d = await get('/v3/assistant/inputtips', params)
    return d.tips || []
  },

  /* —— 路径规划API：驾车 / 步行 / 公交 / 骑行 —— */
  async driving(params) {
    const d = await get('/v3/direction/driving', params)
    const path = d.route?.paths?.[0]
    if (!path) throw new Error('未规划到驾车路线')
    return path
  },
  async walking(params) {
    const d = await get('/v3/direction/walking', params)
    const path = d.route?.paths?.[0]
    if (!path) throw new Error('未规划到步行路线')
    return path
  },
  async transit(params) {
    const d = await get('/v3/direction/transit/integrated', params)
    const transits = d.route?.transits || []
    if (!transits.length) throw new Error('未规划到公交路线')
    return transits
  },
  /** 骑行路径规划为 v4 接口：{errcode:0,data:{paths:[...]}} */
  async bicycling(params) {
    const d = assertV4(await httpGet(`${REST_BASE}/v4/direction/bicycling?${buildQuery(params)}`))
    const path = d.data?.paths?.[0]
    if (!path) throw new Error('未规划到骑行路线')
    return path
  },

  /* —— 坐标转换API —— */
  async convert(params) {
    const d = await get('/v3/assistant/coordinate/convert', params)
    return d.locations || ''
  },

  /* —— 行政区划查询API —— */
  async district(params) {
    const d = await get('/v3/config/district', params)
    return d.districts || []
  },

  /* —— IP定位API —— */
  async ip(params) {
    const d = await get('/v3/ip', params)
    return d
  },

  /* —— 天气查询API —— */
  async weather(params) {
    const d = await get('/v3/weather/weatherInfo', params)
    return { lives: d.lives || [], forecasts: d.forecasts || [] }
  },

  /* —— 交通态势API：矩形 / 圆形 / 指定线路 —— */
  async trafficRectangle(params) {
    const d = await get('/v3/traffic/status/rectangle', params)
    return d.trafficinfo || {}
  },
  async trafficCircle(params) {
    const d = await get('/v3/traffic/status/circle', params)
    return d.trafficinfo || {}
  },
  async trafficRoad(params) {
    const d = await get('/v3/traffic/status/road', params)
    return d.trafficinfo || {}
  },

  /* —— 地理围栏API（v4，需控制台开通） —— */
  async fenceCreate(params) {
    return assertV4(await httpPost(`${REST_BASE}/v4/geofence/metas?${buildQuery(params)}`)).data
  },
  async fenceList(params) {
    const d = assertV4(await httpGet(`${REST_BASE}/v4/geofence/metas?${buildQuery(params)}`))
    return d.data || d
  },
  async fenceStatus(params) {
    const d = assertV4(await httpGet(`${REST_BASE}/v4/geofence/status?${buildQuery(params)}`))
    return d.data || d
  },

  /* —— 猎鹰服务API（tsapi.amap.com，需控制台开通） —— */
  async trackServiceList() {
    return assertTrack(await httpGet(`${TSAPI_BASE}/v1/track/service/list?${buildQuery({})}`))
  },
  async trackServiceCreate(name) {
    return assertTrack(await httpPost(`${TSAPI_BASE}/v1/track/service/create?${buildQuery({ name })}`))
  },
  async trackTerminalList(params) {
    return assertTrack(await httpGet(`${TSAPI_BASE}/v1/track/terminal/list?${buildQuery(params)}`))
  },
  async trackSearch(params) {
    return assertTrack(await httpGet(`${TSAPI_BASE}/v1/track/terminal/trsearch?${buildQuery(params)}`))
  },

  /* —— GeoHUB服务API（通用转发，数据产品需单独订购） —— */
  async geohub(url, extraParams) {
    let fullUrl
    if (/^https?:\/\//i.test(url)) {
      fullUrl = `${url}${url.includes('?') ? '&' : '?'}${buildQuery(extraParams)}`
    } else {
      fullUrl = `${REST_BASE}${url.startsWith('/') ? '' : '/'}${url}?${buildQuery(extraParams)}`
    }
    return httpGet(fullUrl)
  }
}

export default amapRest
