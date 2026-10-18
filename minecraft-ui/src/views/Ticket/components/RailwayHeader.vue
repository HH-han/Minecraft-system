<template>
  <header class="rw-header">
    <!-- 顶部工具条 -->
    <div class="rw-topbar">
      <div class="rw-topbar-inner">
        <button class="rw-brand" type="button" @click="goHome">
          <span class="rw-logo" aria-hidden="true">
            <svg viewBox="0 0 32 32" fill="none">
              <rect x="1" y="1" width="30" height="30" rx="9" fill="#E4002B" />
              <path d="M10 23.5V13.2c0-2.3 1.8-4.2 4.1-4.2h3.8c2.3 0 4.1 1.9 4.1 4.2v10.3"
                stroke="#fff" stroke-width="2.1" stroke-linecap="round" />
              <path d="M8.5 23.5h15M11 16.4h10M12.4 26l1.4-2.5M19.6 26l-1.4-2.5"
                stroke="#fff" stroke-width="2.1" stroke-linecap="round" />
              <circle cx="12.2" cy="20.2" r="1.2" fill="#fff" />
              <circle cx="19.8" cy="20.2" r="1.2" fill="#fff" />
            </svg>
          </span>
          <span class="rw-brand-text">
            <strong>{{ $t('railway.brand') }}</strong>
            <small>CHINA RAILWAY</small>
          </span>
        </button>

        <div class="rw-top-search">
          <el-input v-model="trainKeyword" :placeholder="$t('railway.top.searchPlaceholder')" clearable
            @keyup.enter="searchTrain">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <div class="rw-top-links">
          <button type="button" class="rw-link" @click="comingSoon">{{ $t('railway.top.accessible') }}</button>
          <span class="rw-sep"></span>
          <button type="button" class="rw-link" @click="comingSoon">{{ $t('railway.top.elder') }}</button>
          <span class="rw-sep"></span>
          <button type="button" class="rw-link" @click="toggleLocale">{{ localeButtonText }}</button>
          <span class="rw-sep"></span>

          <el-dropdown v-if="loggedIn" trigger="hover" @command="handleUserCommand">
            <button type="button" class="rw-link rw-link--user">
              {{ $t('railway.top.my') }}
              <el-icon class="rw-caret"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="orders">{{ $t('railway.top.myOrders') }}</el-dropdown-item>
                <el-dropdown-item command="logout" divided>{{ $t('railway.top.logout') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <template v-else>
            <button type="button" class="rw-link rw-link--strong" @click="goLogin">{{ $t('railway.top.login') }}</button>
            <span class="rw-sep"></span>
            <button type="button" class="rw-link" @click="goRegister">{{ $t('railway.top.register') }}</button>
          </template>
        </div>
      </div>
    </div>

    <!-- 主导航 -->
    <nav class="rw-nav">
      <div class="rw-nav-inner">
        <div v-for="item in navItems" :key="item.key" class="rw-nav-item" role="button" tabindex="0"
          :class="{ 'is-active': item.active, 'rw-nav-item--mega': item.key === 'ticket' }"
          @click="handleNav(item)" @keyup.enter="handleNav(item)">
          {{ $t(`railway.nav.${item.key}`) }}
          <span v-if="item.key === 'ticket'" class="rw-nav-caret">
            <el-icon><ArrowDown /></el-icon>
          </span>

          <!-- 车票悬浮大菜单 -->
          <div v-if="item.key === 'ticket'" class="rw-mega">
            <div class="rw-mega-inner">
              <div class="rw-mega-col">
                <h4>{{ $t('railway.menu.buy') }}</h4>
                <button type="button" @click.stop="goHomeWithTrip('single')">{{ $t('railway.menu.single') }}</button>
                <button type="button" @click.stop="goHomeWithTrip('round')">{{ $t('railway.menu.round') }}</button>
                <button type="button" @click.stop="comingSoon">{{ $t('railway.menu.transfer') }}</button>
              </div>
              <div class="rw-mega-col">
                <h4>{{ $t('railway.menu.change') }}</h4>
                <button type="button" @click.stop="goMyOrders">{{ $t('railway.menu.refund') }}</button>
                <button type="button" @click.stop="goMyOrders">{{ $t('railway.menu.reschedule') }}</button>
                <button type="button" @click.stop="goMyOrders">{{ $t('railway.menu.changeDestination') }}</button>
              </div>
              <div class="rw-mega-col">
                <h4>{{ $t('railway.menu.more') }}</h4>
                <button type="button" @click.stop="comingSoon">{{ $t('railway.menu.unionPay') }}</button>
                <button type="button" @click.stop="comingSoon">{{ $t('railway.menu.international') }}</button>
              </div>
              <div class="rw-mega-note">
                <span class="rw-mega-note-title">{{ $t('railway.brandShort') }}</span>
                <p>{{ $t('railway.heroDesc') }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { setLocale } from '@/locales'
import { useAuthStore } from '@/stores/auth'
import { hasLogin } from '../utils'

const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()
const authStore = useAuthStore()

const trainKeyword = ref('')
const loggedIn = ref(hasLogin())

const localeButtonText = computed(() =>
  locale.value === 'zh-CN' ? t('railway.top.english') : '简体中文',
)

const navItems = computed(() => [
  { key: 'home', path: '/ticket', active: route.path === '/ticket' },
  { key: 'ticket', path: '/ticket/list', active: route.path.startsWith('/ticket/list') },
  { key: 'group', active: false },
  { key: 'member', active: false },
  { key: 'station', active: false },
  { key: 'business', active: false },
  { key: 'guide', active: false },
  { key: 'info', active: false },
])

const comingSoon = () => {
  ElMessage.info(t('railway.search.notOpen'))
}

const goHome = () => router.push('/ticket')
const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } })
const goRegister = () => router.push({ path: '/login', query: { redirect: route.fullPath } })
const goMyOrders = () => {
  if (!hasLogin()) {
    goLogin()
    return
  }
  router.push('/ticket/my-orders')
}

