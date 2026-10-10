<template>
  <Teleport to="body">
    <div v-show="visible" class="nav-overlay" :class="{ 'is-light': mapTheme === 'light' }">
      <header class="nav-header">
        <div class="nav-title">
          <span class="nav-logo">🧭</span>
          <div>
            <h2>高德地图 · 智能出行</h2>
            <p>路线导航 / 服务工具箱 / 地图主题</p>
          </div>
        </div>
        <div class="header-actions">
          <button
            class="theme-btn"
            :title="mapTheme === 'dark' ? '切换到明亮模式' : '切换到暗黑模式'"
            @click="toggleMapTheme"
          >{{ mapTheme === 'dark' ? '☀️' : '🌙' }}</button>
          <button class="close-btn" @click="$emit('close')" aria-label="关闭导航">✕</button>
        </div>
      </header>

      <div class="nav-body">
        <aside class="nav-sidebar">
          <div class="nav-tabs">
            <button
              class="nav-tab"
              :class="{ 'is-active': tab === 'nav' }"
              @click="tab = 'nav'"
            >🧭 路线导航</button>
            <button
              class="nav-tab"
              :class="{ 'is-active': tab === 'services' }"
              @click="tab = 'services'"
            >🧰 服务工具箱</button>
          </div>

          <template v-if="tab === 'nav'">
          <div class="input-row">
            <span class="dot start"></span>
            <input
              id="nav-input-start"
              v-model="startText"
              class="nav-input"
              type="text"
              placeholder="输入起点（支持联想搜索）"
              autocomplete="off"
            />
            <button class="mini-btn" title="使用当前位置" @click="useMyLocation">◎</button>
            <button class="mini-btn" title="交换起终点" @click="swapPoints">⇅</button>
          </div>
          <div class="input-row">
            <span class="dot end"></span>
            <input
              id="nav-input-end"
              v-model="endText"
              class="nav-input"
              type="text"
              placeholder="输入终点（支持联想搜索）"
              autocomplete="off"
            />
          </div>

          <div class="mode-tabs">
            <button
              v-for="m in MODES"
              :key="m.key"
              class="mode-tab"
              :class="{ 'is-active': mode === m.key }"
              @click="switchMode(m.key)"
            >{{ m.label }}</button>
          </div>

          <div v-if="policyOptions.length" class="policy-picker" :class="{ 'is-open': policyOpen }">
            <button
              type="button"
              class="policy-select"
              aria-label="路线策略"
              :aria-expanded="policyOpen"
              @click="policyOpen = !policyOpen"
            >
              <span class="policy-label">{{ policyLabel }}</span>
              <span class="policy-arrow">▾</span>
            </button>
            <div v-if="policyOpen" class="policy-pop">
              <button
                v-for="p in policyOptions"
                :key="p.v"
                type="button"
                class="policy-opt"
                :class="{ 'is-active': p.v === policy }"
                @click="pickPolicy(p.v)"
              >{{ p.n }}</button>
            </div>
          </div>

          <button class="plan-btn" :disabled="loading || !amapReady" @click="planRoute">
            {{ loading ? '路线规划中...' : '开始规划' }}
          </button>

          <p v-if="amapError" class="nav-error">{{ amapError }}</p>
          <p v-else-if="routeError" class="nav-error">{{ routeError }}</p>

          <div v-if="summary" class="route-summary">
            <div class="summary-item">
              <span class="summary-label">总里程</span>
              <span class="summary-value">{{ summary.km }}</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">预计耗时</span>
              <span class="summary-value">{{ summary.duration }}</span>
            </div>
          </div>

          <label v-if="summary" class="globe-sync">
            <input v-model="showOnGlobe" type="checkbox" @change="syncGlobeRoute" />
            在 3D 地球上同步显示路线
          </label>

          <div v-if="steps.length" class="steps-header">导航引导（{{ steps.length }} 步）</div>
          <div v-if="steps.length" class="steps-list">
            <div
              v-for="(s, i) in steps"
              :key="i"
              class="step-item"
              @click="locateStep(s)"
            >
              <span class="step-index">{{ i + 1 }}</span>
              <span class="step-text">
                {{ s.instruction }}
                <em v-if="s.road">（{{ s.road }}）</em>
              </span>
              <span v-if="s.distanceText" class="step-distance">{{ s.distanceText }}</span>
            </div>
          </div>
          </template>

          <!-- 服务工具箱（结果同步渲染到右侧地图） -->
          <ServicePanel v-else :amap="AMapRef" :map="mapRef" />
        </aside>

        <div class="nav-map-wrap">
          <div ref="mapElRef" class="nav-map"></div>
          <div v-if="!amapReady && !amapError" class="map-loading">
            <div class="loading-spinner"></div>
            <p>正在加载高德地图服务...</p>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, shallowRef, watch } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { AMAP_KEY, AMAP_VERSION, AMAP_PLUGINS } from '../config.js'
