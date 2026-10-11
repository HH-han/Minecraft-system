<template>
  <div class="phonelogin-container">
    <div class="phonelogin-card">
      <header class="phonelogin-header">
        <div class="phonelogin-brand">
          <img class="phonelogin-brand-logo" src="@/assets/defaultimage/mrtx.png" alt="博览旅行" />
          <span class="phonelogin-brand-name">博览旅行</span>
        </div>
        <h1 class="phonelogin-title">手机号登录</h1>
        <p class="phonelogin-subtitle">未注册的手机号将自动创建账号</p>
      </header>

      <form id="loginForm" class="phonelogin-form" @submit.prevent="handleLogin">
        <div class="phonelogin-field">
          <span class="phonelogin-field-icon">
            <svg t="1744436216245" class="icon" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="7805">
              <path
                d="M768.9 784.8H252.8c-6.6 0-12-5.4-12-12V194.2c0-6.6 5.4-12 12-12h516.1c6.6 0 12 5.4 12 12v578.6c0 6.6-5.4 12-12 12z"
                fill="#83FCE5" p-id="7806"></path>
              <path d="M240.8 177.2h553.9v10H240.8z" fill="#3983FD" p-id="7807"></path>
              <path
                d="M729.7 81c11.9 0 23.2 4.7 31.8 13.2 8.5 8.5 13.2 19.8 13.2 31.8v765.3c0 11.9-4.7 23.2-13.2 31.8-8.5 8.5-19.8 13.2-31.8 13.2H295.1c-11.9 0-23.2-4.7-31.8-13.2-8.5-8.5-13.2-19.8-13.2-31.8V126c0-11.9 4.7-23.2 13.2-31.8 8.5-8.5 19.8-13.2 31.8-13.2h434.6m0-20H295.1c-35.8 0-65 29.2-65 65v765.3c0 35.8 29.3 65 65 65h434.6c35.8 0 65-29.3 65-65V126c0-35.7-29.2-65-65-65z"
                fill="#3983FD" p-id="7808"></path>
              <path d="M510.3 854.4m-30 0a30 30 0 1 0 60 0 30 30 0 1 0-60 0Z" fill="#3983FD" p-id="7809"></path>
              <path d="M240.8 779.8h553.9v10H240.8z" fill="#3983FD" p-id="7810"></path>
            </svg>
          </span>
          <input id="phoneInput" v-model="phoneNumber" class="phonelogin-input" type="text" maxlength="11"
            placeholder="请输入手机号" />
        </div>

        <div class="phonelogin-field">
          <span class="phonelogin-field-icon">
            <svg t="1744436697129" class="icon" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="9792">
              <path
                d="M857.8 239.5c-199-63.7-333.4-176.1-342.5-183.8v-0.4s-0.1 0.1-0.3 0.2c-0.2-0.1-0.3-0.2-0.3-0.2v0.4c-9 7.7-143.3 120-342.1 183.7-14.8 4.7-24.9 18.5-24.9 34v294.9c0 41.7 14.8 82 41.8 113.8 70.8 83.3 217.1 242.1 325.2 269.1v0.1c0.1 0 0.2 0 0.3-0.1 0.1 0 0.2 0 0.3 0.1v-0.1C624.1 924 771.6 763.3 841.9 680.4c26.1-30.8 40.4-69.8 40.4-110.2V273.1c0-15.3-9.9-28.9-24.5-33.6z m-51.9 316.8c0 32-11.3 62.9-32 87.3-55.7 65.6-172.5 192.9-258.7 214.4v0.1c-0.1 0-0.1 0-0.2-0.1-0.1 0-0.1 0-0.2 0.1v-0.1c-85.6-21.4-201.4-147.1-257.5-213.1-21.4-25.2-33.1-57.1-33.1-90.1V321.3c0-12.3 8-23.2 19.7-27C401.4 243.9 507.7 155 514.8 148.9v-0.3l0.2 0.2 0.2-0.2v0.3c7.2 6.1 113.6 95.1 271.2 145.6 11.6 3.7 19.4 14.4 19.4 26.6v235.2z"
                fill="#0C9EEF" p-id="9793"></path>
              <path
                d="M474.3 634.1l-109.1-98.8c-11.6-10.5-12.5-28.6-2-40.1 10.5-11.6 28.6-12.5 40.1-2L512.4 592c11.6 10.5 12.5 28.6 2 40.1-10.4 11.5-28.5 12.4-40.1 2z"
                fill="#57C0ED" p-id="9794"></path>
              <path
                d="M691.9 465.6L513.5 634.1c-11.4 10.7-29.4 10.2-40.2-1.1-10.7-11.4-10.2-29.4 1.1-40.2l178.5-168.5c11.4-10.7 29.4-10.2 40.2 1.1 10.7 11.4 10.2 29.5-1.2 40.2z"
                fill="#57C0ED" p-id="9795"></path>
            </svg>
          </span>
          <input id="codeInput" v-model="verificationCode" class="phonelogin-input" type="text"
            placeholder="请输入验证码" />
          <button id="getCodeBtn" type="button" class="phonelogin-code-btn" :disabled="isCodeButtonDisabled"
            @click="sendVerificationCode">
            {{ countdown > 0 ? `${countdown}s后重新获取` : "获取验证码" }}
          </button>
        </div>

        <input id="loginBtn" type="submit" value="登录" class="phonelogin-submit-btn"
          :disabled="isLoginButtonDisabled" />
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from '@/utils/request';
import { useAuthStore } from '@/stores/auth.js'

