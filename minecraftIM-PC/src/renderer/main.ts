import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import './styles/theme.css';
import './styles/global.css';
import './styles/im.css';
import App from './App.vue';
import { setupI18n } from './locales';
import { initTheme } from './stores/theme';

async function bootstrap(): Promise<void> {
  // 首屏前应用主题，避免闪烁
  initTheme();

  const app = createApp(App);
  app.use(createPinia());
  app.use(ElementPlus);

  await setupI18n(app);
  app.mount('#app');
}

void bootstrap();
