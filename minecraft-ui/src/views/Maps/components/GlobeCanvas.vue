<template>
  <div class="globe-container" :class="{ 'is-dark': isDark }">
    <canvas
      ref="canvasRef"
      class="globe-canvas"
      @contextmenu.prevent
    ></canvas>
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p class="loading-text">正在加载地球...</p>
    </div>
    <div v-if="!webgpuSupported && !loading" class="error-overlay">
      <div class="error-icon">⚠</div>
      <h3 class="error-title">WebGPU 不支持</h3>
      <p class="error-message">{{ errorMessage }}</p>
      <p class="error-hint">请使用最新版 Chrome (113+) 或 Edge 浏览器</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useWebGPU } from '../composables/useWebGPU.js'
import { useGlobeInteraction } from '../composables/useGlobeInteraction.js'
import { multiplyMatrices } from '../utils/math.js'

const props = defineProps({
  isDark: {
    type: Boolean,
    default: false
  },
  textureUrl: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['selectContinent', 'selectCountry', 'ready'])

const canvasRef = ref(null)
const loading = ref(true)
const webgpuSupported = ref(true)
const errorMessage = ref('')

const {
  isSupported,
  isReady,
  errorMessage: wgpuError,
  initWebGPU,
  initRenderPipeline,
  renderFrame,
  resize,
  destroy
} = useWebGPU(canvasRef)

const interaction = useGlobeInteraction(canvasRef)

let animationFrameId = null
let startTime = 0
let lastWidth = 0
let lastHeight = 0

const lightDirection = { x: 0.5, y: 0.7, z: 0.5 }

function startRenderLoop() {
  startTime = performance.now()
  function render() {
    interaction.update()
    const canvas = canvasRef.value
    if (!canvas) return
    const width = canvas.clientWidth * window.devicePixelRatio
    const height = canvas.clientHeight * window.devicePixelRatio
    if (width === 0 || height === 0) {
      animationFrameId = requestAnimationFrame(render)
      return
    }
    if (width !== lastWidth || height !== lastHeight) {
      resize()
      lastWidth = width
      lastHeight = height
    }
    const time = (performance.now() - startTime) / 1000
    const vpMatrix = interaction.getViewProjectionMatrix(
      canvas.clientWidth,
      canvas.clientHeight
    )
    const modelMatrix = interaction.getModelMatrix()
    const mvpMatrix = multiplyMatrices(vpMatrix, modelMatrix)
    const normalMatrix = interaction.getNormalMatrix(modelMatrix)
    const cameraPos = interaction.getCameraPosition()
    const atmosphereStrength = props.isDark ? 0.8 : 0.5
    const clearColor = props.isDark
      ? { r: 0.01, g: 0.01, b: 0.03, a: 1 }
      : { r: 0.02, g: 0.05, b: 0.12, a: 1 }
    const adjustedLight = props.isDark
      ? { x: lightDirection.x * 0.6, y: lightDirection.y * 0.6, z: lightDirection.z * 0.6 }
      : lightDirection
    renderFrame({
      mvpMatrix,
      normalMatrix,
      lightDirection: [adjustedLight.x, adjustedLight.y, adjustedLight.z],
      cameraPosition: [cameraPos.x, cameraPos.y, cameraPos.z],
      time,
      atmosphereStrength,
      clearColor,
      starsVPMatrix: vpMatrix
    })
    animationFrameId = requestAnimationFrame(render)
  }
  animationFrameId = requestAnimationFrame(render)
}

function stopRenderLoop() {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }
}

function handleResize() {
  resize()
}

async function init() {
  try {
    const supported = await initWebGPU()
    if (!supported) {
      webgpuSupported.value = false
      errorMessage.value = wgpuError.value || 'WebGPU 初始化失败'
      loading.value = false
      return
    }
    const pipelineReady = await initRenderPipeline(props.textureUrl)
    if (!pipelineReady) {
      webgpuSupported.value = false
      errorMessage.value = wgpuError.value || '渲染管线创建失败'
      loading.value = false
      return
    }
    resize()
    interaction.setupEventListeners()
    loading.value = false
    emit('ready')
    startRenderLoop()
  } catch (e) {
    webgpuSupported.value = false
    errorMessage.value = e.message || '未知错误'
    loading.value = false
    console.error('Globe initialization failed:', e)
  }
}

watch(() => props.isDark, () => {
})

onMounted(() => {
  init()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  stopRenderLoop()
  interaction.cleanup()
  destroy()
  window.removeEventListener('resize', handleResize)
})

defineExpose({
  interaction,
  zoomIn: () => interaction.zoomIn(),
  zoomOut: () => interaction.zoomOut(),
  resetView: () => interaction.resetView(),
  setAutoRotate: (v) => interaction.setAutoRotate(v)
})
</script>

<style scoped>
.globe-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 50%, #0a0a2a 100%);
}

.globe-container.is-dark {
  background: linear-gradient(135deg, #050510 0%, #0f0f2a 50%, #050520 100%);
}

.globe-canvas {
  width: 100%;
  height: 100%;
  display: block;
  cursor: grab;
}

.globe-canvas:active {
  cursor: grabbing;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(5, 5, 15, 0.9);
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
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 20px;
  color: #a0b4d0;
  font-size: 14px;
  letter-spacing: 2px;
}

.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
  background: rgba(5, 5, 15, 0.95);
  z-index: 100;
}

.error-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.error-title {
  color: #ff6b6b;
  font-size: 24px;
  margin: 0 0 16px 0;
}

.error-message {
  color: #c0c0d0;
  font-size: 14px;
  margin: 0 0 12px 0;
  max-width: 400px;
  line-height: 1.6;
}

.error-hint {
  color: #8090b0;
  font-size: 13px;
  margin: 0;
}
</style>
