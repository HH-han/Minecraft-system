<template>
  <div v-if="visible && letters.length" class="index-bar">
    <span
      v-for="letter in letters"
      :key="letter"
      :class="['index-letter', { active: activeLetter === letter }]"
      :data-letter="letter"
      @pointerdown="emit('index-down', $event, letter)"
      >{{ letter }}</span
    >
  </div>

  <!-- 定位气泡 -->
  <transition name="bubble">
    <div v-if="bubbleLetter" class="index-bubble">{{ bubbleLetter }}</div>
  </transition>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  letters: {
    type: Array,
    default: () => []
  },
  activeLetter: {
    type: String,
    default: ''
  },
  bubbleLetter: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['index-down'])
</script>

<style scoped>
/* ---- A-Z 字母索引条 ---- */
.index-bar {
  position: absolute;
  top: 50%;
  right: 2px;
  transform: translateY(-50%);
  z-index: 6;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  padding: 5px 3px;
  border-radius: 10px;
  background: var(--theme-bg-glass);
  backdrop-filter: blur(4px);
  touch-action: none;
  user-select: none;
}

.index-letter {
  width: 16px;
  height: 14px;
  line-height: 14px;
  text-align: center;
  font-size: 10px;
  font-weight: 500;
  color: var(--theme-text-2);
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.12s ease, color 0.12s ease;
}

.index-letter.active {
  background: var(--theme-primary);
  color: var(--theme-primary-text);
}

/* ---- 定位气泡 ---- */
.index-bubble {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 20;
  width: 64px;
  height: 64px;
  border-radius: 14px;
  background: var(--theme-overlay);
  color: #ffffff;
  font-size: 30px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.bubble-enter-active,
.bubble-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.bubble-enter-from,
.bubble-leave-to {
  opacity: 0;
  transform: translate(-50%, -50%) scale(0.85);
}
</style>
