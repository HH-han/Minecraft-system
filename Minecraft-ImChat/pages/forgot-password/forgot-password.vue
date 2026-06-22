<template>
  <view class="forgot-password-page">
    <view class="header-bar">
      <view class="back-btn" @click="goBack">
        <text>‹</text>
      </view>
      <text class="header-title">忘记密码</text>
      <view class="placeholder"></view>
    </view>

    <view class="form-section">
      <view class="form-title">
        <text class="title-main">重置密码</text>
        <text class="title-sub">通过手机号验证后重置您的密码</text>
      </view>

      <view class="form-list">
        <view class="form-row">
          <text class="row-label">手机号</text>
          <input
            class="row-input"
            v-model="form.phone"
            type="number"
            placeholder="请输入注册时的手机号"
            placeholder-class="input-placeholder"
            :maxlength="11"
          />
        </view>
        <view v-if="errors.phone" class="error-text">
          <text>⚠ {{ errors.phone }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">验证码</text>
          <view class="code-wrapper">
            <input
              class="row-input"
              v-model="form.code"
              type="number"
              placeholder="请输入验证码"
              placeholder-class="input-placeholder"
              :maxlength="6"
            />
            <view
              :class="['send-code-btn', { disabled: form.countdown > 0 }]"
              @click="sendCode"
            >
              <text>{{ form.countdown > 0 ? `${form.countdown}s` : '发送' }}</text>
            </view>
          </view>
        </view>
        <view v-if="errors.code" class="error-text">
          <text>⚠ {{ errors.code }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">新密码</text>
          <view class="password-wrapper">
            <input
              class="row-input"
              v-model="form.newPassword"
              :type="showPassword ? 'text' : 'password'"
              placeholder="6-20位新密码"
              placeholder-class="input-placeholder"
              :maxlength="20"
            />
            <view class="toggle-pwd" @click="showPassword = !showPassword">
              <text>{{ showPassword ? '🙈' : '👁️' }}</text>
            </view>
          </view>
        </view>
        <view v-if="errors.newPassword" class="error-text">
          <text>⚠ {{ errors.newPassword }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">确认密码</text>
          <input
            class="row-input"
            v-model="form.confirmPassword"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请再次输入新密码"
            placeholder-class="input-placeholder"
            :maxlength="20"
          />
        </view>
        <view v-if="errors.confirmPassword" class="error-text">
          <text>⚠ {{ errors.confirmPassword }}</text>
        </view>
      </view>

      <button
        :class="['submit-btn', { loading: isLoading }]"
        :disabled="isLoading"
        @click="handleReset"
      >
        <text v-if="!isLoading">重置密码</text>
        <view v-else class="loading-content">
          <view class="loading-spinner"></view>
          <text>提交中...</text>
        </view>
      </button>

      <view class="footer-tip">
        <text>想起密码了？</text>
        <text class="login-link" @click="goToLogin">返回登录</text>
      </view>
    </view>

    <!-- 错误提示 -->
    <view v-if="showErrorModal" class="error-modal" @click="showErrorModal = false">
      <view class="error-content" @click.stop>
        <view class="error-icon">⚠️</view>
        <text class="error-title">操作失败</text>
        <text class="error-message">{{ errorMessage }}</text>
        <view class="error-btn" @click="showErrorModal = false">
          <text>确定</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { sendResetCode, resetPassword } from '../../utils/chat-api.js'

export default {
  data() {
    return {
      showPassword: false,
      isLoading: false,
      showErrorModal: false,
      errorMessage: '',
      countdownTimer: null,

      form: {
        phone: '',
        code: '',
        newPassword: '',
        confirmPassword: '',
        countdown: 0
      },

      errors: {
        phone: '',
        code: '',
        newPassword: '',
        confirmPassword: ''
      }
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

    /**
     * 发送验证码
     */
    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.form.phone)) {
        this.errors.phone = '请输入有效的手机号'
        return
      }

      if (this.form.countdown > 0) return

      this.errors.phone = ''

      try {
        const res = await sendResetCode(this.form.phone)
        if (res.code === 200) {
          uni.showToast({ title: '验证码已发送', icon: 'success' })
          this.startCountdown()
        } else {
          this.showErrorMsg(res.message || '发送失败')
        }
      } catch (e) {
        this.showErrorMsg('发送失败，请稍后重试')
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

    /**
     * 验证表单
     */
    validateForm() {
      let isValid = true
      this.errors = {
        phone: '',
        code: '',
        newPassword: '',
        confirmPassword: ''
      }

      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.form.phone)) {
        this.errors.phone = '请输入有效的手机号'
        isValid = false
      }

      if (!this.form.code) {
        this.errors.code = '请输入验证码'
        isValid = false
      }

      if (this.form.newPassword.length < 6 || this.form.newPassword.length > 20) {
        this.errors.newPassword = '密码长度必须在6-20位之间'
        isValid = false
      }

      if (this.form.newPassword !== this.form.confirmPassword) {
        this.errors.confirmPassword = '两次输入的密码不一致'
        isValid = false
      }

      return isValid
    },

    /**
     * 处理重置
     */
    async handleReset() {
      if (this.isLoading) return
      if (!this.validateForm()) return

      this.isLoading = true

      try {
        const res = await resetPassword({
          phone: this.form.phone,
          code: this.form.code,
          newPassword: this.form.newPassword
        })

        if (res.code === 200) {
          uni.showToast({
            title: '密码重置成功',
            icon: 'success',
            duration: 1500
          })

          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/login' })
          }, 1500)
        } else {
          this.showErrorMsg(res.message || '重置失败')
        }
      } catch (error) {
        console.error('重置失败:', error)
        this.showErrorMsg(error.message || '重置失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    showErrorMsg(message) {
      this.errorMessage = message
      this.showErrorModal = true
    }
  }
}
</script>

<style scoped>
.forgot-password-page {
  min-height: 100vh;
  background-color: #f5f5f7;
}

.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  background-color: rgba(255, 255, 255, 0.96);
  backdrop-filter: saturate(180%) blur(20rpx);
  padding-top: calc(20rpx + env(safe-area-inset-top));
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 30rpx;
  transition: background-color 0.2s ease;
}

