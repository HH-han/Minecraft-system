/**
 * 铁路票务模块共享工具
 */

/** 席别展示顺序 */
export const SEAT_TYPES = ['SECOND_CLASS', 'FIRST_CLASS', 'SLEEPER']

/** 席别 i18n key */
export const seatLabelKey = (seatType) => `railway.list.seat.${seatType}`

/** 车次类型 i18n key */
export const trainTypeLabelKey = (trainType) => `railway.list.type.${trainType}`

/** 分 → 元（去掉多余的小数 0） */
export const formatPrice = (cents) => {
  const yuan = Number(cents || 0) / 100
  return Number.isInteger(yuan) ? String(yuan) : yuan.toFixed(2)
}

/** 分钟 → 历时文案 */
export const formatDuration = (minutes, t) => {
  const total = Number(minutes || 0)
  const h = Math.floor(total / 60)
  const m = total % 60
  if (h === 0) return t('railway.common.minutes', { n: m })
  if (m === 0) return t('railway.common.hours', { n: h })
  return t('railway.common.hoursMinutes', { h, m })
}

/** 把 'yyyy-MM-dd HH:mm:ss' 解析为本地时间 */
export const parseDateTime = (value) => {
  if (!value) return null
  const normalized = String(value).replace(' ', 'T')
  const date = new Date(normalized)
  return Number.isNaN(date.getTime()) ? null : date
}

/** 订单剩余支付秒数 */
export const remainingSeconds = (expireTime) => {
  const expire = parseDateTime(expireTime)
  if (!expire) return 0
  return Math.max(0, Math.floor((expire.getTime() - Date.now()) / 1000))
}

/** 秒 → mm:ss */
export const formatClock = (seconds) => {
  const total = Math.max(0, Number(seconds || 0))
  const m = Math.floor(total / 60)
  const s = total % 60
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

/** 是否已登录（与全站 token 判定保持一致） */
export const hasLogin = () => {
  const token = localStorage.getItem('token')
  if (!token || token === 'anonymousUser') return false
  const timestamp = Number(localStorage.getItem('tokenTimestamp'))
  if (timestamp && Date.now() - timestamp > 24 * 60 * 60 * 1000) return false
  return true
}

/** 今日日期（yyyy-MM-dd） */
export const todayString = () => {
  const now = new Date()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  return `${now.getFullYear()}-${m}-${d}`
}

/** 日期偏移（yyyy-MM-dd） */
export const offsetDate = (days) => {
  const date = new Date()
  date.setDate(date.getDate() + Number(days || 0))
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${m}-${d}`
}

/** 星期文案（跟随语言） */
export const weekdayLabel = (dateStr, locale) => {
  if (!dateStr) return ''
  const date = new Date(`${dateStr}T00:00:00`)
  if (Number.isNaN(date.getTime())) return ''
  return new Intl.DateTimeFormat(locale === 'en-US' ? 'en-US' : 'zh-CN', {
    weekday: 'short',
  }).format(date)
}

/** 从 query 中读取字符串参数 */
export const queryString = (value, fallback = '') => {
  if (Array.isArray(value)) return value[0] ?? fallback
  return value ?? fallback
}
