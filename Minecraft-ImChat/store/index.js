/**
 * 轻量全局状态（对齐 PC 端 index.vue 的“数据中枢”角色）
 *
 * 采用 Vue3 reactive 单例，无需引入 Pinia（本工程为 HBuilderX 工程，无 npm 依赖）。
 * 负责跨页面共享：当前用户、会话摘要/未读、星标好友、待处理好友申请、设置项。
 */

import { reactive } from 'vue'

const CONVERSATIONS_KEY = 'im-conversations'
const STARRED_KEY = 'im-starred-friends'
const SETTINGS_KEY = 'im-settings'

const DEFAULT_SETTINGS = {
  notification: true,
  sound: true,
  vibration: true,
  // light | dark
  theme: 'dark'
}

export const THEMES = ['light', 'dark']

function readStorage(key, fallback) {
  try {
    const value = uni.getStorageSync(key)
    return value === '' || value === undefined || value === null ? fallback : value
  } catch (e) {
    return fallback
  }
}

function writeStorage(key, value) {
  try {
    uni.setStorageSync(key, value)
  } catch (e) {
    // 忽略存储异常（容量/权限）
  }
}

const state = reactive({
  userInfo: null,
  // key: `single-{id}` | `group-{id}` -> { lastMessage, lastTime, unread, isTop }
  conversations: readStorage(CONVERSATIONS_KEY, {}),
  starredIds: readStorage(STARRED_KEY, []),
  pendingRequestCount: 0,
  settings: { ...DEFAULT_SETTINGS, ...readStorage(SETTINGS_KEY, {}) }
})

/**
 * 会话缓存 key
 */
export function convKey(type, id) {
  return `${type}-${id}`
}

/**
 * 设置当前用户
 */
export function setUser(userInfo) {
  state.userInfo = userInfo || null
}

/**
 * 读取当前登录用户 id
 */
export function currentUserId() {
  return state.userInfo ? state.userInfo.id : null
}

/**
 * 更新会话摘要（最后一条消息 / 时间 / 置顶）
 */
export function setSummary(type, id, patch = {}) {
  const key = convKey(type, id)
  const prev = state.conversations[key] || { unread: 0, isTop: false }
  state.conversations[key] = { ...prev, ...patch }
  writeStorage(CONVERSATIONS_KEY, state.conversations)
}

/**
 * 未读数 +1
 */
export function bumpUnread(type, id) {
  const key = convKey(type, id)
  const prev = state.conversations[key] || { unread: 0, isTop: false }
  state.conversations[key] = { ...prev, unread: (prev.unread || 0) + 1 }
  writeStorage(CONVERSATIONS_KEY, state.conversations)
  syncBadge()
}

/**
 * 清空指定会话未读
 */
export function clearUnread(type, id) {
  const key = convKey(type, id)
  const prev = state.conversations[key]
  if (!prev || !prev.unread) return
  state.conversations[key] = { ...prev, unread: 0 }
  writeStorage(CONVERSATIONS_KEY, state.conversations)
  syncBadge()
}

/**
 * 读取指定会话未读数
 */
export function getUnread(type, id) {
  const conv = state.conversations[convKey(type, id)]
  return conv ? conv.unread || 0 : 0
}

/**
 * 全部会话未读总数
 */
export function totalUnread() {
  return Object.values(state.conversations).reduce((sum, conv) => sum + (conv.unread || 0), 0)
}

/**
 * 切换会话置顶
 */
export function toggleTop(type, id) {
  const key = convKey(type, id)
  const prev = state.conversations[key] || { unread: 0 }
  state.conversations[key] = { ...prev, isTop: !prev.isTop }
  writeStorage(CONVERSATIONS_KEY, state.conversations)
}

/**
 * 是否置顶
 */
export function isTop(type, id) {
  const conv = state.conversations[convKey(type, id)]
  return !!(conv && conv.isTop)
}

/**
 * 删除会话（清空聊天记录时同步移除缓存）
 */
export function removeConversation(type, id) {
  const key = convKey(type, id)
  delete state.conversations[key]
  writeStorage(CONVERSATIONS_KEY, state.conversations)
  syncBadge()
}

/**
 * 星标好友相关
 */
export function isStarred(id) {
  return state.starredIds.map(String).includes(String(id))
}

export function toggleStar(id) {
  const target = String(id)
  const exists = state.starredIds.map(String).indexOf(target)
  if (exists > -1) {
    state.starredIds.splice(exists, 1)
  } else {
    state.starredIds.push(target)
  }
  writeStorage(STARRED_KEY, state.starredIds)
  return isStarred(id)
}

/**
 * 待处理好友申请数
 */
export function setPendingRequestCount(count) {
  state.pendingRequestCount = count || 0
}

/**
 * 设置项持久化
 */
export function saveSettings(patch = {}) {
  state.settings = { ...state.settings, ...patch }
  writeStorage(SETTINGS_KEY, state.settings)
}

/**
 * 切换主题（light | dark）
 */
export function setTheme(theme) {
  if (THEMES.indexOf(theme) === -1) return
  saveSettings({ theme })
}

/**
 * 同步底部 tabBar 未读角标（微信 tab 为 index 0）
 *
 * 注意：tabBar 相关 API 仅在 tabBar 页面可用，H5 下非 tabBar 页面调用会返回
 * 失败的 Promise（removeTabBarBadge:fail not TabBar page），因此这里做两层保护：
 * 1. 仅在当前页面属于 tabBar 页面时才调用；
 * 2. 统一吞掉失败/异常，避免未捕获的 Promise 拒绝。
 */
const TAB_ROUTES = [
  'pages/index/index',
  'pages/contacts/contacts',
  'pages/discover/discover',
  'pages/profile/profile'
]

function safeCall(fn, options) {
  try {
    const result = fn(options)
    if (result && typeof result.catch === 'function') {
      result.catch(() => {})
    }
  } catch (e) {
    // 忽略
  }
}

function isTabBarPage() {
  try {
    const pages = getCurrentPages()
    if (!pages || !pages.length) return false
    const route = pages[pages.length - 1].route || ''
    return TAB_ROUTES.indexOf(route) > -1
  } catch (e) {
    return false
  }
}

export function syncBadge() {
  if (typeof uni.setTabBarBadge !== 'function' || !isTabBarPage()) return
  const total = totalUnread()
  if (total > 0) {
    safeCall(uni.setTabBarBadge, { index: 0, text: total > 99 ? '99+' : String(total), fail: () => {} })
  } else if (typeof uni.removeTabBarBadge === 'function') {
    safeCall(uni.removeTabBarBadge, { index: 0, fail: () => {} })
  }
}

/**
 * 退出登录时重置
 */
export function reset() {
  state.userInfo = null
  state.conversations = {}
  state.pendingRequestCount = 0
  writeStorage(CONVERSATIONS_KEY, {})
  if (typeof uni.removeTabBarBadge === 'function' && isTabBarPage()) {
    safeCall(uni.removeTabBarBadge, { index: 0, fail: () => {} })
  }
}

export default state
