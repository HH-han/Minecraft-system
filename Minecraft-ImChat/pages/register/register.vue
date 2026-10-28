<template>
  <view class="register" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('auth.navRegister')"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="register__body" scroll-y :show-scrollbar="false">
      <!-- 品牌 Hero -->
      <view class="register-hero grid-overlay">
        <view class="hero-brand">
          <view class="hero-brand__logo">
            <UiIcon name="user-plus" :size="56" color="#FFFFFF" />
          </view>
          <view class="hero-brand__text">
            <text class="hero-brand__title">{{ $t('auth.navRegister') }}</text>
            <text class="hero-brand__subtitle">{{ $t('auth.registerSubtitle') }}</text>
          </view>
        </view>
      </view>

      <view class="register-panel">
        <view class="form-card surface-card surface-card--glass">
          <view class="form-body">
            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldNickname') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'username' }">
                <UiIcon name="user" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input"
                  v-model="registerForm.username"
                  :placeholder="$t('auth.phNickname')"
                  placeholder-class="form-ph"
                  maxlength="20"
                  @focus="onFieldFocus('username')"
                  @blur="onFieldBlur"
                />
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldPhone') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'phone' }">
                <UiIcon name="phone" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input"
                  v-model="registerForm.phone"
                  type="number"
                  :placeholder="$t('auth.phPhone')"
                  placeholder-class="form-ph"
                  maxlength="11"
                  @focus="onFieldFocus('phone')"
                  @blur="onFieldBlur"
                />
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldCode') }}</text>
              <view class="code-row">
                <view
                  class="input-wrap code-row__input"
                  :class="{ 'is-focus': focusedField === 'code' }"
                >
                  <UiIcon name="shield" :size="32" color="var(--text-tertiary)" />
                  <input
                    class="form-input"
                    v-model="registerForm.code"
                    type="number"
                    :placeholder="$t('auth.phCode')"
                    placeholder-class="form-ph"
                    maxlength="6"
                    @focus="onFieldFocus('code')"
                    @blur="onFieldBlur"
                  />
                </view>
                <view
                  class="code-btn"
                  :class="{ 'is-disabled': registerForm.countdown > 0 }"
                  hover-class="code-btn--hover"
                  hover-stay-time="60"
                  @click="handleSendCode"
                >
                  <text>{{ registerForm.countdown > 0 ? `${registerForm.countdown}s` : $t('auth.getCode') }}</text>
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldPassword') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'password' }">
                <UiIcon name="lock" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input pwd-input"
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  :placeholder="$t('auth.phRegisterPassword')"
                  placeholder-class="form-ph"
                  maxlength="20"
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
        </view>

        <view class="agreement-row">
          <view class="check" :class="{ checked: agreed }" @click="agreed = !agreed">
            <UiIcon v-if="agreed" name="check" :size="22" color="var(--accent-contrast)" />
          </view>
          <text class="agreement-text">
            {{ $t('auth.agreeRead') }}
            <text class="agreement-link">{{ $t('auth.userAgreement') }}</text>
            {{ $t('auth.agreeAnd') }}
            <text class="agreement-link">{{ $t('auth.privacyPolicy') }}</text>
          </text>
        </view>

        <view
          class="submit-btn"
          :class="{ 'is-disabled': !canSubmit || isLoading }"
          hover-class="submit-btn--hover"
          hover-stay-time="60"
          @click="handleRegister"
        >
          <text v-if="!isLoading">{{ $t('auth.registerBtn') }}</text>
          <view v-else class="submit-btn__loading">
            <view class="spinner"></view>
            <text>{{ $t('auth.registerLoading') }}</text>
          </view>
        </view>

        <view class="login-row">
          <text class="login-row__text">{{ $t('auth.hasAccount') }}</text>
          <text class="login-row__link" @click="goToLogin">{{ $t('auth.goLogin') }}</text>
        </view>

        <view class="safe-bottom-space--sm"></view>
      </view>
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
      focusedField: '',
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
    onFieldFocus(field) {
      this.focusedField = field
    },
    onFieldBlur() {
      this.focusedField = ''
    },
    async handleSendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.registerForm.phone)) {
        uni.showToast({ title: this.$t('auth.invalidPhone'), icon: 'none' })
        return
      }
      if (this.registerForm.countdown > 0) return

      try {
        const res = await sendCode(this.registerForm.phone)
        if (res && res.code === 200) {
          uni.showToast({ title: this.$t('auth.codeSent'), icon: 'success' })
          this.startCountdown()
        } else {
          uni.showToast({ title: (res && res.message) || this.$t('auth.sendFailed'), icon: 'none' })
        }
      } catch (e) {
        console.error('发送验证码失败:', e)
        uni.showToast({ title: this.$t('auth.sendFailedRetry'), icon: 'none' })
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
          uni.showToast({ title: this.$t('auth.registerSuccess'), icon: 'success' })
          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/login' })
          }, 1500)
        } else {
          uni.showToast({ title: (response && response.message) || this.$t('auth.registerFailed'), icon: 'none' })
        }
      } catch (error) {
        console.error('注册失败:', error)
        uni.showToast({ title: (error && error.message) || this.$t('auth.registerFailed'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.register__body {
  flex: 1;
  height: 0;
}

/* 品牌 Hero */
.register-hero {
  position: relative;
  padding: 40rpx 32rpx 104rpx;
  background-image: radial-gradient(120% 80% at 50% 0%, var(--accent-light) 0%, transparent 62%);
}

.hero-brand {
  position: relative;
  display: flex;
  align-items: center;
  z-index: 1;
}

.hero-brand__logo {
  width: 104rpx;
  height: 104rpx;
  border-radius: var(--radius-lg);
  background-image: var(--accent-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: var(--accent-glow);
}

.hero-brand__text {
  margin-left: 24rpx;
  display: flex;
  flex-direction: column;
}

.hero-brand__title {
  font-size: var(--font-xxl);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.4rpx;
}

.hero-brand__subtitle {
  margin-top: 6rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.register-panel {
  position: relative;
  margin-top: -76rpx;
  padding: 0 24rpx;
  z-index: 2;
}

/* 表单卡片 */
.form-card {
  padding: 40rpx 32rpx;
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

/* 验证码 */
.code-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.code-row__input {
  flex: 1;
  min-width: 0;
}

.code-btn {
  flex-shrink: 0;
  height: 92rpx;
  padding: 0 30rpx;
  border-radius: 999rpx;
  background-color: var(--accent-light);
  border: 2rpx solid var(--accent-color);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color var(--transition-fast), transform var(--transition-fast);
}

.code-btn--hover {
  background-color: var(--accent-soft);
  transform: scale(0.98);
}

.code-btn text {
  font-size: var(--font-sm);
  color: var(--accent-color);
  font-weight: 600;
  white-space: nowrap;
}

.code-btn.is-disabled {
  background-color: var(--bg-tertiary);
  border-color: transparent;
}

.code-btn.is-disabled text {
  color: var(--text-disabled);
}

/* 密码 */
.pwd-input {
  padding-right: 4rpx;
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

/* 协议 */
.agreement-row {
  display: flex;
  align-items: flex-start;
  padding: 28rpx 12rpx 0;
}

.check {
  flex-shrink: 0;
  width: 38rpx;
  height: 38rpx;
  margin-top: 2rpx;
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

.agreement-text {
  flex: 1;
  margin-left: 14rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  line-height: 1.6;
}

.agreement-link {
  color: var(--accent-color);
}

/* 提交 */
.submit-btn {
  height: 94rpx;
  margin: 36rpx 12rpx 0;
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
  margin-top: 36rpx;
}

.login-row__text {
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.login-row__link {
  font-size: var(--font-sm);
  color: var(--accent-color);
  font-weight: 600;
  margin-left: 6rpx;
}

.login-row__link:active {
  opacity: 0.6;
}
</style>
