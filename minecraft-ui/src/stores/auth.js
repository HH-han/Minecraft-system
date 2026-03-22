import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUsername, setUsername, removeUsername } from '@/utils/storage'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken() || '',
    username: getUsername() || '',
    isAuthenticated: !!getToken(),
    userInfo: null,
    currentComponentPath: '',
    pageState: {
      payment: {
        activeTab: 'product'
      }
    }
  }),
  
  getters: {
    getToken: (state) => state.token,
    getUsername: (state) => state.username,
    getUserInfo: (state) => state.userInfo,
    getIsAuthenticated: (state) => state.isAuthenticated,
    getPageState: (state) => state.pageState
  },
  
  actions: {
    // 登录成功后设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.username = userInfo.username || userInfo.account
      this.token = userInfo.token
      this.isAuthenticated = true
      
      // 存储到本地
      setToken(userInfo.token)
      setUsername(userInfo.username || userInfo.account)
    },
    
    // 登出
    logout() {
      this.token = ''
      this.username = ''
      this.isAuthenticated = false
      this.userInfo = null
      
      // 从本地存储移除
      removeToken()
      removeUsername()
    },
    
    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
    },
    
    // 邮箱登录
    async emailLogin(data) {
      const response = await request.post('/api/auth/email-login', data)
      if (response.code === 200) {
        this.setUserInfo(response.data)
      }
      return response
    },
    
    // 更新页面状态
    updatePageState(page, state) {
      this.pageState[page] = { ...this.pageState[page], ...state }
    },
    
    // 重置页面状态
    resetPageState(page) {
      this.pageState[page] = {}
    }
  },
  
  persist: true
})