const goHomeWithTrip = (tripType) => {
  router.push({ path: '/ticket', query: { tripType } })
}

const handleNav = (item) => {
  if (item.key === 'home') {
    goHome()
    return
  }
  if (item.key === 'ticket') {
    router.push('/ticket/list')
    return
  }
  comingSoon()
}

const searchTrain = () => {
  const keyword = trainKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning(t('railway.top.searchEmpty'))
    return
  }
  router.push({ path: '/ticket/timetable', query: { trainNo: keyword } })
}

const toggleLocale = () => {
  setLocale(locale.value === 'zh-CN' ? 'en-US' : 'zh-CN')
}

const handleUserCommand = (command) => {
  if (command === 'orders') {
    goMyOrders()
    return
  }
  if (command === 'logout') {
    authStore.logout()
    localStorage.removeItem('tokenTimestamp')
    localStorage.removeItem('user')
    localStorage.removeItem('rememberedUsername')
    loggedIn.value = false
    ElMessage.success(t('railway.top.logout'))
    // 重新加载以同步全站登录态
    setTimeout(() => window.location.assign('/ticket'), 300)
  }
}
</script>

<style scoped>
.rw-header {
  position: sticky;
  top: 0;
  z-index: 120;
  background: var(--rw-card);
  box-shadow: 0 1px 0 rgba(15, 46, 102, 0.06);
}

/* ---------- 顶部工具条 ---------- */
.rw-topbar {
  border-bottom: 1px solid var(--rw-border);
  background: var(--rw-card);
}

.rw-topbar-inner {
  max-width: 1180px;
  margin: 0 auto;
  height: 68px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 28px;
}

.rw-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  border: none;
  background: none;
  cursor: pointer;
  padding: 0;
  flex-shrink: 0;
}

.rw-logo {
  width: 34px;
  height: 34px;
  display: inline-flex;
}

.rw-logo svg {
  width: 100%;
  height: 100%;
}

.rw-brand-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.1;
}

.rw-brand-text strong {
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--rw-text);
}

.rw-brand-text small {
  font-size: 9px;
  letter-spacing: 0.28em;
  color: var(--rw-text-3);
  margin-top: 3px;
}

.rw-top-search {
  flex: 1;
  max-width: 420px;
}

.rw-top-search :deep(.el-input__wrapper) {
  border-radius: 999px;
  background: var(--rw-soft-2);
  box-shadow: inset 0 0 0 1px var(--rw-border);
  height: 38px;
}

