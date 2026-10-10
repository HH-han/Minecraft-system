<template>
  <div
    ref="containerRef"
    class="globe-container"
    :class="{ 'is-dark': isDark }"
    @mousemove="onMouseMove"
    @contextmenu.prevent
  >
    <!-- globe.gl 专用挂载点：其 init 会执行 domNode.innerHTML = "" 清空容器，
         因此不能与 v-if 遮罩同级混用，否则 Vue patch 时找不到父节点报 insertBefore null -->
    <div ref="globeMountRef" class="globe-mount"></div>
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p class="loading-text">正在加载 3D 地球...</p>
      <p class="loading-sub">地形 / 云层 / 热力数据初始化中</p>
    </div>
    <div v-if="!loading && errorMessage" class="error-overlay">
      <div class="error-icon">⚠</div>
      <h3 class="error-title">地球渲染失败</h3>
      <p class="error-message">{{ errorMessage }}</p>
      <p class="error-hint">请确认浏览器已开启 WebGL 加速（Chrome / Edge / Firefox / Safari 最新版）</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import Globe from 'globe.gl'
import * as THREE from 'three'
import earthImg from '../assets/earth-blue-marble.jpg'
import darkImg from '../assets/earth-dark.jpg'
import nightImg from '../assets/earth-night.jpg'
import topoImg from '../assets/earth-topology.png'
import cloudsImg from '../assets/clouds.png'
import skyImg from '../assets/night-sky.png'
import { populationHeatmap } from '../data/population.js'
import { countries } from '../data/countries.js'
import { continents } from '../data/continents.js'
import { createLayerRegistry, REGISTRY_KEYS } from '../layers/index.js'
import { CITIES, FLIGHT_HUBS, FLIGHT_ROUTES, CABLE_ROUTES, GDP_POINTS } from '../layers/data.js'

const props = defineProps({
  isDark: { type: Boolean, default: false },
  autoRotate: { type: Boolean, default: true },
  /** 图层开关：{ terrain, contour, clouds, heatmap, labels, atmosphere } */
  layers: {
    type: Object,
    default: () => ({
      terrain: false, contour: false, clouds: false, heatmap: false, labels: false, atmosphere: false
    })
  }
})

const emit = defineEmits(['ready', 'label-hover', 'globe-select', 'zoom-change'])

const containerRef = ref(null)
const globeMountRef = ref(null)
const loading = ref(true)
const errorMessage = ref('')

let globe = null
let cloudMesh = null
let contourMesh = null
let cloudAnimId = null
let resizeObserver = null
let disposed = false
/* 扩展图层注册表 + 主动画循环 */
let registry = null
let tickId = null
let lastTick = 0
let registryPaused = false

/* ---------- 工具函数 ---------- */

const CONTINENT_COLORS = {
  asia: '#e6994d',
  europe: '#4d99e6',
  africa: '#e6b32d',
  'north-america': '#66b366',
  'south-america': '#4dc98f',
  oceania: '#c94de6',
  antarctica: '#b3c6d9'
}

function countryColor(c) {
  return CONTINENT_COLORS[c.continent] || '#8fb4d9'
}

// '#e6994d' → [0.9, 0.6, 0.3]，供 TooltipOverlay 使用
function hexToRgb01(hex) {
  const n = parseInt(hex.slice(1), 16)
  return [((n >> 16) & 255) / 255, ((n >> 8) & 255) / 255, (n & 255) / 255]
}

// 球面大圆距离（度），用于点击拾取
function angularDistance(lat1, lng1, lat2, lng2) {
  const rad = Math.PI / 180
  const dLat = (lat2 - lat1) * rad
  const dLng = (lng2 - lng1) * rad
  const a = Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1 * rad) * Math.cos(lat2 * rad) * Math.sin(dLng / 2) ** 2
  return Math.acos(Math.min(1, 2 * Math.asin(Math.sqrt(a)))) / rad
}

/* ---------- 等高线着色器图层 ---------- */

