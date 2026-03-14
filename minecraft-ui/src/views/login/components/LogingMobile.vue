<template>
  <Login_background />
  <form action="" class="loginform-3" @submit.prevent="handleLogin">
    <div class="login">欢迎登录</div>
    <div class="loginform-3-center">
      <div class="input-container-3">
        <input required="" id="username" type="text" @input="handleUsernameInput" v-model="loginForm.username" placeholder="请输入账号" />
        <label class="label" for="username">账号</label>
        <div class="underline"></div>
      </div>
      <div class="input-container-3">
        <input type="password" id="password" v-model="loginForm.password" required placeholder="请输入密码" />
        <label class="label" for="password">密码</label>
        <div class="underline"></div>
      </div>
    </div>
    <div class="btn-conteiner">
      <button class="btn-content" type="submit">
        <span class="btn-title">登录</span>
        <span class="icon-arrow">
          <svg width="66px" height="43px" viewBox="0 0 66 43" version="1.1" xmlns="http://www.w3.org/2000/svg"
            xmlns:xlink="http://www.w3.org/1999/xlink">
            <g id="arrow" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
              <path id="arrow-icon-one"
                d="M40.1543933,3.89485454 L43.9763149,0.139296592 C44.1708311,-0.0518420739 44.4826329,-0.0518571125 44.6771675,0.139262789 L65.6916134,20.7848311 C66.0855801,21.1718824 66.0911863,21.8050225 65.704135,22.1989893 C65.7000188,22.2031791 65.6958657,22.2073326 65.6916762,22.2114492 L44.677098,42.8607841 C44.4825957,43.0519059 44.1708242,43.0519358 43.9762853,42.8608513 L40.1545186,39.1069479 C39.9575152,38.9134427 39.9546793,38.5968729 40.1481845,38.3998695 C40.1502893,38.3977268 40.1524132,38.395603 40.1545562,38.3934985 L56.9937789,21.8567812 C57.1908028,21.6632968 57.193672,21.3467273 57.0001876,21.1497035 C56.9980647,21.1475418 56.9959223,21.1453995 56.9937605,21.1432767 L40.1545208,4.60825197 C39.9574869,4.41477773 39.9546013,4.09820839 40.1480756,3.90117456 C40.1501626,3.89904911 40.1522686,3.89694235 40.1543933,3.89485454 Z"
                fill="#FFFFFF"></path>
              <path id="arrow-icon-two"
                d="M20.1543933,3.89485454 L23.9763149,0.139296592 C24.1708311,-0.0518420739 24.4826329,-0.0518571125 24.6771675,0.139262789 L45.6916134,20.7848311 C46.0855801,21.1718824 46.0911863,21.8050225 45.704135,22.1989893 C45.7000188,22.2031791 45.6958657,22.2073326 45.6916762,22.2114492 L24.677098,42.8607841 C24.4825957,43.0519059 24.1708242,43.0519358 23.9762853,42.8608513 L20.1545186,39.1069479 C19.9575152,38.9134427 19.9546793,38.5968729 20.1481845,38.3998695 C20.1502893,38.3977268 20.1524132,38.395603 20.1545562,38.3934985 L36.9937789,21.8567812 C37.1908028,21.6632968 37.193672,21.3467273 37.0001876,21.1497035 C36.9980647,21.1475418 36.9959223,21.1453995 36.9937605,21.1432767 L20.1545208,4.60825197 C19.9574869,4.41477773 19.9546013,4.09820839 20.1480756,3.90117456 C20.1501626,3.89904911 20.1522686,3.89694235 20.1543933,3.89485454 Z"
                fill="#FFFFFF"></path>
              <path id="arrow-icon-three"
                d="M0.154393339,3.89485454 L3.97631488,0.139296592 C4.17083111,-0.0518420739 4.48263286,-0.0518571125 4.67716753,0.139262789 L25.6916134,20.7848311 C26.0855801,21.1718824 26.0911863,21.8050225 25.704135,22.1989893 C25.7000188,22.2031791 25.6958657,22.2073326 25.6916762,22.2114492 L4.67709797,42.8607841 C4.48259567,43.0519059 4.17082418,43.0519358 3.97628526,42.8608513 L0.154518591,39.1069479 C-0.0424848215,38.9134427 -0.0453206733,38.5968729 0.148184538,38.3998695 C0.150289256,38.3977268 0.152413239,38.395603 0.154556228,38.3934985 L16.9937789,21.8567812 C17.1908028,21.6632968 17.193672,21.3467273 17.0001876,21.1497035 C16.9980647,21.1475418 16.9959223,21.1453995 16.9937605,21.1432767 L0.15452076,4.60825197 C-0.0425130651,4.41477773 -0.0453986756,4.09820839 0.148075568,3.90117456 C0.150162624,3.89904911 0.152268631,3.89694235 0.154393339,3.89485454 Z"
                fill="#FFFFFF"></path>
            </g>
          </svg>
        </span>
      </button>
    </div>
    <div class="radio_1">
      <div>
        <input type="checkbox" id="rememberMe" v-model="rememberMe" />
        <span>记住用户名!</span>
      </div>
      <div>
        <router-link to="/Login_3">
          <button class="phone">手机号登录 !</button>
        </router-link>
      </div>
    </div>
  </form>
  <!-- 错误提示框 -->
  <div v-if="showError" class="error-message">
    <div class="error-content">
      <span class="error-icon">❌</span>
      <span class="error-text">{{ errorMessage }}</span>
      <button class="error-close" @click="showError = false">×</button>
    </div>
  </div>
  <!-- 成功提示框 -->
  <div v-if="showSucceeded" class="success-message">
    <div class="success-content">
      <span class="success-icon">✅</span>
      <span class="success-text">{{ successMessage }}</span>
      <button class="success-close" @click="showSucceeded = false">×</button>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Login_background from '@/components/LoginComponent/Login_background.vue';
