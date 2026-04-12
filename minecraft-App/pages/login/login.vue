<template>
  <view class="login-container">
    <view class="login-background">
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
    </view>
    <view class="login-form">
      <view class="logo">
        <text class="logo-text">Minecraft</text>
        <text class="logo-subtext">登录您的账户</text>
      </view>
      <!-- 头像显示 -->
      <view class="avatar-container">
        <view class="avatar">
          <image :src="avatarUrl" mode="aspectFill" class="avatar-img" />
        </view>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207429069" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="7501" width="256" height="256">
              <path d="M502.28224 338.432m-234.496 0a234.496 234.496 0 1 0 468.992 0 234.496 234.496 0 1 0-468.992 0Z"
                fill="#CAE4FF" p-id="7502"></path>
              <path
                d="M492.288 609.792c-160.256-0.512-289.792-130.048-289.792-289.792 0-160.768 129.024-291.84 289.792-294.4 160.256 0.512 289.792 130.048 289.792 289.792 0.512 161.28-129.024 291.84-289.792 294.4z m0-500.224c-113.664 0-205.824 92.16-205.824 205.824 0 113.664 92.16 205.824 205.824 205.824s205.824-92.16 205.824-205.824c0-113.664-92.16-205.824-205.824-205.824z"
                fill="#0972E7" p-id="7503"></path>
              <path
                d="M109.824 1013.248c-23.04 0-41.984-18.944-41.984-41.984v-16.896c1.024-236.544 192.512-427.52 428.544-428.544 23.04 0 41.984 18.944 41.984 41.984 0 23.04-18.944 41.984-41.984 41.984-189.952 1.024-343.552 154.624-344.576 344.576v12.8c0.512 24.064-17.92 44.032-41.984 46.08z"
                fill="#0972E7" p-id="7504"></path>
              <path
                d="M874.752 1009.152c-22.016 1.536-40.448-15.36-41.984-37.376v-17.408c-1.024-189.952-154.624-343.552-344.576-344.576-23.04 0-41.984-18.944-41.984-41.984 0-23.04 18.944-41.984 41.984-41.984 236.544 1.024 427.52 192.512 428.544 428.544v12.8c1.536 22.016-15.36 40.448-37.376 41.984h-4.608z"
                fill="#0972E7" p-id="7505"></path>
            </svg>
          </text>
          <input v-model="formData.account" placeholder="请输入账号" class="form-input" :class="{ 'error': errors.account }"
            @input="handleAccountInput" />
        </view>
        <text v-if="errors.account" class="error-message">{{ errors.account }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1775143734296" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="12595" width="256" height="256">
              <path
                d="M511.2832 509.696m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z"
                fill="#FF579C" p-id="12596"></path>
              <path
                d="M286.3104 415.5904c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776-0.0512-141.7728-114.9952-256.7168-256.768-256.7168z"
                fill="#FF80BA" p-id="12597"></path>
              <path
                d="M408.7808 422.8096V358.7584c0-27.9552 22.7328-50.6368 50.6368-50.6368h103.68c27.9552 0 50.6368 22.7328 50.6368 50.6368v64.0512h61.44V358.7584c0-61.7984-50.2784-112.0768-112.0768-112.0768H459.4176c-61.7984 0-112.0768 50.2784-112.0768 112.0768v64.0512h61.44z"
                fill="#FFC7E3" p-id="12598"></path>
              <path
                d="M668.8768 395.2128H353.6384c-48.9472 0-88.6272 39.68-88.6272 88.6272v186.0608c0 48.9472 39.68 88.6272 88.6272 88.6272h315.2384c48.9472 0 88.6272-39.68 88.6272-88.6272V483.84c0-48.9472-39.68-88.6272-88.6272-88.6272z m-130.1504 188.2112v64.512a27.4432 27.4432 0 0 1-54.8864 0v-64.512a55.31648 55.31648 0 0 1-27.9552-48.0768c0-30.6176 24.7808-55.3984 55.3984-55.3984s55.3984 24.7808 55.3984 55.3984c0 20.6336-11.264 38.5536-27.9552 48.0768z"
                fill="#FFFFFF" p-id="12599"></path>
            </svg>
          </text>
          <input v-model="formData.password" type="password" placeholder="请输入密码" class="form-input"
            :class="{ 'error': errors.password }" />
        </view>
        <text v-if="errors.password" class="error-message">{{ errors.password }}</text>
      </view>
      <view class="form-item">
        <view class="checkbox-group">
          <view class="custom-checkbox" :class="{ 'checked': remember }" @click="remember = !remember">
            <text v-if="remember" class="checkbox-check">✓</text>
          </view>
          <text class="checkbox-label">记住我</text>
        </view>
        <text class="forgot-password" @click="forgotPassword">忘记密码？</text>
      </view>
      <view class="form-item">
        <button :loading="loading" @click="submitForm" class="login-button">
          <text class="button-text">登录</text>
        </button>
      </view>
      <view class="register-link">
        <text>还没有账号？</text>
        <text class="link" @click="goRegister">立即注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { login } from '../../api/auth';

const loading = ref(false);
const remember = ref(false);

// 头像相关
const defaultAvatar = 'https://img.icons8.com/ios-filled/50/000000/user.png';
const avatarUrl = ref(defaultAvatar);

const formData = reactive({
  account: '',
  password: '',
  hasInput: false
});

const errors = reactive({
  account: '',
  password: ''
});

// 处理账号输入，获取头像
const handleAccountInput = async () => {
  const account = formData.account.trim();
  formData.hasInput = account !== '';

  if (account) {
    // 这里可以添加调用后端API获取用户头像的逻辑
    // 暂时使用默认头像
    avatarUrl.value = defaultAvatar;
  } else {
    avatarUrl.value = defaultAvatar;
  }
};

const validateForm = () => {
  let isValid = true;

  // 重置错误信息
  errors.account = '';
  errors.password = '';

  // 验证账号
  if (!formData.account.trim()) {
    errors.account = '请输入账号';
    isValid = false;
  }

  // 验证密码
  if (!formData.password.trim()) {
    errors.password = '请输入密码';
    isValid = false;
  }

  return isValid;
};

const submitForm = async () => {
  if (validateForm()) {
    loading.value = true;
    try {
      const response = await login(formData);
      if (response.code === 200) {
        // 存储登录信息
        uni.setStorageSync('token', response.data.token);
        uni.setStorageSync('userInfo', response.data);

        uni.showToast({
          title: '登录成功',
          icon: 'success'
        });

        uni.switchTab({
          url: '/pages/index/index'
        });
      } else {
        uni.showToast({
          title: response.message || '登录失败',
          icon: 'none'
        });
      }
    } catch (error) {
      uni.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    } finally {
      loading.value = false;
    }
  }
};

const forgotPassword = () => {
  uni.showToast({
    title: '忘记密码功能暂未实现',
    icon: 'none'
  });
};

const goRegister = () => {
  uni.navigateTo({
    url: '/pages/login/register'
  });
};
</script>

<style scoped>
/* CSS 变量定义 */
:root {
  --bg-color: #f5f5f5;
  --card-color: #ffffff;
  --text-color: #333333;
  --border-color: #6c6c6c;
  --primary-color: #007aff;
}

.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20rpx;
  background-color: var(--bg-color);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.block {
  position: absolute;
  background-color: rgba(0, 122, 255, 0.05);
  border-radius: 50%;
  animation: float 8s infinite ease-in-out;
}

.block:nth-child(1) {
  width: 200rpx;
  height: 200rpx;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.block:nth-child(2) {
  width: 150rpx;
  height: 150rpx;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.block:nth-child(3) {
  width: 100rpx;
  height: 100rpx;
  top: 30%;
  right: 20%;
  animation-delay: 4s;
}

.block:nth-child(4) {
  width: 120rpx;
  height: 120rpx;
  bottom: 20%;
  left: 25%;
  animation-delay: 6s;
}

.block:nth-child(5) {
  width: 80rpx;
  height: 80rpx;
  top: 40%;
  left: 30%;
  animation-delay: 1s;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-20rpx) rotate(180deg);
  }
}

.login-form {
  width: 100%;
  max-width: 300px;
  padding: 60rpx 40rpx;
  background-color: var(--card-color);
  border-radius: 20rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(50rpx);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 头像样式 */
.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 40rpx;
  animation: slideIn 0.5s ease-out 0.2s both;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 3rpx solid var(--primary-color);
  box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.3);
  animation: scaleIn 0.5s ease-out 0.4s both;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.avatar-img:hover {
  transform: scale(1.05);
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

.logo {
  text-align: center;
  margin-bottom: 50rpx;
}

.logo-text {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.logo-subtext {
  display: block;
  font-size: 24rpx;
  color: var(--text-color);
  opacity: 0.7;
}

.form-item {
  margin-bottom: 35rpx;
  display: flex;
  gap: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  flex-direction: row;
  border: 1px solid var(--border-color);
  border-radius: 12rpx;
  padding: 8rpx 20rpx;
  transition: all 0.3s ease;
  background-color: var(--bg-color);
}

.input-wrapper:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3rpx rgba(0, 122, 255, 0.1);
}

.input-wrapper.error {
  border-color: #ff4d4f;
}

.input-icon {
  padding: 0 20rpx;
  font-size: 28rpx;
}

.input-icon svg {
  width: 32rpx;
  height: 32rpx;
  fill: var(--text-color);
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  background-color: transparent;
  color: var(--text-color);
  border: none;
  outline: none;
}

.form-input::placeholder {
  color: var(--text-color);
  opacity: 0.5;
}

.error-message {
  display: block;
  font-size: 20rpx;
  color: #ff4d4f;
  margin-top: 10rpx;
  animation: shake 0.3s ease;
}

@keyframes shake {

  0%,
  100% {
    transform: translateX(0);
  }

  25% {
    transform: translateX(-5rpx);
  }

  75% {
    transform: translateX(5rpx);
  }
}

.checkbox-group {
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-between;
}

.custom-checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #6c6c6c;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12rpx;
  transition: all 0.3s ease;
  cursor: pointer;
}

.custom-checkbox.checked {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.checkbox-check {
  color: white;
  font-size: 20rpx;
  font-weight: bold;
}

.checkbox-label {
  font-size: 24rpx;
  color: var(--text-color);
}

.forgot-password {
  float: right;
  font-size: 24rpx;
  color: var(--primary-color);
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #0056b3;
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 2rpx;
  font-size: 30rpx;
  background: linear-gradient(135deg, var(--primary-color), #0056b3);
  color: white;
  border: none;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.3);
  overflow: hidden;
  position: relative;
}

.login-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(to right,
      rgba(255, 255, 255, 0) 0%,
      rgba(255, 255, 255, 0.3) 50%,
      rgba(255, 255, 255, 0) 100%);
  transform: rotate(30deg);
  animation: shine 2s infinite;
}

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(30deg);
  }

  100% {
    transform: translateX(100%) rotate(30deg);
  }
}

.login-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 6rpx 16rpx rgba(0, 122, 255, 0.4);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 2rpx 8rpx rgba(0, 122, 255, 0.3);
}

.button-text {
  position: relative;
  z-index: 1;
  font-weight: bold;
}

.register-link {
  text-align: center;
  margin-top: 40rpx;
  font-size: 24rpx;
  color: var(--text-color);
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.link {
  color: var(--primary-color);
  margin-left: 10rpx;
  font-weight: 500;
  transition: color 0.3s ease;
}

.link:hover {
  color: #0056b3;
  text-decoration: underline;
}
</style>