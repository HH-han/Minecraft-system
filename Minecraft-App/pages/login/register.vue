<template>
  <view class="register-container">
    <view class="login-background">
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
    </view>
    <view class="register-form">
      <view class="logo">
        <text class="logo-text">Minecraft</text>
        <text class="logo-subtext">创建新账户</text>
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
          <input 
            v-model="formData.username" 
            placeholder="请输入用户名" 
            class="form-input"
            :class="{ 'error': errors.username }"
          />
        </view>
        <text v-if="errors.username" class="error-message">{{ errors.username }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207403612" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="6495" width="256" height="256">
              <path
                d="M128 0h768C981.357714 0 1024 42.642286 1024 128v768c0 85.357714-42.642286 128-128 128H128C42.642286 1024 0 981.357714 0 896V128C0 42.642286 42.642286 0 128 0z"
                fill="#4C84FF" p-id="6496"></path>
              <path
                d="M704 398.189714h-31.963429v-64.950857c0-89.746286-71.68-162.596571-160.036571-162.596571-88.283429 0-159.963429 72.850286-159.963429 162.596571v64.950857h-32.036571c-35.181714 0-64 29.257143-64 65.097143v282.331429c0 35.766857 28.818286 65.097143 64 65.097143h384c35.181714 0 64-29.257143 64-65.097143v-282.331429a64.731429 64.731429 0 0 0-64-65.097143zM512 682.715429a85.577143 85.577143 0 0 1-85.357714-85.357715A85.577143 85.577143 0 0 1 512 512a85.577143 85.577143 0 0 1 85.357714 85.357714A85.577143 85.577143 0 0 1 512 682.642286z m-95.963429-284.525715v-64.950857c0-53.979429 42.861714-97.572571 95.963429-97.572571 53.101714 0 96.036571 43.52 96.036571 97.572571v64.950857H416.036571z"
                fill="#FFFFFF" p-id="6497"></path>
            </svg>
          </text>
          <input 
            v-model="formData.password" 
            type="password" 
            placeholder="请输入密码" 
            class="form-input"
            :class="{ 'error': errors.password }"
          />
        </view>
        <text v-if="errors.password" class="error-message">{{ errors.password }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207403612" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="6495" width="256" height="256">
              <path
                d="M128 0h768C981.357714 0 1024 42.642286 1024 128v768c0 85.357714-42.642286 128-128 128H128C42.642286 1024 0 981.357714 0 896V128C0 42.642286 42.642286 0 128 0z"
                fill="#4C84FF" p-id="6496"></path>
              <path
                d="M704 398.189714h-31.963429v-64.950857c0-89.746286-71.68-162.596571-160.036571-162.596571-88.283429 0-159.963429 72.850286-159.963429 162.596571v64.950857h-32.036571c-35.181714 0-64 29.257143-64 65.097143v282.331429c0 35.766857 28.818286 65.097143 64 65.097143h384c35.181714 0 64-29.257143 64-65.097143v-282.331429a64.731429 64.731429 0 0 0-64-65.097143zM512 682.715429a85.577143 85.577143 0 0 1-85.357714-85.357715A85.577143 85.577143 0 0 1 512 512a85.577143 85.577143 0 0 1 85.357714 85.357714A85.577143 85.577143 0 0 1 512 682.642286z m-95.963429-284.525715v-64.950857c0-53.979429 42.861714-97.572571 95.963429-97.572571 53.101714 0 96.036571 43.52 96.036571 97.572571v64.950857H416.036571z"
                fill="#FFFFFF" p-id="6497"></path>
            </svg>
          </text>
          <input 
            v-model="formData.confirmPassword" 
            type="password" 
            placeholder="请确认密码" 
            class="form-input"
            :class="{ 'error': errors.confirmPassword }"
          />
        </view>
        <text v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207717423" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8533" width="256" height="256"><path d="M873.472 0H150.528C67.584 0 0 67.584 0 150.528v722.944C0 956.416 67.584 1024 150.528 1024h722.944c82.944 0 150.528-67.584 150.528-150.528V150.528C1024 67.584 956.416 0 873.472 0z m-61.952 656.896c0 41.472-33.792 74.752-74.752 74.752H287.232c-41.472 0-74.752-33.28-74.752-74.752V367.104c0-41.472 33.28-74.752 74.752-74.752h449.024c41.472 0 74.752 33.28 74.752 74.752v289.792z" fill="#4873ED" p-id="8534"></path><path d="M712.192 427.008L512 556.032 311.808 427.008c-9.216-6.144-21.504-3.072-27.648 6.144-6.144 9.216-3.072 21.504 6.144 27.648l210.944 135.68c3.072 2.048 7.168 3.072 10.752 3.072 3.584 0 7.68-1.024 10.752-3.072L733.696 460.8a20.48 20.48 0 0 0 6.144-27.648 20.48 20.48 0 0 0-27.648-6.144z" fill="#4873ED" p-id="8535"></path></svg>
          </text>
          <input 
            v-model="formData.email" 
            placeholder="请输入邮箱" 
            class="form-input"
            :class="{ 'error': errors.email }"
          />
        </view>
        <text v-if="errors.email" class="error-message">{{ errors.email }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207717423" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8533" width="256" height="256"><path d="M873.472 0H150.528C67.584 0 0 67.584 0 150.528v722.944C0 956.416 67.584 1024 150.528 1024h722.944c82.944 0 150.528-67.584 150.528-150.528V150.528C1024 67.584 956.416 0 873.472 0z m-61.952 656.896c0 41.472-33.792 74.752-74.752 74.752H287.232c-41.472 0-74.752-33.28-74.752-74.752V367.104c0-41.472 33.28-74.752 74.752-74.752h449.024c41.472 0 74.752 33.28 74.752 74.752v289.792z" fill="#4873ED" p-id="8534"></path><path d="M712.192 427.008L512 556.032 311.808 427.008c-9.216-6.144-21.504-3.072-27.648 6.144-6.144 9.216-3.072 21.504 6.144 27.648l210.944 135.68c3.072 2.048 7.168 3.072 10.752 3.072 3.584 0 7.68-1.024 10.752-3.072L733.696 460.8a20.48 20.48 0 0 0 6.144-27.648 20.48 20.48 0 0 0-27.648-6.144z" fill="#4873ED" p-id="8535"></path></svg>
          </text>
          <input 
            v-model="formData.phone" 
            placeholder="请输入手机号" 
            class="form-input"
            :class="{ 'error': errors.phone }"
          />
        </view>
        <text v-if="errors.phone" class="error-message">{{ errors.phone }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207717423" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8533" width="256" height="256"><path d="M873.472 0H150.528C67.584 0 0 67.584 0 150.528v722.944C0 956.416 67.584 1024 150.528 1024h722.944c82.944 0 150.528-67.584 150.528-150.528V150.528C1024 67.584 956.416 0 873.472 0z m-61.952 656.896c0 41.472-33.792 74.752-74.752 74.752H287.232c-41.472 0-74.752-33.28-74.752-74.752V367.104c0-41.472 33.28-74.752 74.752-74.752h449.024c41.472 0 74.752 33.28 74.752 74.752v289.792z" fill="#4873ED" p-id="8534"></path><path d="M712.192 427.008L512 556.032 311.808 427.008c-9.216-6.144-21.504-3.072-27.648 6.144-6.144 9.216-3.072 21.504 6.144 27.648l210.944 135.68c3.072 2.048 7.168 3.072 10.752 3.072 3.584 0 7.68-1.024 10.752-3.072L733.696 460.8a20.48 20.48 0 0 0 6.144-27.648 20.48 20.48 0 0 0-27.648-6.144z" fill="#4873ED" p-id="8535"></path></svg>
          </text>
          <input 
            v-model="formData.avatar" 
            placeholder="请输入头像URL" 
            class="form-input"
            :class="{ 'error': errors.avatar }"
          />
        </view>
        <text v-if="errors.avatar" class="error-message">{{ errors.avatar }}</text>
      </view>
      <view class="form-item">
        <button 
          :loading="loading" 
          @click="submitForm" 
          class="register-button"
        >
          <text class="button-text">注册</text>
        </button>
      </view>
      <view class="login-link">
        <text>已有账号？</text>
        <text class="link" @click="goLogin">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { register } from '../../api/auth';

const loading = ref(false);

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  avatar: ''
});

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  avatar: ''
});

