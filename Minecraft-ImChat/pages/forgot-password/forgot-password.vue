<template>
  <view class="forgot">
    <CustomNavBar
      :show-back="true"
      title="找回密码"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="forgot__body" scroll-y :show-scrollbar="false">
      <view class="hero">
        <text class="hero__title">重置密码</text>
        <text class="hero__subtitle">输入您的手机号以找回账号密码</text>
      </view>

      <view class="form-card">
        <view class="form-body">
          <view class="form-group">
            <text class="form-label">手机号</text>
            <input
              class="form-input"
              v-model="form.phone"
              type="number"
              placeholder="请输入手机号"
              placeholder-class="form-ph"
              maxlength="11"
            />
          </view>

          <view class="form-group">
            <text class="form-label">验证码</text>
            <view class="code-row">
              <input
                class="form-input code-row__input"
                v-model="form.code"
                type="number"
                placeholder="请输入验证码"
                placeholder-class="form-ph"
                maxlength="6"
              />
              <view
                class="code-btn"
                :class="{ 'is-disabled': form.countdown > 0 }"
                hover-class="code-btn--hover"
                hover-stay-time="60"
                @click="sendCode"
              >
                <text>{{ form.countdown > 0 ? `${form.countdown}s` : '获取验证码' }}</text>
              </view>
            </view>
          </view>

          <view class="form-group">
            <text class="form-label">新密码</text>
            <view class="pwd-row">
              <input
                class="form-input pwd-row__input"
                v-model="form.newPassword"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请设置新密码"
                placeholder-class="form-ph"
                maxlength="20"
              />
              <view class="eye-btn" @click="showPassword = !showPassword">
                <UiIcon name="eye" :size="40" :color="showPassword ? '#07C160' : '#B2B2B2'" />
              </view>
            </view>
          </view>
        </view>

        <view
          class="submit-btn"
          :class="{ 'is-disabled': !canSubmit || isLoading }"
          hover-class="submit-btn--hover"
          hover-stay-time="60"
          @click="handleReset"
        >
          <text v-if="!isLoading">确认重置</text>
          <view v-else class="submit-btn__loading">
            <view class="spinner"></view>
            <text>提交中…</text>
          </view>
        </view>
      </view>

      <view class="back-login-row">
        <text class="back-login-link" @click="goBackToLogin">返回登录</text>
      </view>

      <view class="forgot__safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import { sendResetCode, resetPassword } from '../../utils/chat-api.js'

export default {
  components: { CustomNavBar, UiIcon },
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

    goBackToLogin() {
      uni.redirectTo({ url: '/pages/login/login' })
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
.forgot {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.forgot__body {
  flex: 1;
  height: 0;
}

/* 头部说明 */
.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 48rpx 32rpx;
}

.hero__title {
  font-size: 44rpx;
  font-weight: 700;
  color: #191919;
  letter-spacing: -0.5rpx;
  margin-bottom: 12rpx;
}

.hero__subtitle {
  font-size: 26rpx;
  color: #888888;
  text-align: center;
}

/* 表单卡片 */
.form-card {
  background-color: #ffffff;
  margin: 0 24rpx;
  border-radius: 12rpx;
  padding: 40rpx 32rpx;
  box-shadow: var(--shadow-sm);
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.form-label {
  font-size: 28rpx;
  color: #888888;
}

.form-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background-color: #f7f7f7;
  border-radius: 10rpx;
  font-size: 30rpx;
  color: #191919;
  box-sizing: border-box;
  border: none;
  outline: none;
}

.form-ph {
  color: #b2b2b2;
}

.code-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.code-row__input {
  flex: 1;
}

.code-btn {
  flex-shrink: 0;
  height: 88rpx;
  padding: 0 28rpx;
  border-radius: 10rpx;
  background-color: #07c160;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-btn--hover {
  background-color: #06ad56;
}

.code-btn text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
  white-space: nowrap;
}

.code-btn.is-disabled {
  background-color: #b2b2b2;
}

.pwd-row {
  position: relative;
}

.pwd-row__input {
  padding-right: 96rpx;
}

.eye-btn {
  position: absolute;
  right: 12rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.eye-btn:active {
  opacity: 0.6;
}

/* 提交 */
.submit-btn {
  height: 88rpx;
  margin-top: 40rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn--hover {
  background-color: #06ad56;
}

.submit-btn.is-disabled {
  background-color: #c8e9d4;
}

.submit-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
}

.submit-btn__loading {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.submit-btn__loading text {
  font-size: 30rpx;
}

.spinner {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.35);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: forgotSpin 0.8s linear infinite;
}

@keyframes forgotSpin {
  to { transform: rotate(360deg); }
}

/* 返回登录 */
.back-login-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40rpx;
}

.back-login-link {
  font-size: 28rpx;
  color: #576b95;
}

.back-login-link:active {
  opacity: 0.6;
}

.forgot__safe {
  height: calc(48rpx + env(safe-area-inset-bottom));
}
</style>
