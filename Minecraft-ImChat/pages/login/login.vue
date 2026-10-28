<template>
  <view class="login" :class="themeClass">
    <CustomNavBar :title="$t('auth.navLogin')" title-align="center" />

    <scroll-view class="login__body" scroll-y :show-scrollbar="false">
      <!-- 品牌 Hero -->
      <view class="login-hero grid-overlay">
        <view class="brand">
          <view class="brand__logo">
            <UiIcon name="chat" :size="66" color="#FFFFFF" />
          </view>
          <text class="brand__title">{{ appTitle }}</text>
          <text class="brand__subtitle">{{ $t('auth.brandSubtitle') }}</text>
        </view>
      </view>

      <view class="login-panel">
        <!-- 表单卡片 -->
        <view class="form-card surface-card surface-card--glass">
          <view class="form-body">
            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldAccount') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'account' }">
                <UiIcon name="at" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input"
                  v-model="loginForm.account"
                  :placeholder="$t('auth.phAccount')"
                  placeholder-class="form-ph"
                  @focus="onFieldFocus('account')"
                  @blur="onFieldBlur"
                />
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldPassword') }}</text>
              <view class="input-wrap pwd-row" :class="{ 'is-focus': focusedField === 'password' }">
                <UiIcon name="lock" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input pwd-row__input"
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  :placeholder="$t('auth.phPassword')"
                  placeholder-class="form-ph"
                  @focus="onFieldFocus('password')"
                  @blur="onFieldBlur"
                />
                <view class="eye-btn" @click="showPassword = !showPassword">
                  <UiIcon
                    name="eye"
                    :size="34"
                    :color="showPassword ? 'var(--accent-color)' : 'var(--text-tertiary)'"
                  />
                </view>
              </view>
            </view>
          </view>

          <!-- 记住我 -->
          <view class="remember-row">
            <view class="check" :class="{ checked: rememberMe }" @click="rememberMe = !rememberMe">
              <UiIcon v-if="rememberMe" name="check" :size="22" color="var(--accent-contrast)" />
            </view>
            <text class="remember-text" @click="rememberMe = !rememberMe">{{ $t('auth.rememberMe') }}</text>
          </view>

          <!-- 主按钮 -->
          <view
            class="submit-btn"
            :class="{ 'is-disabled': !canSubmit || isLoading }"
            hover-class="submit-btn--hover"
            hover-stay-time="60"
            @click="handleLogin"
          >
            <text v-if="!isLoading">{{ $t('auth.loginBtn') }}</text>
            <view v-else class="submit-btn__loading">
              <view class="spinner"></view>
              <text>{{ $t('auth.loginLoading') }}</text>
            </view>
          </view>
        </view>

        <!-- 注册 / 找回密码 -->
        <view class="links-row">
          <text class="links-row__link" @click="goToRegister">{{ $t('auth.registerLink') }}</text>
          <view class="links-row__split"></view>
          <text class="links-row__link" @click="goToForgotPassword">{{ $t('auth.forgotLink') }}</text>
        </view>

        <!-- 其他登录方式 -->
        <view class="other-section">
          <view class="other-divider">
            <view class="other-divider__line"></view>
            <text class="other-divider__text">{{ $t('auth.otherMethods') }}</text>
            <view class="other-divider__line"></view>
          </view>
          <view class="methods">
            <view class="method" @click="handleQrLogin">
              <view class="method__icon">
                <UiIcon name="scan" :size="40" color="var(--text-secondary)" />
              </view>
              <text class="method__name">{{ $t('auth.scan') }}</text>
            </view>
            <view class="method" @click="handleWechatLogin">
              <view class="method__icon">
                <UiIcon name="user" :size="40" color="var(--text-secondary)" />
              </view>
              <text class="method__name">{{ $t('auth.wechatLogin') }}</text>
            </view>
          </view>
        </view>

        <!-- 协议 -->
        <view class="agreement-section">
          <text class="agreement-text">
            {{ $t('auth.agreePrefix') }}
            <text class="agreement-link">{{ $t('auth.userAgreement') }}</text>
            {{ $t('auth.agreeAnd') }}
            <text class="agreement-link">{{ $t('auth.privacyPolicy') }}</text>
          </text>
        </view>

        <view class="safe-bottom-space--sm"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import config from '../../config/index.js'
import { auth } from '../../utils/auth.js'

