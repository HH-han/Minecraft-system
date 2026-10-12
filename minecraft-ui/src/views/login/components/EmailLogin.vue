<template>
    <div class="login-container">
        <div class="login-card">
            <header class="login-header">
                <div class="login-brand">
                    <img class="login-brand-logo" src="@/assets/defaultimage/mrtx.png" alt="博览旅行" />
                    <span class="login-brand-name">博览旅行</span>
                </div>
                <h1 class="login-title">邮箱验证码登录</h1>
                <p class="login-subtitle">输入邮箱获取验证码，快速进入您的账号</p>
            </header>

            <form @submit.prevent="handleSubmit" class="login-form">
                <div class="login-field">
                    <label class="login-label" for="email">邮箱地址</label>
                    <input id="email" v-model="email" class="login-input" type="email"
                        placeholder="you@example.com" required>
                </div>

                <div class="login-field">
                    <label class="login-label" for="code">验证码</label>
                    <div class="login-code-row">
                        <input id="code" v-model="code" class="login-input" type="text"
                            placeholder="请输入 6 位验证码" required>
                        <button type="button" class="login-code-btn" :disabled="isSending" @click="sendCode">
                            {{ sendBtnText }}
                        </button>
                    </div>
                </div>

                <button type="submit" class="submit-btn">
                    <span class="btn-title">登录</span>
                    <span class="icon-arrow">
                        <svg viewBox="0 0 66 43" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                            <g id="arrow" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
                                <path id="arrow-icon-one" d="M40.1543933,3.89485454 L43.9763149,0.139296592 C44.1708311,-0.0518420739 44.4826329,-0.0518571125 44.6771675,0.139262789 L65.6916134,20.7848311 C66.0855801,21.1718824 66.0911863,21.8050225 65.704135,22.1989893 C65.7000188,22.2031791 65.6958657,22.2073326 65.6916762,22.2114492 L44.677098,42.8607841 C44.4825957,43.0519059 44.1708242,43.0519358 43.9762853,42.8608513 L40.1545186,39.1069479 C39.9575152,38.9134427 39.9546793,38.5968729 40.1481845,38.3998695 C40.1502893,38.3977268 40.1524132,38.395603 40.1545562,38.3934985 L56.9937789,21.8567812 C57.1908028,21.6632968 57.193672,21.3467273 57.0001876,21.1497035 C56.9980647,21.1475418 56.9959223,21.1453995 56.9937605,21.1432767 L40.1545208,4.60825197 C39.9574869,4.41477773 39.9546013,4.09820839 40.1480756,3.90117456 C40.1501626,3.89904911 40.1522686,3.89694235 40.1543933,3.89485454 Z" fill="#FFFFFF"></path>
                                <path id="arrow-icon-two" d="M20.1543933,3.89485454 L23.9763149,0.139296592 C24.1708311,-0.0518420739 24.4826329,-0.0518571125 24.6771675,0.139262789 L45.6916134,20.7848311 C46.0855801,21.1718824 46.0911863,21.8050225 45.704135,22.1989893 C45.7000188,22.2031791 45.6958657,22.2073326 45.6916762,22.2114492 L24.677098,42.8607841 C24.4825957,43.0519059 24.1708242,43.0519358 23.9762853,42.8608513 L20.1545186,39.1069479 C19.9575152,38.9134427 19.9546793,38.5968729 20.1481845,38.3998695 C20.1502893,38.3977268 20.1524132,38.395603 20.1545562,38.3934985 L36.9937789,21.8567812 C37.1908028,21.6632968 37.193672,21.3467273 37.0001876,21.1497035 C36.9980647,21.1475418 36.9959223,21.1453995 36.9937605,21.1432767 L20.1545208,4.60825197 C19.9574869,4.41477773 19.9546013,4.09820839 20.1480756,3.90117456 C20.1501626,3.89904911 20.1522686,3.89694235 20.1543933,3.89485454 Z" fill="#FFFFFF"></path>
                                <path id="arrow-icon-three" d="M0.154393339,3.89485454 L3.97631488,0.139296592 C4.17083111,-0.0518420739 4.48263286,-0.0518571125 4.67716753,0.139262789 L25.6916134,20.7848311 C26.0855801,21.1718824 26.0911863,21.8050225 25.704135,22.1989893 C25.7000188,22.2031791 25.6958657,22.2073326 25.6916762,22.2114492 L4.67709797,42.8607841 C4.48259567,43.0519059 4.17082418,43.0519358 3.97628526,42.8608513 L0.154518591,39.1069479 C-0.0424848215,38.9134427 -0.0453206733,38.5968729 0.148184538,38.3998695 C0.150289256,38.3977268 0.152413239,38.395603 0.154556228,38.3934985 L16.9937789,21.8567812 C17.1908028,21.6632968 17.193672,21.3467273 17.0001876,21.1497035 C16.9980647,21.1475418 16.9959223,21.1453995 16.9937605,21.1432767 L0.15452076,4.60825197 C-0.0425130651,4.41477773 -0.0453986756,4.09820839 0.148075568,3.90117456 C0.150162624,3.89904911 0.152268631,3.89694235 0.154393339,3.89485454 Z" fill="#FFFFFF"></path>
                            </g>
                        </svg>
                    </span>
                </button>
            </form>

            <div class="back-link">
                <router-link to="/login">返回账号密码登录</router-link>
            </div>
        </div>

        <!-- 验证码弹窗 -->
        <div>
            <Captcha v-if="showCaptcha" @verify="handleVerify" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue';
