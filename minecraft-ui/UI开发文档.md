# 前端UI开发文档

## 1. 简介

本文档旨在指导前端UI开发，重点介绍如何使用CSS实现支持主题颜色替换的现代前端界面。通过CSS变量和主题切换机制，实现界面风格的灵活调整，提升用户体验和开发效率。

## 2. 技术栈

- **前端框架**：Vue 3
- **构建工具**：Vite
- **UI组件库**：Element Plus
- **样式方案**：原生CSS + CSS变量
- **包管理器**：npm

## 3. 项目结构

```
minecraft-ui/
├── src/
│   ├── assets/           # 静态资源
│   │   ├── styles/       # 样式文件
│   │   │   ├── variables.css  # CSS变量定义
│   │   │   ├── theme.css      # 主题样式
│   │   │   └── global.css     # 全局样式
│   ├── components/       # 组件
│   ├── views/            # 页面
│   ├── utils/            # 工具函数
│   ├── router/           # 路由
│   └── main.js           # 入口文件
├── index.html
├── package.json
└── vite.config.js
```

## 4. 主题颜色系统设计

### 4.1 设计原则

- **一致性**：确保主题色在整个应用中保持一致
- **可扩展性**：易于添加新的主题
- **可维护性**：通过CSS变量集中管理颜色
- **性能**：主题切换无闪烁，响应迅速

### 4.2 颜色体系

#### 4.2.1 主色调
- 主色：品牌的主要识别色
- 辅色：用于强调和交互元素
- 中性色：用于文本、背景等基础元素

#### 4.2.2 色阶
- 每种颜色定义多个亮度的色阶，适应不同场景
- 例如：主色-50、主色-100、主色-200、...、主色-900

## 5. CSS变量实现

### 5.1 基础变量定义

创建 `src/assets/styles/variables.css` 文件：

