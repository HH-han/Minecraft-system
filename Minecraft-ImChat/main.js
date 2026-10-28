import App from './App'
import store from './store/index.js'
import { t, getLocale } from './locales/index.js'

/**
 * 全局混入：主题类名 + 国际化
 *
 * - themeClass：页面根节点绑定后即可继承对应主题 CSS 变量；
 * - $t：所有页面/组件统一使用的国际化方法。
 */
const appMixin = {
  computed: {
    themeClass() {
      return store.settings.theme === 'light' ? 'theme-light' : 'theme-dark'
    },
    isDarkTheme() {
      return store.settings.theme !== 'light'
    },
    localeCode() {
      return getLocale()
    }
  },
  methods: {
    $t(key, params) {
      return t(key, params)
    }
  }
}

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
Vue.mixin(appMixin)
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  app.mixin(appMixin)
  return {
    app
  }
}
// #endif
