/**
 * 主题管理工具（全站唯一主题入口）
 * ------------------------------------------------------------------
 * 职责：读写 localStorage、把主题落到 <html data-theme="..."> 与 .dark
 *      - data-theme：驱动 css/Theme/theme.css 的主题令牌
 *      - .dark      ：驱动 Element Plus 深色样式（dark / game 均为深色）
 * 取值：light（默认） / dark / game
 */

// 主题列表（顺序即切换顺序）
export const THEME_LIST = [
  { id: 'light', name: '亮色', icon: '☀️' },
  { id: 'dark', name: '暗色', icon: '🌙' },
  { id: 'game', name: '游戏', icon: '🎮' }
]

// 主题枚举（兼容旧用法 THEMES.LIGHT / DARK / GAME）
export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
  GAME: 'game'
}

const STORAGE_KEY = 'theme'

// 深色系主题（决定是否挂载 .dark）
export const isDarkTheme = (theme) => theme === THEMES.DARK || theme === THEMES.GAME

const isValidTheme = (theme) => THEME_LIST.some((t) => t.id === theme)

/**
 * 获取当前主题（localStorage → 系统偏好 → 亮色）
 */
export const getCurrentTheme = () => {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (isValidTheme(saved)) return saved
  const prefersDark =
    typeof window !== 'undefined' &&
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(prefers-color-scheme: dark)').matches
  return prefersDark ? THEMES.DARK : THEMES.LIGHT
}

/**
 * 仅把主题应用到 <html>（不写 localStorage，供首屏/初始化使用）
 * @param {string} theme - 主题名称
 */
export const applyTheme = (theme) => {
  const next = isValidTheme(theme) ? theme : THEMES.LIGHT
  const root = document.documentElement
  root.setAttribute('data-theme', next)
  root.classList.toggle('dark', isDarkTheme(next))
  return next
}

/**
 * 设置主题（应用到 DOM 并持久化）
 * @param {string} theme - 主题名称
 * @returns {string} - 实际生效的主题
 */
export const setTheme = (theme) => {
  const next = applyTheme(theme)
  localStorage.setItem(STORAGE_KEY, next)
  return next
}

/**
 * 在亮/暗之间切换
 * @param {string} currentTheme - 当前主题
 * @returns {string} - 新主题
 */
export const toggleTheme = (currentTheme) => {
  return setTheme(currentTheme === THEMES.DARK ? THEMES.LIGHT : THEMES.DARK)
}

/**
 * 按列表顺序循环切换（light → dark → game → light）
 * @param {string} currentTheme - 当前主题
 * @returns {string} - 新主题
 */
export const cycleTheme = (currentTheme) => {
  const index = THEME_LIST.findIndex((t) => t.id === currentTheme)
  const next = THEME_LIST[(index + 1) % THEME_LIST.length]
  return setTheme(next.id)
}

/**
 * 初始化主题（读取并应用，不改变持久化值）
 * @returns {string} - 生效的主题
 */
export const initTheme = () => {
  return applyTheme(getCurrentTheme())
}
