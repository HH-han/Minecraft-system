<template>
  <ErrorPage
    code="404"
    tone="accent"
    :badge="$t('error.badge.notFound')"
    :caption="$t('error.caption.notFound')"
    :title="$t('error.notFound.title')"
    :desc="$t('error.notFound.desc')"
    :primary-label="$t('error.notFound.primary')"
    :secondary-label="$t('error.notFound.secondary')"
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
        <path d="M12 21s7-6.1 7-11a7 7 0 1 0-14 0c0 4.9 7 11 7 11Z" />
        <circle cx="12" cy="10" r="2.6" />
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
