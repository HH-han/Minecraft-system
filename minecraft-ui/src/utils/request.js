import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 存储pending中的请求
const pendingMap = new Map()

// 生成请求唯一标识
const getRequestKey = (config) => {
  const { method, url, params, data } = config
  return [method, url, JSON.stringify(params), JSON.stringify(data)].join('&')
}

// 添加pending请求
const addPending = (config) => {
  const key = getRequestKey(config)
  if (pendingMap.has(key)) {
    const cancel = pendingMap.get(key)
    cancel(key) // 取消上一个相同请求
    pendingMap.delete(key)
  }
  config.cancelToken = new axios.CancelToken((cancel) => {
    pendingMap.set(key, cancel)
  })
}

// 移除pending请求
const removePending = (config) => {
  const key = getRequestKey(config)
  if (pendingMap.has(key)) {
    const cancel = pendingMap.get(key)
    cancel(key)
    pendingMap.delete(key)
  }
}

// 跳转到错误页面
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
    router.push(path).catch(() => {})
  }
}

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 重复请求取消前一个
    addPending(config)
    
    // 注入Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 根据请求类型设置Content-Type
    if (config.method === 'post' && !config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 请求完成，移除pending记录
    removePending(response.config)
    
    // 直接返回响应数据，由调用方处理
    return response.data
  },
  async (error) => {
    // 被取消的请求不做错误提示
    if (axios.isCancel(error)) {
      console.warn('请求已取消:', error.message)
      return Promise.reject(error)
    }
    
    // 移除pending记录
    error.config && removePending(error.config)
    
    // 超时重试逻辑
    const config = error.config
    if (error.code === 'ECONNABORTED' && config && !config._retry) {
      config._retry = true
      return request(config) // 重试一次
    }
    
    // 统一错误提示
    const status = error.response?.status
    const msg = error.response?.data?.message || '网络异常'
    
    switch (status) {
      case 400:
        ElMessage.error('请求参数错误')
        redirectToErrorPage(400)
        break
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        redirectToErrorPage(401)
        break
      case 403:
        ElMessage.error('没有权限访问')
        redirectToErrorPage(403)
        break
      case 404:
        ElMessage.error('请求资源不存在')
        redirectToErrorPage(404)
        break
      case 500:
        ElMessage.error('服务器内部错误')
        redirectToErrorPage(500)
        break
      default:
        if (!error.config?.silent) {
          ElMessage.error(msg)
        }
    }
    
    return Promise.reject(error)
  }
)

// 扩展请求方法，支持静默错误
export const http = {
  get: (url, params, config = {}) => request.get(url, { params, ...config }),
  post: (url, data, config = {}) => request.post(url, data, config),
  put: (url, data, config = {}) => request.put(url, data, config),
  delete: (url, config = {}) => request.delete(url, config),
  // 静默请求（不自动弹错误）
  silent: {
    get: (url, params) => request.get(url, { params, silent: true }),
    post: (url, data) => request.post(url, data, { silent: true })
  }
}

export default request