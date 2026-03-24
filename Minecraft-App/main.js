import App from './App.vue'
import './src/styles/theme.css'
// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
import axios from 'axios'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App,
  axios
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif