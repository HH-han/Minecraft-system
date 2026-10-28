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
          <!-- 头像上传 -->
          <view class="avatar-picker" @click="handleChooseAvatar">
            <image
              v-if="registerForm.avatar"
              class="avatar-picker__img"
              :src="registerForm.avatar"
              mode="aspectFill"
            />
            <view v-else class="avatar-picker__placeholder">
              <UiIcon name="camera" :size="44" color="var(--text-tertiary)" />
            </view>
            <text class="avatar-picker__text">{{ $t('auth.uploadAvatar') }}</text>
          </view>

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
              <text class="form-label">{{ $t('auth.fieldEmail') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'email' }">
                <UiIcon name="at" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input"
                  v-model="registerForm.email"
                  :placeholder="$t('auth.phEmail')"
                  placeholder-class="form-ph"
                  @focus="onFieldFocus('email')"
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

            <view class="form-group">
              <text class="form-label">{{ $t('auth.fieldConfirmPassword') }}</text>
              <view class="input-wrap" :class="{ 'is-focus': focusedField === 'confirmPassword' }">
                <UiIcon name="lock" :size="32" color="var(--text-tertiary)" />
                <input
                  class="form-input pwd-input"
                  v-model="registerForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  :placeholder="$t('auth.phConfirmPassword')"
                  placeholder-class="form-ph"
                  maxlength="20"
                  @focus="onFieldFocus('confirmPassword')"
                  @blur="onFieldBlur"
                />
                <view class="eye-btn" @click="showConfirmPassword = !showConfirmPassword">
                  <UiIcon
                    name="eye"
                    :size="34"
                    :color="showConfirmPassword ? 'var(--accent-color)' : 'var(--text-tertiary)'"
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
import { register } from '../../utils/chat-api.js'

export default {
  components: { CustomNavBar, UiIcon },
  data() {
    return {
      showPassword: false,
      showConfirmPassword: false,
      isLoading: false,
      agreed: false,
      focusedField: '',
      registerForm: {
        username: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        avatar: ''
      }
    }
  },
  computed: {
    canSubmit() {
      return this.registerForm.username.trim().length > 0 &&
        this.registerForm.email.trim().length > 0 &&
        this.registerForm.phone.length === 11 &&
        this.registerForm.password.length >= 6 &&
        this.registerForm.confirmPassword.length > 0 &&
        this.agreed
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
    handleChooseAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        success: (res) => {
          const tempFile = res.tempFiles && res.tempFiles[0]
          const fileObj = tempFile && (tempFile.file || tempFile)
          // H5 端：File 对象通过 FileReader 读取为 base64
          if (fileObj && typeof FileReader !== 'undefined' && fileObj instanceof Blob) {
            const reader = new FileReader()
            reader.onload = (e) => {
              this.registerForm.avatar = e.target.result
            }
            reader.readAsDataURL(fileObj)
            return
          }
          // App / 小程序端：通过文件系统读取为 base64
          if (typeof uni.getFileSystemManager === 'function') {
            uni.getFileSystemManager().readFile({
              filePath: res.tempFilePaths[0],
              encoding: 'base64',
              success: (r) => {
                this.registerForm.avatar = 'data:image/png;base64,' + r.data
              }
            })
          }
        }
      })
    },
    async handleRegister() {
      if (this.isLoading || !this.canSubmit) return

      // 昵称验证
      const username = this.registerForm.username.trim()
      if (!username) {
        uni.showToast({ title: this.$t('auth.phNickname'), icon: 'none' })
        return
      }

      // 邮箱验证
      const email = this.registerForm.email.trim()
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(email)) {
        uni.showToast({ title: this.$t('auth.invalidEmail'), icon: 'none' })
        return
      }

      // 手机号验证 (11位数字，1开头)
      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.registerForm.phone)) {
        uni.showToast({ title: this.$t('auth.invalidPhone'), icon: 'none' })
        return
      }

      // 密码一致性校验
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        uni.showToast({ title: this.$t('auth.passwordMismatch'), icon: 'none' })
        return
      }

      // 密码长度验证
      if (this.registerForm.password.length < 6 || this.registerForm.password.length > 20) {
        uni.showToast({ title: this.$t('auth.passwordLength'), icon: 'none' })
        return
      }

      // 协议同意校验
      if (!this.agreed) {
        uni.showToast({ title: this.$t('auth.pleaseAgree'), icon: 'none' })
        return
      }

      this.isLoading = true
      try {
        const response = await register({
          username,
          password: this.registerForm.password,
          email,
          phone: this.registerForm.phone,
          avatar: this.registerForm.avatar
        })

        if (response && response.code === 200) {
          // 注册成功，后端返回生成的账号
          const account = response.data
          uni.showModal({
            title: this.$t('auth.registerSuccess'),
            content: `${this.$t('auth.yourAccount')}${account}`,
            showCancel: false,
            confirmText: this.$t('auth.iKnow'),
            success: () => {
              uni.redirectTo({ url: '/pages/login/login' })
            }
          })
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

/* 头像上传 */
.avatar-picker {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 36rpx;
}

.avatar-picker__img,
.avatar-picker__placeholder {
  width: 144rpx;
  height: 144rpx;
  border-radius: 50%;
}

.avatar-picker__img {
  border: 2rpx solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.avatar-picker__placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-input);
  border: 2rpx dashed var(--border-color);
}

.avatar-picker:active .avatar-picker__placeholder {
  background-color: var(--bg-active);
}

.avatar-picker__text {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
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
