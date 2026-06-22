<template>
  <view class="login-page">
    <view class="login-bg"></view>
    
    <view class="logo-section fade-in">
      <view class="logo-icon-container bounce-in">
        <text class="logo-icon">✈</text>
      </view>
      <text class="logo-title">博览通讯</text>
      <text class="logo-subtitle">连接每一段精彩旅程</text>
    </view>

    <view class="form-card slide-up">
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

      <view v-if="loginType === 'account'" class="form-content">
        <view v-if="loginForm.account" class="avatar-preview-wrapper slide-up">
          <image
            class="avatar-preview"
            :src="loginForm.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <text class="avatar-name">{{ loginForm.userName || '用户' }}</text>
        </view>

        <view class="form-item">
          <view class="input-wrapper">
            <text class="input-icon">👤</text>
            <input
              class="input-field"
              v-model="loginForm.account"
              placeholder="请输入账号"
              placeholder-class="input-placeholder"
              :maxlength="50"
              @input="handleAccountInput"
            />
          </view>
          <view v-if="errors.account" class="error-text shake">
            <text>⚠ {{ errors.account }}</text>
          </view>
        </view>

        <view class="form-item">
          <view class="input-wrapper">
            <text class="input-icon">🔑</text>
            <input
              class="input-field"
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
          <view v-if="errors.password" class="error-text shake">
            <text>⚠ {{ errors.password }}</text>
          </view>
        </view>
      </view>

      <view v-else class="form-content">
        <view class="form-item">
          <view class="input-wrapper">
            <text class="input-icon">📱</text>
            <input
              class="input-field"
              v-model="phoneForm.phone"
              type="number"
              placeholder="请输入手机号"
              placeholder-class="input-placeholder"
              :maxlength="11"
            />
          </view>
          <view v-if="phoneErrors.phone" class="error-text shake">
            <text>⚠ {{ phoneErrors.phone }}</text>
          </view>
        </view>

        <view class="form-item">
          <view class="input-wrapper">
            <text class="input-icon">📝</text>
            <input
              class="input-field code-input"
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
          <view v-if="phoneErrors.code" class="error-text shake">
            <text>⚠ {{ phoneErrors.code }}</text>
          </view>
        </view>
      </view>

      <view class="form-options">
        <view class="remember-me" @click="toggleRemember">
          <view :class="['checkbox', { checked: loginForm.rememberMe }]">
            <text v-if="loginForm.rememberMe">✓</text>
          </view>
          <text class="remember-text">记住我</text>
        </view>
        <text class="forgot-link" @click="goToForgotPassword">忘记密码？</text>
      </view>

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

      <view class="register-row">
        <text class="register-text">还没有账号？</text>
        <text class="link" @click="goToRegister">立即注册</text>
      </view>

      <view class="agreement-row">
        <view :class="['checkbox', { checked: agreed }]" @click="agreed = !agreed">
          <text v-if="agreed">✓</text>
        </view>
        <text class="agreement-text">
          已阅读并同意
          <text class="link" @click.stop="showAgreement('user')">《用户协议》</text>
          和
          <text class="link" @click.stop="showAgreement('privacy')">《隐私政策》</text>
        </text>
      </view>
    </view>

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

    <view v-if="showErrorModal" class="modal-overlay" @click="showErrorModal = false">
      <view class="modal-content scale-in" @click.stop>
        <view class="modal-icon">⚠️</view>
        <text class="modal-title">登录失败</text>
        <text class="modal-message">{{ errorMessage }}</text>
        <view class="modal-btn" @click="showErrorModal = false">
          <text>确定</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { auth, userApi } from '../../utils/auth.js'
import { getRememberedAccount } from '../../utils/storage.js'
import wsService from '../../utils/websocket.js'