const validateForm = () => {
  let isValid = true;
  
  // 重置错误信息
  errors.username = '';
  errors.password = '';
  errors.confirmPassword = '';
  errors.email = '';
  errors.phone = '';
  errors.avatar = '';
  
  // 验证用户名
  if (!formData.username.trim()) {
    errors.username = '请输入用户名';
    isValid = false;
  } else if (formData.username.length < 3 || formData.username.length > 20) {
    errors.username = '用户名长度必须在3-20位之间';
    isValid = false;
  }
  
  // 验证密码
  if (!formData.password.trim()) {
    errors.password = '请输入密码';
    isValid = false;
  } else if (formData.password.length < 6 || formData.password.length > 20) {
    errors.password = '密码长度必须在6-20位之间';
    isValid = false;
  }
  
  // 验证确认密码
  if (!formData.confirmPassword.trim()) {
    errors.confirmPassword = '请确认密码';
    isValid = false;
  } else if (formData.confirmPassword !== formData.password) {
    errors.confirmPassword = '两次输入密码不一致';
    isValid = false;
  }
  
  // 验证邮箱
  if (!formData.email.trim()) {
    errors.email = '请输入邮箱';
    isValid = false;
  } else {
    // 简单的邮箱格式验证
    const emailRegex = /^[^@]+@[^@]+\.[^@]+$/;
    if (!emailRegex.test(formData.email)) {
      errors.email = '请输入正确的邮箱格式';
      isValid = false;
    }
  }
  
  // 验证手机号
  if (!formData.phone.trim()) {
    errors.phone = '请输入手机号';
    isValid = false;
  }
  
  // 验证头像
  if (!formData.avatar.trim()) {
    errors.avatar = '请输入头像URL';
    isValid = false;
  }
  
  return isValid;
};

