import { ref, watch, onUnmounted } from 'vue'

export function useThrottle(value, limit = 300) {
  const throttledValue = ref(value.value)
  let lastTime = Date.now()

  watch(value, (newValue) => {
    const now = Date.now()
    if (now - lastTime >= limit) {
      throttledValue.value = newValue
      lastTime = now
    }
  })

  return throttledValue
}

export function useThrottleFn(fn, limit = 300) {
  let lastTime = 0

  function throttledFn(...args) {
    const now = Date.now()
    if (now - lastTime >= limit) {
      fn(...args)
      lastTime = now
    }
  }

  return throttledFn
}