```css
/* 主题变量 */
:root {
  /* 主色调 */
  --primary-50: #f0f9ff;
  --primary-100: #e0f2fe;
  --primary-200: #bae6fd;
  --primary-300: #7dd3fc;
  --primary-400: #38bdf8;
  --primary-500: #0ea5e9;
  --primary-600: #0284c7;
  --primary-700: #0369a1;
  --primary-800: #075985;
  --primary-900: #0c4a6e;
  
  /* 辅助色 */
  --secondary-50: #fdf2f8;
  --secondary-100: #fce7f3;
  --secondary-200: #fbcfe8;
  --secondary-300: #f9a8d4;
  --secondary-400: #f472b6;
  --secondary-500: #ec4899;
  --secondary-600: #db2777;
  --secondary-700: #be185d;
  --secondary-800: #9d174d;
  --secondary-900: #831843;
  
  /* 中性色 */
  --neutral-50: #f9fafb;
  --neutral-100: #f3f4f6;
  --neutral-200: #e5e7eb;
  --neutral-300: #d1d5db;
  --neutral-400: #9ca3af;
  --neutral-500: #6b7280;
  --neutral-600: #4b5563;
  --neutral-700: #374151;
  --neutral-800: #1f2937;
  --neutral-900: #111827;
  
  /* 功能色 */
  --success-500: #10b981;
  --warning-500: #f59e0b;
  --error-500: #ef4444;
  --info-500: #3b82f6;
  
  /* 文本颜色 */
  --text-primary: var(--neutral-900);
  --text-secondary: var(--neutral-600);
  --text-tertiary: var(--neutral-400);
  --text-inverse: var(--neutral-50);
  
  /* 背景颜色 */
  --bg-primary: #ffffff;
  --bg-secondary: var(--neutral-50);
  --bg-tertiary: var(--neutral-100);
  --bg-inverse: var(--neutral-900);
  
  /* 边框颜色 */
  --border-light: var(--neutral-200);
  --border-medium: var(--neutral-300);
  --border-dark: var(--neutral-400);
  
  /* 阴影 */
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
  
  /* 圆角 */
  --radius-sm: 0.125rem;
  --radius-md: 0.375rem;
  --radius-lg: 0.5rem;
  --radius-xl: 0.75rem;
  --radius-2xl: 1rem;
  
  /* 间距 */
  --space-1: 0.25rem;
  --space-2: 0.5rem;
  --space-3: 0.75rem;
  --space-4: 1rem;
  --space-5: 1.25rem;
  --space-6: 1.5rem;
  --space-8: 2rem;
  --space-10: 2.5rem;
  --space-12: 3rem;
  
  /* 过渡 */
  --transition-fast: 150ms ease-in-out;
  --transition-normal: 250ms ease-in-out;
  --transition-slow: 350ms ease-in-out;
}

/* 暗色主题 */
[data-theme="dark"] {
  /* 文本颜色 */
  --text-primary: var(--neutral-50);
  --text-secondary: var(--neutral-300);
  --text-tertiary: var(--neutral-500);
  --text-inverse: var(--neutral-900);
  
  /* 背景颜色 */
  --bg-primary: var(--neutral-900);
  --bg-secondary: var(--neutral-800);
  --bg-tertiary: var(--neutral-700);
  --bg-inverse: var(--neutral-50);
  
  /* 边框颜色 */
  --border-light: var(--neutral-700);
  --border-medium: var(--neutral-600);
  --border-dark: var(--neutral-500);
  
  /* 阴影 */
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.2);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.3), 0 2px 4px -2px rgb(0 0 0 / 0.3);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.3), 0 4px 6px -4px rgb(0 0 0 / 0.3);
  --shadow-xl: 0 20px 25px -5px rgb(0 0 0 / 0.3), 0 8px 10px -6px rgb(0 0 0 / 0.3);
}

/* 自定义主题 - 示例：游戏主题 */
[data-theme="game"] {
  /* 主色调 */
  --primary-500: #4ade80;
  --primary-600: #22c55e;
  --primary-700: #16a34a;
  
  /* 辅助色 */
  --secondary-500: #f472b6;
  --secondary-600: #ec4899;
  
  /* 背景颜色 */
  --bg-primary: #1e293b;
  --bg-secondary: #0f172a;
  --bg-tertiary: #334155;
  
  /* 文本颜色 */
  --text-primary: #f8fafc;
  --text-secondary: #cbd5e1;
  --text-tertiary: #94a3b8;
}
```

### 5.2 全局样式集成

创建 `src/assets/styles/global.css` 文件：

```css
/* 导入变量 */
@import './variables.css';

/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 16px;
  line-height: 1.5;
  color: var(--text-primary);
  background-color: var(--bg-primary);
  transition: background-color var(--transition-normal), color var(--transition-normal);
}

/* 链接样式 */
a {
  color: var(--primary-500);
  text-decoration: none;
  transition: color var(--transition-fast);
}

a:hover {
  color: var(--primary-600);
  text-decoration: underline;
}

/* 按钮基础样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: var(--radius-md);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  text-decoration: none;
}

.btn-primary {
  background-color: var(--primary-500);
  color: white;
}

.btn-primary:hover {
  background-color: var(--primary-600);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
  border: 1px solid var(--border-medium);
}

.btn-secondary:hover {
  background-color: var(--bg-secondary);
  border-color: var(--border-dark);
}

/* 卡片样式 */
.card {
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--space-6);
  transition: box-shadow var(--transition-fast);
}

.card:hover {
  box-shadow: var(--shadow-lg);
}

/* 输入框样式 */
.input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-md);
  font-size: 1rem;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.input:focus {
  outline: none;
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgb(14 165 233 / 0.1);
}

/* 标题样式 */
h1, h2, h3, h4, h5, h6 {
  color: var(--text-primary);
  font-weight: 600;
  line-height: 1.2;
  margin-bottom: var(--space-4);
}

h1 {
  font-size: 2.25rem;
}

h2 {
  font-size: 1.875rem;
}

h3 {
  font-size: 1.5rem;
}

h4 {
  font-size: 1.25rem;
}

h5 {
  font-size: 1.125rem;
}

h6 {
  font-size: 1rem;
}

/* 段落样式 */
p {
  margin-bottom: var(--space-4);
  color: var(--text-secondary);
}

/* 列表样式 */
ul, ol {
  margin-bottom: var(--space-4);
  padding-left: var(--space-6);
}

li {
  margin-bottom: var(--space-2);
  color: var(--text-secondary);
}
```

