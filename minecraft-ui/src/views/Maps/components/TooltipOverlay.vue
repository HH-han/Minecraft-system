<template>
  <Transition name="tooltip-fade">
    <div
      v-if="visible && marker"
      class="tooltip-overlay"
      :class="{ 'is-dark': isDark }"
      :style="tooltipStyle"
    >
      <div class="tooltip-dot" :style="dotStyle"></div>
      <div class="tooltip-content">
        <div class="tooltip-type">{{ marker.type === 'continent' ? '大洲' : '国家' }}</div>
        <div class="tooltip-name">{{ marker.name }}</div>
        <div class="tooltip-name-en">{{ marker.nameEn }}</div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isDark: {
    type: Boolean,
    default: false
  },
  marker: {
    type: Object,
    default: null
  },
  mouseX: {
    type: Number,
    default: 0
  },
  mouseY: {
    type: Number,
    default: 0
  }
})

const tooltipStyle = computed(() => {
  if (!props.marker) return {}
  const x = props.marker.screenX != null ? props.marker.screenX : props.mouseX
  const y = props.marker.screenY != null ? props.marker.screenY : props.mouseY
  return {
    left: `${x + 16}px`,
    top: `${y - 10}px`
  }
})

const dotStyle = computed(() => {
  if (!props.marker || !props.marker.color) return {}
  const [r, g, b] = props.marker.color
  return {
    background: `rgba(${r * 255}, ${g * 255}, ${b * 255}, 0.9)`,
    boxShadow: `0 0 8px rgba(${r * 255}, ${g * 255}, ${b * 255}, 0.5)`
  }
})
</script>

<style scoped>
.tooltip-overlay {
  position: fixed;
  z-index: 1000;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 10px;
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.12),
    0 1px 3px rgba(0, 0, 0, 0.06);
  pointer-events: none;
  transition: all 0.15s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.tooltip-overlay.is-dark {
  background: rgba(25, 28, 45, 0.92);
  border-color: rgba(100, 180, 255, 0.15);
  box-shadow:
    0 4px 20px rgba(0, 0, 0, 0.3),
    0 1px 3px rgba(0, 0, 0, 0.15);
}

.tooltip-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tooltip-content {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.tooltip-type {
  font-size: 10px;
  font-weight: 500;
  color: #8a9bb5;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.tooltip-overlay.is-dark .tooltip-type {
  color: #6a7b95;
}

.tooltip-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a2332;
  line-height: 1.3;
}

.tooltip-overlay.is-dark .tooltip-name {
  color: #e0e6f0;
}

.tooltip-name-en {
  font-size: 11px;
  font-weight: 400;
  color: #6a7b95;
  line-height: 1.2;
}

.tooltip-overlay.is-dark .tooltip-name-en {
  color: #5a6b85;
}

.tooltip-fade-enter-active,
.tooltip-fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.tooltip-fade-enter-from,
.tooltip-fade-leave-to {
  opacity: 0;
  transform: translateY(4px);
}
</style>