function buildContourLayer(radius) {
  const topoTexture = new THREE.TextureLoader().load(topoImg)
  const material = new THREE.ShaderMaterial({
    uniforms: {
      uMap: { value: topoTexture },
      uIso: { value: 16.0 },        // 每单位高程的等高线数量
      uOpacity: { value: 0.6 },
      uColor: { value: new THREE.Color('#ffffff') }
    },
    vertexShader: `
      varying vec2 vUv;
      void main() {
        vUv = uv;
        gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
      }
    `,
    fragmentShader: `
      uniform sampler2D uMap;
      uniform float uIso;
      uniform float uOpacity;
      uniform vec3 uColor;
      varying vec2 vUv;
      void main() {
        float elev = texture2D(uMap, vUv).r;
        // 深海区域不绘制等高线
        if (elev < 0.045) discard;
        float f = fract(elev * uIso);
        float d = min(f, 1.0 - f);
        float line = 1.0 - smoothstep(0.0, 0.05, d);
        gl_FragColor = vec4(uColor, line * uOpacity);
      }
    `,
    transparent: true,
    depthWrite: false
  })
  return new THREE.Mesh(
    new THREE.SphereGeometry(radius * 1.0025, 96, 96),
    material
  )
}

function buildCloudLayer(radius) {
  new THREE.TextureLoader().load(cloudsImg, texture => {
    if (disposed) return
    texture.colorSpace = THREE.SRGBColorSpace
    cloudMesh = new THREE.Mesh(
      new THREE.SphereGeometry(radius * 1.006, 75, 75),
      new THREE.MeshPhongMaterial({ map: texture, transparent: true, opacity: 0.82 })
    )
    cloudMesh.visible = !!props.layers.clouds
    globe.scene().add(cloudMesh)
    // 云层自转动画（独立于地球交互）
    const SPEED = -0.008 // deg / frame
    ;(function rotateClouds() {
      if (disposed || !cloudMesh) return
      cloudMesh.rotation.y += SPEED * Math.PI / 180
      cloudAnimId = requestAnimationFrame(rotateClouds)
    })()
  })
}

/* ---------- 初始化 ---------- */

async function init() {
  try {
    globe = new Globe(globeMountRef.value, { animateIn: true })
      .globeImageUrl(earthImg)
      .atmosphereColor('#5aa6ff')
      .atmosphereAltitude(0.18)
      .backgroundColor('rgba(0,0,0,0)')
      .onGlobeReady(onGlobeReady)
      .onZoom(pov => emit('zoom-change', pov))
      .onGlobeClick(({ lat, lng }) => {
        // 点击拾取：优先匹配最近国家（<10°），其次大洲中心（<26°）
        let best = null, bestDist = Infinity
        for (const c of countries) {
          const dist = angularDistance(lat, lng, c.lat, c.lng)
          if (dist < bestDist) { bestDist = dist; best = { type: 'country', data: c } }
        }
        if (bestDist <= 10) { emit('globe-select', best); return }
        let bestCont = null, contDist = Infinity
        for (const cont of continents) {
          const dist = angularDistance(lat, lng, cont.centerLat, cont.centerLng)
          if (dist < contDist) { contDist = dist; bestCont = cont }
        }
        if (contDist <= 26) {
          emit('globe-select', { type: 'continent', data: bestCont })
        }
      })

    applyLabels()
    applyHeatmap()
    applyArcs()
    applyTerrain()
    applyAtmosphere()
    applyRouteData()
    applyBackground()
    applyAutoRotate()

    globe.width(containerRef.value.clientWidth)
    globe.height(containerRef.value.clientHeight)
  } catch (e) {
    loading.value = false
    errorMessage.value = e?.message || 'WebGL 初始化失败，请检查浏览器兼容性'
    console.error('Globe init failed:', e)
  }
}

function onGlobeReady() {
  if (disposed || !globe) return
  const radius = globe.getGlobeRadius()
  contourMesh = buildContourLayer(radius)
  contourMesh.visible = !!props.layers.contour
  globe.scene().add(contourMesh)
  buildCloudLayer(radius)
  // 扩展图层注册表 + 主动画循环
  registry = createLayerRegistry({ globe, radius, container: containerRef.value })
  applySurfaceTexture()
  syncRegistry()
  startTick()
  loading.value = false
  emit('ready')
}

/** 同步扩展图层（自然地理/人文/环境/氛围）显隐状态 */
function syncRegistry() {
  if (!registry) return
  for (const k of REGISTRY_KEYS) registry.setVisible(k, !!props.layers[k])
}

/** 统一驱动扩展图层动画（洋流/风场/卫星/晨昏线等），与 globe.gl 渲染循环并行 */
function startTick() {
  lastTick = performance.now()
  const loop = now => {
    if (disposed) return
    const dt = Math.min(0.1, (now - lastTick) / 1000)
    lastTick = now
    if (!registryPaused) registry?.tick(dt, now / 1000)
    tickId = requestAnimationFrame(loop)
  }
  tickId = requestAnimationFrame(loop)
}

/* ---------- 图层应用逻辑 ---------- */

function applyLabels() {
  if (!globe) return
  // 城市标注与国家标注合并渲染（城市开启时附加）
  const cityData = props.layers.cities
    ? CITIES.map(([name, lat, lng, pop]) => ({ name, lat, lng, pop, isCity: true }))
    : []
  globe
    .htmlElementsData(props.layers.labels ? [...countries, ...cityData] : [])
    .htmlLat('lat')
    .htmlLng('lng')
    .htmlAltitude(0.012)
    .htmlElement(createLabelElement)
    .htmlTransitionDuration(0)
}

/**
 * 国家/城市标注 DOM 元素。
 * three-globe 的 labelsData 基于 THREE.TextGeometry（typeface 字体不含中文字形，
 * 汉字会渲染为 "???"），故改用真实 DOM 元素渲染文本。
 */
function createLabelElement(d) {
  const isCity = !!d.isCity
  const color = isCity ? '#ffd479' : countryColor(d)
  const el = document.createElement('div')
  el.style.cssText =
    'display:flex;flex-direction:column;align-items:center;gap:3px;' +
    'transform:translateY(8px);pointer-events:none;user-select:none;' +
    'font-family:-apple-system,BlinkMacSystemFont,"PingFang SC","Helvetica Neue",Arial,sans-serif;'
  const dot = document.createElement('span')
  dot.style.cssText = `width:${isCity ? 5 : 7}px;height:${isCity ? 5 : 7}px;border-radius:50%;background:${color};box-shadow:0 0 6px ${color};`
  const text = document.createElement('span')
  text.textContent = d.name
  text.style.cssText =
    `font-size:${isCity ? 10 : 12}px;font-weight:600;color:${color};white-space:nowrap;` +
    'text-shadow:0 1px 4px rgba(0,0,0,0.85);'
  el.append(dot, text)

  if (!isCity) {
    el.style.cursor = 'pointer'
    el.style.pointerEvents = 'auto'
    el.addEventListener('click', () => emit('globe-select', { type: 'country', data: d }))
    el.addEventListener('mouseenter', () => {
      emit('label-hover', {
        name: d.name, nameEn: d.nameEn, type: 'country',
        color: hexToRgb01(color)
      })
    })
    el.addEventListener('mouseleave', () => emit('label-hover', null))
  }
  return el
}

function applyHeatmap() {
  if (!globe) return
  // 人口热力 + GDP 经济热力（开启时叠加为独立热力图层）
  // 注意：globe.gl 的 heatmapsData 每一项本身就是「点数组」（默认 pointsAccessor 为恒等函数），
  // 不能再包一层 { points: ... } 对象，否则 digest 时 pointsAccessor(d).map 会报错
  const sets = []
  if (props.layers.heatmap) {
    sets.push(populationHeatmap.map(p => ({ lat: p.lat, lng: p.lng, weight: p.pop })))
  }
  if (props.layers.gdp) {
    sets.push(GDP_POINTS.map(([, lat, lng, gdp]) => ({ lat, lng, weight: gdp })))
  }
  globe
    .heatmapsData(sets)
    .heatmapPointLat('lat')
    .heatmapPointLng('lng')
    .heatmapPointWeight('weight')
    .heatmapBandwidth(1.1)
    .heatmapColorSaturation(2.6)
    .heatmapBaseAltitude(0.01)
    .heatmapTopAltitude(0.07)
    .heatmapsTransitionDuration(0)
}

