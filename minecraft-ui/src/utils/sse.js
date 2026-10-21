import { ElNotification } from 'element-plus'
import { apiBaseUrl } from '@/config'
import { useRefreshStore } from '@/stores/refreshStore'

/**
 * 统一实时推送客户端（SSE 长连接）
 * <p>
 * 单条连接同时承载两类事件：
 * - data-change：服务端有写操作（新增/修改/删除）后广播，驱动页面自动刷新
 * - announcement：系统公告实时推送
 * <p>
 * 匿名也允许连接（公共页面无需登录即可实时刷新），token 可选用于标识身份。
 * 断线 10s 后自动重连。
 */
let eventSource = null
let reconnectTimer = null

function buildUrl() {
  const token = localStorage.getItem('token')
  const query = token ? `?token=${encodeURIComponent(token)}` : ''
  return `${apiBaseUrl}/api/realtime/sse${query}`
}

/**
 * 初始化实时推送
 * @param {Object} [handlers]
 * @param {(announcement:any)=>void} [handlers.onAnnouncement] 收到新公告时回调
 * @param {(event:any)=>void} [handlers.onDataChange] 收到数据变更时回调
 */
export function initRealtimeSse(handlers = {}) {
  if (typeof EventSource === 'undefined') return

  // 避免重复连接
  closeRealtimeSse()

  eventSource = new EventSource(buildUrl())

  // 数据变更 → 通知刷新总线，页面自动重新拉取
  eventSource.addEventListener('data-change', (e) => {
    try {
      const payload = JSON.parse(e.data)
      useRefreshStore().notify(payload)
      handlers.onDataChange && handlers.onDataChange(payload)
    } catch (err) {
      console.warn('SSE 数据变更解析失败:', err)
    }
  })

  // 新公告推送
  eventSource.addEventListener('announcement', (e) => {
    try {
      const ann = JSON.parse(e.data)
      handlers.onAnnouncement && handlers.onAnnouncement(ann)
      ElNotification({
        title: ann.title,
        message: ann.summary || '有新公告',
        type: 'info',
        duration: 5000
      })
    } catch (err) {
      console.warn('SSE 公告解析失败:', err)
    }
  })

  eventSource.onerror = () => {
    closeRealtimeSse()
    reconnectTimer = setTimeout(() => initRealtimeSse(handlers), 10000)
  }
}

export function closeRealtimeSse() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}
