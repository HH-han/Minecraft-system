import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
// import './assets/styles/btn.css'
// import './assets/styles/global.css'
import { initTheme } from './utils/theme'
import { setupI18n } from './locales'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

async function bootstrap() {
  const app = createApp(App)
  const pinia = createPinia()
  pinia.use(piniaPluginPersistedstate)

  // 挂载全局属性
  app.config.globalProperties.$electron = window.electronAPI

  app.use(pinia)
  app.use(router)
  // Element Plus 语言包由 ElConfigProvider（App.vue）响应式驱动，随 i18n 联动切换
  app.use(ElementPlus, {
    size: 'default'
  })

  // 注册所有图标
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

  // 初始化主题
  initTheme()

  // 初始化国际化（等待首个语言包加载完成后再挂载，避免首屏闪烁）
  await setupI18n(app)

  app.mount('#app')
}

bootstrap()
