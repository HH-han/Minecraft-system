/**
 * 轻量国际化
 *
 * 方案与 minecraft-ui 保持一致：按语言 + 业务模块拆分词条。
 * 目录结构：
 *   locales/langs/<locale>/<namespace>.js
 * 页面内通过全局 mixin 注入的 this.$t('namespace.key') 使用。
 *
 * 每个语言包按文件名自动注册命名空间，新增模块无需改动本文件。
 */

import { reactive } from 'vue'

const STORAGE_KEY = 'im-locale'
const DEFAULT_LOCALE = 'zh-CN'
const FALLBACK_LOCALE = 'zh-CN'

export const SUPPORTED_LOCALES = [
  { code: 'zh-CN', label: '简体中文' },
  { code: 'en-US', label: 'English' }
]

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

const dictionaries = {}

const modules = import.meta.glob('./langs/*/*.js', { eager: true })

Object.keys(modules).forEach((path) => {
  const matched = path.match(/\.\/langs\/([^/]+)\/([^/]+)\.js$/)
  if (!matched) return
  const locale = matched[1]
  const namespace = matched[2]
  const mod = modules[path]
  const content = (mod && mod.default) || mod || {}
  if (!dictionaries[locale]) dictionaries[locale] = {}
  dictionaries[locale][namespace] = content
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
