// router/index.js
import { pa } from 'element-plus/es/locales.mjs'
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/index/index.vue')  
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue')
    },
    // 管理
    {
      path: '/admin',
      name: 'Admin',
      component: () => import('@/views/admin/index.vue'),
      meta: {
        title: '管理',
        requireAuth: true
      }
    },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router