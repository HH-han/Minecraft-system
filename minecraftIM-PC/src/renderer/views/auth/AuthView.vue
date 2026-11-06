<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';

const { t } = useI18n();
const authStore = useAuthStore();

type Mode = 'login' | 'register';
const mode = ref<Mode>('login');

const loginForm = reactive({
  account: '',
  password: ''
});

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
});

async function submitLogin(): Promise<void> {
  if (!loginForm.account.trim() || !loginForm.password) {
    ElMessage.warning(t('home.auth.emptyError'));
    return;
  }
  const ok = await authStore.login({
    account: loginForm.account.trim(),
    password: loginForm.password
  });
  if (ok) ElMessage.success(t('auth.loginSuccess'));
}

async function submitRegister(): Promise<void> {
  const form = registerForm;
  if (!form.username.trim() || !form.password || !form.email.trim() || !form.phone.trim()) {
    ElMessage.warning(t('home.auth.emptyError'));
    return;
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning(t('auth.register.passwordMismatch'));
    return;
  }
  const ok = await authStore.register({
    username: form.username.trim(),
    password: form.password,
    email: form.email.trim(),
    phone: form.phone.trim()
  });
  if (ok) ElMessage.success(t('auth.register.success'));
}

function switchMode(next: Mode): void {
  mode.value = next;
}
</script>

<template>
  <div class="auth-shell">
    <aside class="auth-brand">
      <div class="brand-mark">MC</div>
      <h1 class="brand-title">Minecraft IM</h1>
      <p class="brand-slogan">{{ t('auth.slogan') }}</p>
      <p class="brand-slogan-sub">{{ t('auth.sloganSub') }}</p>
      <div class="brand-blocks" aria-hidden="true">
        <span v-for="i in 24" :key="i" class="block" :style="{ '--i': i }" />
      </div>
    </aside>

    <section class="auth-panel">
      <div class="auth-card">
        <header class="auth-card-header">
          <h2>{{ mode === 'login' ? t('auth.login.title') : t('auth.register.title') }}</h2>
          <p>{{ mode === 'login' ? t('auth.login.subtitle') : t('auth.register.subtitle') }}</p>
        </header>

        <el-form
          v-if="mode === 'login'"
          label-position="top"
          @submit.prevent="submitLogin"
        >
          <el-form-item :label="t('auth.login.account')">
            <el-input
              v-model="loginForm.account"
              :placeholder="t('auth.login.accountPlaceholder')"
              size="large"
              clearable
            />
          </el-form-item>
          <el-form-item :label="t('auth.login.password')">
            <el-input
              v-model="loginForm.password"
              type="password"
              :placeholder="t('auth.login.passwordPlaceholder')"
              size="large"
              show-password
              @keyup.enter="submitLogin"
            />
          </el-form-item>
          <el-button
            class="auth-submit"
            type="primary"
            size="large"
            :loading="authStore.loading"
            @click="submitLogin"
          >
            {{ authStore.loading ? t('auth.login.submitting') : t('auth.login.submit') }}
          </el-button>
          <p class="auth-switch">
            {{ t('auth.login.noAccount') }}
            <button type="button" class="link" @click="switchMode('register')">
              {{ t('auth.login.toRegister') }}
            </button>
          </p>
        </el-form>

        <el-form v-else label-position="top" @submit.prevent="submitRegister">
          <el-form-item :label="t('auth.register.username')">
            <el-input
              v-model="registerForm.username"
              :placeholder="t('auth.register.usernamePlaceholder')"
              size="large"
              clearable
            />
          </el-form-item>
          <div class="form-row">
            <el-form-item :label="t('auth.register.password')">
              <el-input
                v-model="registerForm.password"
                type="password"
                :placeholder="t('auth.register.passwordPlaceholder')"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item :label="t('auth.register.confirmPassword')">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                :placeholder="t('auth.register.confirmPlaceholder')"
                size="large"
                show-password
              />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item :label="t('auth.register.email')">
              <el-input
                v-model="registerForm.email"
                :placeholder="t('auth.register.emailPlaceholder')"
                size="large"
                clearable
              />
            </el-form-item>
            <el-form-item :label="t('auth.register.phone')">
              <el-input
                v-model="registerForm.phone"
                :placeholder="t('auth.register.phonePlaceholder')"
                size="large"
                clearable
              />
            </el-form-item>
          </div>
          <el-button
            class="auth-submit"
            type="primary"
            size="large"
            :loading="authStore.loading"
            @click="submitRegister"
          >
            {{ authStore.loading ? t('auth.register.submitting') : t('auth.register.submit') }}
          </el-button>
          <p class="auth-switch">
            {{ t('auth.register.hasAccount') }}
            <button type="button" class="link" @click="switchMode('login')">
              {{ t('auth.register.toLogin') }}
            </button>
          </p>
        </el-form>
      </div>
    </section>
  </div>
</template>

<style scoped>
.auth-shell {
  display: grid;
  grid-template-columns: minmax(320px, 42%) 1fr;
  width: 100%;
  height: 100%;
  background: var(--theme-bg-app);
}

/* ---- 品牌区：方块像素渐变 ---- */
.auth-brand {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 44px;
  overflow: hidden;
  background: var(--theme-gradient);
  color: #fff;
}

.brand-mark {
  width: 72px;
  height: 72px;
  display: grid;
  place-items: center;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 1px;
  border-radius: var(--theme-radius-md);
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(6px);
  box-shadow: 0 12px 32px -12px rgba(0, 0, 0, 0.45);
}

.brand-title {
  margin: 22px 0 8px;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.brand-slogan {
  margin: 0;
  font-size: 16px;
  opacity: 0.95;
}

.brand-slogan-sub {
  margin: 8px 0 0;
  font-size: 13px;
  opacity: 0.72;
}

.brand-blocks {
  position: absolute;
  inset: auto -40px -60px auto;
  display: grid;
  grid-template-columns: repeat(6, 44px);
  gap: 8px;
  transform: rotate(-8deg);
  opacity: 0.28;
}

.block {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: rgba(255, 255, 255, calc(0.08 + (var(--i) % 5) * 0.05));
  animation: block-float 5s ease-in-out infinite;
  animation-delay: calc(var(--i) * 0.18s);
}

@keyframes block-float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* ---- 表单区 ---- */
.auth-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 28px;
  overflow-y: auto;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 36px 36px 28px;
  border-radius: var(--theme-radius-lg);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-lg);
  animation: card-in var(--theme-duration-slow) var(--theme-ease);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.985);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

.auth-card-header h2 {
  margin: 0;
  font-size: 24px;
  color: var(--theme-text-1);
}

.auth-card-header p {
  margin: 8px 0 24px;
  font-size: 13px;
  color: var(--theme-text-3);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.auth-submit {
  width: 100%;
  margin-top: 6px;
  font-weight: 600;
}

.auth-switch {
  margin: 18px 0 0;
  text-align: center;
  font-size: 13px;
  color: var(--theme-text-3);
}

.link {
  border: none;
  background: none;
  padding: 0;
  margin-left: 4px;
  color: var(--theme-primary);
  font-size: 13px;
  cursor: pointer;
}

.link:hover {
  color: var(--theme-primary-hover);
  text-decoration: underline;
}

@media (max-width: 860px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }
  .auth-brand {
    display: none;
  }
}
</style>
