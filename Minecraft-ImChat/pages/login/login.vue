<template>
  <view class="login-page">
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text>‹</text>
      </view>
      <text class="nav-title">登录</text>
      <view class="nav-right" @click="goToRegister">
        <text>注册</text>
      </view>
    </view>

    <view class="logo-section">
      <view class="logo-icon">
        <text>💬</text>
      </view>
      <text class="logo-title">微信</text>
    </view>

    <view class="form-section">
      <view class="tab-bar">
        <view
          :class="['tab-item', { active: loginType === 'phone' }]"
          @click="switchLoginType('phone')"
        >
          <text>手机号</text>
        </view>
        <view
          :class="['tab-item', { active: loginType === 'password' }]"
          @click="switchLoginType('password')"
        >
          <text>微信号/邮箱</text>
        </view>
      </view>

      <view v-if="loginType === 'phone'" class="login-form">
        <view class="form-item">
          <input
            class="form-input"
            v-model="phoneForm.phone"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="input-placeholder"
            maxlength="11"
          />
        </view>

        <view class="form-item">
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
            <text>{{ phoneForm.countdown > 0 ? `${phoneForm.countdown}s` : '获取验证码' }}</text>
          </view>
        </view>
      </view>

      <view v-else class="login-form">
        <view class="form-item">
          <input
            class="form-input"
            v-model="passwordForm.account"
            placeholder="微信号/QQ号/邮箱"
            placeholder-class="input-placeholder"
          />
        </view>

        <view class="form-item">
          <input
            class="form-input"
            v-model="passwordForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            placeholder-class="input-placeholder"
          />
          <view class="toggle-pwd" @click="showPassword = !showPassword">
            <text>{{ showPassword ? '🙈' : '👁️' }}</text>
          </view>
        </view>
      </view>

      <view v-if="loginType === 'password'" class="form-options">
        <text class="forgot-link" @click="goToForgotPassword">无法登录？</text>
      </view>

      <button
        :class="['login-btn', { loading: isLoading }]"
        :disabled="isLoading || !canSubmit"
        @click="handleLogin"
      >
        <text v-if="!isLoading">登录</text>
        <view v-else class="loading-content">
          <view class="loading-spinner"></view>
          <text>登录中...</text>
        </view>
      </button>
    </view>

    <view class="other-login">
      <view class="divider">
        <view class="divider-line"></view>
        <text class="divider-text">其他登录方式</text>
        <view class="divider-line"></view>
      </view>
      <view class="login-methods">
        <view class="method-item">
          <view class="method-icon qr-icon">
            <text>📷</text>
          </view>
          <text class="method-name">扫码登录</text>
        </view>
        <view class="method-item">
          <view class="method-icon wechat-icon">
            <text>👤</text>
          </view>
          <text class="method-name">微信号登录</text>
        </view>
      </view>
    </view>

    <view class="agreement-row">
      <text class="agreement-text">
        登录即表示同意
        <text class="agreement-link">《用户协议》</text>
        和
        <text class="agreement-link">《隐私政策》</text>
      </text>
    </view>
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
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-color: #f7f7f7;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background-color: transparent;
}

.nav-back,
.nav-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-back text {
  font-size: 48rpx;
  color: #07C160;
  font-weight: 300;
}

.nav-right text {
  font-size: 28rpx;
  color: #07C160;
  font-weight: 500;
}

.nav-title {
  font-size: 32rpx;
  color: #000000;
  font-weight: 600;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}

.logo-icon {
  width: 140rpx;
  height: 140rpx;
  background-color: #07C160;
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
}

.logo-icon text {
  font-size: 72rpx;
}

.logo-title {
  font-size: 40rpx;
  font-weight: 600;
  color: #000000;
  letter-spacing: 4rpx;
}

.form-section {
  padding: 0 32rpx;
}

.tab-bar {
  display: flex;
  border-bottom: 1rpx solid #ededed;
  margin-bottom: 32rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  position: relative;
}

.tab-item text {
  font-size: 28rpx;
  color: #666666;
  font-weight: 500;
}

.tab-item.active text {
  color: #07C160;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 48rpx;
  height: 4rpx;
  background-color: #07C160;
  border-radius: 2rpx;
}

.login-form {
  background-color: #ffffff;
  border-radius: 8rpx;
  overflow: hidden;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 0 24rpx;
  height: 96rpx;
  border-bottom: 1rpx solid #ededed;
  position: relative;
}

.form-item:last-child {
  border-bottom: none;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: #000000;
  background-color: transparent;
  border: none;
  outline: none;
}

.input-placeholder {
  color: #cccccc;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  padding: 12rpx 24rpx;
  background-color: #07C160;
  border-radius: 24rpx;
  flex-shrink: 0;
}

.send-code-btn text {
  font-size: 24rpx;
  color: #ffffff;
}

.send-code-btn.disabled {
  background-color: #dcdcdc;
}

.send-code-btn.disabled text {
  color: #999999;
}

.toggle-pwd {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-pwd text {
  font-size: 32rpx;
}

.form-options {
  display: flex;
  justify-content: flex-end;
  margin-top: 16rpx;
  margin-bottom: 32rpx;
}

.forgot-link {
  font-size: 26rpx;
  color: #07C160;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  background-color: #07C160;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.login-btn::after {
  border: none;
}

.login-btn[disabled] {
  background-color: #cccccc;
}

.login-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
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

.other-login {
  padding: 48rpx 32rpx;
}

.divider {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.divider-line {
  flex: 1;
  height: 1rpx;
  background-color: #dcdcdc;
}

.divider-text {
  font-size: 24rpx;
  color: #999999;
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
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border: 1rpx solid #dcdcdc;
}

.method-icon text {
  font-size: 36rpx;
}

.method-name {
  font-size: 24rpx;
  color: #666666;
}

.agreement-row {
  padding: 24rpx 32rpx 40rpx;
}

.agreement-text {
  font-size: 22rpx;
  color: #999999;
  line-height: 1.5;
  text-align: center;
}

.agreement-link {
  color: #07C160;
}
</style>