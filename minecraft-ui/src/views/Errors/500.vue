<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-visual">
        <div class="error-icon">
          <svg viewBox="0 0 100 100" class="icon-svg">
            <circle cx="50" cy="50" r="45" fill="none" stroke="currentColor" stroke-width="2" class="icon-ring" />
            <circle cx="50" cy="45" r="8" fill="currentColor" class="icon-dot" />
            <path d="M35 65 Q50 80 65 65" stroke="currentColor" stroke-width="2" stroke-linecap="round" fill="none" class="icon-curve" />
          </svg>
        </div>
      </div>
      
      <div class="error-content">
        <h1 class="error-code">500</h1>
        <p class="error-headline">服务器内部错误</p>
        <p class="error-message">抱歉，服务器遇到了一些问题，请稍后重试</p>
        <div class="cta-links">
          <a href="#" @click.prevent="refreshPage" class="cta-primary">刷新页面</a>
          <a href="#" @click.prevent="goHome" class="cta-secondary">返回首页</a>
        </div>
      </div>
    </div>
    
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const refreshPage = () => {
  window.location.reload()
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f5f7 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  bottom: -50px;
  left: -50px;
  animation-delay: -2s;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  top: 50%;
  left: 10%;
  animation-delay: -4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.error-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 40px;
}

.error-visual {
  margin-bottom: 32px;
}

.error-icon {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #10b981;
  animation: iconAppear 0.8s ease-out;
}

.icon-svg {
  width: 100%;
  height: 100%;
}

.icon-ring {
  animation: ringDraw 1.5s ease-out forwards;
  stroke-dasharray: 283;
  stroke-dashoffset: 283;
}

.icon-dot {
  opacity: 0;
  animation: dotAppear 0.3s ease-out 1s forwards;
}

.icon-curve {
  stroke-dasharray: 40;
  stroke-dashoffset: 40;
  animation: curveDraw 0.4s ease-out 1.2s forwards;
}

@keyframes iconAppear {
  from {
    opacity: 0;
    transform: scale(0.5);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes ringDraw {
  to {
    stroke-dashoffset: 0;
  }
}

@keyframes dotAppear {
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes curveDraw {
  to {
    stroke-dashoffset: 0;
  }
}

.error-content {
  animation: contentSlideUp 0.6s ease-out 0.3s both;
}

@keyframes contentSlideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.error-code {
  font-size: 120px;
  font-weight: 300;
  letter-spacing: -0.02em;
  color: #1d1d1f;
  margin: 0 0 16px;
  line-height: 1.05;
}

.error-headline {
  font-size: 28px;
  font-weight: 500;
  color: #1d1d1f;
  margin: 0 0 12px;
  letter-spacing: 0.004em;
}

.error-message {
  font-size: 17px;
  color: #6e6e73;
  margin: 0 0 40px;
  line-height: 1.47;
  max-width: 400px;
}

.cta-links {
  display: flex;
  gap: 24px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-primary {
  padding: 12px 28px;
  background: #0071e3;
  color: #ffffff;
  text-decoration: none;
  border-radius: 20px;
  font-size: 17px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cta-primary:hover {
  background: #0077ed;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.3);
}

.cta-primary:active {
  transform: translateY(0);
}

.cta-secondary {
  padding: 12px 28px;
  background: transparent;
  color: #0071e3;
  text-decoration: none;
  border-radius: 20px;
  font-size: 17px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.cta-secondary:hover {
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
    padding: 0 20px;
  }

  .cta-links {
    flex-direction: column;
    gap: 16px;
    width: 100%;
    max-width: 280px;
  }

  .cta-primary,
  .cta-secondary {
    width: 100%;
    display: inline-block;
  }

  .error-icon {
    width: 80px;
    height: 80px;
  }
}
</style>