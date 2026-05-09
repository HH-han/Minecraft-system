import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getUsername, setUsername, removeUsername, getUserInfo, setUserInfo, removeUserInfo } from '@/utils/storage'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken() || '',
    username: getUsername() || '',
    isAuthenticated: !!getToken(),
    userInfo: getUserInfo(),
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
      // 确保用户ID被正确保存
      if (!userInfo.id && userInfo.user?.id) {
        userInfo.id = userInfo.user.id
      }
      this.userInfo = userInfo
      this.username = userInfo.username || userInfo.account
      this.token = userInfo.token
      this.isAuthenticated = true
      
      // 存储到本地
      setToken(userInfo.token)
      setUsername(userInfo.username || userInfo.account)
      setUserInfo(userInfo)
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
      removeUserInfo()
    },
    
    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      setUserInfo(this.userInfo)
    },
    
    // 邮箱登录
    async emailLogin(data) {
      const response = await request.post('/api/auth/email-login', data)
      if (response.code === 200) {
        const userData = response.data
        const userInfo = {
          token: userData.token,
          username: userData.username || userData.user?.username || userData.user?.email,
          id: userData.id || userData.user?.id,
          ...(userData.user || {})
        }
        this.setUserInfo(userInfo)
      }
      return response
    },
    
    // 账号密码登录
    async login(data) {
      const response = await request.post('/auth/login', data)
      if (response.code === 200) {
        const userData = response.data
        const userInfo = {
          token: userData.token,
          username: userData.username,
          id: userData.id,
          ...userData
        }
        this.setUserInfo(userInfo)
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
