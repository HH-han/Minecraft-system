<script>
import { auth } from './utils/auth.js'
import { getUserInfo } from './utils/storage.js'
import wsService from './utils/websocket.js'
import { initGlobalWs, refreshPendingCount } from './utils/global-ws.js'
import store, { setUser, syncBadge } from './store/index.js'
import env from './config/index.js'

export default {
  onLaunch: function () {
    console.log('App Launch', env.NODE_ENV, env.API_BASE_URL)

    // 恢复全局用户态
    const userInfo = getUserInfo()
    if (userInfo) setUser(userInfo)

    // 注册全局实时消息路由（会话摘要 / 未读角标）
    initGlobalWs()

    this.checkLoginStatus()
  },
  onShow: function () {
    if (store.userInfo) {
      syncBadge()
      refreshPendingCount()
    }
  },
  onHide: function () {},
  onError: function (err) {
    console.error('App Error:', err)
  },
  methods: {
    checkLoginStatus() {
      const loggedIn = auth.isLoggedIn()

      if (loggedIn) {
        const userInfo = auth.getUserInfo()
        if (userInfo && userInfo.id) {
          setUser(userInfo)
          wsService.connect(userInfo.id)
          refreshPendingCount()
          syncBadge()
        }
      }

      // 未登录且不在公开页面时，跳转登录页
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      const currentPath = (currentPage && currentPage.route) || ''
      const publicPages = [
        'pages/login/login',
        'pages/register/register',
        'pages/forgot-password/forgot-password'
      ]
      const isPublicPage = publicPages.some((p) => currentPath.includes(p))

      if (!loggedIn && !isPublicPage) {
        uni.reLaunch({ url: '/pages/login/login' })
      }
    }
  }
}
</script>

<style>
@import './static/css/common.css';
</style>