import ServicePanel from './ServicePanel.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  /** 打开时默认选中的标签：nav=路线导航 / services=服务工具箱 */
  startTab: { type: String, default: 'nav' }
})

const emit = defineEmits(['close', 'route-change'])

const MODES = [
  { key: 'driving', label: '🚗 驾车' },
  { key: 'transit', label: '🚌 公交' },
  { key: 'walking', label: '🚶 步行' }
]

const DRIVING_POLICIES = [
  { v: 'LEAST_TIME', n: '速度最快' },
  { v: 'LEAST_FEE', n: '费用最低' },
  { v: 'LEAST_DISTANCE', n: '距离最短' }
]
const TRANSIT_POLICIES = [
  { v: 'LEAST_TIME', n: '最快' },
  { v: 'LEAST_FEE', n: '最经济' },
  { v: 'LEAST_TRANSFER', n: '最少换乘' },
  { v: 'LEAST_WALK', n: '最少步行' }
]

const mapElRef = ref(null)
const visible = ref(props.visible)
const amapReady = ref(false)
const amapError = ref('')
const routeError = ref('')
const loading = ref(false)
const startText = ref('杭州东站')
const endText = ref('西湖风景名胜区')
const mode = ref('driving')
const policy = ref('LEAST_TIME')
const summary = ref(null)
const steps = ref([])
const showOnGlobe = ref(true)

/** 侧边栏标签：nav=路线导航 / services=服务工具箱 */
const tab = ref('nav')

/** 地图主题（dark / light），持久化到 localStorage */
const mapTheme = ref(localStorage.getItem('amap-nav-theme') || 'dark')

/** 暴露给工具箱的 AMap 构造器与地图实例 */
const AMapRef = shallowRef(null)
const mapRef = shallowRef(null)

let AMap = null
let map = null
let autocompleteStart = null
let autocompleteEnd = null
let geocoder = null
let lastService = null
let stepMarker = null
let startPoi = null
let endPoi = null
let lastRoutePoints = null

watch(() => props.visible, v => {
  visible.value = v
  if (v) {
    tab.value = props.startTab === 'services' ? 'services' : 'nav'
    ensureAMap()
  }
})

function toggleMapTheme() {
  mapTheme.value = mapTheme.value === 'dark' ? 'light' : 'dark'
  localStorage.setItem('amap-nav-theme', mapTheme.value)
  if (map) {
    map.setMapStyle(mapTheme.value === 'dark' ? 'amap://styles/dark' : 'amap://styles/normal')
  }
}

watch([mode], () => {
  policy.value = (mode.value === 'transit' ? TRANSIT_POLICIES : DRIVING_POLICIES)[0].v
})

const policyOptions = ref(mode.value === 'transit' ? TRANSIT_POLICIES : DRIVING_POLICIES)
watch(mode, m => {
  policyOptions.value = m === 'transit' ? TRANSIT_POLICIES : DRIVING_POLICIES
})

/* ---------- 策略选择器（自定义下拉，替代原生 select 弹出层） ---------- */
const policyOpen = ref(false)
const policyLabel = computed(() => policyOptions.value.find(p => p.v === policy.value)?.n || '')

function pickPolicy(v) {
  policy.value = v
  policyOpen.value = false
}

function onDocMouseDown(e) {
  if (!e.target.closest?.('.policy-picker')) policyOpen.value = false
}

function onDocKeydown(e) {
  if (e.key === 'Escape') policyOpen.value = false
}

