/**
 * 国际化核心模块
 * 与 minecraft-ui 保持一致的方案：import.meta.glob 按目录懒加载语言包，
 * 缺失翻译回退到 zh-CN，组件内通过 useI18n / $t 使用。
 */
import { createI18n } from 'vue-i18n'
import { computed } from 'vue'

export const SUPPORTED_LOCALES = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' }
]

export const DEFAULT_LOCALE = 'zh-CN'
export const FALLBACK_LOCALE = 'zh-CN'
const STORAGE_KEY = 'app-locale'

const modules = import.meta.glob('./langs/**/*.json')
const localesMap = {}
for (const path of Object.keys(modules)) {
  const matched = path.match(/\.\/langs\/([^/]+)\/.+\.json$/)
  if (matched) {
    const lang = matched[1]
    ;(localesMap[lang] ||= []).push(modules[path])
  }
}

function deepMerge(target, source) {
  for (const key of Object.keys(source)) {
    const sVal = source[key]
    const tVal = target[key]
    target[key] =
      sVal && typeof sVal === 'object' && !Array.isArray(sVal) && tVal && typeof tVal === 'object'
        ? deepMerge({ ...tVal }, sVal)
        : sVal
  }
  return target
}

async function loadMessages(lang) {
  const loaders = localesMap[lang] || localesMap[FALLBACK_LOCALE] || []
  const parts = await Promise.all(loaders.map((load) => load()))
  return parts.reduce((acc, mod) => deepMerge(acc, mod.default || mod), {})
}

function resolveInitialLocale() {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (saved && SUPPORTED_LOCALES.some((l) => l.value === saved)) return saved
  const nav = navigator.language
  if (nav?.toLowerCase().startsWith('zh')) return 'zh-CN'
  if (SUPPORTED_LOCALES.some((l) => l.value === nav)) return nav
  return DEFAULT_LOCALE
}

export const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: DEFAULT_LOCALE,
  fallbackLocale: FALLBACK_LOCALE,
  fallbackWarn: !import.meta.env.PROD,
  missingWarn: !import.meta.env.PROD,
  messages: {}
})

const loadedLanguages = new Set()

export async function setLocale(lang) {
  if (!SUPPORTED_LOCALES.some((l) => l.value === lang)) {
    lang = FALLBACK_LOCALE
  }
  if (!loadedLanguages.has(lang)) {
    const messages = await loadMessages(lang)
    i18n.global.setLocaleMessage(lang, messages)
    loadedLanguages.add(lang)
  }
  i18n.global.locale.value = lang
  document.documentElement.setAttribute('lang', lang)
  localStorage.setItem(STORAGE_KEY, lang)
}

export const currentLocale = computed(() => i18n.global.locale.value)

export async function setupI18n(app) {
  app.use(i18n)
  await setLocale(resolveInitialLocale())
}
