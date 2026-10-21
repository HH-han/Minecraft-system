<template>
  <ErrorPage
    code="503"
    tone="warning"
    :badge="$t('error.badge.serviceUnavailable')"
    :caption="$t('error.caption.serviceUnavailable')"
    :title="$t('error.serviceUnavailable.title')"
    :desc="$t('error.serviceUnavailable.desc')"
    :primary-label="$t('error.serviceUnavailable.primary')"
    :secondary-label="$t('error.serviceUnavailable.secondary')"
    :footnote="$t('error.footnote')"
    @primary="refreshPage"
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
        <path
          d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.8-3.8a6 6 0 0 1-7.9 7.9l-6.9 6.9a2.1 2.1 0 0 1-3-3l6.9-6.9a6 6 0 0 1 7.9-7.9L14.7 6.3z"
        />
      </svg>
    </template>
  </ErrorPage>
</template>

<script setup>
import { useRouter } from 'vue-router'
import ErrorPage from './components/ErrorPage.vue'

const router = useRouter()

const refreshPage = () => {
  window.location.reload()
}

const goHome = () => {
  router.push('/')
}
</script>
