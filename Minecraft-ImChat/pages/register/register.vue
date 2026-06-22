<template>
  <view class="register-page">
    <view class="header-bar">
      <view class="back-btn" @click="goBack">
        <text>‹</text>
      </view>
      <text class="header-title">注册账号</text>
      <view class="placeholder"></view>
    </view>

    <view class="form-section">
      <view class="form-title">
        <text class="title-main">创建新账号</text>
        <text class="title-sub">加入博览通讯，开始精彩旅程</text>
      </view>

      <!-- 头像上传 -->
      <view class="avatar-upload">
        <view
          class="avatar-preview"
          :style="{ backgroundImage: registerForm.avatar ? `url(${registerForm.avatar})` : `url(${defaultAvatar})` }"
        >
          <view class="avatar-mask" @click="chooseAvatar">
            <text>{{ registerForm.avatar ? '更换' : '上传' }}</text>
          </view>
          <input
            type="file"
            accept="image/*"
            @change="handleAvatarUpload"
            class="file-input"
          />
        </view>
      </view>

      <!-- 表单 -->
      <view class="form-list">
        <view class="form-row">
          <text class="row-label">姓名</text>
          <input
            class="row-input"
            v-model="registerForm.username"
            placeholder="请输入真实姓名"
            placeholder-class="input-placeholder"
            :maxlength="20"
          />
        </view>
        <view v-if="errors.username" class="error-text">
          <text>⚠ {{ errors.username }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">邮箱</text>
          <input
            class="row-input"
            v-model="registerForm.email"
            type="text"
            placeholder="请输入邮箱"
            placeholder-class="input-placeholder"
            :maxlength="50"
          />
        </view>
        <view v-if="errors.email" class="error-text">
          <text>⚠ {{ errors.email }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">手机号</text>
          <input
            class="row-input"
            v-model="registerForm.phone"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="input-placeholder"
            :maxlength="11"
          />
        </view>
        <view v-if="errors.phone" class="error-text">
          <text>⚠ {{ errors.phone }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">密码</text>
          <view class="password-wrapper">
            <input
              class="row-input"
              v-model="registerForm.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="6-20位密码"
              placeholder-class="input-placeholder"
              :maxlength="20"
            />
            <view class="toggle-pwd" @click="showPassword = !showPassword">
              <text>{{ showPassword ? '🙈' : '👁️' }}</text>
            </view>
          </view>
        </view>
        <view v-if="errors.password" class="error-text">
          <text>⚠ {{ errors.password }}</text>
        </view>

        <view class="form-row">
          <text class="row-label">确认密码</text>
          <input
            class="row-input"
            v-model="registerForm.confirmPassword"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请再次输入密码"
            placeholder-class="input-placeholder"
            :maxlength="20"
          />
        </view>
        <view v-if="errors.confirmPassword" class="error-text">
          <text>⚠ {{ errors.confirmPassword }}</text>
        </view>
      </view>

      <!-- 协议 -->
      <view class="agreement-row">
        <view :class="['checkbox', { checked: agreed }]" @click="agreed = !agreed">
          <text v-if="agreed">✓</text>
        </view>
        <text class="agreement-text">
          我已阅读并同意
          <text class="agreement-link" @click="showAgreement">《用户协议》</text>
        </text>
      </view>

      <!-- 注册按钮 -->
      <button
        :class="['register-btn', { loading: isLoading }]"
        :disabled="isLoading"
        @click="handleRegister"
      >
        <text v-if="!isLoading">注册</text>
        <view v-else class="loading-content">
          <view class="loading-spinner"></view>
          <text>注册中...</text>
        </view>
      </button>
    </view>

    <!-- 错误模态框 -->
    <view v-if="showErrorModal" class="error-modal" @click="showErrorModal = false">
      <view class="error-content" @click.stop>
        <view class="error-icon">⚠️</view>
        <text class="error-title">注册失败</text>
        <text class="error-message">{{ errorMessage }}</text>
        <view class="error-btn" @click="showErrorModal = false">
          <text>确定</text>
        </view>
      </view>
    </view>

    <!-- 成功提示模态框 -->
    <view v-if="showSuccessModal" class="success-modal">
      <view class="success-content">
        <view class="success-icon">✅</view>
        <text class="success-title">注册成功</text>
        <text class="success-message">您的账号是：{{ generatedAccount }}</text>
        <view class="success-btn" @click="goToLogin">
          <text>去登录</text>
        </view>
      </view>
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
      showErrorModal: false,
      errorMessage: '',
      showSuccessModal: false,
      generatedAccount: '',

      registerForm: {
        username: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        avatar: ''
      },

      errors: {
        username: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: ''
      },

      defaultAvatar: '/static/default-avatar.png'
    }
  },

  methods: {
    /**
     * 返回
     */
    goBack() {
      uni.navigateBack()
    },

    /**
     * 跳转到登录
     */
    goToLogin() {
      uni.redirectTo({ url: '/pages/login/login' })
    },

    /**
     * 选择头像
     */
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          this.registerForm.avatar = tempFilePath
        }
      })
    },

    /**
     * 头像上传
     */
    handleAvatarUpload(e) {
      const file = e.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (ev) => {
          this.registerForm.avatar = ev.target.result
        }
        reader.readAsDataURL(file)
      }
    },

    /**
     * 显示协议
     */
    showAgreement() {
      uni.showModal({
        title: '用户协议',
        content: '欢迎使用博览通讯，请遵守相关法律法规。',
        showCancel: false
      })
    },

    /**
     * 验证表单
     */
    validateForm() {
      let isValid = true
      this.errors = {
        username: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: ''
      }

      if (!this.registerForm.username.trim()) {
        this.errors.username = '请输入真实姓名'
        isValid = false
      }

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(this.registerForm.email)) {
        this.errors.email = '请输入有效的邮箱地址'
        isValid = false
      }

      const phoneRegex = /^1\d{10}$/
      if (!phoneRegex.test(this.registerForm.phone)) {
        this.errors.phone = '请输入有效的11位手机号'
        isValid = false
      }

      if (this.registerForm.password.length < 6 || this.registerForm.password.length > 20) {
        this.errors.password = '密码长度必须在6-20位之间'
        isValid = false
      }

      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.errors.confirmPassword = '两次输入的密码不一致'
        isValid = false
      }

      if (!this.agreed) {
        this.showError('请先同意用户协议')
        isValid = false
      }

      return isValid
    },

    /**
     * 处理注册
     */
    async handleRegister() {
      if (this.isLoading) return

      if (!this.validateForm()) return

      this.isLoading = true

      try {
        const response = await register({
          username: this.registerForm.username,
          password: this.registerForm.password,
          email: this.registerForm.email,
          phone: this.registerForm.phone,
          avatar: this.registerForm.avatar
        })

        if (response.code === 200) {
          this.generatedAccount = response.data || ''
          this.showSuccessModal = true
        } else {
          this.showError(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        this.showError(error.message || '注册失败，请稍后重试')
      } finally {
        this.isLoading = false
      }
    },

    /**
     * 显示错误
     */
    showError(message) {
      this.errorMessage = message
      this.showErrorModal = true
    }
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60rpx;
}

/* 顶部导航 */
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background: #fff;
  padding-top: calc(20rpx + env(safe-area-inset-top));
}

