<template>
  <div class="theme-switcher">
    <button 
      v-for="theme in availableThemes" 
      :key="theme.value"
      class="theme-btn"
      :class="{ active: currentTheme === theme.value }"
      @click="setThemeHandler(theme.value)"
      :title="theme.label"
    >
      <div class="theme-indicator" :style="{ backgroundColor: theme.color }"></div>
      <span class="theme-label">{{ theme.label }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCurrentTheme, setTheme, THEMES } from '@/utils/theme'

const currentTheme = ref('')

const availableThemes = [
  { value: THEMES.LIGHT, label: '浅色模式', color: '#f3f4f6' },
  { value: THEMES.DARK, label: '深色模式', color: '#1f2937' },
  { value: THEMES.GAME, label: '游戏模式', color: '#1e293b' }
]

onMounted(() => {
  currentTheme.value = getCurrentTheme()
})

const setThemeHandler = (theme) => {
  setTheme(theme)
  currentTheme.value = theme
}
</script>

<style scoped>
.theme-switcher {
  display: flex;
  gap: var(--space-3);
  align-items: center;
  flex-wrap: wrap;
}

.theme-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border: 2px solid var(--border-medium);
  border-radius: var(--radius-lg);
  background-color: var(--bg-tertiary);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.theme-btn:hover {
  border-color: var(--primary-500);
  color: var(--text-primary);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.theme-btn.active {
  border-color: var(--primary-500);
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  box-shadow: 0 0 0 2px rgb(14 165 233 / 0.2);
}

.theme-indicator {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid var(--border-dark);
}

.theme-label {
  white-space: nowrap;
}

@media (max-width: 480px) {
  .theme-switcher {
    gap: var(--space-2);
  }
  
  .theme-btn {
    padding: var(--space-1) var(--space-2);
    font-size: 0.75rem;
  }
  
  .theme-indicator {
    width: 12px;
    height: 12px;
  }
  
  .theme-label {
    display: none;
  }
}
</style>
