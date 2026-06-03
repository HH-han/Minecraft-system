<template>
  <div class="phonelogin-container">
    <div class="phonelogin-card">
      <form id="loginForm" class="phonelogin-form" @submit.prevent="handleLogin">
        <div class="phonelogin-title">手机号登录</div>
        <div class="phonelogin-input-container">
          <div class="phonelogin-input-group">
            <div class="phonelogin-svg-icon">
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
            </div>
            <input type="text" class="phonelogin-input" id="phoneInput" v-model="phoneNumber" maxlength="11"
              placeholder="请输入手机号" />
          </div>
          <div class="phonelogin-input-group">
            <div class="phonelogin-svg-icon">
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
            </div>
            <input type="text" class="phonelogin-input" id="codeInput" v-model="verificationCode"
              placeholder="请输入验证码" />

          </div>
        </div>

        <div>
          <button type="button" class="phonelogin-code-btn" id="getCodeBtn" :disabled="isCodeButtonDisabled"
            @click="sendVerificationCode">
            {{ countdown > 0 ? `${countdown}s后重新获取` : "获取验证码" }}
          </button>
        </div>
        <div>
          <input type="submit" value="登录" class="phonelogin-submit-btn" id="loginBtn"
            :disabled="isLoginButtonDisabled" />
        </div>
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
/* 通用样式 */
.phonelogin-container {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC',
    'Helvetica Neue', Helvetica, Arial, sans-serif;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 卡片样式 */
.phonelogin-card {
  width: 100%;
  min-width: 420px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12),
              0 8px 24px rgba(0, 0, 0, 0.06),
              0 2px 8px rgba(0, 0, 0, 0.04);
  padding: 40px 32px;
  animation: phonelogin-fadeIn 0.3s ease-out;
}

@keyframes phonelogin-fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 标题样式 */
.phonelogin-title {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  text-align: left;
  margin: 0 0 32px 0;
  line-height: 1.2;
  letter-spacing: -0.01em;
}

/* 输入框 */
.phonelogin-input-container {
  margin-bottom: 8px;
}

/* 输入框组 */
.phonelogin-input-group {
  display: flex;
  align-items: center;
  background: #ffffff;
  border-radius: 12px;
  padding: 0 16px;
  margin-bottom: 16px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid #d2d2d6;
}

.phonelogin-input-group:focus-within {
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.15);
}

/* svg样式 */
.phonelogin-svg-icon {
  width: 20px;
  height: 20px;
  margin-right: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
}

.phonelogin-svg-icon .icon {
  width: 20px;
  height: 20px;
}

/* 输入样式 */
.phonelogin-input {
  flex: 1;
  height: 48px;
  border: none;
  background: transparent;
  font-size: 16px;
  color: #1d1d1f;
  outline: none;
  padding: 0;
  font-family: inherit;
  box-sizing: border-box;
}

.phonelogin-input::placeholder {
  color: #a1a1a6;
  font-weight: 400;
}

/* 按钮样式 — 获取验证码按钮 */
.phonelogin-code-btn {
  width: 100%;
  padding: 12px 24px;
  background: #f5f5f7;
  color: #1d1d1f;
  border: 1px solid #d2d2d6;
  border-radius: 980px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  margin: 16px 0 12px 0;
  font-family: inherit;
}

.phonelogin-code-btn:hover:not(:disabled) {
  background: #ebebee;
}

.phonelogin-code-btn:active:not(:disabled) {
  background: #dcdce0;
  transform: scale(0.99);
}

.phonelogin-code-btn:disabled {
  background: #f5f5f7;
  color: #a1a1a6;
  border-color: #d2d2d6;
  cursor: not-allowed;
}

/* 登录按钮样式 — Apple 胶囊主按钮 */
.phonelogin-submit-btn {
  width: 100%;
  padding: 14px 24px;
  background: #0071e3;
  color: white;
  border: none;
  border-radius: 980px;
  font-size: 17px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  font-family: inherit;
  letter-spacing: 0;
  text-transform: none;
}

.phonelogin-submit-btn:hover:not(:disabled) {
  background: #0077ed;
  box-shadow: 0 4px 14px rgba(0, 113, 227, 0.25);
}

.phonelogin-submit-btn:active:not(:disabled) {
  background: #006edc;
  transform: scale(0.99);
}

.phonelogin-submit-btn:disabled {
  background: #a1a1a6;
  cursor: not-allowed;
  box-shadow: none;
  opacity: 0.6;
}

/* 响应式 */
@media (max-width: 480px) {
  .phonelogin-card {
    padding: 32px 24px;
    min-width: auto;
    width: calc(100% - 40px);
    border-radius: 20px;
  }

  .phonelogin-title {
    font-size: 24px;
    margin-bottom: 28px;
  }

  .phonelogin-input {
    font-size: 16px;
    height: 44px;
  }

  .phonelogin-input-group {
    padding: 0 14px;
    margin-bottom: 12px;
  }

  .phonelogin-code-btn {
    padding: 12px 20px;
    font-size: 14px;
  }

  .phonelogin-submit-btn {
    padding: 14px 24px;
    font-size: 17px;
  }
}
</style>