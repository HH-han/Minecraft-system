import request from '@/utils/request'

/**
 * 服务器监控相关接口
 */

// 获取服务器性能监控数据
export const getServerMonitor = () => {
  return request.get('/api/server-monitor')
}

// 获取服务器历史监控数据
export const getServerMonitorHistory = (params) => {
  return request.get('/api/server-monitor/history', { params })
}

// 获取服务器状态
export const getServerStatus = () => {
  return request.get('/api/server-monitor/status')
}
