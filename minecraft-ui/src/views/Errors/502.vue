<template>
  <ErrorPage
    code="502"
    tone="info"
    :badge="$t('error.badge.badGateway')"
    :caption="$t('error.caption.badGateway')"
    :title="$t('error.badGateway.title')"
    :desc="$t('error.badGateway.desc')"
    :primary-label="$t('error.badGateway.primary')"
    :secondary-label="$t('error.badGateway.secondary')"
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
        <path d="M9 17H7a5 5 0 0 1 0-10h2" />
        <path d="M15 7h2a5 5 0 0 1 0 10h-2" />
        <path d="M8 12h2" />
        <path d="M14 12h2" />
        <path d="M11.5 10.5 12.5 13.5" />
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
