<template>
  <div v-if="visible" class="verification-modal" @click="handleClose">
    <div class="verification-container" @click.stop>
      <div class="verification-header">
        <span class="verification-title">二次验证</span>
        <button class="close-btn" @click="handleClose">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <div class="verification-body">
        <div class="puzzle-container" ref="puzzleContainer">
          <canvas ref="mainCanvas" class="main-canvas"></canvas>
          <canvas 
            ref="pieceCanvas" 
            class="piece-canvas" 
            :style="pieceStyle"
            :class="{ dragging: isDragging }"
          ></canvas>
        </div>

        <div class="slider-container">
          <div 
            class="slider-track"
            :class="{ success: isSuccess, fail: isFail }"
          >
            <div class="slider-fill" :style="fillStyle"></div>
          </div>
          <div 
            class="slider-thumb" 
            :style="thumbStyle"
            :class="{ dragging: isDragging, success: isSuccess, fail: isFail }"
            @mousedown="handleMouseDown"
            @touchstart="handleTouchStart"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </div>
          <span class="slider-tip">{{ sliderTip }}</span>
        </div>

        <div class="refresh-btn" @click="refreshPuzzle" v-if="!isDragging">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
            <path d="M21 3v5h-5"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { getCaptcha, verifyCaptcha } from '@/api/captcha'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'image'
  }
})

const emit = defineEmits(['close', 'success', 'fail'])

const puzzleContainer = ref(null)
const mainCanvas = ref(null)
const pieceCanvas = ref(null)

const puzzleSize = ref(56)
const puzzleOffset = ref(0)
const sliderPosition = ref(0)
const maxPosition = ref(200)
const containerWidth = ref(300)
const containerHeight = ref(120)

const isDragging = ref(false)
const isSuccess = ref(false)
const isFail = ref(false)
const isImageLoaded = ref(false)
const isLoading = ref(false)

const startX = ref(0)
const startPosition = ref(0)
const traceId = ref('')
const captchaData = ref(null)

const sliderTip = computed(() => {
  if (isSuccess.value) return '验证成功'
  if (isFail.value) return '验证失败，请重试'
  return '向右拖动滑块填充拼图'
})

const pieceStyle = computed(() => ({
  left: `${sliderPosition.value}px`,
  top: `${(containerHeight.value - puzzleSize.value) / 2}px`,
  width: `${puzzleSize.value}px`,
  height: `${puzzleSize.value}px`
}))

const fillStyle = computed(() => ({
  width: `${sliderPosition.value}px`
}))

const thumbStyle = computed(() => ({
  left: `${sliderPosition.value}px`
}))

const drawPuzzleShape = (ctx, x, y, width, height, isMask = false) => {
  const radius = 5
  const notchSize = 7
  
  ctx.beginPath()
  
  ctx.moveTo(x + radius, y)
  ctx.lineTo(x + width - radius, y)
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius)
  ctx.lineTo(x + width, y + height / 2 - notchSize)
  
  if (isMask) {
    ctx.quadraticCurveTo(x + width + notchSize / 2, y + height / 2, x + width, y + height / 2 + notchSize)
  } else {
    ctx.quadraticCurveTo(x + width - notchSize / 2, y + height / 2, x + width, y + height / 2 + notchSize)
  }
  
  ctx.lineTo(x + width, y + height - radius)
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height)
  ctx.lineTo(x + radius, y + height)
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius)
  ctx.lineTo(x, y + height / 2 + notchSize)
  
  if (isMask) {
    ctx.quadraticCurveTo(x - notchSize / 2, y + height / 2, x, y + height / 2 - notchSize)
  } else {
    ctx.quadraticCurveTo(x + notchSize / 2, y + height / 2, x, y + height / 2 - notchSize)
  }
  
  ctx.lineTo(x, y + radius)
  ctx.quadraticCurveTo(x, y, x + radius, y)
  ctx.closePath()
}

const drawMainCanvas = () => {
  if (!mainCanvas.value || !puzzleContainer.value || !captchaData.value) return
  
  const ctx = mainCanvas.value.getContext('2d')
  const rect = puzzleContainer.value.getBoundingClientRect()
  
  containerWidth.value = rect.width
  containerHeight.value = rect.height
  
  mainCanvas.value.width = containerWidth.value
  mainCanvas.value.height = containerHeight.value
  
  ctx.clearRect(0, 0, containerWidth.value, containerHeight.value)
  
  const pieceY = (containerHeight.value - puzzleSize.value) / 2
  
  drawPuzzleShape(ctx, puzzleOffset.value, pieceY, puzzleSize.value, puzzleSize.value, true)
  ctx.fillStyle = 'rgba(0, 0, 0, 0.5)'
  ctx.fill()
  
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.3)'
  ctx.lineWidth = 1
  ctx.stroke()
}

