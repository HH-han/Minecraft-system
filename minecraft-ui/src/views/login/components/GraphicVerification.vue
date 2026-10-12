<template>
  <Teleport to="body">
    <div v-if="visible" class="captcha-modal" @click.self="handleClose">
      <div class="captcha-card">
        <!-- 左侧插画区（移动端隐藏） -->
        <div class="captcha-aside">
          <img class="aside-illustration" :src="travelIllustration" alt="安全验证插画" />
          <div class="aside-overlay">
            <span class="aside-brand">Minecraft 旅行</span>
            <span class="aside-slogan">安心出行 · 快乐出发</span>
          </div>
        </div>

        <!-- 右侧验证区 -->
        <div class="captcha-main">
          <header class="captcha-header">
            <div class="captcha-title">
              <span class="title-icon">🛡️</span>
              <div class="title-text">
                <h3>安全验证</h3>
                <p>拖动滑块完成拼图，验证后继续操作</p>
              </div>
            </div>
          </header>

          <!-- 加载中 -->
          <div v-if="loadState === 'loading'" class="captcha-status">
            <div class="status-spinner"></div>
            <p>正在加载验证组件...</p>
          </div>

          <!-- 加载失败 -->
          <div v-else-if="loadState === 'error'" class="captcha-status error">
            <p>验证组件加载失败，请检查网络后重试</p>
            <button class="retry-btn" type="button" @click="initCaptcha">重新加载</button>
          </div>

          <!-- TAC 验证码挂载点 -->
          <div v-show="loadState === 'ready'" ref="captchaBox" class="captcha-container"></div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, watch, nextTick, onUnmounted } from 'vue';
import travelIllustration from '@/assets/loging/31d494f83e7fef6eed32f2ac1d746e79dd387cd7bc439-5C2vFY_fw1200webp.webp';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'image'
  }
});

const emit = defineEmits(['close', 'success', 'fail']);

const captchaBox = ref(null);
const loadState = ref('loading'); // loading | ready | error
let tacInstance = null;

const initCaptcha = async () => {
  if (tacInstance) return;

  loadState.value = 'loading';
  try {
    await loadTACScript();
  } catch {
    loadState.value = 'error';
    return;
  }

  await nextTick();
  if (!captchaBox.value) return;

  const captchaConfig = {
    requestCaptchaDataUrl: '/api/captcha/gen',
    validCaptchaUrl: '/api/captcha/check',
    bindEl: captchaBox.value,
    style: {
      // 传 null：TAC loadStyle 会将 logo 置为 display:none
      logoUrl: null
    },
    validSuccess: (res, c, t) => {
      emit('success');
      t.destroyWindow();
      tacInstance = null;
    },
    validFail: (res, c, t) => {
      emit('fail');
      t.reloadCaptcha();
    },
    btnRefreshFun: (el, tac) => {
      tac.reloadCaptcha();
    },
    btnCloseFun: (el, tac) => {
      tac.destroyWindow();
      tacInstance = null;
      emit('close');
    }
  };

  tacInstance = new TAC(captchaConfig);
  tacInstance.init();
  loadState.value = 'ready';
};

const loadTACScript = () => {
  return new Promise((resolve, reject) => {
    if (window.TAC) {
      resolve();
      return;
    }

    const script = document.createElement('script');
    script.src = '/tac/js/tac.min.js';
    script.onload = resolve;
    script.onerror = () => reject(new Error('TAC script load failed'));
    document.head.appendChild(script);
  });
};

const destroyCaptcha = () => {
  if (tacInstance) {
    tacInstance.destroyWindow();
    tacInstance = null;
  }
};

const handleClose = () => {
  destroyCaptcha();
  emit('close');
};

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      nextTick(() => {
        initCaptcha();
      });
    } else {
      destroyCaptcha();
    }
  }
);

onUnmounted(() => {
  destroyCaptcha();
});
</script>

<style scoped>
.captcha-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: var(--theme-overlay);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  animation: modal-fade 0.25s ease-out;
}

@keyframes modal-fade {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 验证卡片：左插画 + 右验证 */
.captcha-card {
  display: flex;
  background: var(--theme-bg-surface);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--theme-shadow-lg);
  animation: card-pop 0.28s cubic-bezier(0.34, 1.4, 0.64, 1);
}

@keyframes card-pop {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 插画区 */
.captcha-aside {
  position: relative;
  width: 220px;
  flex-shrink: 0;
}

.aside-illustration {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.aside-overlay {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 40px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: linear-gradient(180deg, transparent 0%, var(--theme-overlay) 100%);
}

.aside-brand {
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 1px;
}

.aside-slogan {
  color: rgba(255, 255, 255, 0.82);
  font-size: 12px;
}

/* 验证主区 */
.captcha-main {
  padding: 22px 24px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.captcha-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}

.captcha-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  font-size: 20px;
  font-style: normal;
  background: var(--theme-bg-surface-2);
  border-radius: 12px;
}

.title-text h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.title-text p {
  margin: 3px 0 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

.close-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
  transform: rotate(90deg);
}

/* TAC 挂载容器：覆盖 TAC 默认皮肤 */
.captcha-container {
  width: 318px;
  max-width: 100%;
}

.captcha-container :deep(#tianai-captcha-parent) {
  box-shadow: none;
  border: 1px solid var(--theme-hairline);
  border-radius: 14px;
}

.captcha-container :deep(#tianai-captcha-box) {
  border-radius: 10px;
}

.captcha-container :deep(.slider-bottom) {
  height: 26px;
  padding-top: 4px;
}

/* 加载 / 错误状态 */
.captcha-status {
  width: 318px;
  max-width: 100%;
  height: 318px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  border: 1px solid var(--theme-hairline);
  border-radius: 14px;
  background: var(--theme-bg-surface-2);
}

.captcha-status p {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-3);
}

.captcha-status.error p {
  color: var(--theme-danger);
}

.status-spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--theme-border);
  border-top-color: var(--theme-primary);
  border-radius: 50%;
  animation: spin 0.9s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.retry-btn {
  padding: 8px 22px;
  border: none;
  border-radius: 999px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.retry-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 14px color-mix(in srgb, var(--theme-primary) 35%, transparent);
}

/* 移动端：隐藏插画，卡片收窄 */
@media (max-width: 640px) {
  .captcha-aside {
    display: none;
  }

  .captcha-main {
    padding: 18px 16px 16px;
  }
}
</style>
