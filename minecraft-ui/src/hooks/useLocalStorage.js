import { ref, watch, onMounted } from 'vue'

export function useLocalStorage(key, defaultValue) {
  const storedValue = localStorage.getItem(key)
  const data = ref(storedValue ? JSON.parse(storedValue) : defaultValue)

  function setValue(newValue) {
    data.value = newValue
    localStorage.setItem(key, JSON.stringify(newValue))
  }

  function removeValue() {
    data.value = defaultValue
    localStorage.removeItem(key)
  }

  function clear() {
    localStorage.removeItem(key)
    data.value = defaultValue
  }

  watch(data, (newValue) => {
    localStorage.setItem(key, JSON.stringify(newValue))
  }, { deep: true })

  onMounted(() => {
    const stored = localStorage.getItem(key)
    if (stored) {
      try {
        data.value = JSON.parse(stored)
      } catch {
        data.value = defaultValue
      }
    }
  })

  return {
    data,
    setValue,
    removeValue,
    clear
  }
}