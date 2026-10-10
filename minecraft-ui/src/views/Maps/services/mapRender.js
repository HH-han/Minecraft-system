/**
 * 工具箱结果 → AMap 实例覆盖物渲染器
 *
 * 由 ServicePanel 持有，各工具通过 render(values, result, r) 钩子
 * 把结果绘制到导航面板的地图上；每次执行前统一 clear。
 */
export const PALETTE = {
  blue: '#0a84ff',
  green: '#30d158',
  red: '#ff453a',
  orange: '#ff9f0a',
  purple: '#bf5af2'
}

/** 把 'lng,lat' 字符串（或数组）解析为 [lng, lat]，非法返回 null */
export function toLngLat(str) {
  if (Array.isArray(str)) {
    const [a, b] = str.map(Number)
    return Number.isFinite(a) && Number.isFinite(b) ? [a, b] : null
  }
  if (typeof str !== 'string') return null
  const [a, b] = str.split(',').map(Number)
  return Number.isFinite(a) && Number.isFinite(b) ? [a, b] : null
}

/** 把 'lng,lat;lng,lat|...' 字符串解析为路径环数组（| 分隔多环） */
export function parseRings(str) {
  return String(str || '')
    .split('|')
    .map(ring => ring.split(';').map(toLngLat).filter(Boolean))
    .filter(ring => ring.length >= 2)
}

export function createRenderer(map, AMap) {
  const overlays = []

  const add = o => {
    if (o) {
      map.add(o)
      overlays.push(o)
    }
    return o
  }

  function clear() {
    if (!overlays.length) return
    try { map.remove(overlays) } catch { /* 忽略 */ }
    overlays.length = 0
  }

  function fit() {
    if (!overlays.length) return
    try { map.setFitView(overlays, false, [80, 80, 80, 80]) } catch { /* 忽略 */ }
  }

  /** 批量点标记（小圆点） */
  function dot(raw, { color = PALETTE.blue, radius = 6, title = '' } = {}) {
    const pos = toLngLat(raw)
    if (!pos) return null
    return add(new AMap.CircleMarker({
      center: pos,
      radius,
      fillColor: color,
      fillOpacity: 0.85,
      strokeColor: '#ffffff',
      strokeWeight: 1.5,
      bubble: true,
      title
    }))
  }

  /** 关键点标记（带气泡标签） */
  function pin(raw, label = '') {
    const pos = toLngLat(raw)
    if (!pos) return null
    return add(new AMap.Marker({
      position: pos,
      title: label,
      label: label
        ? { content: `<div class="svc-pin-label">${label}</div>`, direction: 'top' }
        : undefined
    }))
  }

  /** 圆形范围 */
  function circle(rawCenter, radius, { color = PALETTE.blue } = {}) {
    const c = toLngLat(rawCenter)
    const r = Number(radius)
    if (!c || !Number.isFinite(r) || r <= 0) return null
    return add(new AMap.Circle({
      center: c,
      radius: r,
      fillColor: color,
      fillOpacity: 0.12,
      strokeColor: color,
      strokeWeight: 2,
      bubble: true
    }))
  }

  /** 路径字符串 → 多边形（fill）或折线（stroke） */
  function pathFromStr(str, { color = PALETTE.blue, fill = false, width = 2 } = {}) {
    const rings = parseRings(str)
    if (!rings.length) return null
    if (fill) {
      return add(new AMap.Polygon({
        path: rings.length === 1 ? rings[0] : rings,
        fillColor: color,
        fillOpacity: 0.14,
        strokeColor: color,
        strokeWeight: width,
        bubble: true
      }))
    }
    return add(new AMap.Polyline({
      path: rings.length === 1 ? rings[0] : rings,
      strokeColor: color,
      strokeWeight: width,
      strokeOpacity: 0.9,
      lineJoin: 'round',
      bubble: true
    }))
  }

  /** 点位数组 → 折线（路线绘制） */
  function polyline(points, { color = PALETTE.blue, width = 5, dashed = false } = {}) {
    if (!Array.isArray(points) || points.length < 2) return null
    return add(new AMap.Polyline({
      path: points,
      strokeColor: color,
      strokeWeight: width,
      strokeOpacity: 0.92,
      strokeStyle: dashed ? 'dashed' : 'solid',
      lineJoin: 'round',
      showDir: !dashed,
      bubble: true
    }))
  }

  /** '左下角;右上角' → 矩形 */
  function rect(str, { color = PALETTE.orange } = {}) {
    const pts = String(str || '').split(';').map(toLngLat).filter(Boolean)
    if (pts.length < 2) return null
    return add(new AMap.Rectangle({
      bounds: [new AMap.LngLat(pts[0][0], pts[0][1]), new AMap.LngLat(pts[1][0], pts[1][1])],
      fillColor: color,
      fillOpacity: 0.1,
      strokeColor: color,
      strokeWeight: 2,
      bubble: true
    }))
  }

  return { clear, fit, dot, pin, circle, pathFromStr, polyline, rect, PALETTE }
}
