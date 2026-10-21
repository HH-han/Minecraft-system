import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { apiRequestBaseUrl } from '@/config'
import { useRefreshStore } from '@/stores/refreshStore'

/**
 * 写操作成功后自动通知「刷新总线」，驱动当前页面重新拉取数据
 * （解决「用户自己发起请求后仍要手动刷新」的问题；服务端 SSE 广播负责其他端/其他人的改动）
 *
 * 以下写操作不触发刷新：登录/验证码/上传/监控/日志等无关请求，以及浏览量/已读/曝光类高频写操作
 */
const NO_REFRESH_PREFIXES = [
  '/auth',
  '/captcha',
  '/upload',
  '/realtime',
  '/server-monitor',
  '/cache',
  '/token',
  '/online-users',
  '/operation-logs',
  '/system-logs',
  '/login-log',
  '/recommendations/exposure',
  '/ai/trip'
]

const NO_REFRESH_CONTAINS = ['/read', '/exposure', '/view', '/typing', '/heartbeat']

const shouldNotifyRefresh = (url = '') => {
  const path = String(url)
  if (NO_REFRESH_PREFIXES.some((prefix) => path.startsWith(prefix))) return false
  if (NO_REFRESH_CONTAINS.some((keyword) => path.includes(keyword))) return false
  return true
}

/**
 * 请求去重：
 * 相同「method + url + params + data」的请求在 pending 期间只真正发出一次，
 * 后续重复请求直接复用同一个 Promise。
 *  - 不再"取消上一个请求"，从根源消除 CanceledError 报错
 *  - 天然防止重复提交（双击按钮、onMounted 重复触发、HMR 重挂载等）
 *  - config.dedup === false 可跳过去重
 *  - 携带自定义 signal / cancelToken 的请求不去重（交给调用方控制）
 */
const pendingMap = new Map()

// 序列化参数/请求体（FormData / URLSearchParams 按内容序列化，避免误判为相同请求）
const serialize = (data) => {
  if (data == null || data === '') return ''
  if (typeof data === 'string') return data
  if (typeof FormData !== 'undefined' && data instanceof FormData) {
    const parts = []
    data.forEach((value, key) => {
      const v =
        typeof Blob !== 'undefined' && value instanceof Blob
          ? `${value.name || value.type}:${value.size}`
          : String(value)
      parts.push(`${key}=${v}`)
    })
    return `FormData[${parts.sort().join('&')}]`
  }
  if (typeof URLSearchParams !== 'undefined' && data instanceof URLSearchParams) {
    return `URLSearchParams[${data.toString()}]`
  }
  try {
    return JSON.stringify(data)
  } catch {
    return String(data)
  }
}

// 生成请求唯一标识
const getRequestKey = (config) => {
  const method = String(config.method || 'get').toLowerCase()
  return [method, config.url || '', serialize(config.params), serialize(config.data)].join('&')
}

// 是否允许去重
const canDedup = (config) =>
  config.dedup !== false && !config.signal && !config.cancelToken

// 发送请求（核心：带去重）
const sendRequest = (config) => {
  if (!canDedup(config)) {
    return instance(config)
  }
  const key = getRequestKey(config)
  // 复用进行中的相同请求
  if (pendingMap.has(key)) {
    return pendingMap.get(key)
  }
  try {
    // 请求结束（无论成功失败）后清除记录，之后的相同请求会重新发起
    const promise = instance(config).finally(() => pendingMap.delete(key))
    pendingMap.set(key, promise)
    return promise
  } catch (err) {
    pendingMap.delete(key)
    return Promise.reject(err)
  }
}

// 跳转到错误页面（携带来源路径，便于错误页展示）
const redirectToErrorPage = (status) => {
  const errorPages = {
    400: '/400',
    401: '/401',
    403: '/403',
    404: '/404',
    500: '/500',
    502: '/502',
    503: '/503',
    504: '/504'
  }
  const path = errorPages[status]
  if (path) {
    router
      .push({ path, query: { from: window.location.pathname } })
      .catch(() => {})
  }
}

