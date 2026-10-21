<template>
  <ErrorPage
    code="401"
    tone="warning"
    :badge="$t('error.badge.unauthorized')"
    :caption="$t('error.caption.unauthorized')"
    :title="$t('error.unauthorized.title')"
    :desc="$t('error.unauthorized.desc')"
    :primary-label="$t('error.unauthorized.primary')"
    :secondary-label="$t('error.unauthorized.secondary')"
    :footnote="$t('error.footnote')"
    @primary="goLogin"
    @secondary="goHome"
  >
    <template #visual>
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="1.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <rect x="3" y="10.5" width="18" height="11" rx="3" />
        <path d="M7 10.5V7a5 5 0 0 1 10 0v3.5" />
        <circle cx="12" cy="16" r="1.5" fill="currentColor" stroke="none" />
      </svg>
    </template>
  </ErrorPage>
</template>

<script setup>
import { useRouter } from 'vue-router'
import ErrorPage from './components/ErrorPage.vue'

const router = useRouter()

const goLogin = () => {
  router.push('/login')
}

const goHome = () => {
  router.push('/')
}
</script>
