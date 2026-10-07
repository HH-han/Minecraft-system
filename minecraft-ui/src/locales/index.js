/**
 * 国际化核心模块
 *
 * 参考 minecraft-admin-main (vben) 的 @vben/locales 方案适配：
 * - import.meta.glob 按目录懒加载语言包（./langs/<locale>/*.json 自动合并）
 * - 组件库（Element Plus）语言包联动切换
 * - 缺失翻译回退机制（fallbackLocale）+ 开发环境 missingWarn
 *
 * 新增语言步骤：
 * 1. 在 SUPPORTED_LOCALES 中登记
 * 2. 创建 ./langs/<locale>/ 目录并补齐与 zh-CN 相同的 json 文件
 * 3. 在 loadElementLocale 的 switch 中补充 Element Plus 语言包分支
 */
import { createI18n } from 'vue-i18n';
import { ref, computed } from 'vue';

/** 支持的语言列表 */
export const SUPPORTED_LOCALES = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' },
];

export const DEFAULT_LOCALE = 'zh-CN';
export const FALLBACK_LOCALE = 'zh-CN';
const STORAGE_KEY = 'app-locale';

/** 当前 Element Plus 组件库语言包（供 ElConfigProvider 使用） */
export const elementLocale = ref(null);

/**
 * 按目录收集语言包：./langs/zh-CN/common.json -> localesMap['zh-CN'] = [loader, ...]
 */
const modules = import.meta.glob('./langs/**/*.json');
const localesMap = {};
for (const path of Object.keys(modules)) {
  const matched = path.match(/\.\/langs\/([^/]+)\/.+\.json$/);
  if (matched) {
    const lang = matched[1];
    (localesMap[lang] ||= []).push(modules[path]);
  }
}

/** 深合并多个语言分片文件（common.json / header.json / ...） */
function deepMerge(target, source) {
  for (const key of Object.keys(source)) {
    const sVal = source[key];
    const tVal = target[key];
    target[key] =
      sVal && typeof sVal === 'object' && !Array.isArray(sVal) && tVal && typeof tVal === 'object'
        ? deepMerge({ ...tVal }, sVal)
        : sVal;
  }
  return target;
}

/** 加载指定语言的应用语言包（可改造为从服务端获取翻译数据） */
async function loadMessages(lang) {
  const loaders = localesMap[lang] || localesMap[FALLBACK_LOCALE] || [];
  const parts = await Promise.all(loaders.map((load) => load()));
  return parts.reduce((acc, mod) => deepMerge(acc, mod.default || mod), {});
}

/** 加载 Element Plus 组件库语言包 */
async function loadElementLocale(lang) {
  switch (lang) {
    case 'en-US': {
      elementLocale.value = (await import('element-plus/es/locale/lang/en')).default;
      break;
    }
    case 'zh-CN': {
      elementLocale.value = (await import('element-plus/es/locale/lang/zh-cn')).default;
      break;
    }
    // 默认使用中文
    default: {
      elementLocale.value = (await import('element-plus/es/locale/lang/zh-cn')).default;
    }
  }
}

/** 日期 / 数字 / 货币的区域格式（vue-i18n 内置 Intl 封装） */
const datetimeFormats = {
  'zh-CN': {
    short: { year: 'numeric', month: '2-digit', day: '2-digit' },
    long: { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false },
  },
  'en-US': {
    short: { month: 'short', day: 'numeric', year: 'numeric' },
    long: { month: 'short', day: 'numeric', year: 'numeric', hour: 'numeric', minute: '2-digit', hour12: true },
  },
};

const numberFormats = {
  'zh-CN': {
    currency: { style: 'currency', currency: 'CNY', notation: 'standard' },
    decimal: { style: 'decimal', maximumFractionDigits: 2 },
  },
  'en-US': {
    currency: { style: 'currency', currency: 'USD', notation: 'standard' },
    decimal: { style: 'decimal', maximumFractionDigits: 2 },
  },
};

/** 解析初始语言：本地存储 > 浏览器语言 > 默认 */
function resolveInitialLocale() {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (saved && SUPPORTED_LOCALES.some((l) => l.value === saved)) return saved;
  const nav = navigator.language;
  if (nav?.toLowerCase().startsWith('zh')) return 'zh-CN';
  if (SUPPORTED_LOCALES.some((l) => l.value === nav)) return nav;
  return DEFAULT_LOCALE;
}

export const i18n = createI18n({
  legacy: false, // Composition API 模式
  globalInjection: true, // 模板中可直接使用 $t / $d / $n（与参考项目 $t 风格一致）
  locale: DEFAULT_LOCALE,
  fallbackLocale: FALLBACK_LOCALE, // 缺失翻译回退
  fallbackWarn: !import.meta.env.PROD,
  missingWarn: !import.meta.env.PROD,
  silentTranslationWarn: import.meta.env.PROD,
  datetimeFormats,
  numberFormats,
  messages: {},
});

/** 已加载的语言集合，避免重复请求 */
const loadedLanguages = new Set();

/**
 * 切换语言：懒加载语言包 + 切换 Element Plus 语言包 + 持久化
 * @param {string} lang
 */
export async function setLocale(lang) {
  if (!SUPPORTED_LOCALES.some((l) => l.value === lang)) {
    console.warn(`[i18n] Unsupported locale "${lang}", fallback to ${FALLBACK_LOCALE}`);
    lang = FALLBACK_LOCALE;
  }
  if (!loadedLanguages.has(lang)) {
    const messages = await loadMessages(lang);
    i18n.global.setLocaleMessage(lang, messages);
    loadedLanguages.add(lang);
  }
  await loadElementLocale(lang);
  i18n.global.locale.value = lang;
  document.documentElement.setAttribute('lang', lang);
  localStorage.setItem(STORAGE_KEY, lang);
}

/** 当前语言（响应式） */
export const currentLocale = computed(() => i18n.global.locale.value);

/**
 * 初始化国际化（在 app.mount 前 await，避免首屏闪烁）
 * @param {import('vue').App} app
 */
export async function setupI18n(app) {
  app.use(i18n);
  await setLocale(resolveInitialLocale());
}