onMounted(() => {
  document.addEventListener('mousedown', onDocMouseDown)
  document.addEventListener('keydown', onDocKeydown)
})

/* ---------- 高德初始化（懒加载，仅首次打开时执行） ---------- */

async function ensureAMap() {
  if (amapReady.value || amapError.value) {
    await nextTick()
    map?.resize?.()
    return
  }
  try {
    AMap = await AMapLoader.load({ key: AMAP_KEY, version: AMAP_VERSION, plugins: AMAP_PLUGINS })
    AMapRef.value = AMap
    await nextTick()
    map = new AMap.Map(mapElRef.value, {
      zoom: 12,
      center: [120.15, 30.27],
      mapStyle: mapTheme.value === 'dark' ? 'amap://styles/dark' : 'amap://styles/normal',
      viewMode: '3D',
      pitch: 35
    })
    mapRef.value = map
    map.addControl(new AMap.ToolBar({ position: 'RB' }))
    map.addControl(new AMap.Scale())
    geocoder = new AMap.Geocoder()

    // 起终点 POI 联想搜索
    autocompleteStart = new AMap.Autocomplete({ input: 'nav-input-start' })
    autocompleteStart.on('select', e => {
      if (e.poi && e.poi.location) {
        startPoi = {
          lnglat: e.poi.location,
          name: e.poi.name,
          district: e.poi.district || '',
          adcode: e.poi.adcode || ''
        }
      }
    })
    autocompleteEnd = new AMap.Autocomplete({ input: 'nav-input-end' })
    autocompleteEnd.on('select', e => {
      if (e.poi && e.poi.location) {
        endPoi = {
          lnglat: e.poi.location,
          name: e.poi.name,
          district: e.poi.district || '',
          adcode: e.poi.adcode || ''
        }
      }
    })

    amapReady.value = true
  } catch (e) {
    amapError.value = '高德服务加载失败：请检查网络连接，或确认 Key 已配置且域名白名单允许当前地址'
    console.error('AMap load failed:', e)
  }
}

function switchMode(m) {
  if (mode.value === m) return
  mode.value = m
  clearRoute()
}

function swapPoints() {
  ;[startText.value, endText.value] = [endText.value, startText.value]
  ;[startPoi, endPoi] = [endPoi, startPoi]
}

/* ---------- 路线规划 ---------- */

async function planRoute() {
  if (loading.value || !amapReady.value) return
  routeError.value = ''
  loading.value = true
  try {
    const origin = await resolvePoint(startText.value, startPoi, '起点')
    const dest = await resolvePoint(endText.value, endPoi, '终点')
    if (mode.value === 'transit') {
      const cityCode = origin.adcode ? origin.adcode.slice(0, 4) + '00' : undefined
      await doTransit(origin, dest, cityCode)
    } else if (mode.value === 'walking') {
      await doWalking(origin, dest)
    } else {
      await doDriving(origin, dest)
    }
  } catch (e) {
    routeError.value = e?.message || '路线规划失败，请重试'
  } finally {
    loading.value = false
  }
}

function resolvePoint(text, poi, label) {
  if (poi && poi.lnglat) return Promise.resolve(poi)
  if (!text) return Promise.reject(new Error(`请先选择${label}`))
  return new Promise((resolve, reject) => {
    geocoder.getLocation(text, (status, result) => {
      if (status === 'complete' && result.geocodes?.length) {
        const g = result.geocodes[0]
        resolve({
          lnglat: g.location,
          name: g.formattedAddress || text,
          district: g.district || '',
          adcode: g.adcode || ''
        })
      } else {
        reject(new Error(`未找到${label}「${text}」，请从联想列表中选择`))
      }
    })
  })
}

function freshService(ctor) {
  if (lastService && lastService.clear) lastService.clear()
  return ctor
}