import request from '@/utils/request';
import { useAuthStore } from '@/stores/auth.js'

const router = useRouter();

// auth store
const authStore = useAuthStore();

// 图片引入
const defaultAvatar = require('@/assets/defaultimage/mrtx.png');

// 状态管理
const showError = ref(false);
const showSucceeded = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
  image: defaultAvatar,
  hasInput: false
});

// 记住用户名功能
const rememberMe = ref(false);

// 用户名输入处理（简化版，不再尝试预加载头像）
const handleUsernameInput = () => {
  loginForm.value.hasInput = loginForm.value.username.trim() !== '';

  // 仅当本地有匹配的已登录用户时才显示头像
  const storedUser = JSON.parse(localStorage.getItem('user') || 'null');
  if (storedUser && storedUser.username === loginForm.value.username.trim()) {
    loginForm.value.image = storedUser.image || defaultAvatar;
  } else {
    loginForm.value.image = defaultAvatar;
  }
};

/**
 * 处理登录逻辑
 */
const handleLogin = async () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');

  errorMessage.value = '';
  showError.value = false;

  if (!loginForm.value.username || !loginForm.value.password) {
    errorMessage.value = '请输入账号和密码';
    showError.value = true;
    return;
  }

  try {
    // 使用auth store的login方法进行登录
    const response = await authStore.login({
      username: loginForm.value.username,
      password: loginForm.value.password
    });

    console.log('登录响应:', response);

    if (response.code === '0') {
      // 严格验证响应数据结构
      if (!response.data?.token || !response.data?.user) {
        throw new Error('响应数据不完整');
      }

      const { user } = response.data;

      // 验证必要字段
      if (!user?.username) {
        throw new Error('无效的用户数据');
      }

      // 处理头像URL（确保包含完整路径）
      let fullImageUrl = defaultAvatar;
      if (user.image) {
        fullImageUrl = user.image.startsWith('http')
          ? user.image
          : `http://localhost:2025${user.image.startsWith('/') ? '' : '/'}${user.image}`;
      }

      // 更新界面显示
      loginForm.value.image = fullImageUrl;

      // 记住用户名处理
      if (rememberMe.value) {
        localStorage.setItem('rememberedUsername', user.username);
      } else {
        localStorage.removeItem('rememberedUsername');
      }

      successMessage.value = '登录成功！';
      showSucceeded.value = true;

      setTimeout(() => {
        router.push({ name: 'systemhomeView' });
      }, 2000);
    } else {
      errorMessage.value = response.msg || '登录失败';
      showError.value = true;
    }
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请检查网络连接';
    showError.value = true;
    console.error('登录失败:', error);
  }
};

