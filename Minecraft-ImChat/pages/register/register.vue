<template>
  <view class="register">
    <CustomNavBar
      :show-back="true"
      title="注册"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="register__body" scroll-y :show-scrollbar="false">
      <view class="form-card">
        <view class="form-row">
          <text class="form-label">昵称</text>
          <input
            class="form-input"
            v-model="registerForm.username"
            placeholder="请输入昵称"
            placeholder-class="form-ph"
            maxlength="20"
          />
        </view>

        <view class="form-row">
          <text class="form-label">手机号</text>
          <input
            class="form-input"
            v-model="registerForm.phone"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="form-ph"
            maxlength="11"
          />
        </view>

        <view class="form-row">
          <text class="form-label">验证码</text>
          <input
            class="form-input"
            v-model="registerForm.code"
            type="number"
            placeholder="请输入验证码"
            placeholder-class="form-ph"
            maxlength="6"
          />
          <view
            class="code-btn"
            :class="{ 'is-disabled': registerForm.countdown > 0 }"
            hover-class="code-btn--hover"
            hover-stay-time="60"
            @click="handleSendCode"
          >
            <text>{{ registerForm.countdown > 0 ? `${registerForm.countdown}s` : '获取验证码' }}</text>
          </view>
        </view>

        <view class="form-row">
          <text class="form-label">密码</text>
          <input
            class="form-input"
            v-model="registerForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请设置密码（6-20位）"
            placeholder-class="form-ph"
            maxlength="20"
          />
          <view class="eye-btn" @click="showPassword = !showPassword">
            <UiIcon name="eye" :size="40" :color="showPassword ? '#07C160' : '#B2B2B2'" />
          </view>
        </view>
      </view>

      <view class="agreement-row">
        <view class="checkbox" :class="{ checked: agreed }" @click="agreed = !agreed">
          <UiIcon v-if="agreed" name="check" :size="26" color="#FFFFFF" />
        </view>
        <text class="agreement-text">
          我已阅读并同意
          <text class="agreement-link">《用户协议》</text>
          和
          <text class="agreement-link">《隐私政策》</text>
        </text>
      </view>

      <view
        class="submit-btn"
        :class="{ 'is-disabled': !canSubmit || isLoading }"
        hover-class="submit-btn--hover"
        hover-stay-time="60"
        @click="handleRegister"
      >
        <text v-if="!isLoading">注册</text>
        <view v-else class="submit-btn__loading">
          <view class="spinner"></view>
          <text>注册中…</text>
        </view>
      </view>

      <view class="login-row">
        <text class="login-row__text">已有账号？</text>
        <text class="login-row__link" @click="goToLogin">立即登录</text>
      </view>

      <view class="register__safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import { register, sendCode } from '../../utils/chat-api.js'

export default {
  components: { CustomNavBar, UiIcon },
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
      this.countdownTimer = null
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    goToLogin() {
      uni.redirectTo({ url: '/pages/login/login' })
    },
    async handleSendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.registerForm.phone)) {
        uni.showToast({ title: '请输入有效的手机号', icon: 'none' })
        return
      }
      if (this.registerForm.countdown > 0) return

      try {
        const res = await sendCode(this.registerForm.phone)
        if (res && res.code === 200) {
          uni.showToast({ title: '验证码已发送', icon: 'success' })
          this.startCountdown()
        } else {
          uni.showToast({ title: (res && res.message) || '发送失败', icon: 'none' })
        }
      } catch (e) {
        console.error('发送验证码失败:', e)
        uni.showToast({ title: '发送失败，请重试', icon: 'none' })
      }
    },
    startCountdown() {
      this.registerForm.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.registerForm.countdown--
        if (this.registerForm.countdown <= 0) {
          clearInterval(this.countdownTimer)
          this.countdownTimer = null
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

        if (response && response.code === 200) {
          uni.showToast({ title: '注册成功', icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/login' })
          }, 1500)
        } else {
          uni.showToast({ title: (response && response.message) || '注册失败', icon: 'none' })
        }
      } catch (error) {
        console.error('注册失败:', error)
        uni.showToast({ title: (error && error.message) || '注册失败', icon: 'none' })
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.register {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.register__body {
  flex: 1;
  height: 0;
}

.form-card {
  background-color: #ffffff;
  margin-top: 20rpx;
}

.form-row {
  display: flex;
  align-items: center;
  height: 100rpx;
  padding: 0 32rpx;
  position: relative;
}

.form-row::after {
  content: '';
  position: absolute;
  left: 32rpx;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.form-row:last-child::after {
  display: none;
}

.form-label {
  flex-shrink: 0;
  min-width: 140rpx;
  font-size: 30rpx;
  color: #191919;
}

.form-input {
  flex: 1;
  margin-left: 24rpx;
  font-size: 30rpx;
  color: #191919;
}

.form-ph {
  color: #b2b2b2;
}

.code-btn {
  flex-shrink: 0;
  height: 60rpx;
  padding: 0 24rpx;
  margin-left: 16rpx;
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
  font-size: 26rpx;
  color: #ffffff;
  font-weight: 500;
  white-space: nowrap;
}

.code-btn.is-disabled {
  background-color: #d2d2d6;
}

.eye-btn {
  flex-shrink: 0;
  width: 60rpx;
  height: 60rpx;
  margin-left: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.eye-btn:active {
  opacity: 0.6;
}

/* 协议 */
.agreement-row {
  display: flex;
  align-items: flex-start;
  padding: 32rpx;
}

.checkbox {
  flex-shrink: 0;
  width: 40rpx;
  height: 40rpx;
  margin-top: 2rpx;
  border-radius: 50%;
  border: 2rpx solid #d2d2d6;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox.checked {
  background-color: #07c160;
  border-color: #07c160;
}

.agreement-text {
  flex: 1;
  margin-left: 14rpx;
  font-size: 24rpx;
  color: #888888;
  line-height: 1.5;
}

.agreement-link {
  color: #576b95;
}

/* 提交 */
.submit-btn {
  height: 88rpx;
  margin: 0 32rpx;
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
  animation: registerSpin 0.8s linear infinite;
}

@keyframes registerSpin {
  to { transform: rotate(360deg); }
}

/* 登录链接 */
.login-row {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40rpx;
}

.login-row__text {
  font-size: 28rpx;
  color: #888888;
}

.login-row__link {
  font-size: 28rpx;
  color: #576b95;
  font-weight: 500;
}

.login-row__link:active {
  opacity: 0.6;
}

.register__safe {
  height: calc(48rpx + env(safe-area-inset-bottom));
}
</style>
