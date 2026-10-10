/**
 * 人文经济图层（场景对象部分：夜光 / 国界示意 / 城市点位 / 时区线 / 语言文化圈）
 * 航线、海底光缆走 globe.gl arcsData；GDP 热力走 heatmapsData（由 GlobeCanvas 统一管理）
 */
import * as THREE from 'three'
import { toVec3, pathsToLineSegments, makeSoftPoints, makeTextSprite, buildGraticuleGeometry } from './util.js'
import { CITIES, BORDER_RADII, BORDER_DEFAULT_RADII, CULTURE_REGIONS } from './data.js'
import { countries } from '../data/countries.js'

/* ---------- 夜光（城市灯光） ---------- */
function buildNightlights(ctx) {
  const { radius } = ctx
  const pts = CITIES.map(([name, lat, lng, pop]) => ({
    lat, lng, alt: 0.004,
    color: pop > 1500 ? '#ffe0a0' : (pop > 700 ? '#ffc873' : '#e8a95a'),
    size: 1.6 + Math.sqrt(pop) * 0.12,
    phase: Math.random() * Math.PI * 2
  }))
  const soft = makeSoftPoints(radius, pts, { opacity: 0.9, pulse: 0.25, pulseFreq: 1.2 })
  const group = new THREE.Group().add(soft.object)
  return { group, update: soft.update, dispose: soft.dispose }
}

/* ---------- 国界示意（国家中心范围圈） ---------- */
function buildBorders(ctx) {
  const { radius } = ctx
  const segs = 56
  const pos = []
  const v = new THREE.Vector3()
  for (const c of countries) {
    const rDeg = BORDER_RADII[c.id] ?? BORDER_DEFAULT_RADII[c.continent] ?? 4
    if (!rDeg) continue
    const r = radius * rDeg * Math.PI / 180 * 1.35
    // 圆心处局部切平面圆
    toVec3(radius, c.lat, c.lng, 0.006, v)
    const normal = v.clone().normalize()
    const up = Math.abs(normal.y) > 0.95 ? new THREE.Vector3(1, 0, 0) : new THREE.Vector3(0, 1, 0)
    const t1 = new THREE.Vector3().crossVectors(normal, up).normalize()
    const t2 = new THREE.Vector3().crossVectors(normal, t1).normalize()
    let prev = null
    for (let i = 0; i <= segs; i++) {
      const a = i / segs * Math.PI * 2
      const p = v.clone().addScaledVector(t1, Math.cos(a) * r).addScaledVector(t2, Math.sin(a) * r)
      if (prev) pos.push(prev.x, prev.y, prev.z, p.x, p.y, p.z)
      prev = p
    }
  }
  const geo = new THREE.BufferGeometry()
  geo.setAttribute('position', new THREE.Float32BufferAttribute(pos, 3))
  const mat = new THREE.LineBasicMaterial({ color: 0xffffff, transparent: true, opacity: 0.22 })
  const group = new THREE.Group().add(new THREE.LineSegments(geo, mat))
  return { group, dispose() { geo.dispose(); mat.dispose() } }
}

/* ---------- 城市点位（人口规模圆点 + 名称精灵） ---------- */
function buildCities(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const pts = CITIES.map(([name, lat, lng, pop]) => ({
    lat, lng, alt: 0.005,
    color: pop > 1500 ? '#ff9f43' : '#ffb877',
    size: 1.8 + Math.sqrt(pop) * 0.1,
    phase: Math.random() * Math.PI * 2
  }))
  const soft = makeSoftPoints(radius, pts, { opacity: 0.85, pulse: 0.3, pulseFreq: 1.6 })
  group.add(soft.object)
  // 仅大城显示名称
  const sprites = []
  for (const [name, lat, lng, pop] of CITIES) {
    if (pop < 1000) continue
    const sp = makeTextSprite(name, { color: '#ffd9a8', fontSize: 40, scale: 0.9 })
    toVec3(radius, lat, lng, 0.035, sp.position)
    group.add(sp)
    sprites.push(sp)
  }
  return {
    group,
    update: soft.update,
    dispose() {
      soft.dispose()
      sprites.forEach(s => s.userData.dispose())
      group.children.forEach(m => m.geometry?.dispose?.())
    }
  }
}

/* ---------- 时区线（24 条经线 + 当前 UTC 经线高亮） ---------- */
function buildTimezones(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const dimGeo = buildGraticuleGeometry(radius, { latStep: 999, lngStep: 15, latRange: [-85, 85], alt: 0.0025 })
  const dimMat = new THREE.LineBasicMaterial({ color: 0x4dd0e1, transparent: true, opacity: 0.18 })
  group.add(new THREE.LineSegments(dimGeo, dimMat))

  // 当前 UTC 时区高亮经线（每小时刷新一次）
  let hiLine = null
  let lastHour = -1
  const buildHighlight = () => {
    const now = new Date()
    const utcH = now.getUTCHours() + now.getUTCMinutes() / 60
    const lng = ((12 - utcH) * 15 + 540) % 360 - 180
    const pos = []
    const v = new THREE.Vector3()
    for (let lat = -85; lat < 85; lat += 2) {
      toVec3(radius, lat, lng, 0.004, v); pos.push(v.x, v.y, v.z)
      toVec3(radius, lat + 2, lng, 0.004, v); pos.push(v.x, v.y, v.z)
    }
    const geo = new THREE.BufferGeometry()
    geo.setAttribute('position', new THREE.Float32BufferAttribute(pos, 3))
    hiLine = new THREE.LineSegments(geo, new THREE.LineBasicMaterial({ color: 0xffe082, transparent: true, opacity: 0.8 }))
    group.add(hiLine)
  }
  buildHighlight()
  return {
    group,
    update(dt, t) {
      const h = new Date().getUTCHours()
      if (h !== lastHour) {
        lastHour = h
        if (hiLine) { group.remove(hiLine); hiLine.geometry.dispose(); hiLine.material.dispose() }
        buildHighlight()
      }
    },
    dispose() {
      dimGeo.dispose(); dimMat.dispose()
      if (hiLine) { hiLine.geometry.dispose(); hiLine.material.dispose() }
    }
  }
}

/* ---------- 语言文化圈示意 ---------- */
function buildCulture(ctx) {
  const { radius } = ctx
  const group = new THREE.Group()
  const sprites = []
  for (const [name, lat, lng, rDeg, color] of CULTURE_REGIONS) {
    const geo = new THREE.CircleGeometry(radius * rDeg * Math.PI / 180 * 1.6, 32)
    const mat = new THREE.MeshBasicMaterial({
      color: new THREE.Color(color), transparent: true, opacity: 0.16,
      side: THREE.DoubleSide, depthWrite: false
    })
    const mesh = new THREE.Mesh(geo, mat)
    toVec3(radius, lat, lng, 0.003, mesh.position)
    mesh.lookAt(mesh.position.clone().multiplyScalar(2))
    group.add(mesh)
    const sp = makeTextSprite(name, { color, fontSize: 38, scale: 0.85 })
    toVec3(radius, lat, lng, 0.02, sp.position)
    group.add(sp)
    sprites.push(sp)
  }
  return {
    group,
    dispose() {
      sprites.forEach(s => s.userData.dispose())
      group.children.forEach(m => { m.geometry?.dispose(); m.material?.dispose?.() })
    }
  }
}

export const defs = {
  nightlights: { build: buildNightlights },
  borders: { build: buildBorders },
  cities: { build: buildCities },
  timezones: { build: buildTimezones },
  culture: { build: buildCulture }
}