// 挂载逻辑
onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername');
  if (rememberedUsername) {
    loginForm.value.username = rememberedUsername;
    loginForm.value.hasInput = true;
    rememberMe.value = true;

    // 从本地存储加载用户数据
    const storedUser = JSON.parse(localStorage.getItem('user') || 'null');
    if (storedUser?.username === rememberedUsername) {
      // 确保头像URL有效
      loginForm.value.image = storedUser.image || defaultAvatar;
    }
  }
});
</script>
<style scoped>
/* 主容器样式 */
.loginform-3 {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 30px;
  width: 90%;
  max-width: 400px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  z-index: 10;
}

/* 标题样式 */
.login {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
}

/* 表单容器 */
.loginform-3-center {
  margin-bottom: 25px;
}

/* 输入框容器 */
.input-container-3 {
  position: relative;
  margin-bottom: 25px;
}

/* 输入框样式 */
.input-container-3 input {
  width: 100%;
  padding: 15px 10px 10px;
  border: none;
  border-bottom: 2px solid #e0e0e0;
  background: transparent;
  font-size: 16px;
  color: #333;
  transition: all 0.3s ease;
}

.input-container-3 input:focus {
  outline: none;
  border-bottom-color: #4CAF50;
}

.input-container-3 input::placeholder {
  color: transparent;
}

/* 标签样式 */
.input-container-3 .label {
  position: absolute;
  top: 15px;
  left: 10px;
  font-size: 16px;
  color: #999;
  transition: all 0.3s ease;
  pointer-events: none;
}

.input-container-3 input:focus + .label,
.input-container-3 input:not(:placeholder-shown) + .label {
  top: -5px;
  left: 5px;
  font-size: 12px;
  color: #4CAF50;
  font-weight: 500;
}

/* 下划线动画 */
.underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: #4CAF50;
  transition: width 0.3s ease;
}

.input-container-3 input:focus ~ .underline {
  width: 100%;
}

/* 按钮容器 */
.btn-conteiner {
  margin-bottom: 20px;
}

/* 登录按钮 */
.btn-content {
  width: 100%;
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 15px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.btn-content:active {
  transform: translateY(0);
}

/* 箭头图标动画 */
.icon-arrow {
  transition: transform 0.3s ease;
}

.btn-content:hover .icon-arrow {
  transform: translateX(5px);
}

/* 选项容器 */
.radio_1 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #666;
}

/* 记住用户名选项 */
.radio_1 div:first-child {
  display: flex;
  align-items: center;
  gap: 8px;
}

.radio_1 input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #4CAF50;
}

/* 手机号登录按钮 */
.phone {
  background: transparent;
  border: none;
  color: #4CAF50;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.phone:hover {
  color: #45a049;
  text-decoration: underline;
}

/* 错误提示框 */
.error-message {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #ffebee;
  border-left: 4px solid #f44336;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

.error-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-icon {
  font-size: 18px;
}

.error-text {
  flex: 1;
  color: #c62828;
  font-size: 14px;
}

.error-close {
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s ease;
}

.error-close:hover {
  color: #333;
}

/* 成功提示框 */
.success-message {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #e8f5e8;
  border-left: 4px solid #4CAF50;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  animation: slideIn 0.3s ease-out;
}

.success-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.success-icon {
  font-size: 18px;
}

.success-text {
  flex: 1;
  color: #2e7d32;
  font-size: 14px;
}

.success-close {
  background: none;
  border: none;
  font-size: 18px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s ease;
}

.success-close:hover {
  color: #333;
}

/* 动画效果 */
@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .loginform-3 {
    padding: 25px;
    width: 95%;
  }
  
  .login {
    font-size: 22px;
  }
  
  .input-container-3 input {
    padding: 12px 10px 8px;
  }
  
  .btn-content {
    padding: 12px;
  }
  
  .radio_1 {
    font-size: 13px;
  }
}
</style>