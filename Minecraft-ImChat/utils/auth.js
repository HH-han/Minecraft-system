/**
 * 认证管理工具
 *
 * 参照 minecraft-App/utils/auth.js 实现
 * 提供登录、注册、退出、Token 管理等认证相关功能
 */

import { API_CONFIG } from './config.js'
import { post, get, put, del } from './request.js'
import {
  setToken,
  getToken,
  removeToken,
  setUserInfo,
  getUserInfo,
  removeUserInfo,
  isLoggedIn,
  setRememberedAccount,
  getRememberedAccount,
  removeRememberedAccount
} from './storage.js'
import wsService from './websocket.js'

const { AUTH, USER } = API_CONFIG

/**
 * 认证管理 API
 */
export const authApi = {
  /**
   * 登录
   * @param {Object} data 登录数据 { account, password }
   * @returns {Promise} 登录结果
   */
  login: (data) => post(AUTH.BASE + AUTH.ENDPOINTS.LOGIN, data),

  /**
   * 注册
   * @param {Object} data 注册数据 { username, password, email, phone, avatar }
   * @returns {Promise} 注册结果
   */
  register: (data) => post(AUTH.BASE + AUTH.ENDPOINTS.REGISTER, data),

  /**
   * 退出登录
   * @returns {Promise} 退出结果
   */
  logout: () => post(AUTH.BASE + AUTH.ENDPOINTS.LOGOUT),

  /**
   * 发送验证码
   * @param {Object} data 邮箱数据
   * @returns {Promise} 发送结果
   */
  sendCode: (data) => post(AUTH.BASE + AUTH.ENDPOINTS.SEND_CODE, data),

  /**
   * 邮箱验证码登录
   * @param {Object} data 登录数据
   * @returns {Promise} 登录结果
   */
  emailLogin: (data) => post(AUTH.BASE + AUTH.ENDPOINTS.EMAIL_LOGIN, data),

  /**
   * 发送重置密码验证码
   * @param {String} phone 手机号
   * @returns {Promise} 发送结果
   */
  sendResetCode: (phone) => post(AUTH.BASE + AUTH.ENDPOINTS.RESET_CODE, { phone }),

  /**
   * 重置密码
   * @param {Object} data { phone, code, newPassword }
   * @returns {Promise} 重置结果
   */
  resetPassword: (data) => post(AUTH.BASE + AUTH.ENDPOINTS.RESET_PASSWORD, data)
}

/**
 * 用户管理 API
 */
export const userApi = {
  /**
   * 获取用户信息
   * @returns {Promise} 用户信息
   */
  getUserInfo: () => get(USER.BASE + USER.ENDPOINTS.INFO),

  /**
   * 根据账号获取用户信息
   * @param {String} account 用户账号
   * @returns {Promise} 用户信息
   */
  getUserByAccount: (account) => get(USER.BASE + USER.ENDPOINTS.BY_ACCOUNT, { account })
}

/**
 * 认证业务封装
 */
export const auth = {
  /**
   * 检查是否已登录
   * @returns {Boolean} 是否已登录
   */
  isLoggedIn,

  /**
   * 获取当前 Token
   * @returns {String} Token
   */
  getToken,

  /**
   * 获取当前用户信息
   * @returns {Object} 用户信息
   */
  getUserInfo,

  /**
   * 处理登录响应
   * @param {Object} response 登录响应
   * @returns {Object} 处理后的用户信息
   */
  handleLoginResponse(response) {
    if (response.code !== 200) {
      throw new Error(response.message || '登录失败')
    }

    if (!response.data) {
      throw new Error('响应数据为空')
    }

    // 兼容后端可能返回数组的情况
    const data = Array.isArray(response.data) ? response.data[0] : response.data

    if (!data) {
      throw new Error('用户数据为空')
    }

    const userId = data.userId || data.id
    const userName = data.username || data.account || data.name

    if (!userId) {
      throw new Error('未获取到用户ID')
    }

    if (!userName) {
      throw new Error('未获取到用户名')
    }

    // 保存 Token
    if (data.token) {
      setToken(data.token)
    }

    // 保存用户信息
    const userInfo = {
      token: data.token || '',
      username: userName,
      id: userId,
      avatar: data.avatar || '',
      account: data.account || '',
      ...data
    }
    setUserInfo(userInfo)

    // 连接 WebSocket
    wsService.connect(userId)

    return userInfo
  },

  /**
   * 完整登录流程
   * @param {Object} data 登录数据
   * @param {Object} options 配置项 { rememberMe }
   * @returns {Promise} 登录结果
   */
  async login(data, options = {}) {
    const response = await authApi.login(data)
    const userInfo = this.handleLoginResponse(response)

    // 处理记住我
    if (options.rememberMe) {
      setRememberedAccount(data.account)
    } else {
      removeRememberedAccount()
    }

    return userInfo
  },

  /**
   * 退出登录
   * @returns {Promise} 退出结果
   */
  async logout() {
    try {
      await authApi.logout()
    } catch (e) {
      console.error('退出登录接口调用失败:', e)
    } finally {
      // 无论接口是否成功，都清除本地数据
      removeToken()
      removeUserInfo()
      // 断开 WebSocket
      wsService.disconnect()
    }
  },

  /**
   * 跳转到登录页
   * @param {String} redirectUrl 登录成功后跳转的页面
   */
  redirectToLogin(redirectUrl = '') {
    uni.reLaunch({
      url: '/pages/login/login'
    })
  },

  /**
   * 加载记住的账号
   * @returns {String} 记住的账号
   */
  getRememberedAccount,

  /**
   * 清除记住的账号
   */
  clearRememberedAccount: removeRememberedAccount
}

// 默认导出
export default {
  authApi,
  userApi,
  auth
}
