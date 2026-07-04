import { ref, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'

export function useFetch(url, options = {}) {
  const data = ref(null)
  const error = ref(null)
  const isLoading = ref(false)
  const response = ref(null)
  let controller = null

  const {
    method = 'GET',
    params = {},
    body = null,
    immediate = true,
    onSuccess = null,
    onError = null,
    transform = null
  } = options

  async function fetchData(customParams = {}, customBody = null) {
    if (controller) {
      controller.abort()
    }

    controller = new AbortController()
    isLoading.value = true
    error.value = null

    try {
      const requestConfig = {
        method,
        signal: controller.signal
      }

      if (method.toUpperCase() === 'GET') {
        requestConfig.params = { ...params, ...customParams }
      } else {
        requestConfig.data = customBody !== null ? customBody : body
        if (customParams && Object.keys(customParams).length > 0) {
          requestConfig.params = { ...params, ...customParams }
        }
      }

      const result = await request(url, requestConfig)
      response.value = result

      if (transform) {
        data.value = transform(result)
      } else {
        data.value = result.data || result
      }

      if (onSuccess) {
        onSuccess(data.value, result)
      }

      return data.value
    } catch (err) {
      if (err.name !== 'AbortError') {
        error.value = err.message || '请求失败'
        if (onError) {
          onError(err)
        }
        throw err
      }
    } finally {
      isLoading.value = false
    }
  }

  function cancel() {
    if (controller) {
      controller.abort()
    }
  }

  onMounted(() => {
    if (immediate) {
      fetchData()
    }
  })

  onUnmounted(() => {
    cancel()
  })

  return {
    data,
    error,
    isLoading,
    response,
    fetchData,
    cancel
  }
}

export function useGet(url, options = {}) {
  return useFetch(url, { ...options, method: 'GET' })
}

export function usePost(url, options = {}) {
  return useFetch(url, { ...options, method: 'POST', immediate: false })
}