.back-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn text {
  font-size: 48rpx;
  color: #2997ff;
  font-weight: bold;
}

.header-title {
  font-size: 34rpx;
  color: #1d1d1f;
  font-weight: 600;
}

.placeholder {
  width: 60rpx;
}

.form-section {
  padding: 40rpx;
}

.form-title {
  margin-bottom: 40rpx;
}

.title-main {
  display: block;
  font-size: 44rpx;
  color: #1d1d1f;
  font-weight: 700;
  margin-bottom: 12rpx;
}

.title-sub {
  font-size: 26rpx;
  color: #6e6e73;
}

/* 头像上传 */
.avatar-upload {
  display: flex;
  justify-content: center;
  margin-bottom: 40rpx;
}

.avatar-preview {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background-color: #f0f2f5;
  background-size: cover;
  background-position: center;
  border: 4rpx solid #fff;
  box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.1);
}

.avatar-mask {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  text-align: center;
  padding: 6rpx 0;
  border-radius: 0 0 80rpx 80rpx;
  font-size: 22rpx;
}

.file-input {
  display: none;
}

/* 表单 */
.form-list {
  background: #fff;
  border-radius: 24rpx;
  padding: 0 30rpx;
  margin-bottom: 30rpx;
}

.form-row {
  display: flex;
  align-items: center;
  padding: 28rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.form-row:last-child {
  border-bottom: none;
}

.row-label {
  width: 160rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
}

.row-input {
  flex: 1;
  height: 60rpx;
  font-size: 30rpx;
  color: #1d1d1f;
}

.input-placeholder {
  color: #a1a1a6;
}

.password-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  position: relative;
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
}

.toggle-pwd text {
  font-size: 32rpx;
}

.error-text {
  font-size: 24rpx;
  color: #ff3b30;
  padding: 4rpx 0 12rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  margin-top: -1rpx;
}

.form-list .error-text:last-child {
  border-radius: 0 0 24rpx 24rpx;
  border-bottom: none;
}

/* 协议 */
.agreement-row {
  display: flex;
  align-items: center;
  margin-bottom: 40rpx;
  gap: 12rpx;
  padding: 0 10rpx;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #d2d2d6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  flex-shrink: 0;
}

.checkbox.checked {
  background: #2997ff;
  border-color: #2997ff;
}

.checkbox text {
  font-size: 20rpx;
  color: #fff;
}

.agreement-text {
  font-size: 24rpx;
  color: #6e6e73;
  line-height: 1.5;
}

.agreement-link {
  color: #2997ff;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #2997ff 0%, #1e6bb8 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(41, 151, 255, 0.3);
}

.register-btn::after {
  border: none;
}

.register-btn[disabled] {
  background: #a1c4ff;
  box-shadow: none;
}

.register-btn text {
  font-size: 34rpx;
  color: #fff;
  font-weight: 600;
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
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 错误模态框 */
.error-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.error-content {
  width: 560rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.error-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.error-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 16rpx;
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
  background: #2997ff;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-btn text {
  font-size: 30rpx;
  color: #fff;
  font-weight: 500;
}

/* 成功模态框 */
.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.success-content {
  width: 560rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.success-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.success-title {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 16rpx;
}

.success-message {
  font-size: 28rpx;
  color: #6e6e73;
  text-align: center;
  line-height: 1.5;
  margin-bottom: 16rpx;
}

.success-btn {
  width: 100%;
  height: 80rpx;
  background: #34c759;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 24rpx;
}

.success-btn text {
  font-size: 30rpx;
  color: #fff;
  font-weight: 500;
}
</style>