export default {
  components: { CustomNavBar, UiIcon },
  data() {
    return {
      appTitle: config.APP_TITLE,
      showPassword: false,
      rememberMe: false,
      isLoading: false,
      focusedField: '',

      loginForm: {
        account: '',
        password: ''
      }
    }
  },

  computed: {
    canSubmit() {
      return this.loginForm.account.trim().length > 0 && this.loginForm.password.length >= 6
    }
  },

  onLoad() {
    // 加载记住的账号
    const rememberedAccount = auth.getRememberedAccount()
    if (rememberedAccount) {
      this.loginForm.account = rememberedAccount
      this.rememberMe = true
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

    onFieldFocus(field) {
      this.focusedField = field
    },

    onFieldBlur() {
      this.focusedField = ''
    },

    async handleLogin() {
      if (this.isLoading || !this.canSubmit) return

      this.isLoading = true

      try {
        await auth.login(
          {
            account: this.loginForm.account.trim(),
            password: this.loginForm.password
          },
          { rememberMe: this.rememberMe }
        )

        uni.showToast({ title: this.$t('auth.loginSuccess'), icon: 'success' })

        setTimeout(() => {
          uni.switchTab({ url: '/pages/index/index' })
        }, 1500)
      } catch (error) {
        uni.showToast({ title: error.message || this.$t('auth.loginFailed'), icon: 'none' })
      } finally {
        this.isLoading = false
      }
    },

    handleQrLogin() {
      uni.showToast({ title: this.$t('common.comingSoon'), icon: 'none' })
    },

    handleWechatLogin() {
      uni.showToast({ title: this.$t('common.comingSoon'), icon: 'none' })
    }
  }
}
</script>

<style scoped>
.login {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.login__body {
  flex: 1;
  height: 0;
}

/* 品牌 Hero */
.login-hero {
  position: relative;
  padding: 56rpx 32rpx 124rpx;
  background-image: radial-gradient(120% 80% at 50% 0%, var(--accent-light) 0%, transparent 62%);
}

.brand {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;
}

.brand__logo {
  width: 132rpx;
  height: 132rpx;
  border-radius: var(--radius-xl);
  background-image: var(--accent-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28rpx;
  box-shadow: var(--accent-glow);
}

.brand__title {
  font-size: var(--font-display);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.6rpx;
  margin-bottom: 12rpx;
}

.brand__subtitle {
  font-size: var(--font-base);
  color: var(--text-secondary);
}

.login-panel {
  position: relative;
  margin-top: -96rpx;
  padding: 0 24rpx;
  z-index: 2;
}

/* 表单卡片 */
.form-card {
  padding: 40rpx 32rpx 36rpx;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.form-label {
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 1.6rpx;
  color: var(--text-tertiary);
}

.input-wrap {
  display: flex;
  align-items: center;
  height: 92rpx;
  padding: 0 28rpx;
  gap: 16rpx;
  background-color: var(--bg-input);
  border: 2rpx solid transparent;
  border-radius: 999rpx;
  box-sizing: border-box;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.input-wrap.is-focus {
  border-color: var(--accent-color);
  box-shadow: 0 0 0 4rpx var(--accent-light);
}

.form-input {
  flex: 1;
  min-width: 0;
  height: 100%;
  font-size: var(--font-md);
  color: var(--text-primary);
  background-color: transparent;
  border: none;
  outline: none;
}

.form-ph {
  color: var(--text-placeholder);
}

/* 密码 */
.pwd-row {
  position: relative;
}

.pwd-row__input {
  padding-right: 8rpx;
}

.eye-btn {
  flex-shrink: 0;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.eye-btn:active {
  background-color: var(--bg-active);
}

/* 记住我 */
.remember-row {
  display: flex;
  align-items: center;
  margin-top: 30rpx;
}

.check {
  flex-shrink: 0;
  width: 38rpx;
  height: 38rpx;
  border-radius: 50%;
  border: 2rpx solid var(--border-color);
  background-color: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.check.checked {
  background-color: var(--accent-color);
  border-color: var(--accent-color);
}

.remember-text {
  margin-left: 14rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

/* 主按钮 */
.submit-btn {
  height: 94rpx;
  margin-top: 36rpx;
  border-radius: 999rpx;
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.submit-btn--hover {
  transform: scale(0.98);
  opacity: 0.92;
}

.submit-btn.is-disabled {
  background-image: none;
  background-color: var(--bg-tertiary);
  box-shadow: none;
}

.submit-btn text {
  font-size: var(--font-lg);
  color: var(--accent-contrast);
  font-weight: 600;
  letter-spacing: 1rpx;
}

.submit-btn.is-disabled text {
  color: var(--text-disabled);
}

.submit-btn__loading {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.submit-btn__loading text {
  font-size: var(--font-base);
}

.spinner {
  width: 32rpx;
  height: 32rpx;
  border: 4rpx solid var(--divider);
  border-top-color: var(--accent-contrast);
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
  margin-top: 36rpx;
}

.links-row__link {
  font-size: var(--font-sm);
  color: var(--accent-color);
}

.links-row__link:active {
  opacity: 0.6;
}

.links-row__split {
  width: 1rpx;
  height: 24rpx;
  margin: 0 24rpx;
  background-color: var(--divider);
}

/* 其他登录方式 */
.other-section {
  margin-top: 56rpx;
}

.other-divider {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 0 40rpx;
  margin-bottom: 32rpx;
}

.other-divider__line {
  flex: 1;
  height: 1rpx;
  background-color: var(--border-light);
}

.other-divider__text {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  letter-spacing: 1rpx;
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
  background-color: var(--bg-primary);
  border: 1rpx solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.method__name {
  font-size: var(--font-xs);
  color: var(--text-secondary);
}

/* 协议 */
.agreement-section {
  padding: 48rpx 40rpx 0;
}

.agreement-text {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  line-height: 1.6;
  text-align: center;
}

.agreement-link {
  color: var(--accent-color);
}
</style>
