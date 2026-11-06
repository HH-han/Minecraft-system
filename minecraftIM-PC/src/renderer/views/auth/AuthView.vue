<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ChatDotRound,
  Lock,
  MagicStick,
  Moon,
  Sunny,
  User
} from '@element-plus/icons-vue';
import { SUPPORTED_LOCALES, setLocale, type AppLocale } from '@/locales';
import { useAuthStore } from '@/stores/auth';
import { useThemeStore } from '@/stores/theme';

const { t, locale } = useI18n();
const authStore = useAuthStore();
const themeStore = useThemeStore();

type Mode = 'login' | 'register';
const mode = ref<Mode>('login');
const agreed = ref(true);

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

const currentLocale = computed(() => locale.value as AppLocale);

const isDark = computed(() => themeStore.currentTheme !== 'light');

function switchMode(next: Mode): void {
  mode.value = next;
  authStore.error = null;
}

function toggleTheme(): void {
  themeStore.setTheme(isDark.value ? 'light' : 'dark');
}

async function toggleLocale(): Promise<void> {
  const next = currentLocale.value === 'zh-CN' ? 'en-US' : 'zh-CN';
  await setLocale(next as AppLocale);
}

async function submitLogin(): Promise<void> {
  if (!loginForm.account.trim() || !loginForm.password) {
    ElMessage.warning(t('auth.errors.emptyAccount'));
    return;
  }
  if (!agreed.value) {
    ElMessage.warning(t('auth.errors.agreementRequired'));
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
    ElMessage.warning(t('auth.errors.emptyRegister'));
    return;
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning(t('auth.register.passwordMismatch'));
    return;
  }
  if (!agreed.value) {
    ElMessage.warning(t('auth.errors.agreementRequired'));
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

function notImplemented(): void {
  ElMessage.info(t('im.common.comingSoon'));
}
</script>

<template>
  <div class="auth-shell">
    <div class="auth-bg" aria-hidden="true">
      <span class="orb orb--one" />
      <span class="orb orb--two" />
      <span class="orb orb--three" />
      <span class="grid-overlay" />
    </div>

    <div class="auth-toolbar">
      <button class="auth-tool" :title="t('auth.theme')" @click="toggleTheme">
        <el-icon :size="15"><component :is="isDark ? Sunny : Moon" /></el-icon>
      </button>
      <button class="auth-tool" :title="t('auth.language')" @click="toggleLocale">
        {{ currentLocale === 'zh-CN' ? '中' : 'EN' }}
      </button>
    </div>

    <section class="auth-card" :class="{ 'is-register': mode === 'register' }">
      <header class="auth-head">
        <div class="auth-logo">
          <span class="auth-logo__cube">
            <span class="cube-face cube-face--top" />
            <span class="cube-face cube-face--left" />
            <span class="cube-face cube-face--right" />
          </span>
        </div>
        <p class="auth-eyebrow">
          {{ mode === 'login' ? t('auth.portal') : t('auth.newUser') }}
        </p>
        <h1 class="auth-title">
          {{ mode === 'login' ? t('auth.login.title') : t('auth.register.title') }}
        </h1>
        <p class="auth-subtitle">
          {{ mode === 'login' ? t('auth.login.subtitle') : t('auth.register.subtitle') }}
        </p>
      </header>

      <!-- 登录表单 -->
      <form v-if="mode === 'login'" class="auth-form" @submit.prevent="submitLogin">
        <label class="auth-field">
          <span class="auth-field__icon">
            <el-icon :size="16"><User /></el-icon>
          </span>
          <input
            v-model="loginForm.account"
            type="text"
            autocomplete="username"
            :placeholder="t('auth.login.accountPlaceholder')"
          />
        </label>

        <label class="auth-field">
          <span class="auth-field__icon">
            <el-icon :size="16"><Lock /></el-icon>
          </span>
          <input
            v-model="loginForm.password"
            type="password"
            autocomplete="current-password"
            :placeholder="t('auth.login.passwordPlaceholder')"
          />
        </label>

        <button type="submit" class="auth-submit" :disabled="authStore.loading">
          <span v-if="authStore.loading" class="auth-submit__spinner" />
          {{ authStore.loading ? t('auth.login.submitting') : t('auth.login.submit') }}
        </button>

        <div class="auth-divider">
          <span>{{ t('auth.login.otherMethods') }}</span>
        </div>

        <div class="auth-methods">
          <button type="button" class="auth-method" @click="notImplemented">
            <el-icon :size="17"><ChatDotRound /></el-icon>
          </button>
          <button type="button" class="auth-method" @click="notImplemented">
            <el-icon :size="17"><MagicStick /></el-icon>
          </button>
          <button type="button" class="auth-method" @click="notImplemented">
            <span class="auth-method__text">QR</span>
          </button>
        </div>

        <label class="auth-agreement">
          <input v-model="agreed" type="checkbox" />
          <span>{{ t('auth.agreement') }}</span>
        </label>

        <div class="auth-foot">
          <button type="button" class="auth-link" @click="notImplemented">
            {{ t('auth.login.forgot') }}
          </button>
          <button type="button" class="auth-link is-strong" @click="switchMode('register')">
            {{ t('auth.login.toRegister') }}
          </button>
        </div>
      </form>

      <!-- 注册表单 -->
      <form v-else class="auth-form auth-form--register" @submit.prevent="submitRegister">
        <label class="auth-line">
          <span>{{ t('auth.register.username') }}</span>
          <input
            v-model="registerForm.username"
            type="text"
            autocomplete="username"
            :placeholder="t('auth.register.usernamePlaceholder')"
          />
        </label>

        <div class="auth-line-row">
          <label class="auth-line">
            <span>{{ t('auth.register.password') }}</span>
            <input
              v-model="registerForm.password"
              type="password"
              autocomplete="new-password"
              :placeholder="t('auth.register.passwordPlaceholder')"
            />
          </label>
          <label class="auth-line">
            <span>{{ t('auth.register.confirmPassword') }}</span>
            <input
              v-model="registerForm.confirmPassword"
              type="password"
              autocomplete="new-password"
              :placeholder="t('auth.register.confirmPlaceholder')"
            />
          </label>
        </div>

        <div class="auth-line-row">
          <label class="auth-line">
            <span>{{ t('auth.register.email') }}</span>
            <input
              v-model="registerForm.email"
              type="email"
              autocomplete="email"
              :placeholder="t('auth.register.emailPlaceholder')"
            />
          </label>
          <label class="auth-line">
            <span>{{ t('auth.register.phone') }}</span>
            <input
              v-model="registerForm.phone"
              type="tel"
              autocomplete="tel"
              :placeholder="t('auth.register.phonePlaceholder')"
            />
          </label>
        </div>

        <label class="auth-agreement">
          <input v-model="agreed" type="checkbox" />
          <span>{{ t('auth.agreement') }}</span>
        </label>

        <button type="submit" class="auth-submit" :disabled="authStore.loading">
          <span v-if="authStore.loading" class="auth-submit__spinner" />
          {{ authStore.loading ? t('auth.register.submitting') : t('auth.register.submit') }}
        </button>

        <div class="auth-foot auth-foot--center">
          <button type="button" class="auth-link is-strong" @click="switchMode('login')">
            {{ t('auth.register.toLogin') }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.auth-shell {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: linear-gradient(160deg, #6fc3ff 0%, #4c8df7 42%, #3253d8 100%);
}

.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.orb {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.18);
  filter: blur(2px);
}

.orb--one {
  width: 320px;
  height: 320px;
  top: -120px;
  left: -90px;
  background: rgba(255, 255, 255, 0.22);
}

.orb--two {
  width: 220px;
  height: 220px;
  bottom: -80px;
  right: 12%;
  background: rgba(255, 255, 255, 0.14);
}

.orb--three {
  width: 120px;
  height: 120px;
  top: 18%;
  right: 8%;
  background: rgba(255, 255, 255, 0.16);
}

.grid-overlay {
  position: absolute;
  inset: 0;
  opacity: 0.14;
  background-image: linear-gradient(rgba(255, 255, 255, 0.5) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.5) 1px, transparent 1px);
  background-size: 46px 46px;
  mask-image: radial-gradient(circle at 50% 40%, #000 0%, transparent 72%);
}

.auth-toolbar {
  position: absolute;
  top: 18px;
  right: 22px;
  display: flex;
  gap: 8px;
  z-index: 3;
}

.auth-tool {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border: 1px solid rgba(255, 255, 255, 0.42);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.auth-tool:hover {
  background: rgba(255, 255, 255, 0.34);
}

/* ---- 卡片 ---- */
.auth-card {
  position: relative;
  z-index: 2;
  width: min(420px, calc(100% - 48px));
  max-height: calc(100% - 56px);
  overflow-y: auto;
  padding: 30px 34px 26px;
  border-radius: 24px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  box-shadow: 0 44px 90px -40px rgba(9, 28, 78, 0.65);
  scrollbar-width: none;
  animation: auth-in 420ms var(--theme-ease);
}

.auth-card::-webkit-scrollbar {
  display: none;
}

.auth-card.is-register {
  width: min(480px, calc(100% - 48px));
}

@keyframes auth-in {
  from {
    opacity: 0;
    transform: translateY(22px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: none;
  }
}

.auth-head {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.auth-logo {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  box-shadow: var(--theme-shadow-sm);
}

/* 等距小方块，呼应 Minecraft 主题 */
.auth-logo__cube {
  position: relative;
  width: 24px;
  height: 24px;
}

.cube-face {
  position: absolute;
  inset: 0;
}

.cube-face--top {
  background: color-mix(in srgb, var(--theme-primary) 65%, #ffffff);
  clip-path: polygon(50% 0%, 100% 25%, 50% 50%, 0% 25%);
}

.cube-face--left {
  background: var(--theme-primary);
  clip-path: polygon(0% 25%, 50% 50%, 50% 100%, 0% 75%);
}

.cube-face--right {
  background: color-mix(in srgb, var(--theme-primary) 55%, #0f172a);
  clip-path: polygon(100% 25%, 50% 50%, 50% 100%, 100% 75%);
}

.auth-eyebrow {
  margin: 14px 0 4px;
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--theme-primary);
}

.auth-title {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 0.5px;
  color: var(--theme-text-1);
}

.auth-subtitle {
  margin: 6px 0 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

/* ---- 表单 ---- */
.auth-form {
  margin-top: 22px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.auth-field {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 46px;
  padding: 0 14px;
  border-radius: 12px;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface-2);
  transition: border-color var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.auth-field:focus-within {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.auth-field__icon {
  color: var(--theme-text-3);
  display: grid;
  place-items: center;
}

.auth-field input {
  flex: 1;
  min-width: 0;
  height: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: var(--theme-text-1);
  font-family: inherit;
}

.auth-field input::placeholder {
  color: var(--theme-text-3);
}

.auth-line {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.auth-line > span {
  font-size: 11px;
  color: var(--theme-text-3);
}

.auth-line input {
  height: 38px;
  border: none;
  border-bottom: 1px solid var(--theme-border);
  outline: none;
  background: transparent;
  font-size: 13.5px;
  color: var(--theme-text-1);
  font-family: inherit;
  transition: border-color var(--theme-duration-fast) var(--theme-ease);
}

.auth-line input:focus {
  border-bottom-color: var(--theme-primary);
}

.auth-line input::placeholder {
  color: var(--theme-text-3);
  font-size: 12.5px;
}

.auth-line-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.auth-submit {
  position: relative;
  height: 44px;
  margin-top: 6px;
  border: none;
  border-radius: 12px;
  background: var(--theme-accent-gradient);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.6px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 18px 30px -18px color-mix(in srgb, var(--theme-primary) 80%, transparent);
  transition: transform var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease), opacity var(--theme-duration-fast);
}

.auth-submit:hover {
  transform: translateY(-1px);
  box-shadow: 0 24px 36px -18px color-mix(in srgb, var(--theme-primary) 85%, transparent);
}

.auth-submit:disabled {
  opacity: 0.72;
  cursor: not-allowed;
  transform: none;
}

.auth-submit__spinner {
  width: 13px;
  height: 13px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-top-color: #fff;
  animation: spin 700ms linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.auth-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 4px 0;
  font-size: 11px;
  color: var(--theme-text-3);
}

.auth-divider::before,
.auth-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--theme-hairline);
}

.auth-methods {
  display: flex;
  justify-content: center;
  gap: 14px;
}

.auth-method {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: transform var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease);
}

.auth-method:hover {
  transform: translateY(-2px);
  border-color: var(--theme-primary);
  color: var(--theme-primary);
}

.auth-method__text {
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.auth-agreement {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 2px;
  font-size: 11.5px;
  line-height: 1.5;
  color: var(--theme-text-3);
  cursor: pointer;
}

.auth-agreement input {
  margin: 2px 0 0;
  accent-color: var(--theme-primary);
}

.auth-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 2px;
  font-size: 12px;
}

.auth-foot--center {
  justify-content: center;
}

.auth-link {
  border: none;
  background: none;
  padding: 0;
  font-size: 12px;
  color: var(--theme-text-3);
  cursor: pointer;
  transition: color var(--theme-duration-fast) var(--theme-ease);
}

.auth-link:hover {
  color: var(--theme-primary);
}

.auth-link.is-strong {
  color: var(--theme-primary);
  font-weight: 600;
}

@media (max-width: 560px) {
  .auth-line-row {
    grid-template-columns: 1fr;
  }
}
</style>
