<template>
  <view class="login">
    <CustomNavBar title="登录" title-align="center" />

    <scroll-view class="login__body" scroll-y :show-scrollbar="false">
      <!-- 品牌区 -->
      <view class="brand">
        <view class="brand__logo">
          <UiIcon name="chat" :size="72" color="#FFFFFF" />
        </view>
        <text class="brand__title">{{ appTitle }}</text>
        <text class="brand__subtitle">与好友畅聊，随时随地</text>
      </view>

      <!-- 登录方式 tab -->
      <view class="tabs">
        <view
          class="tabs__item"
          :class="{ 'is-active': loginType === 'phone' }"
          @click="switchLoginType('phone')"
        >
          <text class="tabs__text">手机号</text>
        </view>
        <view
          class="tabs__item"
          :class="{ 'is-active': loginType === 'password' }"
          @click="switchLoginType('password')"
        >
          <text class="tabs__text">账号密码</text>
        </view>
      </view>

      <!-- 表单卡片 -->
      <view class="form-card">
        <view v-if="loginType === 'phone'" class="form-body">
          <view class="form-group">
            <text class="form-label">手机号</text>
            <input
              class="form-input"
              v-model="phoneForm.phone"
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
                v-model="phoneForm.code"
                type="number"
                placeholder="请输入验证码"
                placeholder-class="form-ph"
                maxlength="6"
              />
              <view
                class="code-btn"
                :class="{ 'is-disabled': phoneForm.countdown > 0 }"
                hover-class="code-btn--hover"
                hover-stay-time="60"
                @click="sendCode"
              >
                <text>{{ phoneForm.countdown > 0 ? `${phoneForm.countdown}s` : '获取验证码' }}</text>
              </view>
            </view>
          </view>
        </view>

        <view v-else class="form-body">
          <view class="form-group">
            <text class="form-label">微信号 / QQ号 / 邮箱</text>
            <input
              class="form-input"
              v-model="passwordForm.account"
              placeholder="请输入账号"
              placeholder-class="form-ph"
            />
          </view>

          <view class="form-group">
            <text class="form-label">密码</text>
            <view class="pwd-row">
              <input
                class="form-input pwd-row__input"
                v-model="passwordForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                placeholder-class="form-ph"
              />
              <view class="eye-btn" @click="showPassword = !showPassword">
                <UiIcon name="eye" :size="40" :color="showPassword ? '#07C160' : '#B2B2B2'" />
              </view>
            </view>
          </view>
        </view>

        <!-- 记住我 -->
        <view class="remember-row">
          <view class="checkbox" :class="{ checked: rememberMe }" @click="rememberMe = !rememberMe">
            <UiIcon v-if="rememberMe" name="check" :size="26" color="#FFFFFF" />
          </view>
          <text class="remember-text" @click="rememberMe = !rememberMe">记住我</text>
        </view>

        <!-- 主按钮 -->
        <view
          class="submit-btn"
          :class="{ 'is-disabled': !canSubmit || isLoading }"
          hover-class="submit-btn--hover"
          hover-stay-time="60"
          @click="handleLogin"
        >
          <text v-if="!isLoading">登录</text>
          <view v-else class="submit-btn__loading">
            <view class="spinner"></view>
            <text>登录中…</text>
          </view>
        </view>
      </view>

      <!-- 注册 / 找回密码 -->
      <view class="links-row">
        <text class="links-row__link" @click="goToRegister">注册账号</text>
        <view class="links-row__split"></view>
        <text class="links-row__link" @click="goToForgotPassword">忘记密码</text>
      </view>

      <!-- 其他登录方式 -->
      <view class="other-section">
        <view class="other-divider">
          <view class="other-divider__line"></view>
          <text class="other-divider__text">其他登录方式</text>
          <view class="other-divider__line"></view>
        </view>
        <view class="methods">
          <view class="method" @click="handleQrLogin">
            <view class="method__icon">
              <UiIcon name="scan" :size="44" color="#888888" />
            </view>
            <text class="method__name">扫一扫</text>
          </view>
          <view class="method" @click="handleWechatLogin">
            <view class="method__icon">
              <UiIcon name="user" :size="44" color="#888888" />
            </view>
            <text class="method__name">微信登录</text>
          </view>
        </view>
      </view>

      <!-- 协议 -->
      <view class="agreement-section">
        <text class="agreement-text">
          登录即表示同意
          <text class="agreement-link">《用户协议》</text>
          和
          <text class="agreement-link">《隐私政策》</text>
        </text>
      </view>

      <view class="login__safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import config from '../../config/index.js'
