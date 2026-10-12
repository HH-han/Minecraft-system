import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  THEME_LIST,
  getCurrentTheme,
  setTheme as persistTheme,
  initTheme as applyInitTheme,
  isDarkTheme
} from '@/utils/theme'

/**
 * 主题状态（唯一主题源）
 * ------------------------------------------------------------------
 * 状态持久化统一由 utils/theme.js 写入 localStorage('theme')，
 * 本 store 只做响应式包装，不再重复持久化，避免与工具函数争抢同一 key。
 */
export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref(getCurrentTheme())
  const themes = ref(THEME_LIST)

  const currentThemeInfo = computed(() => {
    return themes.value.find((t) => t.id === currentTheme.value) || themes.value[0]
  })

  // dark / game 均为深色系
  const isDark = computed(() => isDarkTheme(currentTheme.value))

  function setTheme(themeId) {
    currentTheme.value = persistTheme(themeId)
  }

  function toggleTheme() {
    const ids = themes.value.map((t) => t.id)
    const nextIndex = (ids.indexOf(currentTheme.value) + 1) % ids.length
    setTheme(ids[nextIndex])
  }

  function initTheme() {
    currentTheme.value = applyInitTheme()
  }

  return {
    currentTheme,
    themes,
    currentThemeInfo,
    isDark,
    setTheme,
    toggleTheme,
    initTheme
  }
})
