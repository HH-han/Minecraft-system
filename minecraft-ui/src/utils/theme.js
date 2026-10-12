/**
 * 主题管理工具（全站唯一主题入口）
 * ------------------------------------------------------------------
 * 职责：读写 localStorage、把主题落到 <html data-theme="..."> 与 .dark
 *      - data-theme       ：驱动 css/Theme/theme.css 的主题令牌
 *      - data-theme-preset：驱动 css/Theme/theme.css 4.1 节的亮色内置主题色板
 *      - .dark            ：驱动 Element Plus 深色样式（dark / game 均为深色）
 * 取值：light（默认） / dark / game
 * 内置主题色板仅在 light 主题下生效（CSS 作用域限定，切到 dark / game 自动失效）
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

// 亮色内置主题色板（顺序即面板展示顺序；color 仅用于面板色点，实际配色见 theme.css 4.1 节）
export const THEME_PRESET_LIST = [
  { id: 'default', name: '默认', color: '#0ea5e9' },
  { id: 'violet', name: '紫罗兰', color: '#7c5cff' },
  { id: 'sakura', name: '樱花粉', color: '#f0518b' },
  { id: 'lemon', name: '柠檬黄', color: '#f0b400' },
  { id: 'sky', name: '天蓝色', color: '#38bdf8' },
  { id: 'mint', name: '浅绿色', color: '#34d399' },
  { id: 'plum', name: '绛紫色', color: '#a855f7' },
  { id: 'forest', name: '深绿色', color: '#16a34a' },
  { id: 'navy', name: '深蓝色', color: '#1d4ed8' },
  { id: 'coral', name: '橙红色', color: '#ea580c' },
  { id: 'rose', name: '玫瑰红', color: '#e11d48' },
  { id: 'neutral', name: '中性色', color: '#475569' },
  { id: 'graphite', name: '石墨灰', color: '#3f3f46' },
  { id: 'gray', name: '中灰色', color: '#94a3b8' },
  { id: 'custom', name: '自定义', color: '' }
]

// 默认色板 / 自定义取色器默认值
export const DEFAULT_PRESET = 'default'
const DEFAULT_CUSTOM_COLOR = '#0ea5e9'

const STORAGE_KEY = 'theme'
const PRESET_STORAGE_KEY = 'theme-preset'
const CUSTOM_COLOR_STORAGE_KEY = 'theme-preset-custom'

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
  applyThemePreset(getCurrentPreset(), getCustomPresetColor())
  return applyTheme(getCurrentTheme())
}

/* ============================================================
   主题切换动画（View Transitions：由切换按钮扩散到全站）
   ============================================================ */

// 过渡时长：读取一次后缓存。避免在过渡「关键帧」里读计算样式（会强制样式重算，
// 恰好卡在快照与动画之间，是这类揭示动画最常见的掉帧来源）
let cachedDuration = 0
const getTransitionDuration = () => {
  if (cachedDuration) return cachedDuration
  const raw = parseFloat(
    getComputedStyle(document.documentElement).getPropertyValue('--theme-duration-slow')
  )
  cachedDuration = Math.round(raw * 2000) || 420
  return cachedDuration
}

// 正在进行的主题过渡：过渡未结束时再次切换直接生效，避免叠加两层全页快照
let activeTransition = null

/**
 * 带「圆形揭示」动画地应用主题
 * ------------------------------------------------------------------
 * 以 origin（一般是切换按钮中心，视口坐标）为圆心，把「新主题快照」用
 * circle() 裁剪从按钮处扩散到全屏，形成全局主题变化动画。
 * 以下情况直接应用、不做动画（省掉两次全页快照的开销）：
 * · 浏览器不支持 View Transitions（Firefox / 旧 Safari）
 * · 系统开启「减少动效」
 * · 标签页处于后台（看不见动画）
 * · 上一次过渡尚未结束（此时再开一层快照既卡又会被浏览器跳过）
 * @param {Function} apply - 实际改主题的函数，返回 Promise（等待框架完成 DOM 更新）
 * @param {{x: number, y: number}} [origin] - 动画圆心，缺省为视口中心
 */