## 6. 组件样式示例

### 6.1 导航栏组件

```vue
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

const isDarkTheme = ref(false)

const toggleTheme = () => {
  isDarkTheme.value = !isDarkTheme.value
  document.documentElement.setAttribute('data-theme', isDarkTheme.value ? 'dark' : 'light')
  localStorage.setItem('theme', isDarkTheme.value ? 'dark' : 'light')
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme) {
    isDarkTheme.value = savedTheme === 'dark'
    document.documentElement.setAttribute('data-theme', savedTheme)
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
}

.navbar-link:hover {
  color: var(--text-primary);
}

.navbar-actions {
  display: flex;
  gap: var(--space-4);
}

@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }
}
</style>
```

### 6.2 登录表单组件

```vue
<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-title">
        <h2>{{ activeTab === 'login' ? '登录' : '注册' }}</h2>
      </div>
      
      <div class="tabs">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'login' }"
          @click="activeTab = 'login'"
        >
          登录
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'register' }"
          @click="activeTab = 'register'"
        >
          注册
        </button>
      </div>
      
      <form v-if="activeTab === 'login'" class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username" class="form-label">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="loginForm.username" 
            class="input"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label for="password" class="form-label">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="loginForm.password" 
            class="input"
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="btn btn-primary w-full">登录</button>
      </form>
      
      <form v-else class="login-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="reg-username" class="form-label">用户名</label>
          <input 
            type="text" 
            id="reg-username" 
            v-model="registerForm.username" 
            class="input"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label for="reg-email" class="form-label">邮箱</label>
          <input 
            type="email" 
            id="reg-email" 
            v-model="registerForm.email" 
            class="input"
            placeholder="请输入邮箱"
            required
          />
        </div>
        <div class="form-group">
          <label for="reg-password" class="form-label">密码</label>
          <input 
            type="password" 
            id="reg-password" 
            v-model="registerForm.password" 
            class="input"
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="btn btn-primary w-full">注册</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('login')

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  email: '',
  password: ''
})

const handleLogin = async () => {
  // 登录逻辑
  console.log('Login:', loginForm)
  // 登录成功后跳转到首页
  router.push('/')
}

const handleRegister = async () => {
  // 注册逻辑
  console.log('Register:', registerForm)
  // 注册成功后切换到登录
  activeTab.value = 'login'
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  padding: var(--space-6);
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
  background-color: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  padding: var(--space-8);
  transition: all var(--transition-normal);
}

.login-title {
  text-align: center;
  margin-bottom: var(--space-6);
}

.login-title h2 {
  color: var(--text-primary);
  font-size: 1.875rem;
  font-weight: 700;
  margin: 0;
}

.tabs {
  display: flex;
  margin-bottom: var(--space-6);
  border-bottom: 1px solid var(--border-light);
}

.tab-btn {
  flex: 1;
  padding: var(--space-3) 0;
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  color: var(--primary-500);
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--primary-500);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.form-label {
  color: var(--text-primary);
  font-weight: 500;
  font-size: 0.875rem;
}

.w-full {
  width: 100%;
  margin-top: var(--space-2);
}

@media (max-width: 480px) {
  .login-form-wrapper {
    padding: var(--space-6);
  }
  
  .login-title h2 {
    font-size: 1.5rem;
  }
}
</style>
```

### 6.2 卡片组件

```vue
<template>
  <div class="card">
    <div class="card-header" v-if="title">
      <h3 class="card-title">{{ title }}</h3>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
    <div class="card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.card {
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.card-header {
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--bg-primary);
}

.card-title {
  color: var(--text-primary);
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0;
}

.card-body {
  padding: var(--space-6);
}

.card-footer {
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--border-light);
  background-color: var(--bg-primary);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-4);
}
</style>
```

## 7. 主题切换功能实现

