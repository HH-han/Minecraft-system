<template>
  <view class="login-page">
    <view class="nav-bar glass-effect">
      <view class="nav-back" @click="goBack">
        <text class="nav-icon">‹</text>
      </view>
      <text class="nav-title">登录</text>
      <view class="nav-right" @click="goToRegister">
        <text class="nav-link">注册</text>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="content-wrapper">
        <view class="hero-section">
          <view class="logo-icon">
            <text class="logo-emoji">💬</text>
          </view>
          <text class="hero-title">Minecraft Chat</text>
          <text class="hero-subtitle">与好友畅聊，随时随地</text>
        </view>

        <view class="tab-bar">
          <view
            :class="['tab-item', { active: loginType === 'phone' }]"
            @click="switchLoginType('phone')"
          >
            <text class="tab-text">手机号</text>
          </view>
          <view
            :class="['tab-item', { active: loginType === 'password' }]"
            @click="switchLoginType('password')"
          >
            <text class="tab-text">账号密码</text>
          </view>
        </view>

        <view class="form-card">
          <view v-if="loginType === 'phone'" class="login-form">
            <view class="form-group">
              <text class="form-label">手机号</text>
              <input
                class="form-input"
                v-model="phoneForm.phone"
                type="number"
                placeholder="请输入手机号"
                placeholder-class="input-placeholder"
                maxlength="11"
              />
            </view>

            <view class="form-group">
              <text class="form-label">验证码</text>
              <view class="code-input-wrapper">
                <input
                  class="form-input code-input"
                  v-model="phoneForm.code"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="input-placeholder"
                  maxlength="6"
                />
                <view
                  :class="['send-code-btn', { disabled: phoneForm.countdown > 0 }]"
                  @click="sendCode"
                >
                  <text class="send-code-text">{{ phoneForm.countdown > 0 ? `${phoneForm.countdown}s` : '获取验证码' }}</text>
                </view>
              </view>
            </view>
          </view>

          <view v-else class="login-form">
            <view class="form-group">
              <text class="form-label">微信号 / QQ号 / 邮箱</text>
              <input
                class="form-input"
                v-model="passwordForm.account"
                placeholder="请输入账号"
                placeholder-class="input-placeholder"
              />
            </view>

            <view class="form-group">
              <text class="form-label">密码</text>
              <view class="password-input-wrapper">
                <input
                  class="form-input"
                  v-model="passwordForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="请输入密码"
                  placeholder-class="input-placeholder"
                />
                <view class="toggle-pwd" @click="showPassword = !showPassword">
                  <text class="toggle-icon">{{ showPassword ? '🙈' : '👁️' }}</text>
                </view>
              </view>
            </view>
          </view>

          <view v-if="loginType === 'password'" class="forgot-row">
            <text class="forgot-link" @click="goToForgotPassword">无法登录？</text>
          </view>

          <button
            :class="['login-btn', { loading: isLoading, disabled: !canSubmit }]"
            :disabled="isLoading || !canSubmit"
            @click="handleLogin"
          >
            <text v-if="!isLoading" class="btn-text">登录</text>
            <view v-else class="loading-content">
              <view class="loading-spinner"></view>
              <text class="btn-text">登录中...</text>
            </view>
          </button>
        </view>

        <view class="other-login-section">
          <view class="divider">
            <view class="divider-line"></view>
            <text class="divider-text">其他登录方式</text>
            <view class="divider-line"></view>
          </view>
          <view class="login-methods">
            <view class="method-item" @click="handleQrLogin">
              <view class="method-icon">
                <text class="method-emoji">📷</text>
              </view>
              <text class="method-name">扫码登录</text>
            </view>
            <view class="method-item" @click="handleWechatLogin">
              <view class="method-icon">
                <text class="method-emoji">👤</text>
              </view>
              <text class="method-name">微信号登录</text>
            </view>
          </view>
        </view>

        <view class="agreement-section">
          <text class="agreement-text">
            登录即表示同意
            <text class="agreement-link">《用户协议》</text>
            和
            <text class="agreement-link">《隐私政策》</text>
          </text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { auth, userApi } from '../../utils/auth.js'
import wsService from '../../utils/websocket.js'

export default {
  data() {
    return {
      loginType: 'phone',
      showPassword: false,
      isLoading: false,

      phoneForm: {
        phone: '',
        code: '',
        countdown: 0
      },

      passwordForm: {
        account: '',
        password: ''
      },

      countdownTimer: null
    }
  },

  computed: {
    canSubmit() {
      if (this.loginType === 'phone') {
        return this.phoneForm.phone.length === 11 && this.phoneForm.code.length >= 4
      }
      return this.passwordForm.account.trim().length > 0 && this.passwordForm.password.length >= 6
    }
  },

  onUnload() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  },

  methods: {
    goBack() {
      uni.navigateBack()
    },

    goToRegister() {
      uni.navigateTo({ url: '/pages/register/register' })
    },

    goToForgotPassword() {
      uni.navigateTo({ url: '/pages/forgot-password/forgot-password' })
    },

    switchLoginType(type) {
      this.loginType = type
    },

    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.phoneForm.phone)) {
        uni.showToast({ title: '请输入有效的手机号', icon: 'none' })
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
      if (this.isLoading || !this.canSubmit) return

      this.isLoading = true

      try {
        if (this.loginType === 'phone') {
          await auth.login({
            phone: this.phoneForm.phone,
            code: this.phoneForm.code,
            type: 'phone'
          })
        } else {
          await auth.login({
            account: this.passwordForm.account.trim(),
            password: this.passwordForm.password
          })
        }

        uni.showToast({ title: '登录成功', icon: 'success' })

        setTimeout(() => {
          uni.switchTab({ url: '/pages/index/index' })
        }, 1500)
      } catch (error) {
        uni.showToast({ title: error.message || '登录失败', icon: 'none' })
      } finally {
        this.isLoading = false
      }
    },

    handleQrLogin() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    },

    handleWechatLogin() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-color: #f5f5f7;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  padding-bottom: 20rpx;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.nav-back,
