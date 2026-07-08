<template>
  <div v-if="visible" class="captcha-modal" @click="handleClose">
    <div class="captcha-container" @click.stop>
      <div class="captcha-header">
        <span class="captcha-title">二次验证</span>
        <button class="close-btn" @click="handleClose">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 6L6 18M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <div class="captcha-body">
        <div class="puzzle-container" ref="puzzleContainer">
          <img 
            ref="backgroundImage" 
            class="background-image"
            :src="backgroundImageSrc"
            @load="onImageLoaded"
            @error="onImageError"
          />
          <canvas 
            ref="mainCanvas" 
            class="main-canvas"
            :class="{ 'blur-mask': isDragging }"
          ></canvas>
          <canvas 
            ref="pieceCanvas" 
            class="piece-canvas" 
            :style="pieceStyle"
            :class="{ dragging: isDragging }"
          ></canvas>
          
          <div v-if="isLoading" class="loading-overlay">
            <svg class="loading-spinner" viewBox="0 0 24 24">
              <circle class="spinner-path" cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="2"/>
            </svg>
            <span>图片加载中...</span>
          </div>
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
            :class="{ dragging: isDragging, success: isSuccess, fail: isFail, verifying: isVerifying }"
            @mousedown="handleMouseDown"
            @touchstart="handleTouchStart"
            @keydown="handleKeyDown"
            role="slider"
            :aria-label="sliderTip"
            :aria-valuenow="sliderPercent"
            aria-valuemin="0"
            aria-valuemax="100"
            tabindex="0"
          >
            <svg v-if="isSuccess" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
            <svg v-else-if="isFail" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
            <svg v-else-if="isVerifying" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" class="verify-spinner">
              <circle cx="12" cy="12" r="10" fill="none" stroke-linecap="round"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </div>
          <span class="slider-tip">{{ sliderTip }}</span>
        </div>

        <button class="refresh-btn" @click="refreshPuzzle" :disabled="isDragging || isVerifying" :title="'刷新验证码'">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
            <path d="M21 3v5h-5"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { getCaptcha, verifyCaptcha } from '@/api/captcha';
import { 
  generateCaptchaSVG, 
  svgToBase64, 
  drawMainCanvas, 
  drawPieceCanvas, 
  calculateScaledPosition,
  generateTraceId,
  calculateCaptchaParams,
  calculateCaptchaParamsFromData,
  comparePositionData,
  COMPARISON_STATUS
} from '@/utils/image';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'image'
  }
});

const emit = defineEmits(['close', 'success', 'fail']);

const puzzleContainer = ref(null);
const backgroundImage = ref(null);
const mainCanvas = ref(null);
const pieceCanvas = ref(null);

const puzzleSize = ref(44);
const puzzleOffset = ref(0);
const sliderPosition = ref(0);
const maxPosition = ref(200);
const containerWidth = ref(320);
const containerHeight = ref(180);
const puzzleY = ref(65);

const isDragging = ref(false);
const isSuccess = ref(false);
const isFail = ref(false);
const isVerifying = ref(false);
const isImageLoaded = ref(false);
const isLoading = ref(false);

const startX = ref(0);
const startPosition = ref(0);
const traceId = ref('');
const captchaData = ref(null);
const backgroundImageSrc = ref('');

const sliderTip = computed(() => {
  if (isSuccess.value) return '验证成功';
  if (isFail.value) return '验证失败，请重试';
  if (isVerifying.value) return '验证中...';
  return '拖动滑块完成拼图';
});

const trackMaxPosition = computed(() => {
  return containerWidth.value - 44;
});

const puzzlePosition = computed(() => {
  if (trackMaxPosition.value <= 0) return 0;
  return Math.round((sliderPosition.value / trackMaxPosition.value) * maxPosition.value);
});

const sliderPercent = computed(() => {
  if (trackMaxPosition.value <= 0) return 0;
  return Math.round((sliderPosition.value / trackMaxPosition.value) * 100);
});

const pieceStyle = computed(() => ({
  left: `${puzzlePosition.value}px`,
  top: `${puzzleY.value}px`,
  width: `${puzzleSize.value}px`,
  height: `${puzzleSize.value}px`,
  transform: isSuccess.value ? 'scale(1)' : 'scale(1)',
  transition: isSuccess.value || isFail.value ? 'all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1)' : 'none'
}));