.rw-top-search :deep(.el-input__wrapper.is-focus) {
  box-shadow: inset 0 0 0 1px var(--rw-blue);
}

.rw-top-search :deep(.el-input__inner) {
  font-size: 13px;
}

.rw-top-links {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
  flex-shrink: 0;
}

.rw-link {
  border: none;
  background: none;
  padding: 2px 2px;
  font-size: 13px;
  color: var(--rw-text-2);
  cursor: pointer;
  transition: color 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-family: inherit;
}

.rw-link:hover {
  color: var(--rw-blue);
}

.rw-link--strong {
  color: var(--rw-blue);
  font-weight: 600;
}

.rw-link--user {
  color: var(--rw-text);
  font-weight: 500;
}

.rw-caret {
  font-size: 11px;
}

.rw-sep {
  width: 1px;
  height: 11px;
  background: var(--rw-border);
}

/* ---------- 主导航 ---------- */
.rw-nav {
  background: var(--rw-blue);
  position: relative;
}

.rw-nav-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: stretch;
  gap: 6px;
}

.rw-nav-item {
  position: relative;
  border: none;
  background: none;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  letter-spacing: 0.08em;
  padding: 13px 18px;
  cursor: pointer;
  font-family: inherit;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: background 0.2s ease, color 0.2s ease;
}

.rw-nav-item:hover,
.rw-nav-item.is-active {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
}

.rw-nav-item.is-active {
  box-shadow: inset 0 -2px 0 #ffd166;
}

.rw-nav-caret {
  font-size: 11px;
  opacity: 0.8;
}

/* ---------- 悬浮大菜单 ---------- */
.rw-mega {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translate(-50%, 8px);
  width: min(760px, 92vw);
  background: var(--rw-card);
  border-radius: 0 0 18px 18px;
  box-shadow: var(--rw-shadow);
  border: 1px solid var(--rw-border);
  opacity: 0;
  visibility: hidden;
  pointer-events: none;
  transition: opacity 0.18s ease, transform 0.18s ease, visibility 0.18s;
  z-index: 130;
  padding: 22px 26px;
}

.rw-nav-item--mega:hover .rw-mega {
  opacity: 1;
  visibility: visible;
  pointer-events: auto;
  transform: translate(-50%, 0);
}

.rw-mega-inner {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr)) 200px;
  gap: 18px;
  text-align: left;
}

.rw-mega-col h4 {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.14em;
  color: var(--rw-text-3);
  text-transform: uppercase;
}

.rw-mega-col button {
  display: block;
  width: 100%;
  text-align: left;
  border: none;
  background: none;
  padding: 7px 0;
  font-size: 14px;
  color: var(--rw-text);
  cursor: pointer;
  font-family: inherit;
  letter-spacing: 0.04em;
  transition: color 0.18s ease, transform 0.18s ease;
}

.rw-mega-col button:hover {
  color: var(--rw-blue);
  transform: translateX(3px);
}

.rw-mega-note {
  border-left: 1px solid var(--rw-border);
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.rw-mega-note-title {
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
}

.rw-mega-note p {
  margin: 0;
  font-size: 12px;
  line-height: 1.7;
  color: var(--rw-text-2);
  font-weight: 400;
  letter-spacing: 0;
}

@media (max-width: 900px) {
  .rw-topbar-inner {
    height: auto;
    padding: 12px 16px;
    flex-wrap: wrap;
    gap: 10px 16px;
  }

  .rw-top-search {
    order: 3;
    max-width: none;
    flex-basis: 100%;
  }

  .rw-top-links {
    gap: 8px;
  }

  .rw-top-links .rw-link:nth-child(-n + 4),
  .rw-top-links .rw-sep:nth-child(-n + 4) {
    display: none;
  }

  .rw-nav-inner {
    padding: 0 12px;
    overflow-x: auto;
    scrollbar-width: none;
  }

  .rw-nav-inner::-webkit-scrollbar {
    display: none;
  }

  .rw-nav-item {
    padding: 12px 14px;
    white-space: nowrap;
  }

  .rw-mega {
    display: none;
  }
}
</style>
