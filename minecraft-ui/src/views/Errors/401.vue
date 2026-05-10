<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">401</h1>
      <p class="error-headline">Unauthorized</p>
      <p class="error-message">此页面需要登录后才能访问</p>
      <div class="cta-links">
        <a href="#" @click.prevent="goLogin">立即登录 ›</a>
        <a href="#" @click.prevent="goHome">返回首页 ›</a>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goLogin = () => {
  router.push('/login')
}

const goHome = () => {
  router.push('/home')
}
</script>
<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #000000;
  color: #ffffff;
}

.error-content {
  text-align: center;
  max-width: 1200px;
  padding: 0 22px;
}

.error-code {
  font-size: 120px;
  font-weight: 600;
  letter-spacing: -0.02em;
  line-height: 1.05;
  margin: 0 0 20px;
  color: #ffffff;
}

.error-headline {
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 0.004em;
  line-height: 1.1;
  margin: 0 0 10px;
  color: #ffffff;
}

.error-message {
  font-size: 18px;
  font-weight: 400;
  line-height: 1.4;
  margin: 0 0 40px;
  color: #a1a1a6;
}

.cta-links {
  display: flex;
  gap: 32px;
  justify-content: center;
}

.cta-links a {
  font-size: 18px;
  font-weight: 400;
  color: #2997ff;
  text-decoration: none;
  transition: color 0.2s ease;
}

.cta-links a:hover {
  color: #0066cc;
  text-decoration: underline;
}

@media (max-width: 767px) {
  .error-code {
    font-size: 80px;
  }

  .error-headline {
    font-size: 24px;
  }

  .error-message {
    font-size: 16px;
  }

  .cta-links {
    flex-direction: column;
    gap: 16px;
  }
}
</style>