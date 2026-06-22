<template>
  <view class="login-page">
    <!-- Logo 区域 -->
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
        <view v-if="loginForm.account" class="avatar-preview-wrapper">
          <image
            class="avatar-preview"
            :src="loginForm.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <text class="avatar-name">{{ loginForm.userName || '用户' }}</text>
        </view>

        <view class="form-item">
          <text class="form-label">账号</text>
          <input
            class="input-field"
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

        <view class="form-item">
          <text class="form-label">密码</text>
          <view class="password-wrapper">
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
          <view v-if="errors.password" class="error-text">
            <text>⚠ {{ errors.password }}</text>
          </view>
        </view>
      </view>

      <!-- 手机号登录 -->
      <view v-else class="form-content">
        <view class="form-item">
          <text class="form-label">手机号</text>
          <input
            class="input-field"
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
          <text class="form-label">验证码</text>
          <view class="code-wrapper">
            <input
              class="input-field"
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
        <text class="link" @click="goToRegister">立即注册</text>
      </view>

      <!-- 协议 -->
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
    <view v-if="showErrorModal" class="modal-overlay" @click="showErrorModal = false">
      <view class="modal-content" @click.stop>
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
/* 仿 Apple 官网设计规范 */
/* 配色: #1d1d1f / #6e6e73 / #f5f5f7 / #2997ff / #d2d2d6 */

.login-page {
  min-height: 100vh;
  background-color: #f5f5f7;
  padding: 0 40rpx 60rpx;
  box-sizing: border-box;
}

/* Logo 区域 */
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0 60rpx;
}

.logo-icon {
  font-size: 96rpx;
  color: #1d1d1f;
  margin-bottom: 24rpx;
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

/* 表单卡片 */
.form-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.04);
}

/* 标签栏 */
.tab-bar {
  display: flex;
  border-bottom: 1rpx solid #d2d2d6;
  margin-bottom: 40rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  position: relative;
  transition: all 0.2s ease;
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

/* 头像预览 */
.avatar-preview-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 16rpx 0 32rpx;
}

.avatar-preview {
  width: 112rpx;
  height: 112rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  border: 2rpx solid #d2d2d6;
  transition: all 0.2s ease;
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
  gap: 12rpx;
}

.form-label {
  font-size: 26rpx;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.2rpx;
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
  cursor: pointer;
}

.toggle-password text {
  font-size: 36rpx;
}

.code-wrapper {
  display: flex;
  gap: 16rpx;
}

.code-wrapper .input-field {
  flex: 1;
}

.send-code-btn {
  height: 88rpx;
  padding: 0 28rpx;
  background-color: #2997ff;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.send-code-btn:active {
  background-color: #0066cc;
  transform: scale(0.98);
}

.send-code-btn text {
  color: #ffffff;
  font-size: 26rpx;
  font-weight: 500;
}

.send-code-btn.disabled {
  background-color: #d2d2d6;
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

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
  margin-bottom: 32rpx;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 12rpx;
  cursor: pointer;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
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
  font-size: 20rpx;
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
  cursor: pointer;
}

/* 登录按钮 - Apple 风格 */
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

/* 注册链接 */
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
  cursor: pointer;
}

/* 协议 */
.agreement-row {
  display: flex;
  align-items: flex-start;
  margin-top: 32rpx;
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
  cursor: pointer;
}

/* 其他登录方式 */
.other-login {
  margin-top: 60rpx;
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
  cursor: pointer;
}

.method-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  transition: transform 0.2s ease;
}

.method-item:active .method-icon {
  transform: scale(0.95);
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

/* 模态框 */
.modal-content {
  width: 560rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
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
  cursor: pointer;
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