const router = useRouter();
const phoneNumber = ref("");
const verificationCode = ref("");
const isCodeButtonDisabled = ref(false);
const isLoginButtonDisabled = ref(true);
const countdown = ref(0);

// auth store
const authStore = useAuthStore();

// 发送验证码
const sendVerificationCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(phoneNumber.value)) {
    ElMessage.error("请输入有效的手机号");
    return;
  }

  try {
    const response = await request.post('/api/public/user/send-code', {
      phone: phoneNumber.value
    });

    if (response.success) {
      ElMessage.success("验证码已发送");
      isCodeButtonDisabled.value = true;
      countdown.value = 60;

      const timer = setInterval(() => {
        if (countdown.value <= 0) {
          clearInterval(timer);
          isCodeButtonDisabled.value = false;
          return;
        }
        countdown.value--;
      }, 1000);
    } else {
      ElMessage.error(response.message || "验证码发送失败");
    }
  } catch (error) {
    ElMessage.error("验证码发送失败");
    console.error("验证码发送失败:", error);
  }
};

// 手机号+验证码登录
const handleLogin = async () => {
  try {
    // 清除旧的token和用户数据
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    // 使用auth store的phoneLogin方法进行手机登录
    const response = await authStore.phoneLogin({
      phone: phoneNumber.value,
      code: verificationCode.value
    });

    if (response.success) {
      ElMessage.success("登录成功");
      router.push({ name: "Home_5" });
    } else {
      ElMessage.error(response.message || "登录失败");
    }
  } catch (error) {
    ElMessage.error("登录请求失败");
    console.error("登录失败:", error);
  }
};

// 自动启用/禁用登录按钮
watchEffect(() => {
  const isPhoneValid = /^1[3-9]\d{9}$/.test(phoneNumber.value);
  const isCodeValid = /^\d{6}$/.test(verificationCode.value);
  isLoginButtonDisabled.value = !(isPhoneValid && isCodeValid);
});
</script>

<style scoped>
/* 容器：填满登录弹层，由内层卡片负责视觉 */
.phonelogin-container {
  width: 100%;
}

/* 卡片 —— 主题令牌在此定义，暗色主题只需覆盖令牌 */
.phonelogin-card {
  position: relative;
  width: 100%;
  box-sizing: border-box;
  padding: 44px 36px 36px;
  border-radius: 15px;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC',
    'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  /* 亮色主题令牌 */
  --card-bg: #ffffff;
  --card-fg: #1d1d1f;
  --card-muted: #6e6e73;
  --card-border: #d2d2d6;
  --field-bg: #ffffff;
  --accent: #0071e3;
  --accent-hover: #0077ed;
  --accent-active: #006edc;
  --accent-ring: rgba(0, 113, 227, 0.16);
  --card-glow: rgba(0, 113, 227, 0.08);

  background:
    radial-gradient(220px 220px at 100% 0%, var(--card-glow), transparent 70%),
    var(--card-bg);
  color: var(--card-fg);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.10),
              0 2px 8px rgba(0, 0, 0, 0.04);
  animation: phonelogin-card-in 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* 顶部品牌渐变条 */
