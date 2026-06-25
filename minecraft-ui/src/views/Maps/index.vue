<template>
  <div class="maps-page" :class="{ 'is-dark': isDark }">
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">🌍</span>
        交互式地球探索
      </h1>
      <p class="page-subtitle">点击大洲查看详情，继续放大可探索国家</p>
    </div>
    <div class="globe-wrapper">
      <GlobeCanvas
        ref="globeRef"
        :is-dark="isDark"
        :texture-url="textureUrl"
        @ready="onGlobeReady"
      />
      <ControlPanel
        :is-dark="isDark"
        :auto-rotate="autoRotate"
        :zoom-level="zoomLevel"
        :min-zoom="1"
        :max-zoom="5"
        @zoom-in="handleZoomIn"
        @zoom-out="handleZoomOut"
        @reset="handleReset"
        @toggle-theme="toggleTheme"
        @toggle-auto-rotate="toggleAutoRotate"
      />
      <div class="bottom-hint">
        <div class="hint-item">
          <span class="hint-icon">🖱️</span>
          <span>拖拽旋转</span>
        </div>
        <div class="hint-item">
          <span class="hint-icon">🔍</span>
          <span>滚轮缩放</span>
        </div>
        <div class="hint-item">
          <span class="hint-icon">📍</span>
          <span>点击探索</span>
        </div>
      </div>
    </div>
    <InfoPanel
      :visible="infoPanelVisible"
      :is-dark="isDark"
      :continent="displayContinent"
      :country="displayCountry"
      :is-hover="isHoverMode"
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import GlobeCanvas from './components/GlobeCanvas.vue'
import ControlPanel from './components/ControlPanel.vue'
import InfoPanel from './components/InfoPanel.vue'
import TooltipOverlay from './components/TooltipOverlay.vue'
import { continents } from './data/continents.js'
import { getCountriesByContinent } from './data/countries.js'
import { getContinentById } from './data/continents.js'
import { getCountriesByContinent as getCountryByContinentId } from './data/countries.js'

const globeRef = ref(null)
const isDark = ref(false)
const autoRotate = ref(true)
const isGlobeReady = ref(false)
const selectedContinent = ref(null)
const selectedCountry = ref(null)
const hoveredContinent = ref(null)
const hoveredCountry = ref(null)
const hoveredMarker = ref(null)
const selectedMarker = ref(null)
const mouseX = ref(0)
const mouseY = ref(0)
const zoomLevel = ref(1)
const isHoverMode = ref(false)

const textureUrl = ref('')
let pollIntervalId = null

const infoPanelVisible = computed(() => {
  return !!selectedContinent.value || !!selectedCountry.value || !!hoveredContinent.value || !!hoveredCountry.value
})

const displayContinent = computed(() => {
  return selectedContinent.value || hoveredContinent.value
})

const displayCountry = computed(() => {
  return selectedCountry.value || hoveredCountry.value
})

function toggleTheme() {
  isDark.value = !isDark.value
}

function toggleAutoRotate() {
  autoRotate.value = !autoRotate.value
  if (globeRef.value) {
    globeRef.value.setAutoRotate(autoRotate.value)
  }
}

function handleZoomIn() {
  if (globeRef.value) {
    globeRef.value.zoomIn()
  }
}

function handleZoomOut() {
  if (globeRef.value) {
    globeRef.value.zoomOut()
  }
}

function handleReset() {
  if (globeRef.value) {
    globeRef.value.resetView()
  }
  selectedContinent.value = null
  selectedCountry.value = null
  selectedMarker.value = null
}

function onGlobeReady() {
  isGlobeReady.value = true
  setupInteractionListeners()
}

