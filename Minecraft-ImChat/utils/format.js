/**
 * 时间与文案格式化工具（跨页面共享，避免各页面重复实现）
 */

import { t } from '../locales/index.js'

function weekdayName(index) {
  return t(`common.weekday${index}`)
}

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
  if (isSameDay(date, yesterday)) return t('common.yesterday')

  const diffDays = Math.floor((now - date) / 86400000)
  if (diffDays > 0 && diffDays < 7) return weekdayName(date.getDay())

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
  if (isSameDay(date, yesterday)) return `${t('common.yesterday')} ${time}`

  const diffDays = Math.floor((now - date) / 86400000)
  if (diffDays > 0 && diffDays < 7) return `${weekdayName(date.getDay())} ${time}`

  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} ${time}`
}

/**
 * 相对时间（好友申请等场景）
 */
export function formatRelativeTime(value) {
  const date = toDate(value)
  if (!date) return ''
  const diff = Date.now() - date.getTime()
  if (diff < 60000) return t('common.justNow')
  if (diff < 3600000) return t('common.minutesAgo', { count: Math.floor(diff / 60000) })
  if (diff < 86400000) return t('common.hoursAgo', { count: Math.floor(diff / 3600000) })
  if (diff < 604800000) return t('common.daysAgo', { count: Math.floor(diff / 86400000) })
  return t('common.monthDay', { month: date.getMonth() + 1, day: date.getDate() })
}

/**
 * 消息在会话列表中的预览文案
 */
export function previewText(message) {
  if (!message) return ''
  const type = String(message.messageType || 'text').toLowerCase()
  if (type === 'image') return t('common.previewImage')
  if (type === 'file') return t('common.previewFile')
  if (type === 'emoji') return t('common.previewEmoji')
  if (type === 'voice') return t('common.previewVoice')
  if (type === 'video') return t('common.previewVideo')
  return message.content || ''
}

/**
 * 电话号码脱敏
 */
export function maskPhone(phone) {
  if (!phone) return ''
  return String(phone).replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}
