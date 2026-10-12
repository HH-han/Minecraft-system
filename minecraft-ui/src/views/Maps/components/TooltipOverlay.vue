<template>
  <Transition name="tooltip-fade">
    <div
      v-if="visible && marker"
      class="tooltip-overlay"
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
  background: var(--theme-bg-glass-strong);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 10px;
  box-shadow: var(--theme-shadow-md);
  pointer-events: none;
  transition: all 0.15s cubic-bezier(0.16, 1, 0.3, 1);
  border: 1px solid var(--theme-border);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.tooltip-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tooltip-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tooltip-type {
  font-size: 10px;
  font-weight: 500;
  color: var(--theme-text-3);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  line-height: 1;
}

.tooltip-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-1);
  line-height: 1.3;
  letter-spacing: -0.1px;
}

.tooltip-name-en {
  font-size: 11px;
  font-weight: 400;
  color: var(--theme-text-2);
  line-height: 1.2;
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