function doDriving(origin, dest) {
  const Cls = freshService(AMap.Driving)
  const policyMap = AMap.DrivingPolicy || {}
  lastService = new Cls({
    map,
    autoFitView: true,
    policy: policyMap[policy.value] ?? policyMap.LEAST_TIME,
    ferry: true
  })
  lastService.search(origin.lnglat, dest.lnglat, (status, result) => {
    if (status !== 'complete') return handleSearchFail(status)
    const route = result.routes?.[0]
    if (!route) return handleSearchFail('no_data')
    const pts = []
    for (const s of route.steps || []) {
      for (const p of s.path || []) pts.push([p.lat, p.lng])
    }
    const stepList = (route.steps || []).map(s => ({
      instruction: s.instruction || '继续前行',
      road: s.road || '',
      distance: s.distance,
      location: s.path?.length ? [s.path[0].lng, s.path[0].lat] : null
    }))
    finishRoute(origin, dest, pts, stepList, route.distance, route.time, '驾车')
  })
}

function doWalking(origin, dest) {
  const Cls = freshService(AMap.Walking)
  lastService = new Cls({ map, autoFitView: true })
  lastService.search(origin.lnglat, dest.lnglat, (status, result) => {
    if (status !== 'complete') return handleSearchFail(status)
    const route = result.routes?.[0]
    if (!route) return handleSearchFail('no_data')
    const pts = []
    for (const s of route.steps || []) {
      for (const p of s.path || []) pts.push([p.lat, p.lng])
    }
    const stepList = (route.steps || []).map(s => ({
      instruction: s.instruction || '继续步行',
      road: s.road || '',
      distance: s.distance,
      location: s.path?.length ? [s.path[0].lng, s.path[0].lat] : null
    }))
    finishRoute(origin, dest, pts, stepList, route.distance, route.time, '步行')
  })
}

function doTransit(origin, dest, cityCode) {
  const Cls = freshService(AMap.Transfer)
  const policyMap = AMap.TransferPolicy || {}
  lastService = new Cls({
    map,
    autoFitView: true,
    city: cityCode || origin.district || '杭州',
    policy: policyMap[policy.value] ?? policyMap.LEAST_TIME,
    nightflag: false
  })
  lastService.search(origin.lnglat, dest.lnglat, (status, result) => {
    if (status !== 'complete') return handleSearchFail(status)
    const plan = result.plans?.[0]
    if (!plan) return handleSearchFail('no_data')
    const stepList = []
    let distance = 0
    for (const seg of plan.segments || []) {
      if (seg.walking && seg.walking.distance > 0) {
        distance += seg.walking.distance
        const p0 = seg.walking.steps?.[0]?.path?.[0]
        stepList.push({
          instruction: `步行 ${fmtDist(seg.walking.distance)}`,
          road: '',
          distance: seg.walking.distance,
          location: p0 ? [p0.lng, p0.lat] : null
        })
      }
      const line = seg.bus?.buslines?.[0]
      if (line) {
        distance += line.distance || 0
        stepList.push({
          instruction: `乘坐 ${line.name}（${line.departure_stop?.name || ''} 上车 → ${line.arrival_stop?.name || ''} 下车）`,
          road: '',
          distance: line.distance || 0,
          location: line.departure_stop?.location
            ? [line.departure_stop.location.lng, line.departure_stop.location.lat]
            : null
        })
      }
      const rail = seg.railway
      if (rail) {
        distance += rail.distance || 0
        stepList.push({
          instruction: `乘坐 ${rail.name || '城际列车'}（${rail.departure_stop?.name || ''} → ${rail.arrival_stop?.name || ''}）`,
          road: '',
          distance: rail.distance || 0,
          location: rail.departure_stop?.location
            ? [rail.departure_stop.location.lng, rail.departure_stop.location.lat]
            : null
        })
      }
    }
    finishRoute(origin, dest, [[origin.lnglat.lat, origin.lnglat.lng], [dest.lnglat.lat, dest.lnglat.lng]],
      stepList, distance || plan.distance, plan.time, '公交')
  })
}

function handleSearchFail(status) {
  routeError.value = status === 'no_data'
    ? '未找到可达路线，请尝试调整起终点或出行方式'
    : '路线规划服务异常，请稍后重试'
}

function finishRoute(origin, dest, rawPoints, stepList, distanceM, timeS, modeLabel) {
  // 采样抽稀（保留 ≤600 点）以保证 3D 地球渲染流畅
  const step = Math.max(1, Math.ceil(rawPoints.length / 600))
  const points = rawPoints.filter((_, i) => i % step === 0 || i === rawPoints.length - 1)
  steps.value = stepList.map(s => ({
    ...s,
    distanceText: s.distance ? fmtDist(s.distance) : ''
  }))
  summary.value = {
    km: fmtDist(distanceM),
    duration: fmtDuration(timeS),
    text: `${modeLabel} · ${fmtDist(distanceM)} · ${fmtDuration(timeS)}`
  }
  lastRoutePoints = points
  syncGlobeRoute()
}

