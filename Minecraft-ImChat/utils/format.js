/**
 * 时间与文案格式化工具（跨页面共享，避免各页面重复实现）
 */

const WEEKDAYS = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

function toDate(value) {
  if (!value && value !== 0) return null
  if (value instanceof Date) return value
  if (typeof value === 'number') return new Date(value)
  // 兼容 "2026-01-01 10:00:00" 这类后端格式
  const normalized = String(value).replace(/-/g, '/').replace('T', ' ').replace(/\.\d+Z?$/, '')
  const date = new Date(normalized)
  return Number.isNaN(date.getTime()) ? null : date
}

function pad(n) {
  return n < 10 ? `0${n}` : String(n)
}

function isSameDay(a, b) {
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  )
}

/**
 * 会话列表右上角时间（微信风格）
 */
export function formatListTime(value) {
  const date = toDate(value)
  if (!date) return ''
  const now = new Date()
  if (isSameDay(date, now)) return `${pad(date.getHours())}:${pad(date.getMinutes())}`

  const yesterday = new Date(now)
  yesterday.setDate(now.getDate() - 1)
  if (isSameDay(date, yesterday)) return '昨天'

  const diffDays = Math.floor((now - date) / 86400000)
  if (diffDays > 0 && diffDays < 7) return WEEKDAYS[date.getDay()]

  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
}

/**
 * 聊天区时间分隔（微信风格）
 */
export function formatChatTime(value) {
  const date = toDate(value)
  if (!date) return ''
  const now = new Date()
  const time = `${pad(date.getHours())}:${pad(date.getMinutes())}`

  if (isSameDay(date, now)) return time

  const yesterday = new Date(now)
  yesterday.setDate(now.getDate() - 1)
  if (isSameDay(date, yesterday)) return `昨天 ${time}`

  const diffDays = Math.floor((now - date) / 86400000)
  if (diffDays > 0 && diffDays < 7) return `${WEEKDAYS[date.getDay()]} ${time}`

  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} ${time}`
}

/**
 * 相对时间（好友申请等场景）
 */
export function formatRelativeTime(value) {
  const date = toDate(value)
  if (!date) return ''
  const diff = Date.now() - date.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

/**
 * 消息在会话列表中的预览文案
 */
export function previewText(message) {
  if (!message) return ''
  const type = String(message.messageType || 'text').toLowerCase()
  if (type === 'image') return '[图片]'
  if (type === 'file') return '[文件]'
  if (type === 'emoji') return '[表情]'
  if (type === 'voice') return '[语音]'
  if (type === 'video') return '[视频]'
  return message.content || ''
}

/**
 * 电话号码脱敏
 */
export function maskPhone(phone) {
  if (!phone) return ''
  return String(phone).replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}