.back-btn:active {
  background-color: #f5f5f7;
}

.back-btn text {
  font-size: 48rpx;
  color: #2997ff;
  font-weight: 300;
  line-height: 1;
}

.header-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.2rpx;
}

.placeholder {
  width: 60rpx;
}

.form-section {
  padding: 48rpx 32rpx;
}

.form-title {
  margin-bottom: 48rpx;
}

.title-main {
  display: block;
  font-size: 56rpx;
  color: #1d1d1f;
  font-weight: 700;
  margin-bottom: 12rpx;
  letter-spacing: -0.6rpx;
  line-height: 1.1;
}

.title-sub {
  font-size: 28rpx;
  color: #6e6e73;
  font-weight: 400;
}

.form-list {
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 8rpx 32rpx;
  margin-bottom: 48rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.form-row {
  display: flex;
  align-items: center;
  padding: 32rpx 0;
  border-bottom: 1rpx solid #f5f5f7;
  gap: 24rpx;
}

.form-row:last-child {
  border-bottom: none;
}

.row-label {
  width: 144rpx;
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 500;
  flex-shrink: 0;
}

.row-input {
  flex: 1;
  height: 60rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  background-color: transparent;
  border: none;
  outline: none;
}

.input-placeholder {
  color: #a1a1a6;
}

.code-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.code-wrapper .row-input {
  flex: 1;
}

.send-code-btn {
  height: 64rpx;
  padding: 0 24rpx;
  background-color: #2997ff;
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.send-code-btn:active:not(.disabled) {
  background-color: #0066cc;
  transform: scale(0.97);
}

.send-code-btn text {
  color: #ffffff;
  font-size: 24rpx;
  font-weight: 500;
  letter-spacing: -0.1rpx;
}

.send-code-btn.disabled {
  background-color: #e8e8ed;
}

.send-code-btn.disabled text {
  color: #6e6e73;
}

.password-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
}

.password-wrapper .row-input {
  flex: 1;
}

.toggle-pwd {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 30rpx;
  transition: background-color 0.2s ease;
}

.toggle-pwd:active {
  background-color: #f5f5f7;
}

.toggle-pwd text {
  font-size: 32rpx;
  line-height: 1;
}

.error-text {
  font-size: 24rpx;
  color: #ff3b30;
  padding: 4rpx 0 12rpx 30rpx;
  font-weight: 400;
  display: block;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background-color: #2997ff;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  transition: all 0.2s ease;
}

.submit-btn::after {
  border: none;
}

.submit-btn[disabled] {
  background-color: #a1c4ff;
}

.submit-btn:not([disabled]):active {
  background-color: #0066cc;
  transform: scale(0.99);
}

.submit-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
  letter-spacing: -0.2rpx;
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
  to {
    transform: rotate(360deg);
  }
}

.footer-tip {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32rpx;
  font-size: 26rpx;
  color: #6e6e73;
  gap: 8rpx;
}

.login-link {
  color: #2997ff;
  font-weight: 500;
  transition: opacity 0.2s ease;
}

.login-link:active {
  opacity: 0.6;
}

.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.error-content {
  width: 560rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 64rpx 48rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.1);
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.error-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
  line-height: 1;
}

.error-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 16rpx;
  letter-spacing: -0.2rpx;
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
  background-color: #2997ff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.error-btn:active {
  background-color: #0066cc;
  transform: scale(0.99);
}

.error-btn text {
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}
</style>