function syncGlobeRoute() {
  if (summary.value && showOnGlobe.value && lastRoutePoints?.length > 1) {
    emit('route-change', { points: lastRoutePoints, summary: summary.value })
  } else {
    emit('route-change', null)
  }
}

function clearRoute() {
  if (lastService && lastService.clear) lastService.clear()
  steps.value = []
  summary.value = null
  lastRoutePoints = null
  routeError.value = ''
  emit('route-change', null)
}

/* ---------- 导航引导 ---------- */

function locateStep(step) {
  if (!step.location || !map) return
  map.panTo(step.location)
  map.setZoom(15)
  if (stepMarker) map.remove(stepMarker)
  stepMarker = new AMap.Marker({
    position: step.location,
    title: '当前引导位置'
  })
  map.add(stepMarker)
}

function useMyLocation() {
  if (!amapReady.value) return
  const geo = new AMap.Geolocation({ enableHighAccuracy: true, timeout: 8000 })
  geo.getCurrentPosition((status, result) => {
    if (status !== 'complete') {
      routeError.value = '定位失败，请检查浏览器定位权限'
      return
    }
    const pos = result.position
    geocoder.getAddress(pos, (s, res) => {
      const addr = s === 'complete' ? res.regeocode?.formattedAddress || '' : ''
      const comp = s === 'complete' ? res.regeocode?.addressComponent || {} : {}
      startText.value = addr || '我的位置'
      startPoi = {
        lnglat: pos,
        name: startText.value,
        district: [comp.province, comp.city].filter(Boolean).join('') || '',
        adcode: comp.adcode || ''
      }
    })
  })
}

/* ---------- 格式化 ---------- */

function fmtDist(m) {
  return m >= 1000 ? `${(m / 1000).toFixed(1)} 公里` : `${Math.round(m)} 米`
}

function fmtDuration(s) {
  const min = Math.round(s / 60)
  if (min < 60) return `${min} 分钟`
  return `${Math.floor(min / 60)} 小时 ${min % 60} 分`
}

onBeforeUnmount(() => {
  try {
    autocompleteStart?.off?.('select')
    autocompleteEnd?.off?.('select')
    map?.destroy?.()
  } catch (e) { /* 忽略 */ }
  mapRef.value = null
})
</script>

<style scoped>
.nav-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  background: #0d1117;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.nav-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-logo {
  font-size: 26px;
}

.nav-title h2 {
  margin: 0;
  font-size: 17px;
  color: #f5f5f7;
}

.nav-title p {
  margin: 2px 0 0;
  font-size: 12px;
  color: #86868b;
}

