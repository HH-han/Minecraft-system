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
      :continent="selectedContinent"
      :country="selectedCountry"
      @close="closeInfoPanel"
      @explore="handleExploreContinent"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import GlobeCanvas from './components/GlobeCanvas.vue'
import ControlPanel from './components/ControlPanel.vue'
import InfoPanel from './components/InfoPanel.vue'
import { continents } from './data/continents.js'
import { getCountriesByContinent } from './data/countries.js'

const globeRef = ref(null)
const isDark = ref(false)
const autoRotate = ref(true)
const isGlobeReady = ref(false)
const selectedContinent = ref(null)
const selectedCountry = ref(null)
const zoomLevel = ref(1)

const textureUrl = ref('')
let pollIntervalId = null

const infoPanelVisible = computed(() => {
  return !!selectedContinent.value || !!selectedCountry.value
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
      if (interaction.state.selectedContinent &&
          interaction.state.selectedContinent !== selectedContinent.value) {
        selectedContinent.value = interaction.state.selectedContinent
        selectedCountry.value = null
      }
      if (interaction.state.selectedCountry &&
          interaction.state.selectedCountry !== selectedCountry.value) {
        selectedCountry.value = interaction.state.selectedCountry
        selectedContinent.value = null
      }
      zoomLevel.value = interaction.zoomLevel?.value || 1
    }
  }, 100)
}

function closeInfoPanel() {
  selectedContinent.value = null
  selectedCountry.value = null
  if (globeRef.value?.interaction) {
    globeRef.value.interaction.closeInfoPanel()
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
  background: linear-gradient(180deg, #0a1628 0%, #0d1f3c 50%, #0a1628 100%);
  overflow: hidden;
  transition: background 0.5s ease;
}

.maps-page.is-dark {
  background: linear-gradient(180deg, #050a14 0%, #081024 50%, #050a14 100%);
}

.page-header {
  position: relative;
  z-index: 5;
  padding: 24px 40px 16px;
  text-align: center;
}

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  letter-spacing: 2px;
  text-shadow: 0 2px 20px rgba(100, 150, 255, 0.3);
}

.title-icon {
  font-size: 32px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.page-subtitle {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(180, 200, 230, 0.7);
  letter-spacing: 1px;
}

.globe-wrapper {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.bottom-hint {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 30px;
  z-index: 5;
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 12px;
  color: rgba(200, 220, 240, 0.8);
}

.hint-icon {
  font-size: 14px;
}

@media (max-width: 768px) {
  .page-header {
    padding: 16px 20px 12px;
  }
  .page-title {
    font-size: 20px;
    letter-spacing: 1px;
  }
  .page-subtitle {
    font-size: 12px;
  }
  .bottom-hint {
    gap: 10px;
    bottom: 20px;
  }
  .hint-item {
    padding: 6px 12px;
    font-size: 11px;
    gap: 6px;
  }
}
</style>
