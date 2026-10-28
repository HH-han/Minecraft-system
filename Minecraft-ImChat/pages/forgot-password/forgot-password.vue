<template>
  <view class="forgot" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('auth.navForgot')"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="forgot__body" scroll-y :show-scrollbar="false">
      <!-- 说明 Hero -->
      <view class="forgot-hero grid-overlay">
        <view class="hero">
          <view class="hero__icon">
            <UiIcon name="lock" :size="56" color="#FFFFFF" />
          </view>
          <text class="hero__title">{{ $t('auth.resetTitle') }}</text>
          <text class="hero__subtitle">{{ $t('auth.resetSubtitle') }}</text>
        </view>
      </view>

      <view class="forgot-panel">
        <view class="form-card surface-card surface-card--glass">
          <view class="form-body">
            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldPhone') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'phone' }">
                <UiIcon name="phone" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input"
                  v-model="form.phone"
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
                    v-model="form.code"
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
                  :class="{ 'is-disabled': form.countdown > 0 }"
                  hover-class="code-btn--hover"
                  hover-stay-time="60"
                  @click="sendCode"
                >
                  <text>{{ form.countdown > 0 ? `${form.countdown}s` : $t('auth.getCode') }}</text>
                </view>
              </view>
            </view>

            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldNewPassword') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'password' }">
                <UiIcon name="lock" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input pwd-input"
                  v-model="form.newPassword"
                  :type="showPassword ? 'text' : 'password'"
                  :placeholder="$t('auth.phNewPassword')"
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

          <view
            class="submit-btn"
            :class="{ 'is-disabled': !canSubmit || isLoading }"
            hover-class="submit-btn--hover"
            hover-stay-time="60"
            @click="handleReset"
          >
            <text v-if="!isLoading">{{ $t('auth.resetBtn') }}</text>
            <view v-else class="submit-btn__loading">
              <view class="spinner"></view>
              <text>{{ $t('auth.resetLoading') }}</text>
            </view>
          </view>
        </view>

        <view class="back-login-row">
          <text class="back-login-link" @click="goBackToLogin">{{ $t('auth.backToLogin') }}</text>
        </view>

        <view class="safe-bottom-space--sm"></view>
      </view>
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
      focusedField: '',
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

    onFieldFocus(field) {
      this.focusedField = field
    },

    onFieldBlur() {
      this.focusedField = ''
    },

    async sendCode() {
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.form.phone)) {
        uni.showToast({ title: this.$t('auth.invalidPhone'), icon: 'none' })
        return
      }

      if (this.form.countdown > 0) return

      try {
        const res = await sendResetCode(this.form.phone)
        if (res.code === 200) {
          uni.showToast({ title: this.$t('auth.codeSent'), icon: 'success' })
          this.startCountdown()
        } else {
          uni.showToast({ title: res.message || this.$t('auth.sendFailed'), icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: this.$t('auth.sendFailedRetry'), icon: 'none' })
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
          uni.showToast({ title: this.$t('auth.resetSuccess'), icon: 'success' })

          setTimeout(() => {
            uni.redirectTo({ url: '/pages/login/login' })
          }, 1500)
        } else {
          uni.showToast({ title: res.message || this.$t('auth.resetFailed'), icon: 'none' })
        }
      } catch (error) {
        uni.showToast({ title: error.message || this.$t('auth.resetFailed'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.forgot__body {
  flex: 1;
  height: 0;
}

/* 说明 Hero */
.forgot-hero {
  position: relative;
  padding: 48rpx 32rpx 104rpx;
  background-image: radial-gradient(120% 80% at 50% 0%, var(--accent-light) 0%, transparent 62%);
}

.hero {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;
}

.hero__icon {
  width: 108rpx;
  height: 108rpx;
  border-radius: var(--radius-lg);
  background-image: var(--accent-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  box-shadow: var(--accent-glow);
}

.hero__title {
  font-size: var(--font-xl);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.3rpx;
  margin-bottom: 10rpx;
}

.hero__subtitle {
  font-size: var(--font-sm);
  color: var(--text-secondary);
  text-align: center;
}

.forgot-panel {
  position: relative;
  margin-top: -72rpx;
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

/* 提交 */
.submit-btn {
  height: 94rpx;
  margin-top: 40rpx;
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
  margin-top: 36rpx;
}

.back-login-link {
  font-size: var(--font-sm);
  color: var(--accent-color);
}

.back-login-link:active {
  opacity: 0.6;
}
</style>
