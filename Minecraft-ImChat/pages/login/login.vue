<template>
  <view class="login-page">
    <!-- 顶部装饰 -->
    <view class="top-decoration">
      <view class="decoration-circle circle-1"></view>
      <view class="decoration-circle circle-2"></view>
    </view>

    <!-- Logo区域 -->
    <view class="logo-section">
      <view class="logo-icon">✈</view>
      <text class="logo-title">博览通讯</text>
      <text class="logo-subtitle">连接每一段精彩旅程</text>
    </view>

    <!-- 表单卡片 -->
    <view class="form-card">
      <!-- 切换登录方式 -->
      <view class="tab-bar">
        <view
          :class="['tab-item', { active: loginType === 'account' }]"
          @click="switchLoginType('account')"
        >
          <text>账号登录</text>
        </view>
        <view
          :class="['tab-item', { active: loginType === 'phone' }]"
          @click="switchLoginType('phone')"
        >
          <text>手机登录</text>
        </view>
      </view>

      <!-- 账号密码登录 -->
      <view v-if="loginType === 'account'" class="form-content">
        <!-- 用户头像显示 -->
        <view v-if="loginForm.account" class="avatar-preview-wrapper">
          <image
            class="avatar-preview"
            :src="loginForm.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <text class="avatar-name">{{ loginForm.userName || '用户' }}</text>
        </view>

        <!-- 账号输入框 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-icon">👤</text>
            <text>账号</text>
          </view>
          <input
            class="form-input"
            v-model="loginForm.account"
            placeholder="请输入账号"
            placeholder-class="input-placeholder"
            :maxlength="50"
            @input="handleAccountInput"
          />
          <view v-if="errors.account" class="error-text">
            <text>⚠ {{ errors.account }}</text>
          </view>
        </view>

        <!-- 密码输入框 -->
        <view class="form-item">
          <view class="form-label">
            <text class="label-icon">🔒</text>
            <text>密码</text>
          </view>
          <view class="password-wrapper">
            <input
              class="form-input"
              v-model="loginForm.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              placeholder-class="input-placeholder"
              :maxlength="20"
              @input="clearError('password')"
            />
            <view class="toggle-password" @click="togglePassword">
              <text>{{ showPassword ? '🙈' : '👁️' }}</text>
            </view>
          </view>
          <view v-if="errors.password" class="error-text">
            <text>⚠ {{ errors.password }}</text>
          </view>
        </view>
      </view>

      <!-- 手机号登录 -->
      <view v-else class="form-content">
        <view class="form-item">
          <view class="form-label">
            <text class="label-icon">📱</text>
            <text>手机号</text>
          </view>
          <input
            class="form-input"
            v-model="phoneForm.phone"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="input-placeholder"
            :maxlength="11"
          />
          <view v-if="phoneErrors.phone" class="error-text">
            <text>⚠ {{ phoneErrors.phone }}</text>
          </view>
        </view>

        <view class="form-item">
          <view class="form-label">
            <text class="label-icon">🔑</text>
            <text>验证码</text>
          </view>
          <view class="code-wrapper">
            <input
              class="form-input"
              v-model="phoneForm.code"
              type="number"
              placeholder="请输入验证码"
              placeholder-class="input-placeholder"
              :maxlength="6"
            />
            <view
              :class="['send-code-btn', { disabled: phoneForm.countdown > 0 }]"
              @click="sendCode"
            >
              <text>{{ phoneForm.countdown > 0 ? `${phoneForm.countdown}s` : '发送' }}</text>
            </view>
          </view>
          <view v-if="phoneErrors.code" class="error-text">
            <text>⚠ {{ phoneErrors.code }}</text>
          </view>
        </view>
      </view>

      <!-- 记住我 & 忘记密码 -->
      <view class="form-options">
        <view class="remember-me" @click="toggleRemember">
          <view :class="['checkbox', { checked: loginForm.rememberMe }]">
            <text v-if="loginForm.rememberMe">✓</text>
          </view>
          <text class="remember-text">记住我</text>
        </view>
        <text class="forgot-link" @click="goToForgotPassword">忘记密码？</text>
      </view>

      <!-- 登录按钮 -->
      <button
        :class="['submit-btn', { loading: isLoading }]"
        :disabled="isLoading"
        @click="handleLogin"
      >
        <text v-if="!isLoading">立即登录</text>
        <view v-else class="loading-content">
          <view class="loading-spinner"></view>
          <text>登录中...</text>
        </view>
      </button>

      <!-- 注册链接 -->
      <view class="register-row">
        <text class="register-text">还没有账号？</text>
        <text class="register-link" @click="goToRegister">立即注册</text>
      </view>

      <!-- 协议 -->
      <view class="agreement-row">
        <view :class="['checkbox-small', { checked: agreed }]" @click="agreed = !agreed">
          <text v-if="agreed">✓</text>
        </view>
        <text class="agreement-text">
          已阅读并同意
          <text class="agreement-link" @click.stop="showAgreement('user')">《用户协议》</text>
          和
          <text class="agreement-link" @click.stop="showAgreement('privacy')">《隐私政策》</text>
        </text>
      </view>
    </view>

    <!-- 其他登录方式 -->
    <view class="other-login">
      <view class="divider">
        <view class="divider-line"></view>
        <text class="divider-text">其他方式登录</text>
        <view class="divider-line"></view>
      </view>
      <view class="login-methods">
        <view class="method-item" @click="wechatLogin">
          <view class="method-icon wechat-icon">💬</view>
          <text class="method-name">微信</text>
        </view>
        <view class="method-item" @click="qqLogin">
          <view class="method-icon qq-icon">🐧</view>
          <text class="method-name">QQ</text>
        </view>
        <view class="method-item" @click="appleLogin">
          <view class="method-icon apple-icon"></view>
          <text class="method-name">Apple</text>
        </view>
      </view>
    </view>

    <!-- 错误提示模态框 -->
    <view v-if="showErrorModal" class="error-modal" @click="showErrorModal = false">
      <view class="error-content" @click.stop>
        <view class="error-icon">⚠️</view>
        <text class="error-title">登录失败</text>
        <text class="error-message">{{ errorMessage }}</text>
        <view class="error-btn" @click="showErrorModal = false">
          <text>确定</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { login, getUserByAccount, sendResetCode } from '../../utils/chat-api.js'
