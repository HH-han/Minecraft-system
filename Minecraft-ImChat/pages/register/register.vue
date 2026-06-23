<template>
  <view class="register-page">
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text>‹</text>
      </view>
      <text class="nav-title">注册</text>
      <view class="nav-right"></view>
    </view>

    <view class="form-section">
      <view class="form-item">
        <text class="form-label">昵称</text>
        <input
          class="form-input"
          v-model="registerForm.username"
          placeholder="请输入昵称"
          placeholder-class="input-placeholder"
          maxlength="20"
        />
      </view>

      <view class="form-item">
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

      <view class="form-item">
        <text class="form-label">验证码</text>
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
          <text>{{ registerForm.countdown > 0 ? `${registerForm.countdown}s` : '获取验证码' }}</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">密码</text>
        <input
          class="form-input"
          v-model="registerForm.password"
          :type="showPassword ? 'text' : 'password'"
          placeholder="请设置密码（6-20位）"
          placeholder-class="input-placeholder"
          maxlength="20"
        />
        <view class="toggle-pwd" @click="showPassword = !showPassword">
          <text>{{ showPassword ? '🙈' : '👁️' }}</text>
        </view>
      </view>
    </view>

    <view class="agreement-row">
      <view :class="['checkbox', { checked: agreed }]" @click="agreed = !agreed">
        <text v-if="agreed">✓</text>
      </view>
      <text class="agreement-text">
        我已阅读并同意
        <text class="agreement-link">《用户协议》</text>
        和
        <text class="agreement-link">《隐私政策》</text>
      </text>
    </view>

    <button
      :class="['register-btn', { loading: isLoading }]"
      :disabled="isLoading || !canSubmit"
      @click="handleRegister"
    >
      <text v-if="!isLoading">注册</text>
      <view v-else class="loading-content">
        <view class="loading-spinner"></view>
        <text>注册中...</text>
      </view>
    </button>

    <view class="login-link-row">
      <text class="login-text">已有账号？</text>
      <text class="login-link" @click="goToLogin">登录</text>
    </view>
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
  background-color: #f7f7f7;
  padding-bottom: 40rpx;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background-color: #ffffff;
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

.nav-title {
  font-size: 32rpx;
  color: #000000;
  font-weight: 600;
}

.form-section {
  background-color: #ffffff;
  margin: 24rpx 32rpx;
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

.form-label {
  width: 140rpx;
  font-size: 28rpx;
  color: #000000;
  font-weight: 500;
  flex-shrink: 0;
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

.agreement-row {
  display: flex;
  align-items: flex-start;
  padding: 0 32rpx;
  margin-bottom: 32rpx;
  gap: 12rpx;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  border: 2rpx solid #dcdcdc;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  flex-shrink: 0;
  margin-top: 4rpx;
}

.checkbox.checked {
  background-color: #07C160;
  border-color: #07C160;
}

.checkbox text {
  font-size: 22rpx;
  color: #ffffff;
  font-weight: 600;
}

.agreement-text {
  font-size: 24rpx;
  color: #999999;
  line-height: 1.5;
  flex: 1;
}

.agreement-link {
  color: #07C160;
}

.register-btn {
  width: calc(100% - 64rpx);
  height: 88rpx;
  background-color: #07C160;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  margin: 0 32rpx;
}

.register-btn::after {
  border: none;
}

.register-btn[disabled] {
  background-color: #cccccc;
}

.register-btn text {
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

.login-link-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32rpx;
  gap: 8rpx;
}

.login-text {
  font-size: 28rpx;
  color: #999999;
}

.login-link {
  font-size: 28rpx;
  color: #07C160;
  font-weight: 500;
}
</style>