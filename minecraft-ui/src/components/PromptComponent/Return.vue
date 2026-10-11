<template>
  <!-- 返回上一页按钮：固定于左下角，悬停展开文字胶囊 -->
  <div class="return-wrap">
    <button class="return-btn" type="button" aria-label="返回上一页" @click="goBack">
      <!-- 硬阴影底盘，营造可"按压"的 3D 质感 -->
      <span class="return-btn__base" aria-hidden="true"></span>

      <span class="return-btn__face">
        <span class="return-btn__icon">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path class="arrow-head" d="M11 5.5 4.5 12l6.5 6.5" stroke="currentColor" stroke-width="2.6"
              stroke-linecap="round" stroke-linejoin="round" />
            <path class="arrow-line" d="M5 12h13.5" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" />
          </svg>
        </span>
        <span class="return-btn__label">返回上一页</span>
      </span>
    </button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

/**
 * 返回上一页：
 * - 若存在内部历史记录则回退
 * - 否则（如直接从外部链接进入）兜底跳转首页
 */
const goBack = () => {
  const canBack = window.history.state && window.history.state.back
  if (canBack) {
    router.back()
  } else {
    router.push('/home')
  }
}
</script>

<style scoped>
.return-wrap {
  position: fixed;
  left: 24px;
  bottom: 24px;
  z-index: 1500;
  animation: return-in 0.55s cubic-bezier(0.22, 1.4, 0.36, 1) both;
}

.return-btn {
  position: relative;
  border: none;
  background: transparent;
  padding: 0;
  cursor: pointer;
  outline: none;
  -webkit-tap-highlight-color: transparent;
  font-family: 'Montserrat', 'Noto Sans SC', sans-serif;
}

/* 底盘：白描边 + 蓝色渐变 + 硬阴影 */
.return-btn__base {
  position: absolute;
  inset: 0;
  border-radius: 999px;
  background: linear-gradient(120deg, #0062ff 0%, #00a2ff 45%, #00d4ff 100%);
  border: 3px solid #ffffff;
  box-shadow: 0 5px 0 #0b4bb8, 0 10px 22px rgba(0, 98, 255, 0.35);
  transition: box-shadow 0.25s ease;
}

/* 面层：承载图标与文字，负责形变与位移 */
.return-btn__face {
  position: relative;
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 16px;
  border-radius: 999px;
  transition: padding 0.32s cubic-bezier(0.34, 1.3, 0.5, 1);
}

.return-btn__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  color: #ffffff;
  flex-shrink: 0;
  filter: drop-shadow(0 1px 1px rgba(0, 40, 120, 0.45));
}

.return-btn__icon svg {
  width: 100%;
  height: 100%;
}

.return-btn__label {
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.04em;
  white-space: nowrap;
  overflow: hidden;
  max-width: 0;
  opacity: 0;
  margin-left: 0;
  transform: translateX(-4px);
  transition: max-width 0.34s cubic-bezier(0.34, 1.3, 0.5, 1), opacity 0.24s ease,
    margin-left 0.34s cubic-bezier(0.34, 1.3, 0.5, 1), transform 0.34s ease;
}

/* 悬停：按压弹起 + 展开文字 */
.return-btn:hover .return-btn__face {
  padding: 0 22px 0 16px;
  transform: translateY(-3px);
}

.return-btn:hover .return-btn__base {
  box-shadow: 0 8px 0 #0b4bb8, 0 16px 30px rgba(0, 98, 255, 0.45);
}

.return-btn:hover .return-btn__label {
  max-width: 120px;
  opacity: 1;
  margin-left: 10px;
  transform: translateX(0);
}

/* 悬停时箭头做一次"回退"律动 */
.return-btn:hover .arrow-head,
.return-btn:hover .arrow-line {
  animation: arrow-nudge 0.75s ease-in-out infinite;
}

.return-btn:hover .arrow-line {
  animation-delay: 0.06s;
}

/* 按下：整体下沉，硬阴影收紧 */
.return-btn:active .return-btn__face {
  transform: translateY(2px);
}

.return-btn:active .return-btn__base {
  box-shadow: 0 2px 0 #0b4bb8, 0 6px 14px rgba(0, 98, 255, 0.35);
}

/* 键盘可达性 */
.return-btn:focus-visible .return-btn__base {
  outline: 3px solid rgba(0, 162, 255, 0.55);
  outline-offset: 3px;
}

@keyframes return-in {
  from {
    opacity: 0;
    transform: translateX(-28px) scale(0.85);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes arrow-nudge {
  0%,
  100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-3px);
  }
}

@media (prefers-reduced-motion: reduce) {
  .return-wrap,
  .return-btn__face,
  .return-btn__base,
  .return-btn__label {
    animation: none !important;
    transition: none !important;
  }
}

@media (max-width: 768px) {
  .return-wrap {
    left: 16px;
    bottom: 16px;
  }

  .return-btn__face {
    height: 48px;
  }
}
</style>
