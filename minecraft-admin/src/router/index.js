// router/index.js
import { pa } from 'element-plus/es/locales.mjs'
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
      path: '/home',
      name: 'Home',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '首页',
        requireAuth: true
      }
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: {
        title: '登录',
        requireAuth: false
      }
    },
    // 管理
    {
      path: '/',
      name: 'Admin',
      component: () => import('@/views/admin/index.vue'),
      meta: {
        title: '博览管理系统',
        requireAuth: true
      }
    },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '管理系统'
  
  // 检查是否需要认证
  if (to.meta.requireAuth) {
    // 检查是否已登录
    const token = localStorage.getItem('token')
    const user = localStorage.getItem('user')
    
    if (token && user) {
      // 已登录，继续导航
      next()
    } else {
      // 未登录，重定向到登录页
      next({ name: 'Login' })
    }
  } else {
    // 不需要认证的页面，直接放行
    next()
  }
})

export default router