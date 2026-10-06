import { ElNotification } from 'element-plus'

let eventSource = null
let reconnectTimer = null

/**
 * 初始化公告 SSE 实时推送（登录后调用）
 * 断线 10s 后自动重连
 */
export function initAnnouncementSse(onAnnouncement) {
  const token = localStorage.getItem('token')
  if (!token) return
  if (eventSource) eventSource.close()
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }

  const base = import.meta.env.VITE_API_BASE_URL || ''
  eventSource = new EventSource(
    `${base}/api/announcements/sse?token=${encodeURIComponent(token)}`
  )

  eventSource.addEventListener('announcement', (e) => {
    try {
      const ann = JSON.parse(e.data)
      onAnnouncement && onAnnouncement(ann)
      ElNotification({
        title: ann.title,
        message: ann.summary || '有新公告',
        type: 'info',
        duration: 5000
      })
    } catch (err) {
      console.warn('SSE 消息解析失败:', err)
    }
  })

  eventSource.onerror = () => {
    closeAnnouncementSse()
    reconnectTimer = setTimeout(() => initAnnouncementSse(onAnnouncement), 10000)
  }
}

export function closeAnnouncementSse() {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}