const drawPieceCanvas = () => {
  if (!pieceCanvas.value || !captchaData.value) return
  
  const ctx = pieceCanvas.value.getContext('2d')
  
  pieceCanvas.value.width = puzzleSize.value
  pieceCanvas.value.height = puzzleSize.value
  
  ctx.clearRect(0, 0, puzzleSize.value, puzzleSize.value)
  
  const colors = ['#b0c4de', '#f7dc6f', '#a3d8a3', '#f5b7b1', '#d7bde2', '#aed6f1']
  const bgColor = colors[Math.floor(Math.random() * colors.length)]
  
  ctx.fillStyle = bgColor
  ctx.fillRect(0, 0, puzzleSize.value, puzzleSize.value)
  
  drawPuzzleShape(ctx, 0, 0, puzzleSize.value, puzzleSize.value, false)
  ctx.clip()
  
  ctx.fillStyle = colors[Math.floor(Math.random() * colors.length)]
  ctx.globalAlpha = 0.6
  ctx.beginPath()
  ctx.arc(puzzleSize.value / 2, puzzleSize.value / 2, puzzleSize.value / 3, 0, Math.PI * 2)
  ctx.fill()
  
  ctx.restore()
  
  drawPuzzleShape(ctx, 0, 0, puzzleSize.value, puzzleSize.value, false)
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.8)'
  ctx.lineWidth = 1
  ctx.stroke()
}

