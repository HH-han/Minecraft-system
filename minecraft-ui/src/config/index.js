/**
 * 应用运行时配置
 *
 * 取值优先级：
 * 1. 构建产物 dist/_app-config-{version}-{hash}.js 注入的 window.__APP_CONFIG__
 * 2. 构建期 .env 中的同名变量（import.meta.env）
 * 3. 代码内默认值
 *
 * 因此部署后只需修改 dist/_app-config-*.js 中的 VITE_GLOB_* 配置，刷新页面即可生效。
 */

const runtimeConfig = (typeof window !== 'undefined' && window.__APP_CONFIG__) || {}

const read = (key, fallback) => {
  const value = runtimeConfig[key] ?? import.meta.env[key]
  return value === undefined || value === null || value === '' ? fallback : String(value)
}

const trimTrailingSlash = (value) => String(value || '').replace(/\/+$/, '')

/** 后端服务根地址（不含 /api）；为空表示与前端同源 */
export const apiBaseUrl = trimTrailingSlash(read('VITE_GLOB_API_BASE_URL', ''))

/** 接口请求前缀：配置了后端地址时拼 /api，同源时交由网关/代理转发 */
export const apiRequestBaseUrl = apiBaseUrl ? `${apiBaseUrl}/api` : '/api'

/** WebSocket 根地址（不含 /ws） */
export const wsBaseUrl =
  trimTrailingSlash(read('VITE_GLOB_WS_URL', '').replace(/^http/, 'ws').replace(/\/ws$/, '')) ||
  apiBaseUrl.replace(/^http/, 'ws') ||
  (typeof window === 'undefined'
    ? ''
    : `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}`)
