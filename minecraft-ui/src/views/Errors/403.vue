<template>
  <ErrorPage
    code="403"
    tone="danger"
    :badge="$t('error.badge.forbidden')"
    :caption="$t('error.caption.forbidden')"
    :title="$t('error.forbidden.title')"
    :desc="$t('error.forbidden.desc')"
    :primary-label="$t('error.forbidden.primary')"
    :secondary-label="$t('error.forbidden.secondary')"
    :footnote="$t('error.footnote')"
    @primary="goHome"
    @secondary="goBack"
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
        <circle cx="12" cy="12" r="9" />
        <path d="M5.65 5.65l12.7 12.7" />
      </svg>
    </template>
  </ErrorPage>
</template>

<script setup>
import { useRouter } from 'vue-router'
import ErrorPage from './components/ErrorPage.vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  if (window.history.length > 1) router.back()
  else router.push('/')
}
</script>