import Captcha from '@/components/PromptComponent/CaptchamodalBox.vue';
import { ElMessage } from 'element-plus';
import { EmailCaptcha } from '@/api/user';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.js'

// 定义响应式数据
const email = ref('');
const code = ref('');
const isSending = ref(false);
const countdown = ref(0);
const timer = ref(null);
const showCaptcha = ref(false);

// 路由实例
const router = useRouter();

// auth store
const authStore = useAuthStore();

// 计算属性：发送按钮的文本
const sendBtnText = computed(() => {
    return countdown.value > 0 ? `${countdown.value}秒后重试` : '获取验证码';
});

// 发送验证码
const sendCode = async () => {
    if (!email.value || !email.value.trim()) {
        ElMessage.warning('请输入有效的邮箱地址');
        showCaptcha.value = false;
        return;
    }

    if (isSending.value) return;

    isSending.value = true;
    countdown.value = 60;

    try {
        const response = await EmailCaptcha({
            email: email.value
        });

        ElMessage.success('验证码已发送，请查收邮箱');
        showCaptcha.value = true;

        // 开始倒计时
        timer.value = setInterval(() => {
            countdown.value--;
            if (countdown.value <= 0) {
                clearInterval(timer.value);
                isSending.value = false;
            }
        }, 1000);
    } catch (error) {
        ElMessage.error('发送验证码失败: ' + (error.response?.data?.message || error.message));
        showCaptcha.value = false;
        isSending.value = false;
        countdown.value = 0;
        clearInterval(timer.value);
    }
};

// 提交表单
const handleSubmit = async () => {
    console.log('提交表单:', email.value, code.value);
    if (!email.value || !code.value) {
        ElMessage.warning('请输入邮箱和验证码');
        return;
    }

    if (!email.value.trim()) {
        ElMessage.error('邮箱不能为空');
        return;
    }

    try {
        // 清除旧的token和用户数据
        localStorage.removeItem('token');
        localStorage.removeItem('user');

        // 使用auth store的emailLogin方法进行邮箱登录
        const response = await authStore.emailLogin({
            email: email.value.trim(),
            captcha: code.value
        });

        console.log('登录响应:', response);

        if (response.code === 200) {
            // 后端 LoginResponse 为扁平结构：token/username/email 等直接位于 data 上
            const { token, email: userEmail } = response.data || {};

            // 验证必要字段
            if (!token || !userEmail) {
                throw new Error('响应数据不完整');
            }

            ElMessage.success('登录成功！');
            // 跳转主页
            router.push('/home');
        } else {
            ElMessage.error(response.message || '登录失败，请使用账号密码登录');
            setTimeout(() => {
                router.push('/login');
            }, 3000);
        }
    } catch (error) {
        showCaptcha.value = true;
        ElMessage.error('登录失败: ' + (error.response?.data?.message || error.message));
    }

};

// 处理验证码验证
const handleVerify = (receivedCode) => {
    code.value = receivedCode;
    showCaptcha.value = false;
    handleSubmit();
};

// 组件销毁时清除定时器
onUnmounted(() => {
    clearInterval(timer.value);
});
</script>

<style scoped>
/* 容器：填满登录弹层，由内层卡片负责视觉 */
.login-container {
  width: 100%;
}

