<template>
  <view class="forgot-page">
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text>‹</text>
      </view>
      <text class="nav-title">找回账号密码</text>
      <view class="nav-right"></view>
    </view>

    <view class="form-section">
      <view class="form-item">
        <text class="form-label">手机号</text>
        <input
          class="form-input"
          v-model="form.phone"
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
          v-model="form.code"
          type="number"
          placeholder="请输入验证码"
          placeholder-class="input-placeholder"
          maxlength="6"
        />
        <view
          :class="['send-code-btn', { disabled: form.countdown > 0 }]"
          @click="sendCode"
        >
          <text>{{ form.countdown > 0 ? `${form.countdown}s` : '获取验证码' }}</text>
        </view>
      </view>

      <view class="form-item">
        <text class="form-label">新密码</text>
        <input
          class="form-input"
          v-model="form.newPassword"
          :type="showPassword ? 'text' : 'password'"
          placeholder="请设置新密码"
          placeholder-class="input-placeholder"
          maxlength="20"
        />
        <view class="toggle-pwd" @click="showPassword = !showPassword">
          <text>{{ showPassword ? '🙈' : '👁️' }}</text>
        </view>
      </view>
    </view>

    <button
      :class="['submit-btn', { loading: isLoading }]"
      :disabled="isLoading || !canSubmit"
      @click="handleReset"
    >
      <text v-if="!isLoading">下一步</text>
      <view v-else class="loading-content">
        <view class="loading-spinner"></view>
        <text>提交中...</text>
      </view>
    </button>
  </view>
</template>

<script>
import { sendResetCode, resetPassword } from '../../utils/chat-api.js'

export default {
  data() {
    return {
      showPassword: false,
      isLoading: false,
      countdownTimer: null,

      form: {
        phone: '',
        code: '',
        newPassword: '',
        countdown: 0
      }
    }
  },

  computed: {
    canSubmit() {
      return this.form.phone.length === 11 &&
        this.form.code.length >= 4 &&
        this.form.newPassword.length >= 6
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

    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.form.phone)) {
        uni.showToast({ title: '请输入有效的手机号', icon: 'none' })
        return
      }

      if (this.form.countdown > 0) return

      try {
        const res = await sendResetCode(this.form.phone)
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
      this.form.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.form.countdown--
        if (this.form.countdown <= 0) {
          clearInterval(this.countdownTimer)
        }
      }, 1000)
    },

    async handleReset() {
      if (this.isLoading || !this.canSubmit) return

      this.isLoading = true

      try {
        const res = await resetPassword({
          phone: this.form.phone,
          code: this.form.code,
          newPassword: this.form.newPassword
        })

        if (res.code === 200) {
          uni.showToast({ title: '密码重置成功', icon: 'success' })

          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/login' })
          }, 1500)
        } else {
          uni.showToast({ title: res.message || '重置失败', icon: 'none' })
        }
      } catch (error) {
        uni.showToast({ title: error.message || '重置失败', icon: 'none' })
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.forgot-page {
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

.submit-btn {
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

.submit-btn::after {
  border: none;
}

.submit-btn[disabled] {
  background-color: #cccccc;
}

.submit-btn text {
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
</style>