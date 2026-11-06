import { createI18n } from 'vue-i18n';
import { computed } from 'vue';
import type { App } from 'vue';

/**
 * 国际化方案与 minecraft-ui 保持一致：
 * - import.meta.glob 按目录懒加载语言包（langs/<locale>/*.json 自动合并）
 * - 缺失翻译回退到 zh-CN
 * - 新增语言：登记 SUPPORTED_LOCALES + 新建同名目录并补齐 json 分片
 */
export const SUPPORTED_LOCALES = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' }
] as const;

export type AppLocale = (typeof SUPPORTED_LOCALES)[number]['value'];

export const DEFAULT_LOCALE: AppLocale = 'zh-CN';
export const FALLBACK_LOCALE: AppLocale = 'zh-CN';
const STORAGE_KEY = 'app-locale';

const modules = import.meta.glob('./langs/**/*.json');
const localesMap: Record<string, Array<() => Promise<unknown>>> = {};

for (const path of Object.keys(modules)) {
  const matched = path.match(/\.\/langs\/([^/]+)\/.+\.json$/);
  if (matched) {
    const lang = matched[1];
    (localesMap[lang] ||= []).push(modules[path]);
  }
}

function isPlainObject(value: unknown): value is Record<string, unknown> {
  return Boolean(value) && typeof value === 'object' && !Array.isArray(value);
}

function deepMerge(
  target: Record<string, unknown>,
  source: Record<string, unknown>
): Record<string, unknown> {
  for (const key of Object.keys(source)) {
    const sourceValue = source[key];
    const targetValue = target[key];
    target[key] =
      isPlainObject(sourceValue) && isPlainObject(targetValue)
        ? deepMerge({ ...targetValue }, sourceValue)
        : sourceValue;
  }
  return target;
}

async function loadMessages(lang: AppLocale): Promise<Record<string, unknown>> {
  const loaders = localesMap[lang] ?? localesMap[FALLBACK_LOCALE] ?? [];
  const parts = await Promise.all(loaders.map((load) => load()));
  return parts.reduce<Record<string, unknown>>((acc, part) => {
    const source = (part as { default?: Record<string, unknown> }).default;
    return source ? deepMerge(acc, source) : acc;
  }, {});
}

export const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: DEFAULT_LOCALE,
  fallbackLocale: FALLBACK_LOCALE,
  fallbackWarn: !import.meta.env.PROD,
  missingWarn: !import.meta.env.PROD,
  messages: {}
});

const loadedLanguages = new Set<AppLocale>();

function isSupportedLocale(lang: string): lang is AppLocale {
  return SUPPORTED_LOCALES.some((item) => item.value === lang);
}

function resolveInitialLocale(): AppLocale {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved && isSupportedLocale(saved)) return saved;
  const nav = navigator.language;
  if (nav?.toLowerCase().startsWith('zh')) return 'zh-CN';
  if (nav && isSupportedLocale(nav)) return nav;
  return DEFAULT_LOCALE;
}

export async function setLocale(lang: AppLocale): Promise<void> {
  if (!loadedLanguages.has(lang)) {
    const messages = await loadMessages(lang);
    i18n.global.setLocaleMessage(lang, messages);
    loadedLanguages.add(lang);
  }

  i18n.global.locale.value = lang;
  document.documentElement.setAttribute('lang', lang);
  localStorage.setItem(STORAGE_KEY, lang);
}

export const currentLocale = computed<AppLocale>(() => i18n.global.locale.value as AppLocale);

/** 在 app.mount 前 await，避免首屏语言闪烁 */
export async function setupI18n(app: App): Promise<void> {
  app.use(i18n);
  await setLocale(resolveInitialLocale());
}