/* 卡片 —— 主题令牌在此定义，暗色主题只需覆盖令牌 */
.login-card {
  position: relative;
  width: 100%;
  box-sizing: border-box;
  padding: 44px 36px 32px;
  border-radius: 15px;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC',
    'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  /* 主题令牌别名（随全局主题自动切换） */
  --card-bg: var(--theme-bg-surface);
  --card-fg: var(--theme-text-1);
  --card-muted: var(--theme-text-2);
  --card-border: var(--theme-border);
  --field-bg: var(--theme-bg-surface);
  --accent: var(--theme-primary);
  --accent-hover: var(--theme-primary-hover);
  --accent-active: var(--theme-primary-hover);
  --accent-ring: var(--theme-primary-soft);
  --card-glow: var(--theme-primary-soft);

  background:
    radial-gradient(220px 220px at 100% 0%, var(--card-glow), transparent 70%),
    var(--card-bg);
  color: var(--card-fg);
  box-shadow: var(--theme-shadow-lg);
  animation: login-card-in 0.45s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* 顶部品牌渐变条 */
.login-card::before {
  content: '';
  position: absolute;
  inset: 0 0 auto 0;
  height: 4px;
  background: var(--theme-gradient);
}

@keyframes login-card-in {
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
.login-header {
  text-align: center;
  margin-bottom: 26px;
}

.login-brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 22px;
}

.login-brand-logo {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: var(--theme-shadow-sm);
}

.login-brand-name {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.12em;
  color: var(--card-muted);
}

.login-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--card-fg);
}

.login-subtitle {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--card-muted);
}

/* 表单 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.login-label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--card-muted);
}

.login-input {
  width: 100%;
  height: 48px;
  box-sizing: border-box;
  padding: 0 14px;
  border: 1px solid var(--card-border);
  border-radius: 12px;
  background: var(--field-bg);
  color: var(--card-fg);
  font-size: 16px;
  font-family: inherit;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.login-input::placeholder {
  color: var(--theme-text-3);
}

.login-input:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 4px var(--accent-ring);
}

/* 验证码行 */
.login-code-row {
  display: flex;
  gap: 10px;
}

.login-code-row .login-input {
  flex: 1;
  min-width: 0;
}

.login-code-btn {
  flex: 0 0 auto;
  min-width: 104px;
  height: 48px;
  padding: 0 16px;
  border: none;
  border-radius: 12px;
  background: var(--accent);
  color: var(--theme-primary-text);
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  white-space: nowrap;
  cursor: pointer;
  transition: background-color 0.2s ease, opacity 0.2s ease;
}

.login-code-btn:hover:not(:disabled) {
  background: var(--accent-hover);
}

.login-code-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

/* 主按钮 */
.submit-btn {
  position: relative;
  width: 100%;
  height: 50px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  border-radius: 980px;
  background: var(--accent);
  color: var(--theme-primary-text);
  font-size: 17px;
  font-weight: 500;
  font-family: inherit;
  letter-spacing: -0.01em;
  cursor: pointer;
  overflow: hidden;
  transition: background-color 0.2s ease, box-shadow 0.2s ease, transform 0.1s ease;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -60%;
  width: 40%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.55s ease;
}

.submit-btn:hover {
  background: var(--accent-hover);
  box-shadow: 0 8px 22px var(--accent-ring);
}

.submit-btn:hover::before {
  left: 120%;
}

.submit-btn:active {
  background: var(--accent-active);
  transform: scale(0.99);
}

.btn-title {
  position: relative;
}

.icon-arrow {
  position: relative;
  display: inline-flex;
  align-items: center;
  transition: transform 0.2s ease;
}

.submit-btn:hover .icon-arrow {
  transform: translateX(4px);
}

.icon-arrow svg {
  width: 18px;
  height: 18px;
}

/* 返回链接 */
.back-link {
  margin-top: 22px;
  text-align: center;
  font-size: 14px;
}

.back-link a {
  color: var(--accent);
  text-decoration: none;
  transition: opacity 0.2s ease;
}

.back-link a:hover {
  opacity: 0.75;
  text-decoration: underline;
  text-underline-offset: 3px;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card {
    padding: 36px 22px 28px;
    border-radius: 0;
  }

  .login-title {
    font-size: 22px;
  }

  .login-code-btn {
    min-width: 92px;
    padding: 0 12px;
    font-size: 13px;
  }
}
</style>
