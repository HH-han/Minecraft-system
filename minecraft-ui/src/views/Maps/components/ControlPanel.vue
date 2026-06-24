<template>
  <div class="control-panel" :class="{ 'is-dark': isDark }">
    <div class="control-group">
      <button
        class="control-btn"
        @click="$emit('zoomIn')"
        title="放大"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          <line x1="11" y1="8" x2="11" y2="14"/>
          <line x1="8" y1="11" x2="14" y2="11"/>
        </svg>
      </button>
      <button
        class="control-btn"
        @click="$emit('zoomOut')"
        title="缩小"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          <line x1="8" y1="11" x2="14" y2="11"/>
        </svg>
      </button>
      <div class="control-divider"></div>
      <button
        class="control-btn"
        @click="$emit('reset')"
        title="重置视图"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
          <path d="M3 3v5h5"/>
        </svg>
      </button>
    </div>
    <div class="control-group">
      <button
        class="control-btn theme-btn"
        @click="$emit('toggleTheme')"
        :title="isDark ? '切换到白天模式' : '切换到暗黑模式'"
      >
        <svg v-if="isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5"/>
          <line x1="12" y1="1" x2="12" y2="3"/>
          <line x1="12" y1="21" x2="12" y2="23"/>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
          <line x1="1" y1="12" x2="3" y2="12"/>
          <line x1="21" y1="12" x2="23" y2="12"/>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
      </button>
      <button
        class="control-btn"
        :class="{ 'is-active': autoRotate }"
        @click="$emit('toggleAutoRotate')"
        title="自动旋转"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polygon points="5 3 19 12 5 21 5 3"/>
        </svg>
      </button>
    </div>
    <div class="zoom-info">
      <span class="zoom-label">缩放级别</span>
      <div class="zoom-bar">
        <div class="zoom-fill" :style="{ height: zoomLevelPercent + '%' }"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  isDark: {
    type: Boolean,
    default: false
  },
  autoRotate: {
    type: Boolean,
    default: true
  },
  zoomLevel: {
    type: Number,
    default: 1
  },
  minZoom: {
    type: Number,
    default: 1
  },
  maxZoom: {
    type: Number,
    default: 5
  }
})

defineEmits(['zoomIn', 'zoomOut', 'reset', 'toggleTheme', 'toggleAutoRotate'])

const zoomLevelPercent = computed(() => {
  const range = props.maxZoom - props.minZoom
  const level = Math.min(Math.max(props.zoomLevel - props.minZoom, 0), range)
  return (level / range) * 100
})
</script>

<style scoped>
.control-panel {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 10;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.is-dark .control-group {
  background: rgba(20, 20, 40, 0.6);
  border-color: rgba(100, 120, 200, 0.2);
}

.control-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 8px;
  color: #e0e8f0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.control-btn:hover {
  background: rgba(100, 150, 255, 0.3);
  color: #fff;
}

.control-btn:active {
  transform: scale(0.95);
}

.control-btn svg {
  width: 20px;
  height: 20px;
}

.control-btn.is-active {
  background: rgba(100, 150, 255, 0.4);
  color: #8ab4ff;
}

.control-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 4px 0;
}

.theme-btn svg {
  width: 18px;
  height: 18px;
}

.zoom-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 10px 8px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.is-dark .zoom-info {
  background: rgba(20, 20, 40, 0.6);
  border-color: rgba(100, 120, 200, 0.2);
}

.zoom-label {
  font-size: 10px;
  color: rgba(200, 210, 230, 0.7);
  letter-spacing: 1px;
}

.zoom-bar {
  width: 6px;
  height: 80px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
  position: relative;
}

.zoom-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, #4a9eff, #8ab4ff);
  border-radius: 3px;
  transition: height 0.3s ease;
}
</style>