// 创建axios实例
const instance = axios.create({
  baseURL: apiRequestBaseUrl,
  timeout: 15000
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 注入Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 根据请求类型设置Content-Type
    const method = String(config.method || 'get').toLowerCase()
    if (['post', 'put', 'patch'].includes(method) && !config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }

    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 写操作成功后通知刷新总线：让页面自动重新拉取数据
    const method = String(response.config?.method || 'get').toLowerCase()
    const body = response.data
    const bizOk = !body || body.code === undefined || body.code === 200
    if (bizOk && ['post', 'put', 'patch', 'delete'].includes(method)) {
      try {
        if (shouldNotifyRefresh(response.config?.url)) {
          useRefreshStore().notify()
        }
      } catch {
        // Pinia 尚未就绪（如应用初始化阶段）时忽略
      }
    }
    // 直接返回响应数据，由调用方处理
    return response.data
  },
  async (error) => {
    // 被取消的请求（调用方主动 abort）不做错误提示
    if (axios.isCancel(error)) {
      if (import.meta.env.DEV) {
        console.warn('请求已取消:', error.message)
      }
      return Promise.reject(error)
    }

    const config = error.config

    // 超时自动重试一次（直接走实例，避免与去重 Map 形成循环等待）
    if (error.code === 'ECONNABORTED' && config && !config._retry) {
      config._retry = true
      return instance(config)
    }

    const status = error.response?.status
    const url = config?.url || ''

    // 对于 /user/info 请求的 401/403 错误，不显示提示也不跳转页面
    const isUserInfoRequest = url.includes('/user/info')
    const isAuthError = status === 401 || status === 403
    if (isUserInfoRequest && isAuthError) {
      // 静默处理，让调用方自己处理
      return Promise.reject(error)
    }

    // 统一错误提示（silent 请求不弹任何提示）
    const showError = (msg) => {
      if (!config?.silent) {
        ElMessage.error(msg)
      }
    }
    const serverMsg = error.response?.data?.message
    const defaultMsg =
      error.code === 'ERR_NETWORK'
        ? '网络连接异常，请检查网络'
        : error.code === 'ECONNABORTED'
          ? '请求超时，请稍后重试'
          : '网络异常'

    switch (status) {
      case 400:
        showError('请求参数错误')
        redirectToErrorPage(400)
        break
      case 401:
        showError('登录已过期，请重新登录')
        redirectToErrorPage(401)
        break
      case 403:
        showError('没有权限访问')
        redirectToErrorPage(403)
        break
      case 404:
        showError('请求资源不存在')
        redirectToErrorPage(404)
        break
      case 500:
        showError('服务器内部错误')
        redirectToErrorPage(500)
        break
      case 502:
        showError('网关错误')
        redirectToErrorPage(502)
        break
      case 503:
        showError('服务暂不可用，请稍后重试')
        redirectToErrorPage(503)
        break
      case 504:
        showError('网关超时')
        redirectToErrorPage(504)
        break
      default:
        showError(serverMsg || defaultMsg)
    }

    return Promise.reject(error)
  }
)

/**
 * 对外暴露的请求方法：
 * 用法与 axios 实例一致（request.get / request.post / request(url, config)），
 * 仅在此基础上增加 pending 期间的请求去重。
 */
const request = (urlOrConfig, maybeConfig) => {
  const config =
    typeof urlOrConfig === 'string'
      ? { ...(maybeConfig || {}), url: urlOrConfig }
      : urlOrConfig
  return sendRequest(config)
}

request.request = (config) => sendRequest(config)
request.get = (url, config = {}) => sendRequest({ ...config, url, method: 'get' })
request.delete = (url, config = {}) => sendRequest({ ...config, url, method: 'delete' })
request.head = (url, config = {}) => sendRequest({ ...config, url, method: 'head' })
request.options = (url, config = {}) => sendRequest({ ...config, url, method: 'options' })
request.post = (url, data, config = {}) => sendRequest({ ...config, url, data, method: 'post' })
request.put = (url, data, config = {}) => sendRequest({ ...config, url, data, method: 'put' })
request.patch = (url, data, config = {}) => sendRequest({ ...config, url, data, method: 'patch' })

// 透出 axios 实例能力
request.interceptors = instance.interceptors
request.defaults = instance.defaults
request.getUri = (config) => instance.getUri(config)

// 扩展请求方法，支持静默错误
export const http = {
  get: (url, params, config = {}) => request.get(url, { params, ...config }),
  post: (url, data, config = {}) => request.post(url, data, config),
  put: (url, data, config = {}) => request.put(url, data, config),
  delete: (url, config = {}) => request.delete(url, config),
  // 静默请求（不自动弹错误）
  silent: {
    get: (url, params) => request.get(url, { params, silent: true }),
    post: (url, data) => request.post(url, data, { silent: true }),
    put: (url, data) => request.put(url, data, { silent: true }),
    delete: (url, config = {}) => request.delete(url, { ...config, silent: true })
  }
}

export default request