.close-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.08);
  color: #f5f5f7;
  font-size: 15px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.16);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.theme-btn {
  width: 36px;
  height: 36px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  font-size: 15px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.theme-btn:hover {
  background: rgba(255, 255, 255, 0.14);
}

/* ---------- 标签切换 ---------- */
.nav-tabs {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.nav-tab {
  flex: 1;
  padding: 9px 0;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background: transparent;
  color: #d1d1d6;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.18s ease;
}

.nav-tab.is-active {
  background: rgba(10, 132, 255, 0.22);
  border-color: #0a84ff;
  color: #6db8ff;
}

.nav-tab:not(.is-active):hover {
  background: rgba(255, 255, 255, 0.06);
}

.nav-body {
  flex: 1;
  display: flex;
  min-height: 0;
}

.nav-sidebar {
  width: 360px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
  overflow-y: auto;
  scrollbar-width: none; /* Firefox 隐藏滚动条 */
  background: rgba(255, 255, 255, 0.03);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

/* Chrome / Edge / Safari 隐藏滚动条（保留滚动能力） */
.nav-sidebar::-webkit-scrollbar {
  display: none;
}

.input-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot.start { background: #30d158; }
.dot.end { background: #ff453a; }

.nav-input {
  flex: 1;
  min-width: 0;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: #f5f5f7;
  font-size: 13px;
  outline: none;
  transition: border-color 0.18s ease;
}

.nav-input:focus {
  border-color: #0a84ff;
}

.nav-input::placeholder {
  color: #6e6e73;
}

.mini-btn {
  width: 32px;
  height: 34px;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.06);
  color: #d1d1d6;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.mini-btn:hover {
  background: rgba(255, 255, 255, 0.14);
}

.mode-tabs {
  display: flex;
  gap: 6px;
}

.mode-tab {
  flex: 1;
  padding: 9px 0;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background: transparent;
  color: #d1d1d6;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.mode-tab.is-active {
  background: rgba(10, 132, 255, 0.22);
  border-color: #0a84ff;
  color: #6db8ff;
}

.policy-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 9px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
  color: #f5f5f7;
  font-size: 13px;
  font-family: inherit;
  text-align: left;
  outline: none;
  cursor: pointer;
}

.policy-select:focus {
  border-color: #0a84ff;
}

/* ---------- 自定义下拉（隐藏滚动条，随明暗主题切换） ---------- */
.policy-picker {
  position: relative;
}

.policy-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.policy-arrow {
  flex-shrink: 0;
  font-size: 11px;
  opacity: 0.7;
  transition: transform 0.18s ease;
}

.policy-picker.is-open .policy-arrow {
  transform: rotate(180deg);
}

.policy-picker.is-open .policy-select {
  border-color: #0a84ff;
}

.policy-pop {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  z-index: 30;
  max-height: 240px;
  overflow-y: auto;
  scrollbar-width: none; /* Firefox 隐藏滚动条 */
  padding: 6px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: #1c1c1e;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.45);
}

/* Chrome / Edge / Safari 隐藏滚动条（保留滚动能力） */
.policy-pop::-webkit-scrollbar {
  display: none;
}

.policy-opt {
  display: block;
  width: 100%;
  padding: 8px 10px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #f5f5f7;
  font-size: 12.5px;
  font-family: inherit;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s ease;
}

.policy-opt:hover {
  background: rgba(255, 255, 255, 0.07);
}

.policy-opt.is-active {
  background: #0a84ff;
  color: #fff;
}

.plan-btn {
  padding: 12px 0;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #0a84ff, #0055d4);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.plan-btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.plan-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.nav-error {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(255, 69, 58, 0.14);
  border: 1px solid rgba(255, 69, 58, 0.3);
  color: #ff9f9a;
  font-size: 12px;
  line-height: 1.5;
}

.route-summary {
  display: flex;
  gap: 10px;
}

.summary-item {
  flex: 1;
  padding: 10px 12px;
  border-radius: 10px;
  background: rgba(10, 132, 255, 0.12);
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.summary-label {
  font-size: 11px;
  color: #8ea8c9;
}

.summary-value {
  font-size: 15px;
  font-weight: 700;
  color: #6db8ff;
}

.globe-sync {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #aeb7c4;
  cursor: pointer;
  user-select: none;
}

.globe-sync input {
  accent-color: #0a84ff;
}

.steps-header {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #86868b;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.18s ease;
}

.step-item:hover {
  background: rgba(255, 255, 255, 0.07);
}

.step-index {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(10, 132, 255, 0.25);
  color: #6db8ff;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 1px;
}

.step-text {
  flex: 1;
  font-size: 12.5px;
  color: #e5e5ea;
  line-height: 1.5;
}

.step-text em {
  font-style: normal;
  color: #8e8e93;
}

.step-distance {
  flex-shrink: 0;
  font-size: 11px;
  color: #86868b;
}

.nav-map-wrap {
  flex: 1;
  position: relative;
  min-width: 0;
}

.nav-map {
  position: absolute;
  inset: 0;
}

.map-loading {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  background: #0d1117;
  color: #8ea8c9;
  font-size: 13px;
}

.loading-spinner {
  width: 44px;
  height: 44px;
  border: 3px solid rgba(100, 150, 255, 0.2);
  border-top-color: #0a84ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 联想下拉面板样式（AMap 渲染在 input 附近） */
:deep(.amap-sug-result) {
  z-index: 2100;
  max-height: 260px;
}

/* ---------- 响应式 ---------- */
@media (max-width: 900px) {
  .nav-body {
    flex-direction: column-reverse;
  }

  .nav-sidebar {
    width: 100%;
    max-height: 45%;
    border-right: none;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
  }

  .nav-map-wrap {
    min-height: 0;
  }
}

/* ---------- 浅色主题（主题按钮切换时与地图同步生效） ---------- */
.nav-overlay.is-light {
  background: #f5f5f7;
}

.nav-overlay.is-light .nav-header {
  background: rgba(0, 0, 0, 0.03);
  border-bottom-color: rgba(0, 0, 0, 0.08);
}

.nav-overlay.is-light .nav-title h2 {
  color: #1d1d1f;
}

.nav-overlay.is-light .close-btn {
  background: rgba(0, 0, 0, 0.06);
  color: #1d1d1f;
}

.nav-overlay.is-light .close-btn:hover {
  background: rgba(0, 0, 0, 0.12);
}

.nav-overlay.is-light .theme-btn {
  border-color: rgba(0, 0, 0, 0.12);
  background: rgba(0, 0, 0, 0.04);
}

.nav-overlay.is-light .theme-btn:hover {
  background: rgba(0, 0, 0, 0.1);
}

.nav-overlay.is-light .nav-tab {
  border-color: rgba(0, 0, 0, 0.14);
  color: #3a3a3c;
}

.nav-overlay.is-light .nav-tab.is-active {
  background: rgba(0, 102, 204, 0.1);
  border-color: #0066cc;
  color: #0066cc;
}

.nav-overlay.is-light .nav-tab:not(.is-active):hover {
  background: rgba(0, 0, 0, 0.05);
}

.nav-overlay.is-light .nav-sidebar {
  background: rgba(255, 255, 255, 0.65);
  border-right-color: rgba(0, 0, 0, 0.08);
}

.nav-overlay.is-light .nav-input {
  border-color: rgba(0, 0, 0, 0.14);
  background: #fff;
  color: #1d1d1f;
}

.nav-overlay.is-light .nav-input::placeholder {
  color: #a1a1a6;
}

.nav-overlay.is-light .mini-btn {
  border-color: rgba(0, 0, 0, 0.14);
  background: #fff;
  color: #3a3a3c;
}

.nav-overlay.is-light .mini-btn:hover {
  background: rgba(0, 0, 0, 0.06);
}

.nav-overlay.is-light .mode-tab {
  border-color: rgba(0, 0, 0, 0.14);
  color: #3a3a3c;
}

.nav-overlay.is-light .mode-tab.is-active {
  background: rgba(0, 102, 204, 0.1);
  border-color: #0066cc;
  color: #0066cc;
}

.nav-overlay.is-light .policy-select {
  border-color: rgba(0, 0, 0, 0.14);
  background: #fff;
  color: #1d1d1f;
}

.nav-overlay.is-light .policy-pop {
  border-color: rgba(0, 0, 0, 0.1);
  background: #fff;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.18);
}

.nav-overlay.is-light .policy-opt {
  color: #1d1d1f;
}

.nav-overlay.is-light .policy-opt:hover {
  background: rgba(0, 0, 0, 0.05);
}

.nav-overlay.is-light .policy-opt.is-active {
  background: #0a84ff;
  color: #fff;
}

.nav-overlay.is-light .nav-error {
  background: rgba(255, 69, 58, 0.08);
  border-color: rgba(255, 69, 58, 0.25);
  color: #d70015;
}

.nav-overlay.is-light .summary-label {
  color: #58719b;
}

.nav-overlay.is-light .summary-value {
  color: #0066cc;
}

.nav-overlay.is-light .globe-sync {
  color: #6e6e73;
}

.nav-overlay.is-light .step-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.nav-overlay.is-light .step-index {
  color: #0066cc;
}

.nav-overlay.is-light .step-text {
  color: #1d1d1f;
}

.nav-overlay.is-light .map-loading {
  background: #f5f5f7;
}

@media (max-width: 900px) {
  .nav-overlay.is-light .nav-sidebar {
    border-top-color: rgba(0, 0, 0, 0.08);
  }
}
</style>
