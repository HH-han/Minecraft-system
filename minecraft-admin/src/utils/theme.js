/**
 * 主题管理工具
 */

// 主题列表
export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
  GAME: 'game'
}

/**
 * 获取当前主题
 */
export const getCurrentTheme = () => {
  return localStorage.getItem('theme') || THEMES.LIGHT
}

/**
 * 设置主题
 * @param {string} theme - 主题名称
 */
export const setTheme = (theme) => {
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('theme', theme)
}

/**
 * 切换主题
 * @param {string} currentTheme - 当前主题
 * @returns {string} - 新主题
 */
export const toggleTheme = (currentTheme) => {
  let newTheme
  
  switch (currentTheme) {
    case THEMES.LIGHT:
      newTheme = THEMES.DARK
      break
    case THEMES.DARK:
      newTheme = THEMES.LIGHT
      break
    default:
      newTheme = THEMES.LIGHT
  }
  
  setTheme(newTheme)
  return newTheme
}

/**
 * 初始化主题
 */
export const initTheme = () => {
  const theme = getCurrentTheme()
  setTheme(theme)
}
