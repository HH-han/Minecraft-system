<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <div class="login-title">
        <h2>{{ activeTab === 'login' ? '登录' : '注册' }}</h2>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
            <el-form-item label="账号" prop="account">
              <el-input v-model="loginForm.account" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" show-password />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="registerForm.phone" placeholder="请输入电话（选填）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="login-button" @click="handleRegister" :loading="loading">注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { setToken, setUsername } from '@/utils/storage'
import { getCurrentTheme, setTheme, THEMES } from '@/utils/theme'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  account: '',
  password: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

// 登录表单验证规则
const loginRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 注册表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20位之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: []
}

// 登录表单引用
const loginFormRef = ref()
// 注册表单引用
const registerFormRef = ref()

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const response = await login(loginForm)
    
    if (response.code === 200) {
      ElMessage.success(response.message)
      // 存储token等信息
      setToken(response.data.token)
      setUsername(response.data.username)
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message || '登录失败')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    // 移除确认密码字段
    const { confirmPassword, ...registerData } = registerForm
    
    const response = await register(registerData)
    
    if (response.code === 200) {
      ElMessage.success(response.message)
      // 注册成功后切换到登录标签
      activeTab.value = 'login'
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message || '注册失败')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  overflow: hidden;
  position: relative;
  transition: background var(--transition-slow);
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="p" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23p)"/></svg>');
  opacity: 0.3;
  z-index: 0;
}

.login-form-wrapper {
  width: 400px;
  padding: var(--space-8);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-xl);
  border: 1px solid rgba(255, 255, 255, 0.18);
  z-index: 1;
  transition: all var(--transition-normal);
}

.login-form-wrapper:hover {
  box-shadow: 0 12px 40px 0 rgba(31, 38, 135, 0.45);
  transform: translateY(-5px);
}

.login-title {
  text-align: center;
  margin-bottom: var(--space-8);
}

.login-title h2 {
  color: var(--text-inverse);
  font-size: 28px;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  font-weight: 600;
}

.login-tabs {
  width: 100%;
}

.login-tabs .el-tabs__header {
  margin-bottom: var(--space-8);
}

.login-tabs .el-tabs__nav {
  justify-content: center;
}

.login-tabs .el-tabs__tab {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  padding: 10px 20px;
}

.login-tabs .el-tabs__tab.is-active {
  color: var(--text-inverse);
  font-weight: 600;
}

.login-tabs .el-tabs__active-bar {
  background-color: var(--text-inverse);
  height: 3px;
  border-radius: 3px;
}

.login-form-wrapper .el-form-item__label {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.login-form-wrapper .el-input {
  --el-input-bg-color: rgba(255, 255, 255, 0.1);
  --el-input-border-color: rgba(255, 255, 255, 0.3);
  --el-input-text-color: var(--text-inverse);
  --el-input-placeholder-color: rgba(255, 255, 255, 0.6);
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.login-form-wrapper .el-input:hover {
  --el-input-border-color: rgba(255, 255, 255, 0.5);
}

.login-form-wrapper .el-input.is-focus {
  --el-input-border-color: var(--text-inverse);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2);
}

.login-button {
  width: 100%;
  margin-top: var(--space-8);
  padding: 12px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: var(--text-inverse);
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.login-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.3);
}

.login-button:active {
  background: rgba(255, 255, 255, 0.4);
}

.login-button:disabled {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.5);
}

@media (max-width: 480px) {
  .login-form-wrapper {
    width: 90%;
    padding: var(--space-6);
  }
  
  .login-title h2 {
    font-size: 24px;
  }
}
</style>