<template>
  <div class="test-project-005">
    <!-- 复杂的加载过渡层 -->
    <div class="loader-overlay" :class="{ 'loader-hidden': !isLoading }">
      <div class="loader-content">
        <div class="loader-ring"></div>
        <div class="loader-ring"></div>
        <div class="loader-ring"></div>
        <div class="loader-text" data-text="LOADING">LOADING</div>
        <div class="loader-particles">
          <span v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></span>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content" :class="{ 'content-visible': !isLoading }">
      <div class="background-layer">
        <div class="gradient-bg"></div>
        <div class="noise"></div>
      </div>
      
      <div class="hero-section">
        <h1 class="glitch-title" data-text="博览旅行">博览旅行</h1>
        <div class="subtitle">
          <span class="line"></span>
          <span class="text">沉浸式视觉体验</span>
          <span class="line"></span>
        </div>
        <div class="scroll-indicator">
          <span>探索更多</span>
          <div class="mouse">
            <div class="wheel"></div>
          </div>
        </div>
      </div>
      
      <div class="floating-elements">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const isLoading = ref(true)

// 随机粒子样式
const getParticleStyle = (index) => {
  const duration = 1 + Math.random() * 2
  const delay = index * 0.1
  const x = Math.random() * 100
  const y = Math.random() * 100
  return {
    '--duration': `${duration}s`,
    '--delay': `${delay}s`,
    '--x': `${x}%`,
    '--y': `${y}%`,
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}

onMounted(() => {
  // 模拟加载过程
  setTimeout(() => {
    isLoading.value = false
  }, 2500)
})
</script>

<style scoped>
.test-project-005 {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  position: relative;
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
}

/* ==================== 加载动画层 ==================== */
.loader-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #0a0a0a 0%, #1a1a2e 100%);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: opacity 0.8s cubic-bezier(0.77, 0, 0.18, 1), visibility 0.8s;
  opacity: 1;
  visibility: visible;
}

.loader-overlay.loader-hidden {
  opacity: 0;
  visibility: hidden;
}

.loader-content {
  position: relative;
  width: 200px;
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 环形动画 */
.loader-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-radius: 50%;
  animation: ringRotate 2s cubic-bezier(0.68, -0.55, 0.265, 1.55) infinite;
}

.loader-ring:nth-child(1) {
  border-top-color: #ff006e;
  border-right-color: #ff006e;
  animation-delay: 0s;
}

.loader-ring:nth-child(2) {
  width: 80%;
  height: 80%;
  border-bottom-color: #00f5d4;
  border-left-color: #00f5d4;
  animation-delay: 0.3s;
  animation-direction: reverse;
}

.loader-ring:nth-child(3) {
  width: 55%;
  height: 55%;
  border-top-color: #ffbe0b;
  border-right-color: #ffbe0b;
  border-bottom-color: #ffbe0b;
  animation-delay: 0.6s;
  animation-duration: 1.5s;
}

/* 加载文字 */
.loader-text {
  position: relative;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 4px;
  color: rgba(255, 255, 255, 0.3);
  text-transform: uppercase;
  animation: textPulse 1.5s ease-in-out infinite;
}

.loader-text::before {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  color: #fff;
  overflow: hidden;
  white-space: nowrap;
  animation: textReveal 1.5s ease-in-out infinite;
}

/* 粒子系统 */
.loader-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 3px;
  height: 3px;
  background: radial-gradient(circle, #fff, #ff006e);
  border-radius: 50%;
  opacity: 0;
  animation: particleFloat var(--duration) ease-in-out infinite;
  animation-delay: var(--delay);
}

/* ==================== 主内容区域 ==================== */
.main-content {
  width: 100%;
  height: 100%;
  position: relative;
  opacity: 0;
  transform: scale(1.1);
  transition: opacity 1.2s cubic-bezier(0.23, 1, 0.32, 1), transform 1.2s cubic-bezier(0.23, 1, 0.32, 1);
}

.content-visible {
  opacity: 1;
  transform: scale(1);
}

/* 背景层 */
.background-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.gradient-bg {
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at center, #0f0f1f 0%, #000000 100%);
  position: relative;
}