function setupInteractionListeners() {
  if (!globeRef.value) return
  const interaction = globeRef.value.interaction
  if (!interaction) return
  pollIntervalId = setInterval(() => {
    if (interaction.state) {
      // Handle marker selection (click on marker)
      if (interaction.state.selectedMarker &&
          interaction.state.selectedMarker !== selectedMarker.value) {
        const marker = interaction.state.selectedMarker
        selectedMarker.value = marker
        // Map marker to continent/country data for InfoPanel
        if (marker.type === 'continent') {
          const continent = getContinentById(marker.id)
          selectedContinent.value = continent || marker
          selectedCountry.value = null
        } else {
          const countries = getCountryByContinentId(marker.continentId)
          const country = countries?.find(c => c.id === marker.id)
          selectedCountry.value = country || marker
          selectedContinent.value = null
        }
        hoveredContinent.value = null
        hoveredCountry.value = null
        hoveredMarker.value = null
        isHoverMode.value = false
      }
      // Handle selection (persistent on click)
      if (interaction.state.selectedContinent &&
          interaction.state.selectedContinent !== selectedContinent.value && !interaction.state.selectedMarker) {
        selectedContinent.value = interaction.state.selectedContinent
        selectedCountry.value = null
        selectedMarker.value = null
        hoveredContinent.value = null
        hoveredCountry.value = null
        hoveredMarker.value = null
        isHoverMode.value = false
      }
      if (interaction.state.selectedCountry &&
          interaction.state.selectedCountry !== selectedCountry.value && !interaction.state.selectedMarker) {
        selectedCountry.value = interaction.state.selectedCountry
        selectedContinent.value = null
        selectedMarker.value = null
        hoveredContinent.value = null
        hoveredCountry.value = null
        hoveredMarker.value = null
        isHoverMode.value = false
      }
      // Handle hover (temporary) - markers take priority
      if (!selectedContinent.value && !selectedCountry.value && !selectedMarker.value) {
        if (interaction.state.hoveredMarker &&
            interaction.state.hoveredMarker !== hoveredMarker.value) {
          hoveredMarker.value = interaction.state.hoveredMarker
          hoveredContinent.value = null
          hoveredCountry.value = null
          isHoverMode.value = true
        } else if (interaction.state.hoveredCountry &&
                   interaction.state.hoveredCountry !== hoveredCountry.value) {
          hoveredCountry.value = interaction.state.hoveredCountry
          hoveredContinent.value = null
          hoveredMarker.value = null
          isHoverMode.value = true
        } else if (interaction.state.hoveredContinent &&
                   interaction.state.hoveredContinent !== hoveredContinent.value) {
          hoveredContinent.value = interaction.state.hoveredContinent
          hoveredCountry.value = null
          hoveredMarker.value = null
          isHoverMode.value = true
        } else if (!interaction.state.hoveredCountry && !interaction.state.hoveredContinent && !interaction.state.hoveredMarker) {
          if (hoveredContinent.value || hoveredCountry.value || hoveredMarker.value) {
            hoveredContinent.value = null
            hoveredCountry.value = null
            hoveredMarker.value = null
            isHoverMode.value = false
          }
        }
      }
      zoomLevel.value = interaction.zoomLevel?.value || 1
    }
  }, 100)
}

function closeInfoPanel() {
  selectedContinent.value = null
  selectedCountry.value = null
  hoveredContinent.value = null
  hoveredCountry.value = null
  hoveredMarker.value = null
  selectedMarker.value = null
  isHoverMode.value = false
  if (globeRef.value?.interaction) {
    globeRef.value.interaction.closeInfoPanel()
    globeRef.value.interaction.clearHover()
  }
}

function handleExploreContinent(continent) {
  const countries = getCountriesByContinent(continent.id)
  if (countries.length > 0 && globeRef.value?.interaction) {
    const firstCountry = countries[0]
    globeRef.value.interaction.focusOnCountry(firstCountry)
    selectedContinent.value = null
    selectedCountry.value = firstCountry
  }
}

onMounted(() => {
  const savedTheme = localStorage.getItem('globe-theme')
  if (savedTheme) {
    isDark.value = savedTheme === 'dark'
  }
})

onUnmounted(() => {
  if (pollIntervalId) {
    clearInterval(pollIntervalId)
    pollIntervalId = null
  }
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
  font-size: 17px;
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
  .page-title {
    font-size: 28px;
  }
  .page-subtitle {
    font-size: 15px;
  }
  .hint-item {
    padding: 8px 14px;
    font-size: 12px;
  }
}

@media (max-width: 767px) {
  .page-header {
    padding: 16px 20px 0;
  }
  .page-title {
    font-size: 24px;
    gap: 8px;
  }
  .title-icon {
    font-size: 24px;
  }
  .page-subtitle {
    font-size: 14px;
    margin-top: 6px;
  }
  .globe-wrapper {
    margin-top: 4px;
  }
  .bottom-hint {
    bottom: 20px;
    gap: 4px;
    padding: 4px;
  }
  .hint-item {
    padding: 6px 12px;
    font-size: 11px;
    gap: 4px;
  }
  .hint-icon {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 22px;
  }
  .page-subtitle {
    font-size: 13px;
  }
  .hint-item span:last-child {
    display: none;
  }
  .hint-item {
    padding: 8px 10px;
  }
  .hint-icon {
    font-size: 14px;
  }
}
</style>