.nav-right {
  width: 80rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-icon {
  font-size: 48rpx;
  color: #2997ff;
  font-weight: 300;
  line-height: 1;
}

.nav-link {
  font-size: 28rpx;
  color: #2997ff;
  font-weight: 400;
}

.nav-title {
  font-size: 34rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.5rpx;
}

.content-scroll {
  flex: 1;
  padding-top: calc(88rpx + env(safe-area-inset-top));
}

.content-wrapper {
  padding: 48rpx 32rpx 64rpx;
}

.hero-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0 56rpx;
}

.logo-icon {
  width: 140rpx;
  height: 140rpx;
  background: linear-gradient(135deg, #2997ff 0%, #0066cc 100%);
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
  box-shadow: 0 8rpx 32rpx rgba(41, 151, 255, 0.3);
}

.logo-emoji {
  font-size: 72rpx;
}

.hero-title {
  font-size: 48rpx;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -1rpx;
  margin-bottom: 12rpx;
}

.hero-subtitle {
  font-size: 28rpx;
  color: #6e6e73;
  font-weight: 400;
}

.tab-bar {
  display: flex;
  background-color: #e8e8ed;
  border-radius: 16rpx;
  padding: 6rpx;
  margin-bottom: 32rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  border-radius: 12rpx;
  transition: all 0.2s ease;
}

.tab-text {
  font-size: 28rpx;
  color: #6e6e73;
  font-weight: 500;
  transition: color 0.2s ease;
}

.tab-item.active {
  background-color: #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.tab-item.active .tab-text {
  color: #1d1d1f;
  font-weight: 600;
}

.form-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.form-label {
  font-size: 26rpx;
  color: #6e6e73;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

.form-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background-color: #f5f5f7;
  border-radius: 16rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  box-sizing: border-box;
  border: none;
  outline: none;
  transition: all 0.15s ease;
}

.form-input:focus {
  background-color: #ffffff;
  box-shadow: inset 0 0 0 3rpx #2997ff;
}

.input-placeholder {
  color: #a1a1a6;
}

.code-input-wrapper {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  padding: 0 28rpx;
  height: 88rpx;
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

.send-code-btn.disabled {
  background-color: #d2d2d6;
}

.send-code-btn.disabled:active {
  transform: none;
}

.send-code-text {
  font-size: 26rpx;
  color: #ffffff;
  font-weight: 500;
  white-space: nowrap;
}

.password-input-wrapper {
  position: relative;
}

.toggle-pwd {
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

.toggle-icon {
  font-size: 32rpx;
}

.forgot-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 8rpx;
  margin-bottom: -8rpx;
}

.forgot-link {
  font-size: 26rpx;
  color: #2997ff;
  font-weight: 400;
}

.login-btn {
  width: 100%;
  height: 96rpx;
  background-color: #2997ff;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  margin-top: 40rpx;
  transition: all 0.2s ease;
}

.login-btn::after {
  border: none;
}

.login-btn:active {
  background-color: #0066cc;
  transform: scale(0.98);
}

.login-btn.disabled {
  background-color: #d2d2d6;
}

.login-btn.disabled:active {
  transform: none;
}

.login-btn[disabled] {
  background-color: #d2d2d6;
}

.btn-text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 600;
  letter-spacing: -0.3rpx;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 12rpx;
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

.other-login-section {
  margin-top: 56rpx;
}

.divider {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 36rpx;
}

.divider-line {
  flex: 1;
  height: 1rpx;
  background-color: #d2d2d6;
}

.divider-text {
  font-size: 24rpx;
  color: #a1a1a6;
  font-weight: 400;
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
  transition: opacity 0.2s ease;
}

.method-item:active {
  opacity: 0.7;
}

.method-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border: 1rpx solid #d2d2d6;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.method-item:active .method-icon {
  transform: scale(0.95);
  background-color: #f5f5f7;
}

.method-emoji {
  font-size: 36rpx;
}

.method-name {
  font-size: 24rpx;
  color: #6e6e73;
  font-weight: 400;
}

.agreement-section {
  padding: 48rpx 24rpx 0;
}

.agreement-text {
  font-size: 22rpx;
  color: #a1a1a6;
  line-height: 1.5;
  text-align: center;
}

.agreement-link {
  color: #2997ff;
}

.glass-effect {
  background-color: rgba(245, 245, 247, 0.72);
  backdrop-filter: saturate(180%) blur(20rpx);
  -webkit-backdrop-filter: saturate(180%) blur(20rpx);
}
</style>