### 7.1 主题切换工具函数

创建 `src/utils/theme.js` 文件：

```javascript
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
```

### 7.2 主题切换组件

```vue
<template>
  <div class="theme-switcher">
    <button 
      v-for="theme in availableThemes" 
      :key="theme.value"
      class="theme-btn"
      :class="{ active: currentTheme === theme.value }"
      @click="setTheme(theme.value)"
      :title="theme.label"
    >
      <div class="theme-indicator" :style="{ backgroundColor: theme.color }"></div>
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
  gap: var(--space-2);
  align-items: center;
}

.theme-btn {
  width: 32px;
  height: 32px;
  border: 2px solid var(--border-medium);
  border-radius: 50%;
  background: none;
  cursor: pointer;
  padding: 2px;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.theme-btn:hover {
  border-color: var(--primary-500);
  transform: scale(1.1);
}

.theme-btn.active {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 2px rgb(14 165 233 / 0.2);
}

.theme-indicator {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}
</style>
```

## 8. 主题切换实战

### 8.1 应用入口文件配置

在 `src/main.js` 中初始化主题：

```javascript
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import { initTheme } from './utils/theme'

// 初始化主题
initTheme()

const app = createApp(App)
app.use(router)
app.mount('#app')
```

### 8.2 全局布局组件

```vue
<template>
  <div class="app">
    <Navbar />
    <main class="main-content">
      <slot></slot>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Navbar from './Navbar.vue'
import Footer from './Footer.vue'
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-secondary);
}

.main-content {
  flex: 1;
  padding: var(--space-8) 0;
}
</style>
```

## 9. 最佳实践

### 9.1 CSS变量使用规范

- **命名规范**：使用 `--[prefix]-[name]` 格式，如 `--primary-500`
- **分类管理**：按功能和用途分类组织CSS变量
- **默认值**：为所有CSS变量提供合理的默认值
- **语义化**：使用语义化的变量名，如 `--text-primary` 而非 `--color-1`

### 9.2 性能优化

- **减少重绘**：主题切换时使用 `transition` 平滑过渡
- **避免过度使用变量**：只对需要主题化的属性使用CSS变量
- **合理组织样式**：使用CSS预处理器的嵌套功能，保持样式结构清晰
- **按需加载**：对于大型应用，考虑按需加载主题样式

### 9.3 可访问性

- **对比度**：确保文本与背景的对比度符合WCAG标准
- **键盘导航**：所有交互元素都支持键盘导航
- **屏幕阅读器**：为所有交互元素提供适当的ARIA属性
- **响应式设计**：确保在不同设备上都有良好的显示效果

### 9.4 开发工具

- **浏览器开发者工具**：使用浏览器的开发者工具调试CSS变量
- **CSS linting**：使用Stylelint等工具检查CSS代码质量
- **代码格式化**：使用Prettier等工具统一代码格式
- **版本控制**：使用Git等版本控制系统管理样式文件

## 10. 总结

通过CSS变量和主题切换机制，我们可以实现高度可定制的前端界面。这种方法不仅提升了用户体验，还提高了开发效率和代码可维护性。在实际开发中，应根据项目需求和设计规范，灵活运用本文档介绍的技术和最佳实践，打造出美观、易用、可定制的前端界面。

## 11. 附录

### 11.1 浏览器兼容性

CSS变量在现代浏览器中得到了广泛支持：

- Chrome 49+
- Firefox 31+
- Safari 9.1+
- Edge 15+

对于不支持CSS变量的旧浏览器，可以使用PostCSS等工具进行降级处理。

### 11.2 参考资源

- [MDN Web Docs - CSS Variables](https://developer.mozilla.org/en-US/docs/Web/CSS/Using_CSS_custom_properties)
- [CSS-Tricks - CSS Variables Guide](https://css-tricks.com/a-complete-guide-to-custom-properties/)
- [WCAG 2.1 Guidelines](https://www.w3.org/TR/WCAG21/)
- [Vue 3 Documentation](https://v3.vuejs.org/)
- [Element Plus Documentation](https://element-plus.org/en-US/)