import { auth, userApi } from '../../utils/auth.js'
import wsService from '../../utils/websocket.js'

export default {
  components: { CustomNavBar, UiIcon },
  data() {
    return {
      appTitle: config.APP_TITLE,
      loginType: 'phone',
      showPassword: false,
      rememberMe: false,
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
.login {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.login__body {
  flex: 1;
  height: 0;
}

/* 品牌区 */
.brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 0 40rpx;
}

.brand__logo {
  width: 140rpx;
  height: 140rpx;
  border-radius: 32rpx;
  background-color: #07c160;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
  box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.28);
}

.brand__title {
  font-size: 44rpx;
  font-weight: 700;
  color: #191919;
  letter-spacing: -0.5rpx;
  margin-bottom: 12rpx;
}

.brand__subtitle {
  font-size: 26rpx;
  color: #888888;
}

/* tab */
.tabs {
  display: flex;
  justify-content: center;
  gap: 72rpx;
  padding: 0 32rpx;
  margin-bottom: 32rpx;
}

.tabs__item {
  position: relative;
  padding: 12rpx 8rpx 18rpx;
}

.tabs__text {
  font-size: 30rpx;
  color: #888888;
  transition: color 0.2s ease;
}

.tabs__item.is-active .tabs__text {
  color: #191919;
  font-weight: 600;
}

.tabs__item.is-active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 48rpx;
  height: 6rpx;
  border-radius: 3rpx;
  background-color: #07c160;
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

/* 记住我 */
.remember-row {
  display: flex;
  align-items: center;
  margin-top: 32rpx;
}

.checkbox {
  flex-shrink: 0;
  width: 40rpx;
  height: 40rpx;
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

.remember-text {
  margin-left: 14rpx;
  font-size: 28rpx;
  color: #888888;
}

/* 主按钮 */
.submit-btn {
  height: 88rpx;
  margin-top: 32rpx;
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
  animation: loginSpin 0.8s linear infinite;
}

@keyframes loginSpin {
  to { transform: rotate(360deg); }
}

/* 注册 / 找回密码 */
.links-row {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40rpx;
}

.links-row__link {
  font-size: 28rpx;
  color: #576b95;
}

.links-row__link:active {
  opacity: 0.6;
}

.links-row__split {
  width: 1rpx;
  height: 24rpx;
  margin: 0 24rpx;
  background-color: #d2d2d6;
}

/* 其他登录方式 */
.other-section {
  margin-top: 64rpx;
}

.other-divider {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 0 48rpx;
  margin-bottom: 36rpx;
}

.other-divider__line {
  flex: 1;
  height: 1rpx;
  background-color: #d2d2d6;
}

.other-divider__text {
  font-size: 24rpx;
  color: #b2b2b2;
}

.methods {
  display: flex;
  justify-content: center;
  gap: 96rpx;
}

.method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.method:active {
  opacity: 0.7;
}

.method__icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  border: 1rpx solid #e5e5e5;
}

.method__name {
  font-size: 24rpx;
  color: #888888;
}

/* 协议 */
.agreement-section {
  padding: 48rpx 48rpx 0;
}

.agreement-text {
  font-size: 22rpx;
  color: #b2b2b2;
  line-height: 1.6;
  text-align: center;
}

.agreement-link {
  color: #576b95;
}

.login__safe {
  height: calc(48rpx + env(safe-area-inset-bottom));
}
</style>
