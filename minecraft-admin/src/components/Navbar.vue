<template>
  <nav class="navbar">
    <div class="navbar-container">
      <a href="/" class="navbar-brand">Minecraft UI</a>
      <div class="navbar-menu">
        <a href="/" class="navbar-link">首页</a>
        <a href="/about" class="navbar-link">关于</a>
        <a href="/contact" class="navbar-link">联系我们</a>
      </div>
      <div class="navbar-actions">
        <button class="btn btn-secondary" @click="toggleTheme">
          {{ isDarkTheme ? '浅色模式' : '深色模式' }}
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCurrentTheme, setTheme, THEMES } from '@/utils/theme'

const isDarkTheme = ref(false)

const toggleTheme = () => {
  isDarkTheme.value = !isDarkTheme.value
  setTheme(isDarkTheme.value ? THEMES.DARK : THEMES.LIGHT)
}

onMounted(() => {
  const savedTheme = getCurrentTheme()
  if (savedTheme) {
    isDarkTheme.value = savedTheme === THEMES.DARK
  }
})
</script>

<style scoped>
.navbar {
  background-color: var(--bg-secondary);
  border-bottom: 1px solid var(--border-light);
  padding: var(--space-4) 0;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: background-color var(--transition-normal), border-color var(--transition-normal);
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--space-6);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-500);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.navbar-brand:hover {
  color: var(--primary-600);
  text-decoration: none;
}

.navbar-menu {
  display: flex;
  gap: var(--space-6);
}

.navbar-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 500;
  transition: color var(--transition-fast);
  padding: var(--space-2) 0;
  position: relative;
}

.navbar-link:hover {
  color: var(--text-primary);
  text-decoration: none;
}

.navbar-link:hover::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--primary-500);
  border-radius: 2px;
}

.navbar-actions {
  display: flex;
  gap: var(--space-4);
  align-items: center;
}

@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }
  
  .navbar-container {
    padding: 0 var(--space-4);
  }
  
  .navbar-brand {
    font-size: 1.25rem;
  }
  
  .btn {
    padding: 0.375rem 0.75rem;
    font-size: 0.875rem;
  }
}
</style>