export default {
  data() {
    return {
      loginType: 'account',
      showPassword: false,
      isLoading: false,
      agreed: false,
      showErrorModal: false,
      errorMessage: '',

      loginForm: {
        account: '',
        password: '',
        avatar: '',
        userName: '',
        rememberMe: false
      },

      phoneForm: {
        phone: '',
        code: '',
        countdown: 0
      },

      errors: {
        account: '',
        password: ''
      },

      phoneErrors: {
        phone: '',
        code: ''
      },

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
    switchLoginType(type) {
      this.loginType = type
      this.clearAllErrors()
    },

    togglePassword() {
      this.showPassword = !this.showPassword
    },

    toggleRemember() {
      this.loginForm.rememberMe = !this.loginForm.rememberMe
    },

    loadRememberedAccount() {
      const remembered = getRememberedAccount()
      if (remembered) {
        this.loginForm.account = remembered
        this.loginForm.rememberMe = true
        this.handleAccountInput()
      }
    },

    async handleAccountInput() {
      const account = this.loginForm.account.trim()
      this.clearError('account')

      if (!account) {
        this.loginForm.avatar = ''
        this.loginForm.userName = ''
        return
      }

      try {
        const res = await userApi.getUserByAccount(account)
        if (res.code === 200 && res.data) {
          this.loginForm.avatar = res.data.avatar || ''
          this.loginForm.userName = res.data.username || ''
        }
      } catch (e) {
        this.loginForm.avatar = ''
        this.loginForm.userName = ''
      }
    },

    clearError(field) {
      this.errors[field] = ''
    },

    clearAllErrors() {
      this.errors = { account: '', password: '' }
      this.phoneErrors = { phone: '', code: '' }
    },

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

    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.phoneForm.phone)) {
        this.phoneErrors.phone = '请输入有效的手机号'
        return
      }

      if (this.phoneForm.countdown > 0) return

      try {
        const res = await auth.authApi.sendResetCode(this.phoneForm.phone)
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

    startCountdown() {
      this.phoneForm.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.phoneForm.countdown--
        if (this.phoneForm.countdown <= 0) {
          clearInterval(this.countdownTimer)
        }
      }, 1000)
    },

    async handleLogin() {
      if (this.isLoading) return

      if (!this.agreed) {
        uni.showToast({ title: '请先同意用户协议', icon: 'none' })
        return
      }

      if (this.loginType === 'account') {
        if (!this.validateAccountForm()) return
        await this.doAccountLogin()
      } else {
        if (!this.validatePhoneForm()) return
        await this.doPhoneLogin()
      }
    },

    async doAccountLogin() {
      this.isLoading = true

      try {
        await auth.login(
          {
            account: this.loginForm.account.trim(),
            password: this.loginForm.password
          },
          {
            rememberMe: this.loginForm.rememberMe
          }
        )

        uni.showToast({
          title: '登录成功',
          icon: 'success',
          duration: 1500
        })

        setTimeout(() => {
          uni.switchTab({ url: '/pages/index/index' })
        }, 1500)
      } catch (error) {
        console.error('登录失败:', error)
        this.showError(error.message || '登录失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    async doPhoneLogin() {
      this.isLoading = true

      try {
        await auth.login(
          {
            phone: this.phoneForm.phone,
            code: this.phoneForm.code,
            type: 'phone'
          },
          {
            rememberMe: false
          }
        )

        uni.showToast({ title: '登录成功', icon: 'success' })

        setTimeout(() => {
          uni.switchTab({ url: '/pages/index/index' })
        }, 1500)
      } catch (error) {
        this.showError(error.message || '登录失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    showError(message) {
      this.errorMessage = message
      this.showErrorModal = true
    },

    goToRegister() {
      uni.navigateTo({ url: '/pages/register/register' })
    },

    goToForgotPassword() {
      uni.navigateTo({ url: '/pages/forgot-password/forgot-password' })
    },

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

    wechatLogin() {
      uni.showToast({ title: '微信登录功能开发中', icon: 'none' })
    },

    qqLogin() {
      uni.showToast({ title: 'QQ登录功能开发中', icon: 'none' })
    },

    appleLogin() {
      uni.showToast({ title: 'Apple登录功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-color: #f5f5f7;
  padding: 0 40rpx 60rpx;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: -50%;
  right: -50%;
  height: 500rpx;
  background: linear-gradient(180deg, #e8f0fe 0%, #f5f5f7 100%);
  border-radius: 0 0 50% 50%;
  z-index: 0;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0 60rpx;
  position: relative;
  z-index: 1;
}

.logo-icon-container {
  width: 160rpx;
  height: 160rpx;
  background: linear-gradient(135deg, #2997ff 0%, #5856d6 100%);
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  box-shadow: 0 20rpx 40rpx rgba(41, 151, 255, 0.3);
}

.logo-icon {
  font-size: 72rpx;
  color: #ffffff;
  font-weight: 300;
}

.logo-title {
  font-size: 56rpx;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 12rpx;
  letter-spacing: -1rpx;
  line-height: 1.1;
}

.logo-subtitle {
  font-size: 28rpx;
  color: #6e6e73;
  font-weight: 400;
  line-height: 1.4;
}

.form-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 1;
}

.tab-bar {
  display: flex;
  border-bottom: 1rpx solid #e5e5e5;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  position: relative;
  transition: all 0.25s ease;
}

.tab-item text {
  font-size: 32rpx;
  color: #6e6e73;
  font-weight: 500;
  letter-spacing: -0.3rpx;
}

.tab-item.active text {
  color: #1d1d1f;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 56rpx;
  height: 4rpx;
  background-color: #2997ff;
  border-radius: 2rpx;
}

.avatar-preview-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16rpx 0 32rpx;
}

.avatar-preview {
  width: 112rpx;
  height: 112rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  border: 4rpx solid #e5e5e5;
  transition: all 0.25s ease;
}

.avatar-name {
  font-size: 26rpx;
  color: #6e6e73;
  margin-top: 12rpx;
}

.form-content {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background-color: #f5f5f7;
  border-radius: 16rpx;
  padding: 0 24rpx;
  height: 88rpx;
  transition: all 0.2s ease;
  border: 2rpx solid transparent;
}

.input-wrapper:focus-within {
  background-color: #ffffff;
  border-color: #2997ff;
  box-shadow: 0 0 0 4rpx rgba(41, 151, 255, 0.1);
}

.input-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
  opacity: 0.6;
}

.input-field {
  flex: 1;
  height: 100%;
  font-size: 30rpx;
  color: #1d1d1f;
  background: transparent;
  border: none;
  outline: none;
}

.input-field.code-input {
  flex: 1;
}

.input-placeholder {
  color: #c7c7cc;
}

.toggle-password {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 30rpx;
  transition: background-color 0.2s ease;
}

.toggle-password:active {
  background-color: #e8e8ed;
}

.toggle-password text {
  font-size: 32rpx;
}

.send-code-btn {
  height: 64rpx;
  padding: 0 28rpx;
  background-color: #2997ff;
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.send-code-btn:active {
  background-color: #0066cc;
  transform: scale(0.97);
}

.send-code-btn text {
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 500;
}

.send-code-btn.disabled {
  background-color: #e8e8ed;
}

.send-code-btn.disabled text {
  color: #6e6e73;
}

.error-text {
  font-size: 24rpx;
  color: #ff3b30;
  padding-left: 4rpx;
  line-height: 1.4;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24rpx;
  margin-bottom: 32rpx;
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
  background-color: #ffffff;
  transition: all 0.2s ease;
}

.checkbox.checked {
  background-color: #2997ff;
  border-color: #2997ff;
}

.checkbox text {
  font-size: 22rpx;
  color: #ffffff;
  font-weight: 700;
}

.remember-text {
  font-size: 26rpx;
  color: #6e6e73;
}

.forgot-link {
  font-size: 26rpx;
  color: #2997ff;
  font-weight: 400;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background-color: #2997ff;
  color: #ffffff;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  font-size: 32rpx;
  font-weight: 500;
  letter-spacing: -0.2rpx;
  transition: all 0.2s ease;
  margin-top: 8rpx;
}

.submit-btn::after {
  border: none;
}

.submit-btn:not([disabled]):active {
  background-color: #0066cc;
  transform: scale(0.99);
}

.submit-btn[disabled] {
  background-color: #a1c4ff;
  opacity: 0.7;
}

.submit-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
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
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.register-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32rpx;
  gap: 8rpx;
}

.register-text {
  font-size: 28rpx;
  color: #6e6e73;
}

.register-row .link {
  font-size: 28rpx;
  color: #2997ff;
  font-weight: 500;
}

.agreement-row {
  display: flex;
  align-items: flex-start;
  margin-top: 24rpx;
  gap: 12rpx;
  padding: 0 4rpx;
}

.agreement-text {
  font-size: 24rpx;
  color: #6e6e73;
  line-height: 1.5;
  flex: 1;
}

.agreement-text .link {
  color: #2997ff;
}

.other-login {
  margin-top: 60rpx;
  position: relative;
  z-index: 1;
}

.divider {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 40rpx;
}

.divider-line {
  flex: 1;
  height: 1rpx;
  background-color: #d2d2d6;
}

.divider-text {
  font-size: 24rpx;
  color: #6e6e73;
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
  transition: transform 0.2s ease;
}

.method-item:active {
  transform: scale(0.95);
}

.method-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.wechat-icon {
  background-color: #07c160;
  color: #ffffff;
}

.qq-icon {
  background-color: #12b7f5;
  color: #ffffff;
}

.apple-icon {
  background-color: #000000;
  position: relative;
}

.apple-icon::after {
  content: '';
  width: 28rpx;
  height: 32rpx;
  background: #ffffff;
  -webkit-mask: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><path d='M18 17.6c-.3.7-.7 1.3-1.2 1.8-.6.7-1.1 1.1-1.6 1.2-.4.1-.9 0-1.5-.2-.6-.2-1.1-.3-1.6-.3s-1 .1-1.6.3c-.5.2-1 .3-1.4.2-.5-.1-1-.5-1.6-1.2-1-1-1.7-2.3-2.1-3.8-.5-1.7-.2-3.4.7-4.6.7-.9 1.6-1.4 2.7-1.5.5 0 1.1.1 1.7.4.6.2 1 .3 1.2.3.2 0 .6-.1 1.2-.4.7-.3 1.4-.4 1.9-.4 1.4.1 2.5.7 3.2 1.7-1.2.7-1.8 1.7-1.8 3 0 1.1.4 2 1.2 2.7.4.3.7.5 1.1.6-.1.3-.1.5-.2.7zM15 4.3c.5-.6.7-1.3.7-2 0-.1 0-.2-.1-.3-.6.1-1.2.4-1.8.9-.5.4-.8 1-.8 1.6 0 .1 0 .2.1.3.6 0 1.2-.3 1.9-.5z'/></svg>") center/contain no-repeat;
  background: #ffffff;
}

.method-name {
  font-size: 24rpx;
  color: #6e6e73;
}

.modal-content {
  width: 560rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
}

.modal-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.modal-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.modal-message {
  font-size: 28rpx;
  color: #6e6e73;
  text-align: center;
  line-height: 1.5;
  margin-bottom: 40rpx;
}

.modal-btn {
  width: 100%;
  height: 80rpx;
  background-color: #2997ff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-btn:active {
  background-color: #0066cc;
  transform: scale(0.99);
}

.modal-btn text {
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 500;
}
</style>