.phonelogin-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 4px;
  background: linear-gradient(90deg, var(--accent), #5a9bff);
}

@keyframes phonelogin-card-in {
  from {
    opacity: 0;
    transform: translateY(14px) scale(0.99);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

/* 头部 */
.phonelogin-header {
  text-align: center;
  margin-bottom: 28px;
}

.phonelogin-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 22px;
}

.phonelogin-brand-logo {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.phonelogin-brand-name {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.12em;
  color: var(--card-muted);
}

.phonelogin-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--card-fg);
}

.phonelogin-subtitle {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--card-muted);
}

/* 表单 */
.phonelogin-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.phonelogin-field {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 48px;
  box-sizing: border-box;
  padding: 0 14px;
  border: 1px solid var(--card-border);
  border-radius: 12px;
  background: var(--field-bg);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.phonelogin-field:focus-within {
  border-color: var(--accent);
  box-shadow: 0 0 0 4px var(--accent-ring);
}

.phonelogin-field-icon {
  flex: 0 0 20px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.phonelogin-field-icon .icon {
  width: 20px;
  height: 20px;
}

.phonelogin-input {
  flex: 1;
  min-width: 0;
  height: 100%;
  border: none;
  background: transparent;
  outline: none;
  font-size: 16px;
  color: var(--card-fg);
  font-family: inherit;
}

.phonelogin-input::placeholder {
  color: #a1a1a6;
}

.phonelogin-code-btn {
  flex: 0 0 auto;
  padding: 0 2px;
  border: none;
  background: transparent;
  color: var(--accent);
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  white-space: nowrap;
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.phonelogin-code-btn:hover:not(:disabled) {
  opacity: 0.72;
}

.phonelogin-code-btn:disabled {
  color: var(--card-muted);
  cursor: not-allowed;
}

/* 主按钮 */
.phonelogin-submit-btn {
  width: 100%;
  height: 50px;
  margin-top: 6px;
  border: none;
  border-radius: 980px;
  background: var(--accent);
  color: #ffffff;
  font-size: 17px;
  font-weight: 500;
  font-family: inherit;
  letter-spacing: -0.01em;
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease, transform 0.1s ease;
}

.phonelogin-submit-btn:hover:not(:disabled) {
  background: var(--accent-hover);
  box-shadow: 0 8px 22px var(--accent-ring);
}

.phonelogin-submit-btn:active:not(:disabled) {
  background: var(--accent-active);
  transform: scale(0.99);
}

.phonelogin-submit-btn:disabled {
  background: var(--card-border);
  color: var(--card-muted);
  cursor: not-allowed;
  box-shadow: none;
}

/* ================= 暗色主题 ================= */
html[data-theme="dark"] .phonelogin-card {
  --card-bg: #1c1c1e;
  --card-fg: #f5f5f7;
  --card-muted: #98989d;
  --card-border: #3a3a3c;
  --field-bg: #2c2c2e;
  --accent: #0a84ff;
  --accent-hover: #3395ff;
  --accent-active: #0071e3;
  --accent-ring: rgba(10, 132, 255, 0.28);
  --card-glow: rgba(10, 132, 255, 0.10);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.55),
              0 2px 8px rgba(0, 0, 0, 0.35);
}

html[data-theme="dark"] .phonelogin-input::placeholder {
  color: #6e6e73;
}

/* 响应式 */
@media (max-width: 480px) {
  .phonelogin-card {
    padding: 36px 22px 28px;
    border-radius: 0;
  }

  .phonelogin-title {
    font-size: 22px;
  }

  .phonelogin-code-btn {
    font-size: 13px;
  }
}
</style>