/** 航线 / 海底光缆（globe.gl arcsData，动态虚线流动） */
function applyArcs() {
  if (!globe) return
  const arcs = []
  if (props.layers.flights) {
    for (const pair of FLIGHT_ROUTES) {
      const [a, b] = pair.split('-')
      const A = FLIGHT_HUBS[a]
      const B = FLIGHT_HUBS[b]
      if (!A || !B) continue
      arcs.push({ kind: 'flight', startLat: A[0], startLng: A[1], endLat: B[0], endLng: B[1] })
    }
  }
  if (props.layers.cables) {
    for (const c of CABLE_ROUTES) {
      const s = c.path[0]
      const e = c.path[c.path.length - 1]
      arcs.push({ kind: 'cable', startLat: s[0], startLng: s[1], endLat: e[0], endLng: e[1] })
    }
  }
  globe
    .arcsData(arcs)
    .arcStartLat('startLat')
    .arcStartLng('startLng')
    .arcEndLat('endLat')
    .arcEndLng('endLng')
    .arcColor(d => (d.kind === 'flight' ? ['#ffb347', '#ff5e3a'] : ['#39d0ff', '#3a7bd5']))
    .arcStroke(d => (d.kind === 'flight' ? 0.42 : 0.3))
    .arcDashLength(d => (d.kind === 'flight' ? 0.4 : 0.9))
    .arcDashGap(d => (d.kind === 'flight' ? 0.28 : 0.6))
    .arcDashAnimateTime(d => (d.kind === 'flight' ? 3800 : 0))
    .arcAltitudeAutoScale(0.35)
    .arcsTransitionDuration(0)
}

function applyTerrain() {
  if (!globe) return
  globe.bumpImageUrl(props.layers.terrain ? topoImg : null)
}

/* ---------- 地表贴图自适应 ---------- */

/** 备选地表底图：marble 彩色影像 / dark 暗色中性底 / night 夜景灯光 */
const SURFACE_TEXTURES = { marble: earthImg, dark: darkImg, night: nightImg }

/**
 * 图层 → 底图方案（按数组顺序首个命中生效）：
 * 热力/等高线/网格等分析类叠加在暗色底图上色彩更突出、细线更清晰，
 * 夜光图层配夜景灯光底图，其余场景保持蓝色大理石影像，减少视觉干扰。
 */
const TEXTURE_RULES = [
  ['heatmap', 'dark'], ['gdp', 'dark'], ['airquality', 'dark'], ['co2', 'dark'],
  ['contour', 'dark'], ['hillshade', 'dark'], ['bathymetry', 'dark'],
  ['graticule', 'dark'], ['timezones', 'dark'], ['borders', 'dark'],
  ['nightlights', 'night']
]

let surfaceTextureId = 'marble'

function applySurfaceTexture() {
  if (!globe) return
  const hit = TEXTURE_RULES.find(([key]) => props.layers[key])
  const id = hit ? hit[1] : 'marble'
  if (id === surfaceTextureId) return
  surfaceTextureId = id
  globe.globeImageUrl(SURFACE_TEXTURES[id])
}

function applyAtmosphere() {
  if (!globe) return
  globe.showAtmosphere(!!props.layers.atmosphere)
}

function applyBackground() {
  if (!globe) return
  globe.backgroundImageUrl(props.isDark ? skyImg : null)
}

function applyAutoRotate() {
  if (!globe) return
  const controls = globe.controls()
  controls.autoRotate = !!props.autoRotate
  controls.autoRotateSpeed = 0.45
}

function applyRouteData() {
  if (!globe) return
  globe
    .pathsData([])
    .pathPoints('points')
    .pathPointLat(p => p[0])
    .pathPointLng(p => p[1])
    .pathPointAlt(0.012)
    .pathColor('#ff7a3d')
    .pathStroke(0.75)
    .pathTransitionDuration(0)
}

/* ---------- 对外暴露的方法 ---------- */

function pointOfView(pov, ms = 300) {
  if (!globe) return null
  if (pov) globe.pointOfView(pov, ms)
  return globe.pointOfView()
}

function zoomIn() {
  const pov = globe?.pointOfView()
  if (pov) globe.pointOfView({ altitude: Math.max(0.16, pov.altitude * 0.6) }, 250)
}

function zoomOut() {
  const pov = globe?.pointOfView()
  if (pov) globe.pointOfView({ altitude: Math.min(3.4, pov.altitude / 0.6) }, 250)
}