.gradient-bg::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: repeating-linear-gradient(
    45deg,
    rgba(255, 0, 110, 0.03) 0px,
    rgba(255, 0, 110, 0.03) 2px,
    transparent 2px,
    transparent 8px
  );
}

.noise {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3' /%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.08' /%3E%3C/svg%3E");
  opacity: 0.15;
  pointer-events: none;
}

/* 英雄区域 */
.hero-section {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(2px);
}

/* 故障风格标题 */
.glitch-title {
  font-size: 8vw;
  font-weight: 900;
  background: linear-gradient(135deg, #ffffff 0%, #ff006e 50%, #00f5d4 100%);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  position: relative;
  margin: 0;
  padding: 0;
  animation: titleFloat 3s ease-in-out infinite;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.glitch-title::before,
.glitch-title::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ffffff 0%, #ff006e 50%, #00f5d4 100%);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.glitch-title::before {
  animation: glitchShift 0.3s infinite;
  left: 2px;
  text-shadow: -2px 0 #ff006e;
  clip-path: polygon(0 0, 100% 0, 100% 45%, 0 45%);
}

.glitch-title::after {
  animation: glitchShift 0.3s infinite reverse;
  left: -2px;
  text-shadow: 2px 0 #00f5d4;
  clip-path: polygon(0 80%, 100% 20%, 100% 100%, 0 100%);
}

/* 副标题 */
.subtitle {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out 0.5s forwards;
}

.subtitle .line {
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent, #ff006e, #00f5d4, transparent);
}

.subtitle .text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  letter-spacing: 4px;
  text-transform: uppercase;
}

/* 滚动指示器 */
.scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  opacity: 0;
  animation: fadeInUp 0.8s ease-out 1s forwards;
}

.scroll-indicator span {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  letter-spacing: 2px;
}

.mouse {
  width: 26px;
  height: 42px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-radius: 20px;
  position: relative;
}

.wheel {
  width: 3px;
  height: 8px;
  background: #ff006e;
  border-radius: 2px;
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  animation: wheelScroll 1.5s ease-in-out infinite;
}

/* 浮动装饰元素 */
.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  overflow: hidden;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.15;
  animation: floatShape 8s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  background: #ff006e;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #00f5d4;
  bottom: -150px;
  right: -150px;
  animation-delay: 2s;
  animation-duration: 12s;
}

.shape-3 {
  width: 250px;
  height: 250px;
  background: #ffbe0b;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 4s;
  animation-duration: 10s;
  opacity: 0.1;
}

/* ==================== 动画定义 ==================== */
@keyframes ringRotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes textPulse {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}

@keyframes textReveal {
  0%, 100% {
    clip-path: inset(0 0 0 0);
  }
  50% {
    clip-path: inset(0 0 0 50%);
  }
}

@keyframes particleFloat {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0);
  }
  20% {
    opacity: 1;
    transform: translate(calc(-10px + 20px * var(--random)), calc(-10px + 20px * var(--random))) scale(1);
  }
  80% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translate(calc(var(--x) * 0.5), calc(var(--y) * 0.5)) scale(0);
  }
}

@keyframes titleFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes glitchShift {
  0% {
    transform: translate(0);
  }
  20% {
    transform: translate(-2px, 2px);
  }
  40% {
    transform: translate(-2px, -2px);
  }
  60% {
    transform: translate(2px, 2px);
  }
  80% {
    transform: translate(2px, -2px);
  }
  100% {
    transform: translate(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes wheelScroll {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(15px);
  }
}

@keyframes floatShape {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(30px, -30px) rotate(120deg);
  }
  66% {
    transform: translate(-20px, 20px) rotate(240deg);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glitch-title {
    font-size: 12vw;
  }
  
  .subtitle .line {
    width: 30px;
  }
  
  .subtitle .text {
    font-size: 10px;
  }
  
  .floating-shape {
    filter: blur(40px);
  }
  
  .shape-1 {
    width: 200px;
    height: 200px;
  }
  
  .shape-2 {
    width: 250px;
    height: 250px;
  }
}
</style>