<template>
  <view class="register-page">
    <view class="nav-bar glass-effect">
      <view class="nav-back" @click="goBack">
        <text class="nav-icon">‹</text>
      </view>
      <text class="nav-title">创建账号</text>
      <view class="nav-right"></view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="content-wrapper">
        <view class="hero-section">
          <text class="hero-title">创建您的账号</text>
          <text class="hero-subtitle">开启 Minecraft Chat 之旅</text>
        </view>

        <view class="form-card">
          <view class="register-form">
            <view class="form-group">
              <text class="form-label">昵称</text>
              <input
                class="form-input"
                v-model="registerForm.username"
                placeholder="请输入昵称"
                placeholder-class="input-placeholder"
                maxlength="20"
              />
            </view>

            <view class="form-group">
              <text class="form-label">手机号</text>
              <input
                class="form-input"
                v-model="registerForm.phone"
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
                  v-model="registerForm.code"
                  type="number"
                  placeholder="请输入验证码"
                  placeholder-class="input-placeholder"
                  maxlength="6"
                />
                <view
                  :class="['send-code-btn', { disabled: registerForm.countdown > 0 }]"
                  @click="sendCode"
                >
                  <text class="send-code-text">{{ registerForm.countdown > 0 ? `${registerForm.countdown}s` : '获取验证码' }}</text>
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">密码</text>
              <view class="password-input-wrapper">
                <input
                  class="form-input"
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="请设置密码（6-20位）"
                  placeholder-class="input-placeholder"
                  maxlength="20"
                />
                <view class="toggle-pwd" @click="showPassword = !showPassword">
                  <text class="toggle-icon">{{ showPassword ? '🙈' : '👁️' }}</text>
                </view>
              </view>
            </view>
          </view>

          <view class="agreement-row">
            <view :class="['checkbox', { checked: agreed }]" @click="agreed = !agreed">
              <text v-if="agreed" class="check-icon">✓</text>
            </view>
            <text class="agreement-text">
              我已阅读并同意
              <text class="agreement-link">《用户协议》</text>
              和
              <text class="agreement-link">《隐私政策》</text>
            </text>
          </view>

          <button
            :class="['register-btn', { loading: isLoading, disabled: !canSubmit }]"
            :disabled="isLoading || !canSubmit"
            @click="handleRegister"
          >
            <text v-if="!isLoading" class="btn-text">注册</text>
            <view v-else class="loading-content">
              <view class="loading-spinner"></view>
              <text class="btn-text">注册中...</text>
            </view>
          </button>
        </view>

        <view class="login-link-row">
          <text class="login-text">已有账号？</text>
          <text class="login-link" @click="goToLogin">立即登录</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { register } from '../../utils/chat-api.js'

export default {
  data() {
    return {
      showPassword: false,
      isLoading: false,
      agreed: false,
      countdownTimer: null,

      registerForm: {
        username: '',
        phone: '',
        code: '',
        password: '',
        countdown: 0
      }
    }
  },

  computed: {
    canSubmit() {
      return this.registerForm.username.trim().length > 0 &&
        this.registerForm.phone.length === 11 &&
        this.registerForm.code.length >= 4 &&
        this.registerForm.password.length >= 6 &&
        this.agreed
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

    goToLogin() {
      uni.redirectTo({ url: '/pages/login/login' })
    },

    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.registerForm.phone)) {
        uni.showToast({ title: '请输入有效的手机号', icon: 'none' })
        return
      }

      if (this.registerForm.countdown > 0) return

      try {
        const res = await register.sendCode(this.registerForm.phone)
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
      this.registerForm.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.registerForm.countdown--
        if (this.registerForm.countdown <= 0) {
          clearInterval(this.countdownTimer)
        }
      }, 1000)
    },

    async handleRegister() {
      if (this.isLoading || !this.canSubmit) return

      this.isLoading = true

      try {
        const response = await register({
          username: this.registerForm.username.trim(),
          password: this.registerForm.password,
          phone: this.registerForm.phone,
          code: this.registerForm.code
        })

        if (response.code === 200) {
          uni.showToast({ title: '注册成功', icon: 'success' })

          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/login' })
          }, 1500)
        } else {
          uni.showToast({ title: response.message || '注册失败', icon: 'none' })
        }
      } catch (error) {
        console.error('注册失败:', error)
        uni.showToast({ title: error.message || '注册失败', icon: 'none' })
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.register-page {
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
  padding: 32rpx 0 48rpx;
}

.hero-title {
  font-size: 44rpx;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -1rpx;
  margin-bottom: 12rpx;
  text-align: center;
}

.hero-subtitle {
  font-size: 28rpx;
  color: #6e6e73;
  font-weight: 400;
  text-align: center;
}

.form-card {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

.register-form {
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

.agreement-row {
  display: flex;
  align-items: flex-start;
  margin-top: 32rpx;
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
  flex-shrink: 0;
  margin-top: 4rpx;
  transition: all 0.2s ease;
}

.checkbox.checked {
  background-color: #2997ff;
  border-color: #2997ff;
}

.check-icon {
  font-size: 22rpx;
  color: #ffffff;
  font-weight: 700;
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

.register-btn {
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

.register-btn::after {
  border: none;
}

.register-btn:active {
  background-color: #0066cc;
  transform: scale(0.98);
}

.register-btn.disabled {
  background-color: #d2d2d6;
}

.register-btn.disabled:active {
  transform: none;
}

.register-btn[disabled] {
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

.login-link-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40rpx;
  gap: 8rpx;
}

.login-text {
  font-size: 28rpx;
  color: #6e6e73;
}

.login-link {
  font-size: 28rpx;
  color: #2997ff;
  font-weight: 500;
}

.glass-effect {
  background-color: rgba(245, 245, 247, 0.72);
  backdrop-filter: saturate(180%) blur(20rpx);
  -webkit-backdrop-filter: saturate(180%) blur(20rpx);
}
</style>
