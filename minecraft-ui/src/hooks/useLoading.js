import { ref } from 'vue'

export function useLoading() {
  const isLoading = ref(false)
  const loadingText = ref('加载中...')
  const loadingCount = ref(0)

  function start(text = '加载中...') {
    loadingCount.value++
    loadingText.value = text
    isLoading.value = true
  }

  function stop() {
    loadingCount.value--
    if (loadingCount.value <= 0) {
      loadingCount.value = 0
      isLoading.value = false
      loadingText.value = '加载中...'
    }
  }

  function setText(text) {
    loadingText.value = text
  }

  async function withLoading(promise, text = '加载中...') {
    start(text)
    try {
      const result = await promise
      return result
    } finally {
      stop()
    }
  }

  return {
    isLoading,
    loadingText,
    loadingCount,
    start,
    stop,
    setText,
    withLoading
  }
}