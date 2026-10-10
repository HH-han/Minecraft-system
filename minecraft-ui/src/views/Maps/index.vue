<template>
  <div class="maps-page" :class="{ 'is-dark': isDark }">
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">🌍</span>
        3D 交互地球 · 高德导航
      </h1>
      <p class="page-subtitle">地形 · 洋流 · 极光 · 星空 · 卫星轨道 等 39 个图层，左侧面板支持独立开关与场景预设</p>
    </div>
    <div class="globe-wrapper">
      <GlobeCanvas
        ref="globeRef"
        :is-dark="isDark"
        :auto-rotate="autoRotate"
        :layers="layers"
        @ready="onGlobeReady"
        @label-hover="onLabelHover"
        @globe-select="onGlobeSelect"
        @zoom-change="onZoomChange"
      />
      <ControlPanel
        :is-dark="isDark"
        :auto-rotate="autoRotate"
        :zoom-level="zoomLevel"
        :layers="layers"
        @toggle-layer="toggleLayer"
        @preset-view="handlePresetView"
        @apply-scene="applyScene"
        @open-navigation="openNavigation"
        @open-services="openServices"
        @zoom-in="globeRef?.zoomIn()"
        @zoom-out="globeRef?.zoomOut()"
        @reset="handleReset"
        @toggle-theme="toggleTheme"
        @toggle-auto-rotate="toggleAutoRotate"
      />
      <div class="bottom-hint">
        <div class="hint-item"><span class="hint-icon">🖱️</span><span>拖拽旋转</span></div>
        <div class="hint-item"><span class="hint-icon">🔍</span><span>滚轮缩放</span></div>
        <div class="hint-item"><span class="hint-icon">🏷️</span><span>点击标注探索</span></div>
        <div class="hint-item"><span class="hint-icon">🧭</span><span>左侧开启导航</span></div>
      </div>
    </div>
    <InfoPanel
      :visible="infoPanelVisible"
      :is-dark="isDark"
      :continent="selectedContinent"
      :country="selectedCountry"
      :is-hover="false"
      @close="closeInfoPanel"
      @explore="handleExploreContinent"
    />
    <TooltipOverlay
      :visible="!!hoveredMarker"
      :is-dark="isDark"
      :marker="hoveredMarker"
      :mouse-x="mouseX"
      :mouse-y="mouseY"
    />
    <NavigationPanel
      :visible="navOpen"
      @close="closeNavigation"
      @route-change="onRouteChange"
    />
    <ServicePanel
      :visible="servicesOpen"
      @close="closeServices"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import GlobeCanvas from './components/GlobeCanvas.vue'
import ControlPanel from './components/ControlPanel.vue'
import InfoPanel from './components/InfoPanel.vue'
import TooltipOverlay from './components/TooltipOverlay.vue'
import NavigationPanel from './components/NavigationPanel.vue'
import ServicePanel from './components/ServicePanel.vue'
import { DEFAULT_LAYERS, VIEW_PRESETS } from './config.js'
import { getCountriesByContinent } from './data/countries.js'

const globeRef = ref(null)
const isDark = ref(false)
const autoRotate = ref(true)
const navOpen = ref(false)
const servicesOpen = ref(false)
const selectedContinent = ref(null)
const selectedCountry = ref(null)
const hoveredMarker = ref(null)
const mouseX = ref(0)
const mouseY = ref(0)
const zoomLevel = ref(1)

const layers = reactive({ ...DEFAULT_LAYERS })

const infoPanelVisible = computed(() => !!selectedContinent.value || !!selectedCountry.value)

/* ---------- 图层控制 ---------- */

function toggleLayer(key) {
  layers[key] = !layers[key]
}

/** 应用场景预设：整体替换图层开关状态 */
function applyScene(preset) {
  if (!preset?.layers) return
  for (const k of Object.keys(layers)) layers[k] = false
  for (const [k, v] of Object.entries(preset.layers)) {
    if (k in layers) layers[k] = v
  }
}

function handlePresetView(preset) {
  globeRef.value?.pointOfView({ lat: preset.lat, lng: preset.lng, altitude: preset.alt }, 600)
}

/* ---------- 主题 / 旋转 / 缩放 ---------- */

function toggleTheme() {
  isDark.value = !isDark.value
  localStorage.setItem('globe-theme', isDark.value ? 'dark' : 'light')
}

function toggleAutoRotate() {
  autoRotate.value = !autoRotate.value
}

function handleReset() {
  globeRef.value?.resetView()
  globeRef.value?.clearLocateRing()
  selectedContinent.value = null
  selectedCountry.value = null
}

function onZoomChange(pov) {
  // altitude(0.1~3.5) 映射为 1~5 缩放级别
  const z = 1 + (2.6 - pov.altitude) / 2.4 * 4
  zoomLevel.value = Math.round(Math.min(5, Math.max(1, z)) * 10) / 10
}

/* ---------- 地球交互 ---------- */