const fillStyle = computed(() => ({
  width: `${sliderPosition.value}px`,
  transition: isDragging.value ? 'width 0.08s linear' : 'width 0.2s ease-out'
}));

const thumbStyle = computed(() => ({
  left: `${sliderPosition.value}px`,
  transition: isSuccess.value || isFail.value ? 'left 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94)' : 'none'
}));

const onImageLoaded = () => {
  if (!isImageLoaded.value) {
    isImageLoaded.value = true;
    isLoading.value = false;
    renderCanvas();
  }
};

const onImageError = () => {
  console.error('图片加载失败，切换到本地生成模式');
  handleFetchError();
};

const renderCanvas = () => {
  const targetX = containerWidth.value - puzzleSize.value - 20;
  drawMainCanvas(
    mainCanvas.value, 
    puzzleOffset.value, 
    puzzleY.value, 
    puzzleSize.value, 
    targetX,
    captchaData.value,
    containerWidth.value,
    containerHeight.value
  );
  drawPieceCanvas(
    pieceCanvas.value, 
    backgroundImage.value, 
    puzzleOffset.value, 
    puzzleY.value, 
    puzzleSize.value, 
    containerWidth.value
  );
};

const fetchCaptcha = async () => {
  if (isLoading.value) return;
  
  isLoading.value = true;
  isImageLoaded.value = false;
  
  try {
    traceId.value = generateTraceId();
    const response = await getCaptcha(traceId.value);
    
    if (response.code === 200 && response.data) {
      captchaData.value = response.data;
      backgroundImageSrc.value = svgToBase64(captchaData.value.imageData);
      
      nextTick(() => {
        setTimeout(() => {
          if (puzzleContainer.value) {
            const rect = puzzleContainer.value.getBoundingClientRect();
            containerWidth.value = rect.width;
            containerHeight.value = rect.height;
            
            const params = calculateCaptchaParamsFromData(captchaData.value, containerWidth.value, containerHeight.value);
            puzzleSize.value = params.puzzleSize;
            puzzleOffset.value = params.puzzleOffset;
            puzzleY.value = params.puzzleY;
            maxPosition.value = params.maxPosition;
            
            if (params.warnings && params.warnings.length > 0) {
              console.warn('验证码数据警告:', params.warnings);
            }
            
            if (params.comparison) {
              console.log('位置数据对比:', params.comparison);
            }
            
            isImageLoaded.value = false;
          }
        }, 50);
      });
    } else {
      handleFetchError();
    }
  } catch (error) {
    console.error('获取验证码失败:', error);
    handleFetchError();
  }
};

