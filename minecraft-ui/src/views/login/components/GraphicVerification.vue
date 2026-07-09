<template>
  <div v-if="visible" class="captcha-modal">
    <div ref="captchaBox" class="captcha-container"></div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onUnmounted } from 'vue';

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
let tacInstance = null;

const initCaptcha = async () => {
  if (!captchaBox.value || tacInstance) return;
  
  await loadTACScript();
  
  const captchaConfig = {
    requestCaptchaDataUrl: '/api/captcha/gen',
    validCaptchaUrl: '/api/captcha/check',
    bindEl: captchaBox.value,
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
      emit('close');
      tac.destroyWindow();
      tacInstance = null;
    }
  };
  
  tacInstance = new TAC(captchaConfig);
  tacInstance.init();
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
    script.onerror = reject;
    document.head.appendChild(script);
  });
};

const destroyCaptcha = () => {
  if (tacInstance) {
    tacInstance.destroyWindow();
    tacInstance = null;
  }
};

watch(() => props.visible, (newVal) => {
  if (newVal) {
    nextTick(() => {
      initCaptcha();
    });
  } else {
    destroyCaptcha();
  }
});

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
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.captcha-container {
  width: 100%;
  max-width: 360px;
}
</style>