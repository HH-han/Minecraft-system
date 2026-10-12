import { defineStore } from 'pinia'
import { ref, computed, nextTick } from 'vue'
import {
  THEME_LIST,
  THEME_PRESET_LIST,
  THEMES,
  getCurrentTheme,
  setTheme as persistTheme,
  initTheme as applyInitTheme,
  isDarkTheme,
  withThemeTransition,
  getCurrentPreset,
  setThemePreset as persistPreset,
  getCustomPresetColor,
  previewCustomPresetColor,
  setCustomPresetColor as persistCustomColor
} from '@/utils/theme'

/**
 * 主题状态（唯一主题源）
 * ------------------------------------------------------------------
 * 状态持久化统一由 utils/theme.js 写入 localStorage('theme')，
 * 本 store 只做响应式包装，不再重复持久化，避免与工具函数争抢同一 key。
 * 内置主题色板（preset）同样由 utils/theme.js 持久化，
 * 仅在亮色主题下生效，见 css/Theme/theme.css 4.1 节。
 */
export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref(getCurrentTheme())
  const themes = ref(THEME_LIST)

  const currentPreset = ref(getCurrentPreset())
  const presets = ref(THEME_PRESET_LIST)
  const customColor = ref(getCustomPresetColor())

  const currentThemeInfo = computed(() => {
    return themes.value.find((t) => t.id === currentTheme.value) || themes.value[0]
  })

  // dark / game 均为深色系
  const isDark = computed(() => isDarkTheme(currentTheme.value))

  // 内置主题色板仅亮色主题可用
  const isLight = computed(() => currentTheme.value === THEMES.LIGHT)

  const currentPresetInfo = computed(() => {
    return presets.value.find((p) => p.id === currentPreset.value) || presets.value[0]
  })

  // 当前色点颜色（自定义色板取取色器里的值）
  const currentPresetColor = computed(() => {
    return currentPreset.value === 'custom' ? customColor.value : currentPresetInfo.value.color
  })

  /**
   * 切换主题（带「从触发按钮扩散到全站」的圆形揭示动画）
   * @param {string} themeId - 主题名称
   * @param {{x: number, y: number}} [origin] - 动画圆心（一般为按钮中心，视口坐标）
   */
  function setTheme(themeId, origin) {
    withThemeTransition(async () => {
      currentTheme.value = persistTheme(themeId)
      // 等 Vue 把 DOM 更新完，浏览器才能快照到「新主题」画面
      await nextTick()
    }, origin)
  }

  function toggleTheme(origin) {
    const ids = themes.value.map((t) => t.id)
    const nextIndex = (ids.indexOf(currentTheme.value) + 1) % ids.length
    setTheme(ids[nextIndex], origin)
  }

  function setPreset(presetId) {
    currentPreset.value = persistPreset(presetId)
  }

  // 取色器拖拽中的实时预览：只改内联变量，不写 localStorage
  // （色板 id 仅在首次预览时落盘一次，避免每个 input 事件都写存储）
  function previewCustomColor(color) {
    if (currentPreset.value !== 'custom') currentPreset.value = persistPreset('custom')
    customColor.value = previewCustomPresetColor(color)
  }

  // 取色器确定（change）：写入 localStorage
  function commitCustomColor(color) {
    customColor.value = persistCustomColor(color)
    if (currentPreset.value !== 'custom') currentPreset.value = persistPreset('custom')
  }

  function initTheme() {
    currentTheme.value = applyInitTheme()
    currentPreset.value = getCurrentPreset()
    customColor.value = getCustomPresetColor()
  }

  return {
    currentTheme,
    themes,
    currentThemeInfo,
    isDark,
    isLight,
    currentPreset,
    presets,
    customColor,
    currentPresetInfo,
    currentPresetColor,
    setTheme,
    toggleTheme,
    setPreset,
    previewCustomColor,
    commitCustomColor,
    initTheme
  }
})
