<template>
  <transition name="fade">
    <div v-if="visible" class="success-notification">
      <div class="success-content">
        <div class="success-icon">
          <svg viewBox="0 0 24 24" class="checkmark">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
        </div>
        <div class="success-text">
          <h3 class="success-title">操作成功!</h3>
          <p class="success-message">{{ message || `欢迎回来，${username}！即将跳转到首页...` }}</p>
        </div>
        <div class="progress-bar" :style="{ width: progress + '%' }"></div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  username: {
    type: String,
    default: '用户'
  },
  message: {
    type: String,
    default: ''
  },
  duration: {
    type: Number,
    default: 2000
  }
})

const emit = defineEmits(['close'])

const visible = ref(false)
const progress = ref(0)
let intervalId = null
let timeoutId = null

const show = () => {
  visible.value = true
  startProgress()
}

const hide = () => {
  visible.value = false
  clearInterval(intervalId)
  clearTimeout(timeoutId)
  emit('close')
}

const startProgress = () => {
  const steps = 100
  const interval = props.duration / steps
  
  intervalId = setInterval(() => {
    if (progress.value < 100) {
      progress.value += 1
    } else {
      clearInterval(intervalId)
      hide()
    }
  }, interval)
}

onMounted(() => {
  show()
})

onUnmounted(() => {
  clearInterval(intervalId)
  clearTimeout(timeoutId)
})

// 暴露方法，供父组件调用
defineExpose({
  show,
  hide
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.success-notification {
  position: fixed;
  top: 5%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
  width: 350px;
  background: var(--theme-bg-elevated);
  border-radius: 12px;
  box-shadow: var(--theme-shadow-lg);
  overflow: hidden;
  border: 1px solid var(--theme-border);
  backdrop-filter: blur(10px);
}

.success-content {
  position: relative;
  padding: 20px;
  display: flex;
  align-items: center;
}

.success-icon {
  width: 50px;
  height: 50px;
  background: var(--theme-gradient);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
  box-shadow: 0 4px 10px color-mix(in srgb, var(--theme-primary) 30%, transparent);
  animation: pulse 2s infinite;
}

.checkmark {
  width: 24px;
  height: 24px;
  fill: var(--theme-primary-text);
  stroke: var(--theme-primary-text);
  stroke-width: 2;
  animation: checkmark 0.6s ease;
}

.success-text {
  flex-grow: 1;
}

.success-title {
  margin: 0;
  color: var(--theme-text-1);
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 5px;
}

.success-message {
  margin: 0;
  color: var(--theme-text-2);
  font-size: 14px;
  line-height: 1.4;
}

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 4px;
  background: var(--theme-gradient);
  border-radius: 0 0 0 12px;
  transition: width 0.1s linear;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 color-mix(in srgb, var(--theme-primary) 40%, transparent);
  }
  70% {
    box-shadow: 0 0 0 10px color-mix(in srgb, var(--theme-primary) 0%, transparent);
  }
  100% {
    box-shadow: 0 0 0 0 color-mix(in srgb, var(--theme-primary) 0%, transparent);
  }
}

@keyframes checkmark {
  0% {
    stroke-dashoffset: 50;
    opacity: 0;
    transform: scale(0.8);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
  100% {
    stroke-dashoffset: 0;
    transform: scale(1);
  }
}
</style>