/**
 * 轻量国际化
 *
 * 方案与 minecraft-ui 保持一致：按语言 + 业务模块拆分词条。
 * 目录结构：
 *   locales/langs/<locale>/<namespace>.js
 * 页面内通过全局 mixin 注入的 this.$t('namespace.key') 使用。
 *
 * 新增模块时，在下方 import 并在 dictionaries 中注册即可。
 */

import { reactive } from 'vue'

// 简体中文
import zhCommon from './langs/zh-CN/common.js'
import zhTabs from './langs/zh-CN/tabs.js'
import zhIndex from './langs/zh-CN/index.js'
import zhChat from './langs/zh-CN/chat.js'
import zhSettings from './langs/zh-CN/settings.js'
import zhContacts from './langs/zh-CN/contacts.js'
import zhContactDetail from './langs/zh-CN/contactDetail.js'
import zhAddFriend from './langs/zh-CN/addFriend.js'
import zhFriendRequests from './langs/zh-CN/friendRequests.js'
import zhCreateGroup from './langs/zh-CN/createGroup.js'
import zhChatInfo from './langs/zh-CN/chatInfo.js'
import zhDiscover from './langs/zh-CN/discover.js'
import zhProfile from './langs/zh-CN/profile.js'
import zhProfileEdit from './langs/zh-CN/profileEdit.js'
import zhFavorites from './langs/zh-CN/favorites.js'
import zhMoments from './langs/zh-CN/moments.js'
import zhAuth from './langs/zh-CN/auth.js'

// English
import enCommon from './langs/en-US/common.js'
import enTabs from './langs/en-US/tabs.js'
import enIndex from './langs/en-US/index.js'
import enChat from './langs/en-US/chat.js'
import enSettings from './langs/en-US/settings.js'
import enContacts from './langs/en-US/contacts.js'
import enContactDetail from './langs/en-US/contactDetail.js'
import enAddFriend from './langs/en-US/addFriend.js'
import enFriendRequests from './langs/en-US/friendRequests.js'
import enCreateGroup from './langs/en-US/createGroup.js'
import enChatInfo from './langs/en-US/chatInfo.js'
import enDiscover from './langs/en-US/discover.js'
import enProfile from './langs/en-US/profile.js'
import enProfileEdit from './langs/en-US/profileEdit.js'
import enFavorites from './langs/en-US/favorites.js'
import enMoments from './langs/en-US/moments.js'
import enAuth from './langs/en-US/auth.js'

const STORAGE_KEY = 'im-locale'
const DEFAULT_LOCALE = 'zh-CN'
const FALLBACK_LOCALE = 'zh-CN'

export const SUPPORTED_LOCALES = [
  { code: 'zh-CN', label: '简体中文' },
  { code: 'en-US', label: 'English' }
]

const dictionaries = {
  'zh-CN': {
    common: zhCommon,
    tabs: zhTabs,
    index: zhIndex,
    chat: zhChat,
    settings: zhSettings,
    contacts: zhContacts,
    contactDetail: zhContactDetail,
    addFriend: zhAddFriend,
    friendRequests: zhFriendRequests,
    createGroup: zhCreateGroup,
    chatInfo: zhChatInfo,
    discover: zhDiscover,
    profile: zhProfile,
    profileEdit: zhProfileEdit,
    favorites: zhFavorites,
    moments: zhMoments,
    auth: zhAuth
  },
  'en-US': {
    common: enCommon,
    tabs: enTabs,
    index: enIndex,
    chat: enChat,
    settings: enSettings,
    contacts: enContacts,
    contactDetail: enContactDetail,
    addFriend: enAddFriend,
    friendRequests: enFriendRequests,
    createGroup: enCreateGroup,
    chatInfo: enChatInfo,
    discover: enDiscover,
    profile: enProfile,
    profileEdit: enProfileEdit,
    favorites: enFavorites,
    moments: enMoments,
    auth: enAuth
  }
}

function readStoredLocale() {
  try {
    const value = uni.getStorageSync(STORAGE_KEY)
    return typeof value === 'string' ? value : ''
  } catch (e) {
    return ''
  }
}

const state = reactive({
  locale: readStoredLocale() || DEFAULT_LOCALE
})

function lookup(dict, key) {
  if (!dict) return undefined
  return key.split('.').reduce((acc, part) => {
    if (acc && typeof acc === 'object') return acc[part]
    return undefined
  }, dict)
}

export function getLocale() {
  return state.locale
}

export function setLocale(locale) {
  if (!SUPPORTED_LOCALES.some((item) => item.code === locale)) return
  state.locale = locale
  try {
    uni.setStorageSync(STORAGE_KEY, locale)
  } catch (e) {
    // 忽略存储异常
  }
}

export function t(key, params) {
  if (!key) return ''
  let value = lookup(dictionaries[state.locale], key)
  if (value === undefined) value = lookup(dictionaries[FALLBACK_LOCALE], key)
  if (typeof value !== 'string') return key
  if (!params) return value
  return value.replace(/\{(\w+)\}/g, (match, name) => (
    params[name] === undefined ? match : params[name]
  ))
}

export default { state, t, getLocale, setLocale, SUPPORTED_LOCALES }
