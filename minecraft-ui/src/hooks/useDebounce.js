import { ref, watch, onUnmounted } from 'vue'

export function useDebounce(value, delay = 300) {
  const debouncedValue = ref(value.value)
  let timer = null

  watch(value, (newValue) => {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  onUnmounted(() => {
    if (timer) {
      clearTimeout(timer)
    }
  })

  return debouncedValue
}

export function useDebounceFn(fn, delay = 300) {
  let timer = null

  function debouncedFn(...args) {
    if (timer) {
      clearTimeout(timer)
    }
    timer = setTimeout(() => {
      fn(...args)
    }, delay)
  }

  function cancel() {
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
  }

  return {
    debouncedFn,
    cancel
  }
}