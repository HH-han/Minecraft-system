<template>
    <div class="login-container">
        <div class="login-content">
            <div class="login-left">
                <h1>欢迎登录</h1>
                <p>使用邮箱验证码登录您的账号</p>
                <div class="login-image">
                    <img src="@/assets/defaultimage/mrtx.png" alt="登录背景" />
                </div>
            </div>
            <div class="login-right">
                <div class="login-form-wrapper">
                    <h2>邮箱验证码登录</h2>
                    <form @submit.prevent="handleSubmit" class="login-form">
                        <div class="input-container">
                            <input v-model="email" type="email" required id="email">
                            <label class="label" for="email">输入邮箱</label>
                            <div class="underline"></div>
                        </div>
                        <div class="input-container">
                            <input v-model="code" type="text" required id="code">
                            <label class="label" for="code">输入验证码</label>
                            <div class="underline"></div>
                            <button type="button" class="code-btn" @click="sendCode" :disabled="isSending">
                                {{ sendBtnText }}
                            </button>
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
            </div>
        </div>
        <!-- 验证码弹窗 -->
        <div>
            <Captcha v-if="showCaptcha" @verify="receivedCode => {
                code.value = receivedCode
                showCaptcha.value = false
                handleSubmit()
            }" />
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
            code: code.value
        });

        console.log('登录响应:', response);

        if (response.code === '0') {
            // 严格验证响应数据结构
            if (!response.data?.token || !response.data?.user) {
                throw new Error('响应数据不完整');
            }

            const { user, token } = response.data;

            // 验证必要字段
            if (!token || !user?.email) {
                throw new Error('无效的用户数据');
            }

            ElMessage.success('登录成功！');
            // 跳转
            router.push({ name: 'systemhomeView' });
        } else {
            ElMessage.error('登录失败/使用账号密码登录');
            setTimeout(() => {
                router.push('/login');
            }, 3000);
        }
    } catch (error) {
        showCaptcha.value = true;
        ElMessage.error('登录失败: ' + (error.response?.data?.message || error.message));
    }

};

// 组件销毁时清除定时器
onUnmounted(() => {
    clearInterval(timer.value);
});
</script>

<style scoped>
/* 主容器样式 */
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 登录内容容器 */
.login-content {
  display: flex;
  width: 90%;
  max-width: 1200px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 左侧内容 */
.login-left {
  flex: 1;
  padding: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-left h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
}

.login-left p {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.login-image {
  margin-top: 40px;
}

.login-image img {
  width: 100%;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

/* 右侧内容 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
}

.login-form-wrapper h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 40px;
  text-align: center;
}

/* 表单容器 */
.login-form {
  width: 100%;
}

/* 输入框容器 */
.input-container {
  position: relative;
  margin-bottom: 30px;
}

/* 输入框样式 */
.input-container input {
  width: 100%;
  padding: 16px 0px 16px 0px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #f9f9f9;
  font-size: 16px;
  color: #333;
  transition: all 0.3s ease;
}

.input-container input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-container input::placeholder {
  color: #999;
}

/* 标签样式 */
.input-container .label {
  position: absolute;
  top: -10px;
  left: 15px;
  padding: 0 10px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
  transition: all 0.3s ease;
}

.input-container input:focus + .label {
  color: #667eea;
}

/* 下划线动画 */
.underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 3px;
  background: #667eea;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.input-container input:focus ~ .underline {
  width: 100%;
}

/* 验证码按钮 */
.code-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.code-btn:hover:not(:disabled) {
  background: #5a6fd8;
  transform: translateY(-50%) translateY(-1px);
}

.code-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 登录按钮 */
.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  margin-top: 20px;
  display: flex;
  align-items: stretch;
  justify-content: center;
  gap: 10px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:active {
  transform: translateY(0);
}

/* 箭头图标动画 */
.icon-arrow {
  transition: transform 0.3s ease;
}

.submit-btn:hover .icon-arrow {
  transform: translateX(5px);
}

.icon-arrow svg {
  width: 24px;
  height: 24px;
  fill: white;
}

/* 返回链接 */
.back-link {
  margin-top: 25px;
  text-align: center;
}

.back-link a {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.back-link a:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .login-content {
    flex-direction: column;
  }
  
  .login-left {
    padding: 40px;
    text-align: center;
  }
  
  .login-right {
    padding: 40px;
  }
  
  .login-left h1 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 20px;
  }
  
  .login-content {
    width: 100%;
  }
  
  .login-left,
  .login-right {
    padding: 30px 20px;
  }
  
  .login-form-wrapper h2 {
    font-size: 20px;
  }
  
  .input-container input {
    padding: 15px 12px 10px;
  }
  
  .submit-btn {
    padding: 15px;
  }
}
</style>