<template>
  <ErrorPage
    code="504"
    tone="accent"
    :badge="$t('error.badge.gatewayTimeout')"
    :caption="$t('error.caption.gatewayTimeout')"
    :title="$t('error.gatewayTimeout.title')"
    :desc="$t('error.gatewayTimeout.desc')"
    :primary-label="$t('error.gatewayTimeout.primary')"
    :secondary-label="$t('error.gatewayTimeout.secondary')"
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
        <circle cx="12" cy="12" r="9" />
        <path d="M12 7v5.2l3.4 2" />
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