export const withThemeTransition = (apply, origin) => {
  const doc = document
  const reducedMotion =
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches

  if (
    typeof doc.startViewTransition !== 'function' ||
    reducedMotion ||
    doc.hidden ||
    activeTransition
  ) {
    apply()
    return
  }

  const x = origin?.x ?? window.innerWidth / 2
  const y = origin?.y ?? window.innerHeight / 2
  // 半径取圆心到最远角的距离，保证圆能覆盖整屏；提前算好，避免进入动画帧后再算
  const radius = Math.hypot(
    Math.max(x, window.innerWidth - x),
    Math.max(y, window.innerHeight - y)
  )
  const duration = getTransitionDuration()

  const transition = doc.startViewTransition(apply)
  activeTransition = transition

  // ready 后新旧快照均就绪，此时给「新快照」加裁剪动画
  transition.ready
    .then(() => {
      doc.documentElement.animate(
        {
          clipPath: [`circle(0px at ${x}px ${y}px)`, `circle(${radius}px at ${x}px ${y}px)`]
        },
        {
          duration,
          // 与 --theme-ease 保持一致
          easing: 'cubic-bezier(0.22, 1, 0.36, 1)',
          pseudoElement: '::view-transition-new(root)'
        }
      )
    })
    .catch(() => {
      // 过渡被跳过（如切换过快触发了下一次过渡）时忽略
    })

  // 兜底：标签页在过渡中被切到后台等情况下 finished 可能迟迟不落，
  // 超时释放标记，避免后续主题切换被永久降级为「无动画」
  const watchdog = setTimeout(() => {
    activeTransition = null
  }, duration + 600)

  transition.finished
    .catch(() => {})
    .finally(() => {
      clearTimeout(watchdog)
      activeTransition = null
    })
}

/* ============================================================
   亮色内置主题色板（仅 light 主题生效）
   ============================================================ */

const isValidPreset = (preset) => THEME_PRESET_LIST.some((p) => p.id === preset)

/**
 * 获取当前内置色板（localStorage → 默认色板）
 */
export const getCurrentPreset = () => {
  const saved = localStorage.getItem(PRESET_STORAGE_KEY)
  return isValidPreset(saved) ? saved : DEFAULT_PRESET
}

/**
 * 获取自定义色（取色器选过的值）
 */
export const getCustomPresetColor = () => {
  return localStorage.getItem(CUSTOM_COLOR_STORAGE_KEY) || DEFAULT_CUSTOM_COLOR
}

/**
 * 仅把内置色板应用到 <html>（不写 localStorage，供初始化使用）
 * @param {string} preset - 色板 id
 * @param {string} customColor - 自定义色（写入内联变量，供 custom 色板引用）
 * @returns {string} - 实际生效的色板 id
 */
export const applyThemePreset = (preset, customColor) => {
  const next = isValidPreset(preset) ? preset : DEFAULT_PRESET
  const root = document.documentElement
  root.setAttribute('data-theme-preset', next)
  root.style.setProperty('--theme-preset-custom', customColor || getCustomPresetColor())
  return next
}

/**
 * 设置内置色板（应用到 DOM 并持久化）
 * @param {string} preset - 色板 id
 * @returns {string} - 实际生效的色板 id
 */
export const setThemePreset = (preset) => {
  const next = applyThemePreset(preset, getCustomPresetColor())
  localStorage.setItem(PRESET_STORAGE_KEY, next)
  return next
}

/**
 * 仅把自定义色写入 <html> 内联变量（不落盘）
 * 供取色器拖拽时的实时预览使用，避免每个 input 事件都写 localStorage
 * @param {string} color - 十六进制颜色
 * @returns {string} - 实际生效的颜色
 */
export const previewCustomPresetColor = (color) => {
  const next = color || DEFAULT_CUSTOM_COLOR
  document.documentElement.style.setProperty('--theme-preset-custom', next)
  return next
}

/**
 * 设置自定义色并持久化（取色器确定后调用）
 * @param {string} color - 十六进制颜色
 * @returns {string} - 实际生效的颜色
 */
export const setCustomPresetColor = (color) => {
  const next = color || DEFAULT_CUSTOM_COLOR
  localStorage.setItem(CUSTOM_COLOR_STORAGE_KEY, next)
  return previewCustomPresetColor(next)
}