function onGlobeReady() { /* 地球就绪（loading 已由组件内部关闭） */ }

function onLabelHover(marker) {
  hoveredMarker.value = marker
  if (marker && window.__globeMouse) {
    mouseX.value = window.__globeMouse.x
    mouseY.value = window.__globeMouse.y
  }
}

function onGlobeSelect({ type, data }) {
  if (type === 'country') {
    selectedContinent.value = null
    selectedCountry.value = data
    hoveredMarker.value = null
    globeRef.value?.locateAt(data.lat, data.lng, 1.5)
  } else if (type === 'continent') {
    selectedCountry.value = null
    selectedContinent.value = data
    hoveredMarker.value = null
    globeRef.value?.locateAt(data.centerLat, data.centerLng, 1.7)
  }
}

function closeInfoPanel() {
  selectedContinent.value = null
  selectedCountry.value = null
  hoveredMarker.value = null
}

function handleExploreContinent(continent) {
  const first = getCountriesByContinent(continent.id)?.[0]
  if (first) {
    selectedContinent.value = null
    selectedCountry.value = first
    globeRef.value?.locateAt(first.lat, first.lng, 1.4)
  } else {
    globeRef.value?.locateAt(continent.centerLat, continent.centerLng, 1.6)
  }
}

/* ---------- 高德导航 ---------- */

function openNavigation() {
  navOpen.value = true
  // 导航面板全屏覆盖时暂停地球渲染，节省 GPU
  globeRef.value?.pauseAnimation()
}

function closeNavigation() {
  navOpen.value = false
  globeRef.value?.resumeAnimation()
}

/* ---------- 高德服务工具箱 ---------- */

function openServices() {
  servicesOpen.value = true
  globeRef.value?.pauseAnimation()
}

function closeServices() {
  servicesOpen.value = false
  globeRef.value?.resumeAnimation()
}

function onRouteChange(route) {
  if (route && route.points?.length > 1) {
    globeRef.value?.setRoutePath(route.points)
  } else {
    globeRef.value?.clearRoutePath()
  }
}

onMounted(() => {
  const savedTheme = localStorage.getItem('globe-theme')
  if (savedTheme) isDark.value = savedTheme === 'dark'
})

onUnmounted(() => {
  if (navOpen.value || servicesOpen.value) globeRef.value?.resumeAnimation()
})
</script>

<style scoped>
.maps-page {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: rgba(255, 255, 255, 0.98);
  overflow: hidden;
  transition: background 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.maps-page.is-dark {
  background: #000000;
}

.page-header {
  position: relative;
  z-index: 10;
  padding: 22px 0 0;
  text-align: center;
  pointer-events: none;
}

.page-title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: -0.5px;
  line-height: 1.08;
  color: #1d1d1f;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: color 0.5s ease;
}

.maps-page.is-dark .page-title {
  color: #f5f5f7;
}

.title-icon {
  font-size: 30px;
  line-height: 1;
  animation: float 4s ease-in-out infinite;
  display: inline-block;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

.page-subtitle {
  margin: 8px 0 0;
  font-size: 15px;
  font-weight: 400;
  color: #6e6e73;
  letter-spacing: -0.2px;
  line-height: 1.38;
  transition: color 0.5s ease;
}

.maps-page.is-dark .page-subtitle {
  color: #86868b;
}

.globe-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
  margin-top: 8px;
}

.bottom-hint {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 5;
  padding: 6px;
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 980px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: background 0.5s ease, border-color 0.5s ease, box-shadow 0.5s ease;
}

.maps-page.is-dark .bottom-hint {
  background: rgba(29, 29, 31, 0.72);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 980px;
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
  line-height: 1;
  transition: background 0.2s ease, color 0.5s ease;
}

.maps-page.is-dark .hint-item {
  color: #f5f5f7;
}

.hint-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.maps-page.is-dark .hint-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.hint-icon {
  font-size: 14px;
  line-height: 1;
}

@media (max-width: 1199px) {
  .page-title { font-size: 28px; }
  .page-subtitle { font-size: 14px; }
  .hint-item { padding: 8px 14px; font-size: 12px; }
}

@media (max-width: 767px) {
  .page-header { padding: 16px 20px 0; }
  .page-title { font-size: 24px; gap: 8px; }
  .title-icon { font-size: 24px; }
  .page-subtitle { font-size: 12px; margin-top: 6px; }
  .globe-wrapper { margin-top: 4px; }
  .bottom-hint { bottom: 20px; gap: 4px; padding: 4px; flex-wrap: wrap; justify-content: center; }
  .hint-item { padding: 6px 12px; font-size: 11px; gap: 4px; }
  .hint-icon { font-size: 12px; }
}

@media (max-width: 480px) {
  .page-title { font-size: 22px; }
  .page-subtitle { font-size: 11px; }
  .hint-item span:last-child { display: none; }
  .hint-item { padding: 8px 10px; }
}
</style>