function resetView() {
  globe?.pointOfView({ lat: 20, lng: 0, altitude: 2.6 }, 500)
}

function locateAt(lat, lng, alt = 1.4) {
  if (!globe) return
  globe.pointOfView({ lat, lng, altitude: alt }, 500)
  // 定位涟漪
  globe.ringsData([{ lat, lng }])
    .ringColor(() => t => `rgba(255,122,61,${1 - t})`)
    .ringMaxRadius(4.5)
    .ringPropagationSpeed(2.4)
    .ringRepeatPeriod(900)
}

function clearLocateRing() {
  globe?.ringsData([])
}

/** 在 3D 地球上渲染路线（points: [[lat,lng], ...]） */
function setRoutePath(points) {
  if (!globe) return
  globe.pathsData(points && points.length > 1 ? [{ points }] : [])
}

function clearRoutePath() {
  globe?.pathsData([])
}

/* ---------- 事件 / 生命周期 ---------- */

function onMouseMove(e) {
  // 供 TooltipOverlay 定位
  window.__globeMouse = { x: e.clientX, y: e.clientY }
}

watch(() => props.layers, () => {
  applyLabels()
  applyHeatmap()
  applyArcs()
  applyTerrain()
  applyAtmosphere()
  applySurfaceTexture()
  syncRegistry()
  if (contourMesh) contourMesh.visible = !!props.layers.contour
  if (cloudMesh) cloudMesh.visible = !!props.layers.clouds
}, { deep: true })

watch(() => props.isDark, applyBackground)
watch(() => props.autoRotate, applyAutoRotate)

onMounted(() => {
  init()
  resizeObserver = new ResizeObserver(() => {
    if (!globe || !containerRef.value) return
    const w = containerRef.value.clientWidth
    const h = containerRef.value.clientHeight
    if (w > 0 && h > 0) { globe.width(w); globe.height(h) }
  })
  if (containerRef.value) resizeObserver.observe(containerRef.value)
})

onBeforeUnmount(() => {
  disposed = true
  if (tickId) cancelAnimationFrame(tickId)
  registry?.dispose()
  registry = null
  if (cloudAnimId) cancelAnimationFrame(cloudAnimId)
  if (resizeObserver && containerRef.value) resizeObserver.disconnect()
  if (globe) {
    clearRoutePath()
    try { globe._destructor() } catch (e) { /* 忽略析构异常 */ }
    globe = null
  }
})

defineExpose({
  pointOfView,
  zoomIn,
  zoomOut,
  resetView,
  locateAt,
  clearLocateRing,
  setRoutePath,
  clearRoutePath,
  setAutoRotate: v => { if (globe) globe.controls().autoRotate = !!v },
  pauseAnimation: () => { registryPaused = true; globe?.pauseAnimation() },
  resumeAnimation: () => { registryPaused = false; globe?.resumeAnimation() },
  isReady: () => !!globe
})
</script>

<style scoped>
.globe-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: linear-gradient(135deg, #dce9f7 0%, #b8d0ee 50%, #9fc1e8 100%);
  cursor: grab;
}

.globe-container:active {
  cursor: grabbing;
}

.globe-container.is-dark {
  background: linear-gradient(135deg, #050510 0%, #0f0f2a 50%, #050520 100%);
}

.globe-mount {
  position: absolute;
  inset: 0;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(8, 10, 24, 0.92);
  z-index: 100;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 3px solid rgba(100, 150, 255, 0.2);
  border-top-color: #4a9eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 20px;
  color: #dfe9f7;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
}

.loading-sub {
  margin-top: 8px;
  color: #8fa5c5;
  font-size: 12px;
  letter-spacing: 1px;
}

.error-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
  background: rgba(8, 10, 24, 0.95);
  z-index: 100;
}

.error-icon {
  font-size: 56px;
  margin-bottom: 16px;
}

.error-title {
  color: #ff6b6b;
  font-size: 22px;
  margin: 0 0 12px 0;
}

.error-message {
  color: #c0c8d8;
  font-size: 14px;
  margin: 0 0 10px 0;
  max-width: 420px;
  line-height: 1.6;
}

.error-hint {
  color: #7d8aa5;
  font-size: 13px;
  margin: 0;
}
</style>