const generateTraceId = () => {
  return 'captcha_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const fetchCaptcha = async () => {
  if (isLoading.value) return
  
  isLoading.value = true
  try {
    traceId.value = generateTraceId()
    const response = await getCaptcha(traceId.value)
    
    if (response.code === 200 && response.data) {
      captchaData.value = response.data
      isImageLoaded.value = true
      
      nextTick(() => {
        setTimeout(() => {
          if (puzzleContainer.value) {
            const rect = puzzleContainer.value.getBoundingClientRect()
            containerWidth.value = rect.width
            containerHeight.value = rect.height
            maxPosition.value = containerWidth.value - puzzleSize.value - 10
            puzzleOffset.value = captchaData.value.puzzleX || Math.floor(Math.random() * (maxPosition.value - 30)) + 30
            
            drawMainCanvas()
            drawPieceCanvas()
          }
        }, 50)
      })
    } else {
      handleFetchError()
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    handleFetchError()
  } finally {
    isLoading.value = false
  }
}

const handleFetchError = () => {
  nextTick(() => {
    setTimeout(() => {
      if (puzzleContainer.value) {
        const rect = puzzleContainer.value.getBoundingClientRect()
        containerWidth.value = rect.width
        containerHeight.value = rect.height
        maxPosition.value = containerWidth.value - puzzleSize.value - 10
        puzzleOffset.value = Math.floor(Math.random() * (maxPosition.value - 30)) + 30
        
        drawMainCanvas()
        drawPieceCanvas()
        isImageLoaded.value = true
      }
    }, 50)
  })
}

const refreshPuzzle = () => {
  isSuccess.value = false
  isFail.value = false
  sliderPosition.value = 0
  isImageLoaded.value = false
  captchaData.value = null
  fetchCaptcha()
}

const checkResult = async () => {
  if (!captchaData.value) {
    const tolerance = 5
    const diff = Math.abs(sliderPosition.value - puzzleOffset.value)
    if (diff <= tolerance) {
      isSuccess.value = true
      emit('success')
    } else {
      isFail.value = true
      emit('fail')
      setTimeout(() => {
        sliderPosition.value = 0
        isFail.value = false
      }, 1000)
    }
    return
  }
  
  try {
    const response = await verifyCaptcha({
      traceId: traceId.value,
      sliderPosition: sliderPosition.value,
      userAgent: navigator.userAgent
    })
    
    if (response.code === 200 && response.data) {
      if (response.data.passed) {
        isSuccess.value = true
        emit('success')
      } else {
        isFail.value = true
        emit('fail')
        setTimeout(() => {
          sliderPosition.value = 0
          isFail.value = false
        }, 1000)
      }
    } else {
      isFail.value = true
      emit('fail')
      setTimeout(() => {
        sliderPosition.value = 0
        isFail.value = false
      }, 1000)
    }
  } catch (error) {
    console.error('验证失败:', error)
    const tolerance = 5
    const diff = Math.abs(sliderPosition.value - puzzleOffset.value)
    if (diff <= tolerance) {
      isSuccess.value = true
      emit('success')
    } else {
      isFail.value = true
      emit('fail')
      setTimeout(() => {
        sliderPosition.value = 0
        isFail.value = false
      }, 1000)
    }
  }
}

const handleMouseDown = (e) => {
  if (isSuccess.value || isFail.value) return
  isDragging.value = true
  startX.value = e.clientX
  startPosition.value = sliderPosition.value
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

const handleTouchStart = (e) => {
  if (isSuccess.value || isFail.value) return
  isDragging.value = true
  startX.value = e.touches[0].clientX
  startPosition.value = sliderPosition.value
  
  document.addEventListener('touchmove', handleTouchMove)
  document.addEventListener('touchend', handleTouchUp)
}

const handleMouseMove = (e) => {
  if (!isDragging.value) return
  const deltaX = e.clientX - startX.value
  let newPosition = startPosition.value + deltaX
  newPosition = Math.max(0, Math.min(newPosition, maxPosition.value))
  sliderPosition.value = newPosition
}

const handleTouchMove = (e) => {
  if (!isDragging.value) return
  const deltaX = e.touches[0].clientX - startX.value
  let newPosition = startPosition.value + deltaX
  newPosition = Math.max(0, Math.min(newPosition, maxPosition.value))
  sliderPosition.value = newPosition
}

const handleMouseUp = () => {
  if (!isDragging.value) return
  isDragging.value = false
  
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  
  checkResult()
}

const handleTouchUp = () => {
  if (!isDragging.value) return
  isDragging.value = false
  
  document.removeEventListener('touchmove', handleTouchMove)
  document.removeEventListener('touchend', handleTouchUp)
  
  checkResult()
}

const handleClose = () => {
  emit('close')
}

watch(() => props.visible, (newVal) => {
  if (newVal) {
    refreshPuzzle()
  }
})

watch([sliderPosition], () => {
  if (isImageLoaded.value) {
    drawPieceCanvas()
  }
})

onMounted(() => {
  if (props.visible) {
    refreshPuzzle()
  }
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
  document.removeEventListener('touchmove', handleTouchMove)
  document.removeEventListener('touchend', handleTouchUp)
})
</script>

<style scoped>
.verification-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

.verification-container {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 340px;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

.verification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.verification-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  width: 26px;
  height: 26px;
  border: none;
  background: transparent;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f5f5f7;
  color: #666;
}

.verification-body {
  padding: 18px 20px 22px;
  position: relative;
}

.puzzle-container {
  position: relative;
  width: 100%;
  height: 120px;
  border-radius: 10px;
  overflow: hidden;
  background: #f5f5f7;
  margin-bottom: 18px;
}

.main-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.piece-canvas {
  position: absolute;
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
  z-index: 10;
  transition: box-shadow 0.2s ease;
}

.piece-canvas.dragging {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.35);
}

.slider-container {
  position: relative;
  height: 36px;
}

.slider-track {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 36px;
  background: #f0f0f0;
  border-radius: 18px;
  transform: translateY(-50%);
  transition: background-color 0.3s ease;
}

.slider-track.success {
  background: #e8f4fc;
}

.slider-track.fail {
  background: #fef0f0;
}

.slider-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(135deg, #3490de 0%, #1e6bb8 100%);
  border-radius: 18px;
  transition: width 0.08s ease;
}

.slider-track.success .slider-fill {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.slider-track.fail .slider-fill {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.slider-thumb {
  position: absolute;
  top: 50%;
  width: 40px;
  height: 36px;
  margin-left: -20px;
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3490de;
  cursor: grab;
  transform: translateY(-50%);
  transition: transform 0.1s ease, box-shadow 0.2s ease, background-color 0.2s ease;
  z-index: 20;
  border: 1px solid rgba(52, 144, 222, 0.1);
}

.slider-thumb:active {
  cursor: grabbing;
}

.slider-thumb.dragging {
  box-shadow: 0 4px 16px rgba(52, 144, 222, 0.3);
  transform: translateY(-50%) scale(1.05);
}

.slider-thumb.success {
  background: #10b981;
  color: #ffffff;
  border-color: #10b981;
}

.slider-thumb.fail {
  background: #ef4444;
  color: #ffffff;
  border-color: #ef4444;
}

.slider-tip {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 13px;
  color: #999;
  transform: translateY(-50%);
  pointer-events: none;
  font-weight: 500;
}

.slider-track.success .slider-tip {
  color: #10b981;
}

.slider-track.fail .slider-tip {
  color: #ef4444;
}

.refresh-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
  z-index: 30;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 1);
  color: #3490de;
  transform: rotate(180deg);
  box-shadow: 0 4px 12px rgba(52, 144, 222, 0.2);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 480px) {
  .verification-container {
    width: 92%;
    max-width: 320px;
  }
  
  .verification-body {
    padding: 16px 16px 20px;
  }
  
  .puzzle-container {
    height: 100px;
  }
}
</style>