import { setToken, setUserInfo, getUserInfo } from '../../utils/storage.js'
import wsService from '../../utils/websocket.js'

export default {
  data() {
    return {
      loginType: 'account', // account | phone
      showPassword: false,
      isLoading: false,
      agreed: false,
      showErrorModal: false,
      errorMessage: '',

      // 账号登录表单
      loginForm: {
        account: '',
        password: '',
        avatar: '',
        userName: '',
        rememberMe: false
      },

      // 手机号登录表单
      phoneForm: {
        phone: '',
        code: '',
        countdown: 0
      },

      // 错误信息
      errors: {
        account: '',
        password: ''
      },

      phoneErrors: {
        phone: '',
        code: ''
      },

      // 默认头像
      defaultAvatar: '/static/default-avatar.png',
      countdownTimer: null
    }
  },

  onLoad() {
    this.loadRememberedAccount()
  },

  onUnload() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  },

  methods: {
    /**
     * 切换登录方式
     */
    switchLoginType(type) {
      this.loginType = type
      this.clearAllErrors()
    },

    /**
     * 切换密码显示
     */
    togglePassword() {
      this.showPassword = !this.showPassword
    },

    /**
     * 切换记住我
     */
    toggleRemember() {
      this.loginForm.rememberMe = !this.loginForm.rememberMe
    },

    /**
     * 加载记住的账号
     */
    loadRememberedAccount() {
      const remembered = uni.getStorageSync('rememberedAccount')
      if (remembered) {
        this.loginForm.account = remembered
        this.loginForm.rememberMe = true
        this.handleAccountInput()
      }
    },

    /**
     * 账号输入处理 - 实时获取头像
     */
    async handleAccountInput() {
      const account = this.loginForm.account.trim()
      this.clearError('account')

      if (!account) {
        this.loginForm.avatar = ''
        this.loginForm.userName = ''
        return
      }

      // 尝试获取用户头像
      try {
        const res = await getUserByAccount(account)
        if (res.code === 200 && res.data) {
          this.loginForm.avatar = res.data.avatar || ''
          this.loginForm.userName = res.data.username || ''
        }
      } catch (e) {
        // 静默失败，不影响输入
        this.loginForm.avatar = ''
        this.loginForm.userName = ''
      }
    },

    /**
     * 清除错误
     */
    clearError(field) {
      this.errors[field] = ''
    },

    /**
     * 清除所有错误
     */
    clearAllErrors() {
      this.errors = { account: '', password: '' }
      this.phoneErrors = { phone: '', code: '' }
    },

    /**
     * 验证账号密码表单
     */
    validateAccountForm() {
      let isValid = true

      if (!this.loginForm.account.trim()) {
        this.errors.account = '请输入账号'
        isValid = false
      } else if (this.loginForm.account.length < 3) {
        this.errors.account = '账号至少3个字符'
        isValid = false
      }

      if (!this.loginForm.password) {
        this.errors.password = '请输入密码'
        isValid = false
      } else if (this.loginForm.password.length < 6) {
        this.errors.password = '密码至少6个字符'
        isValid = false
      }

      return isValid
    },

    /**
     * 验证手机号表单
     */
    validatePhoneForm() {
      let isValid = true

      const phoneRegex = /^1\d{10}$/
      if (!this.phoneForm.phone) {
        this.phoneErrors.phone = '请输入手机号'
        isValid = false
      } else if (!phoneRegex.test(this.phoneForm.phone)) {
        this.phoneErrors.phone = '请输入有效的11位手机号'
        isValid = false
      }

      if (!this.phoneForm.code) {
        this.phoneErrors.code = '请输入验证码'
        isValid = false
      } else if (this.phoneForm.code.length < 4) {
        this.phoneErrors.code = '验证码至少4位'
        isValid = false
      }

      return isValid
    },

    /**
     * 发送验证码
     */
    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.phoneForm.phone)) {
        this.phoneErrors.phone = '请输入有效的手机号'
        return
      }

      if (this.phoneForm.countdown > 0) return

      try {
        const res = await sendResetCode(this.phoneForm.phone)
        if (res.code === 200) {
          uni.showToast({ title: '验证码已发送', icon: 'success' })
          this.startCountdown()
        } else {
          uni.showToast({ title: res.message || '发送失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '发送失败，请重试', icon: 'none' })
      }
    },

    /**
     * 开始倒计时
     */
    startCountdown() {
      this.phoneForm.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.phoneForm.countdown--
        if (this.phoneForm.countdown <= 0) {
          clearInterval(this.countdownTimer)
        }
      }, 1000)
    },

    /**
     * 处理登录
     */
    async handleLogin() {
      if (this.isLoading) return

      // 验证协议
      if (!this.agreed) {
        uni.showToast({ title: '请先同意用户协议', icon: 'none' })
        return
      }

      // 验证表单
      if (this.loginType === 'account') {
        if (!this.validateAccountForm()) return
        await this.doAccountLogin()
      } else {
        if (!this.validatePhoneForm()) return
        await this.doPhoneLogin()
      }
    },

    /**
     * 账号密码登录
     */
    async doAccountLogin() {
      this.isLoading = true

      try {
        const response = await login({
          account: this.loginForm.account.trim(),
          password: this.loginForm.password
        })

        console.log('登录响应:', response)

        if (response.code === 200) {
          if (!response.data) {
            throw new Error('响应数据为空')
          }

          // 兼容后端可能返回数组的情况
          const data = Array.isArray(response.data) ? response.data[0] : response.data

          if (!data) {
            throw new Error('用户数据为空')
          }

          const userId = data.userId
          const userName = data.username || data.account

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
            avatar: data.avatar || this.loginForm.avatar,
            account: data.account || this.loginForm.account,
            ...data
          }
          setUserInfo(userInfo)

          // 记住账号
          if (this.loginForm.rememberMe) {
            uni.setStorageSync('rememberedAccount', this.loginForm.account)
          } else {
            uni.removeStorageSync('rememberedAccount')
          }

          // 连接 WebSocket
          wsService.connect(userId)

          uni.showToast({
            title: '登录成功',
            icon: 'success',
            duration: 1500
          })

          // 跳转到首页
          setTimeout(() => {
            uni.switchTab({ url: '/pages/index/index' })
          }, 1500)
        } else {
          this.showError(response.message || '登录失败，请检查账号密码')
        }
      } catch (error) {
        console.error('登录失败:', error)
        this.showError(error.message || '登录失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    /**
     * 手机号登录
     */
    async doPhoneLogin() {
      this.isLoading = true

      try {
        // 模拟手机号登录（实际项目应调用对应接口）
        const response = await login({
          phone: this.phoneForm.phone,
          code: this.phoneForm.code,
          type: 'phone'
        })

        if (response.code === 200) {
          // 保存登录信息（同账号登录）
          const data = Array.isArray(response.data) ? response.data[0] : response.data

          if (data.token) {
            setToken(data.token)
          }

          setUserInfo({
            token: data.token || '',
            username: data.username || this.phoneForm.phone,
            id: data.userId,
            phone: this.phoneForm.phone,
            ...data
          })

          uni.showToast({ title: '登录成功', icon: 'success' })

          setTimeout(() => {
            uni.switchTab({ url: '/pages/index/index' })
          }, 1500)
        } else {
          this.showError(response.message || '登录失败')
        }
      } catch (error) {
        this.showError('登录失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    /**
     * 显示错误
     */
    showError(message) {
      this.errorMessage = message
      this.showErrorModal = true
    },

    /**
     * 跳转到注册
     */
    goToRegister() {
      uni.navigateTo({ url: '/pages/register/register' })
    },

    /**
     * 跳转到忘记密码
     */
    goToForgotPassword() {
      uni.navigateTo({ url: '/pages/forgot-password/forgot-password' })
    },

    /**
     * 显示协议
     */
    showAgreement(type) {
      uni.showModal({
        title: type === 'user' ? '用户协议' : '隐私政策',
        content: type === 'user'
          ? '欢迎使用博览通讯，请遵守相关法律法规，文明使用本应用。'
          : '我们重视您的隐私，保护您的个人信息安全。',
        showCancel: false,
        confirmText: '我知道了'
      })
    },

    /**
     * 微信登录
     */
    wechatLogin() {
      uni.showToast({ title: '微信登录功能开发中', icon: 'none' })
    },

    /**
     * QQ登录
     */
    qqLogin() {
      uni.showToast({ title: 'QQ登录功能开发中', icon: 'none' })
    },

    /**
     * Apple登录
     */
    appleLogin() {
      uni.showToast({ title: 'Apple登录功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e8f4ff 0%, #f5f5f5 50%);
  padding: 0 40rpx 60rpx;
  position: relative;
  overflow: hidden;
}

/* 顶部装饰 */
.top-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 400rpx;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
}

.circle-1 {
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, #2997ff 0%, transparent 70%);
  top: -200rpx;
  right: -100rpx;
}

.circle-2 {
  width: 300rpx;
  height: 300rpx;
  background: radial-gradient(circle, #34c759 0%, transparent 70%);
  top: -100rpx;
  left: -150rpx;
}

/* Logo区域 */
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0 60rpx;
  position: relative;
  z-index: 1;
}

.logo-icon {
  font-size: 100rpx;
  color: #2997ff;
  margin-bottom: 20rpx;
  animation: bounce 2s ease-in-out infinite;
}

.logo-title {
  font-size: 48rpx;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 12rpx;
  letter-spacing: 2rpx;
}

.logo-subtitle {
  font-size: 26rpx;
  color: #6e6e73;
  letter-spacing: 1rpx;
}

/* 表单卡片 */
.form-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 40rpx 36rpx;
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 1;
}

/* 标签栏 */
.tab-bar {
  display: flex;
  border-bottom: 2rpx solid #f0f0f0;
  margin-bottom: 30rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  position: relative;
}

.tab-item text {
  font-size: 32rpx;
  color: #6e6e73;
  font-weight: 500;
}

.tab-item.active text {
  color: #1d1d1f;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 6rpx;
  background: #2997ff;
  border-radius: 3rpx;
}

/* 头像预览 */
.avatar-preview-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 20rpx 0 30rpx;
}

.avatar-preview {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #f5f5f5;
  border: 4rpx solid #2997ff;
  box-shadow: 0 6rpx 16rpx rgba(41, 151, 255, 0.3);
}

.avatar-name {
  font-size: 26rpx;
  color: #6e6e73;
  margin-top: 12rpx;
}

/* 表单内容 */
.form-content {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #1d1d1f;
  font-weight: 500;
}

.label-icon {
  font-size: 28rpx;
}

.form-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  box-sizing: border-box;
}

.input-placeholder {
  color: #a1a1a6;
}

.password-wrapper {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-password text {
  font-size: 36rpx;
}

.code-wrapper {
  position: relative;
  display: flex;
  gap: 16rpx;
}

.code-wrapper .form-input {
  flex: 1;
}

.send-code-btn {
  width: 180rpx;
  height: 88rpx;
  background: #2997ff;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-code-btn text {
  color: #fff;
  font-size: 26rpx;
  font-weight: 500;
}

.send-code-btn.disabled {
  background: #e5e5e5;
}

.send-code-btn.disabled text {
  color: #6e6e73;
}

.error-text {
  font-size: 24rpx;
  color: #ff3b30;
  padding-left: 8rpx;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
  margin-bottom: 30rpx;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #d2d2d6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  transition: all 0.2s ease;
}

.checkbox.checked {
  background: #2997ff;
  border-color: #2997ff;
}

.checkbox text {
  font-size: 24rpx;
  color: #fff;
}

.remember-text {
  font-size: 26rpx;
  color: #6e6e73;
}

.forgot-link {
  font-size: 26rpx;
  color: #2997ff;
}

/* 登录按钮 */
.submit-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #2997ff 0%, #1e6bb8 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(41, 151, 255, 0.3);
  transition: all 0.3s ease;
}

.submit-btn::after {
  border: none;
}

.submit-btn:not([disabled]):active {
  transform: scale(0.98);
}

.submit-btn[disabled] {
  background: #a1c4ff;
  box-shadow: none;
}

.submit-btn text {
  font-size: 34rpx;
  color: #fff;
  font-weight: 600;
  letter-spacing: 2rpx;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.loading-spinner {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 注册链接 */
.register-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 24rpx;
  gap: 8rpx;
}

.register-text {
  font-size: 26rpx;
  color: #6e6e73;
}

.register-link {
  font-size: 26rpx;
  color: #2997ff;
  font-weight: 500;
}

/* 协议 */
.agreement-row {
  display: flex;
  align-items: flex-start;
  margin-top: 30rpx;
  gap: 12rpx;
}

.checkbox-small {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #d2d2d6;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  flex-shrink: 0;
  margin-top: 2rpx;
}

.checkbox-small.checked {
  background: #2997ff;
  border-color: #2997ff;
}

.checkbox-small text {
  font-size: 20rpx;
  color: #fff;
}

.agreement-text {
  font-size: 24rpx;
  color: #6e6e73;
  line-height: 1.5;
  flex: 1;
}

.agreement-link {
  color: #2997ff;
}

/* 其他登录方式 */
.other-login {
  margin-top: 60rpx;
  position: relative;
  z-index: 1;
}

.divider {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.divider-line {
  flex: 1;
  height: 1rpx;
  background: #d2d2d6;
}

.divider-text {
  font-size: 24rpx;
  color: #a1a1a6;
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: 80rpx;
}

.method-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.method-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  transition: transform 0.3s ease;
}

.method-item:active .method-icon {
  transform: scale(0.95);
}

.wechat-icon {
  background: #07c160;
  color: #fff;
}

.qq-icon {
  background: #12b7f5;
  color: #fff;
}

.apple-icon {
  background: #000;
  position: relative;
}

.apple-icon::after {
  content: '';
  width: 30rpx;
  height: 36rpx;
  background: #fff;
  -webkit-mask: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><path d='M18 17.6c-.3.7-.7 1.3-1.2 1.8-.6.7-1.1 1.1-1.6 1.2-.4.1-.9 0-1.5-.2-.6-.2-1.1-.3-1.6-.3s-1 .1-1.6.3c-.5.2-1 .3-1.4.2-.5-.1-1-.5-1.6-1.2-1-1-1.7-2.3-2.1-3.8-.5-1.7-.2-3.4.7-4.6.7-.9 1.6-1.4 2.7-1.5.5 0 1.1.1 1.7.4.6.2 1 .3 1.2.3.2 0 .6-.1 1.2-.4.7-.3 1.4-.4 1.9-.4 1.4.1 2.5.7 3.2 1.7-1.2.7-1.8 1.7-1.8 3 0 1.1.4 2 1.2 2.7.4.3.7.5 1.1.6-.1.3-.1.5-.2.7zM15 4.3c.5-.6.7-1.3.7-2 0-.1 0-.2-.1-.3-.6.1-1.2.4-1.8.9-.5.4-.8 1-.8 1.6 0 .1 0 .2.1.3.6 0 1.2-.3 1.9-.5z'/></svg>") center/contain no-repeat;
  background: #fff;
}

.method-name {
  font-size: 24rpx;
  color: #6e6e73;
}

/* 错误模态框 */
.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.error-content {
  width: 560rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.error-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.error-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.error-message {
  font-size: 28rpx;
  color: #6e6e73;
  text-align: center;
  line-height: 1.5;
  margin-bottom: 40rpx;
}

.error-btn {
  width: 100%;
  height: 80rpx;
  background: #2997ff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-btn text {
  font-size: 30rpx;
  color: #fff;
  font-weight: 500;
}

/* 动画 */
@keyframes bounce {

  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }

  40% {
    transform: translateY(-10rpx);
  }

  60% {
    transform: translateY(-5rpx);
  }
}
</style>