const submitForm = async () => {
  if (validateForm()) {
    loading.value = true;
    try {
      // 准备注册数据，移除confirmPassword字段
      const registerData = {
        username: formData.username,
        password: formData.password,
        email: formData.email,
        phone: formData.phone,
        avatar: formData.avatar
      };
      
      const response = await register(registerData);
      if (response.code === 200) {
        uni.showToast({
          title: '注册成功',
          icon: 'success'
        });
        uni.navigateTo({
          url: '/src/views/login/login'
        });
      } else {
        uni.showToast({
          title: response.message || '注册失败',
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

const goLogin = () => {
  uni.navigateTo({
    url: '/src/views/login/login'
  });
};
</script>

<style scoped>
.register-container {
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
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20rpx) rotate(180deg);
  }
}

.register-form {
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
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
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
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5rpx);
  }
  75% {
    transform: translateX(5rpx);
  }
}

.register-button {
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

.register-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to right, 
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0) 100%
  );
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

.register-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 6rpx 16rpx rgba(0, 122, 255, 0.4);
}

.register-button:active {
  transform: translateY(0);
  box-shadow: 0 2rpx 8rpx rgba(0, 122, 255, 0.3);
}

.button-text {
  position: relative;
  z-index: 1;
  font-weight: bold;
}

.login-link {
  text-align: center;
  margin-top: 40rpx;
  font-size: 24rpx;
  color: var(--text-color);
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