const handleFetchError = () => {
  isLoading.value = false;
  const svgContent = generateCaptchaSVG();
  backgroundImageSrc.value = svgToBase64(svgContent);
  
  nextTick(() => {
    setTimeout(() => {
      if (puzzleContainer.value) {
        const rect = puzzleContainer.value.getBoundingClientRect();
        containerWidth.value = rect.width;
        containerHeight.value = rect.height;
        
        const params = calculateCaptchaParams(containerWidth.value, containerHeight.value);
        puzzleSize.value = params.puzzleSize;
        puzzleOffset.value = params.puzzleOffset;
        puzzleY.value = params.puzzleY;
        maxPosition.value = params.maxPosition;
        
        isImageLoaded.value = false;
      }
    }, 50);
  });
};
const refreshPuzzle = () => {
 isSuccess.value = false;
 isFail.value = false;
 isVerifying.value = false;
 sliderPosition.value = 0;
 isImageLoaded.value = false;
 captchaData.value = null;
 backgroundImageSrc.value = '';
 fetchCaptcha();
};
const checkResult = async () => {
  isDragging.value = false;
  isVerifying.value = true;
  
  try {
    const scaledPosition = calculateScaledPosition(puzzlePosition.value, containerWidth.value);
    
    const requestData = {
      traceId: traceId.value,
      sliderPosition: scaledPosition,
      userAgent: navigator.userAgent,
      isLocalFallback: !captchaData.value,
      frontendParams: captchaData.value ? {
        puzzleOffset: puzzleOffset.value,
        puzzleY: puzzleY.value,
        puzzleSize: puzzleSize.value,
        targetX: containerWidth.value - puzzleSize.value - 20
      } : null
    };
    
    const response = await verifyCaptcha(requestData);
    
    isVerifying.value = false;
    
    if (response.code === 200 && response.data) {
      if (response.data.passed) {
        isSuccess.value = true;
        emit('success');
        
        if (response.data.positionComparison) {
          console.log('位置数据对比结果:', response.data.positionComparison);
        }
      } else {
        isFail.value = true;
        emit('fail', response.data);
        setTimeout(() => {
          animateSliderBack();
        }, 500);
      }
    } else {
      isFail.value = true;
      emit('fail', response);
      setTimeout(() => {
        animateSliderBack();
      }, 500);
    }
  } catch (error) {
    console.error('验证失败:', error);
    isVerifying.value = false;
    isFail.value = true;
    emit('fail', { error: error.message });
    setTimeout(() => {
      animateSliderBack();
    }, 500);
  }
};
const animateSliderBack = () => {
 const startPos = sliderPosition.value;
 const duration = 400;
 const startTime = performance.now();
 const animate = (currentTime) => {
 const elapsed = currentTime - startTime;
 const progress = Math.min(elapsed / duration, 1);
 const easeProgress = 1 - Math.pow(1 - progress, 3);
 sliderPosition.value = startPos * (1 - easeProgress);
 if (progress < 1) {
 requestAnimationFrame(animate);
 }
 else {
 sliderPosition.value = 0;
 isFail.value = false;
 refreshPuzzle();
 }
 };
 requestAnimationFrame(animate);
};
const handleMouseDown = (e) => {
 if (isSuccess.value || isFail.value || isVerifying.value)
 return;
 e.preventDefault();
 isDragging.value = true;
 startX.value = e.clientX;
 startPosition.value = sliderPosition.value;
 document.addEventListener('mousemove', handleMouseMove);
 document.addEventListener('mouseup', handleMouseUp);
};
const handleTouchStart = (e) => {
 if (isSuccess.value || isFail.value || isVerifying.value)
 return;
 e.preventDefault();
 isDragging.value = true;
 startX.value = e.touches[0].clientX;
 startPosition.value = sliderPosition.value;
 document.addEventListener('touchmove', handleTouchMove, { passive: false });
 document.addEventListener('touchend', handleTouchUp);
};
const handleMouseMove = (e) => {
 if (!isDragging.value)
 return;
 const deltaX = e.clientX - startX.value;
 let newPosition = startPosition.value + deltaX;
 newPosition = Math.max(0, Math.min(newPosition, trackMaxPosition.value));
 sliderPosition.value = newPosition;
};
const handleTouchMove = (e) => {
 if (!isDragging.value)
 return;
 e.preventDefault();
 const deltaX = e.touches[0].clientX - startX.value;
 let newPosition = startPosition.value + deltaX;
 newPosition = Math.max(0, Math.min(newPosition, trackMaxPosition.value));
 sliderPosition.value = newPosition;
};
const handleMouseUp = () => {
 if (!isDragging.value)
 return;
 document.removeEventListener('mousemove', handleMouseMove);
 document.removeEventListener('mouseup', handleMouseUp);
 checkResult();
};
const handleTouchUp = () => {
 if (!isDragging.value)
 return;
 document.removeEventListener('touchmove', handleTouchMove);
 document.removeEventListener('touchend', handleTouchUp);
 checkResult();
};
const handleKeyDown = (e) => {
 if (isSuccess.value || isFail.value || isVerifying.value)
 return;
 switch (e.key) {
 case 'ArrowLeft':
 e.preventDefault();
 sliderPosition.value = Math.max(0, sliderPosition.value - 4);
 break;
 case 'ArrowRight':
 e.preventDefault();
 sliderPosition.value = Math.min(trackMaxPosition.value, sliderPosition.value + 4);
 break;
 case 'Home':
 e.preventDefault();
 sliderPosition.value = 0;
 break;
 case 'End':
 e.preventDefault();
 sliderPosition.value = trackMaxPosition.value;
 break;
 case 'Enter':
 case ' ':
 e.preventDefault();
 checkResult();
 break;
 }
};
const handleClose = () => {
 emit('close');
};
watch(() => props.visible, (newVal) => {
 if (newVal) {
 refreshPuzzle();
 }
});
watch([sliderPosition], () => {
  if (isImageLoaded.value) {
    const targetX = containerWidth.value - puzzleSize.value - 20;
    drawPieceCanvas(
      pieceCanvas.value, 
      backgroundImage.value, 
      puzzleOffset.value, 
      puzzleY.value, 
      puzzleSize.value, 
      containerWidth.value
    );
  }
});
onMounted(() => {
 if (props.visible) {
 refreshPuzzle();
 }
});
onUnmounted(() => {
 document.removeEventListener('mousemove', handleMouseMove);
 document.removeEventListener('mouseup', handleMouseUp);
 document.removeEventListener('touchmove', handleTouchMove);
 document.removeEventListener('touchend', handleTouchUp);
});
</script>

