import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref('light')
  const themes = ref([
    { id: 'light', name: '亮色', icon: '☀️' },
    { id: 'dark', name: '暗色', icon: '🌙' },
    { id: 'game', name: '游戏', icon: '🎮' }
  ])

  const currentThemeInfo = computed(() => {
    return themes.value.find(t => t.id === currentTheme.value) || themes.value[0]
  })

  function setTheme(themeId) {
    const theme = themes.value.find(t => t.id === themeId)
    if (theme) {
      currentTheme.value = themeId
      document.documentElement.setAttribute('data-theme', themeId)
      localStorage.setItem('theme', themeId)
    }
  }

  function toggleTheme() {
    const themeIds = themes.value.map(t => t.id)
    const currentIndex = themeIds.indexOf(currentTheme.value)
    const nextIndex = (currentIndex + 1) % themeIds.length
    setTheme(themeIds[nextIndex])
  }

  function initTheme() {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme && themes.value.some(t => t.id === savedTheme)) {
      setTheme(savedTheme)
    } else {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
      setTheme(prefersDark ? 'dark' : 'light')
    }
  }

  return {
    currentTheme,
    themes,
    currentThemeInfo,
    setTheme,
    toggleTheme,
    initTheme
  }
}, {
  persist: true
})