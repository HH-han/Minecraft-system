import axios from 'axios'
import { getToken, removeToken } from './storage'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 直接返回响应数据，由调用方处理
    return res
  },
  error => {
    uni.showToast({
      title: error.message || '网络错误',
      icon: 'none',
      duration: 5000
    })
    return Promise.reject(error)
  }
)

export default request
