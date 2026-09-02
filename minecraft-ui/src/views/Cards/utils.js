/**
 * 旅行卡片展示工具函数
 */

const BACKEND_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

/**
 * 规范化图片 URL：
 * - data: / http(s): 原样返回
 * - 相对路径拼接后端地址
 */
export function normalizeImageUrl(url) {
  if (!url) return ''
  const raw = String(url).trim()
  if (!raw || raw === 'null' || raw === 'undefined') return ''
  if (/^data:/i.test(raw)) return raw
  if (/^https?:\/\//i.test(raw)) return raw
  if (raw.startsWith('/')) return BACKEND_BASE + raw
  return `${BACKEND_BASE}/${raw}`
}

/**
 * 解析卡片图片列表：
 * 优先使用后端已解析的 imageList，兼容 JSON 数组字符串与逗号分隔字符串
 */
export function parseImageList(card) {
  if (!card) return []
  if (Array.isArray(card.imageList)) return card.imageList
  const raw = card.images
  if (!raw) return []
  const str = String(raw).trim()
  if (!str) return []
  if (str.startsWith('[')) {
    try {
      const parsed = JSON.parse(str)
      if (Array.isArray(parsed)) {
        return parsed.map((item) => String(item).trim()).filter(Boolean)
      }
    } catch {
      // JSON 解析失败时回退为逗号分隔解析
    }
  }
  return str
    .split(',')
    .map((item) => item.trim().replaceAll('`', '').replaceAll('"', ''))
    .filter(Boolean)
}

/**
 * 解析标签名：优先使用后端关联表标签名，回退 tags 逗号分隔字段
 */
export function parseTagNames(card) {
  if (!card) return []
  if (Array.isArray(card.tagNames) && card.tagNames.length > 0) return card.tagNames
  if (!card.tags) return []
  return String(card.tags)
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)
}

/**
 * 格式化时间：2026-09-02T12:00:00 → 2026-09-02 12:00
 */
export function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

/**
 * 卡片状态元信息：1-正常，0-删除，2-审核中
 */
export function statusMeta(status) {
  if (Number(status) === 0) {
    return { key: 'deleted', text: '已删除', color: '#6e6e73', bg: 'rgba(110,110,115,0.12)' }
  }
  if (Number(status) === 2) {
    return { key: 'reviewing', text: '审核中', color: '#b25000', bg: 'rgba(255,159,10,0.16)' }
  }
  return { key: 'normal', text: '正常', color: '#1d7d3f', bg: 'rgba(52,199,89,0.16)' }
}