<style scoped>
.captcha-modal {
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

.captcha-container {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  width: 90%;
  max-width: 360px;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

.captcha-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.captcha-title {
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

.captcha-body {
  padding: 20px 24px;
  position: relative;
}

.puzzle-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  min-height: 140px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f7;
  margin-bottom: 20px;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.main-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  transition: filter 0.2s ease;
}

.main-canvas.blur-mask {
  filter: blur(2px);
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
  transform: scale(1.02);
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 20;
  gap: 8px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  color: #3490de;
  animation: spin 0.8s linear infinite;
}

.spinner-path {
  stroke-dasharray: 62.8;
  stroke-dashoffset: 15.7;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-overlay span {
  font-size: 14px;
  color: #999;
}

.slider-container {
  position: relative;
  height: 40px;
}

.slider-track {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 40px;
  background: #f0f2f5;
  border-radius: 20px;
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
  border-radius: 20px;
}

.slider-track.success .slider-fill {
  background: linear-gradient(135deg, #52C41A 0%, #389e0d 100%);
}

.slider-track.fail .slider-fill {
  background: linear-gradient(135deg, #FF4D4F 0%, #cf1322 100%);
}

.slider-thumb {
  position: absolute;
  top: 50%;
  width: 44px;
  height: 40px;
  margin-left: -22px;
  background: #ffffff;
  border-radius: 20px;
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
  touch-action: none;
}

.slider-thumb:active {
  cursor: grabbing;
}

.slider-thumb:hover {
  border-color: #B3D8FF;
}

.slider-thumb.dragging {
  box-shadow: 0 4px 16px rgba(52, 144, 222, 0.3);
  transform: translateY(-50%) scale(1.05);
}

.slider-thumb.verifying {
  pointer-events: none;
}

.slider-thumb.success {
  background: #52C41A;
  color: #ffffff;
  border-color: #52C41A;
}

.slider-thumb.fail {
  background: #FF4D4F;
  color: #ffffff;
  border-color: #FF4D4F;
  animation: shake 0.4s ease;
}

@keyframes shake {
  0%, 100% { transform: translateY(-50%) translateX(0); }
  25% { transform: translateY(-50%) translateX(-5px); }
  75% { transform: translateY(-50%) translateX(5px); }
}

.verify-spinner {
  animation: spin 0.8s linear infinite;
}

.slider-tip {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 14px;
  color: #8C8C8C;
  transform: translateY(-50%);
  pointer-events: none;
  font-weight: 400;
}

.slider-track.success .slider-tip {
  color: #52C41A;
}

.slider-track.fail .slider-tip {
  color: #FF4D4F;
}

.refresh-btn {
  position: absolute;
  top: 20px;
  right: 24px;
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

.refresh-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 1);
  color: #3490de;
  transform: rotate(180deg);
  box-shadow: 0 4px 12px rgba(52, 144, 222, 0.2);
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
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
  .captcha-container {
    width: 92%;
    max-width: 340px;
  }
  
  .captcha-body {
    padding: 16px 20px;
  }
  
  .puzzle-container {
    min-height: 140px;
  }
  
  .slider-container {
    height: 44px;
  }
  
  .slider-track {
    height: 44px;
    border-radius: 22px;
  }
  
  .slider-fill {
    border-radius: 22px;
  }
  
  .slider-thumb {
    width: 44px;
    height: 44px;
    margin-left: -22px;
    border-radius: 22px;
  }
}

@media (max-width: 320px) {
  .puzzle-container {
    min-height: 120px;
  }
  
  .slider-tip {
    font-size: 12px;
  }
}
</style>