/**
 * 太阳/月亮天文近似计算（昼夜分界线、日月图层共用）
 * 采用简化模型：太阳赤纬按年度余弦近似，时角按 UTC 线性近似，精度约 ±1°，足够可视化用途
 */
import * as THREE from 'three'

const DEG = Math.PI / 180

/** 与 three-globe 一致的球坐标转换（单位向量） */
export function dirFromLatLng(lat, lng, out = new THREE.Vector3()) {
  const phi = (90 - lat) * DEG
  const theta = (90 - lng) * DEG
  return out.set(
    Math.sin(phi) * Math.cos(theta),
    Math.cos(phi),
    Math.sin(phi) * Math.sin(theta)
  )
}

/** 太阳直射点（subsolar point） */
export function subsolarPoint(date = new Date()) {
  const start = Date.UTC(date.getUTCFullYear(), 0, 0)
  const doy = (date.getTime() - start) / 86400000
  // 赤纬：-23.44° * cos(2π (N+10)/365)
  const decl = -23.44 * Math.cos(2 * Math.PI * (doy + 10) / 365)
  const utcH = date.getUTCHours() + date.getUTCMinutes() / 60 + date.getUTCSeconds() / 3600
  // 太阳直射经度：UTC 正午时在 lng=0，每偏离 1 小时西移 15°
  const lng = ((12 - utcH) * 15 + 540) % 360 - 180
  return { lat: decl, lng }
}

/** 太阳方向单位向量 */
export function sunDirection(date = new Date(), out = new THREE.Vector3()) {
  const p = subsolarPoint(date)
  return dirFromLatLng(p.lat, p.lng, out)
}

/** 月亮方向（简化：以 27.32 天为周期沿黄道附近圆轨道） */
export function moonDirection(date = new Date(), out = new THREE.Vector3()) {
  const days = date.getTime() / 86400000
  const phase = (days % 27.32) / 27.32
  const { lng: sunLng } = subsolarPoint(date)
  const lng = ((sunLng + 180 + phase * 360) + 540) % 360 - 180
  const lat = 5.1 * Math.sin(2 * Math.PI * phase)
  return dirFromLatLng(lat, lng, out)
}
