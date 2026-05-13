<template>
  <transition name="fade">
    <div v-if="visible" class="social-login-modal" @click="handleClose">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="handleClose">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>

        <div class="modal-header">
          <div class="platform-icon" :style="{ backgroundColor: platformInfo.bgColor }">
            <component :is="platformIcon" />
          </div>
          <h3>{{ platformInfo.title }}</h3>
          <p>{{ platformInfo.subtitle }}</p>
        </div>

        <div class="qr-container">
          <div class="qr-wrapper">
            <div class="qr-code">
              <svg viewBox="0 0 200 200" class="qr-svg">
                <rect width="200" height="200" fill="#fff" />
                <g fill="#333">
                  <rect x="20" y="20" width="40" height="40" />
                  <rect x="20" y="140" width="40" height="40" />
                  <rect x="140" y="20" width="40" height="40" />
                  <rect x="150" y="150" width="30" height="30" />
                  <rect x="150" y="20" width="30" height="30" />
                  <rect x="20" y="150" width="30" height="30" />
                </g>
                <g fill="#333">
                  <rect x="90" y="90" width="20" height="20" />
                  <rect x="70" y="90" width="10" height="20" />
                  <rect x="120" y="90" width="10" height="20" />
                  <rect x="90" y="70" width="20" height="10" />
                  <rect x="90" y="120" width="20" height="10" />
                </g>
                <rect x="60" y="60" width="80" height="80" fill="none" stroke="#333" stroke-width="2" />
              </svg>
              <div class="qr-placeholder">
                <span class="qr-text">{{ platformInfo.qrText }}</span>
              </div>
            </div>
            <div class="qr-scan-line"></div>
          </div>

          <div class="qr-tips">
            <div class="tip-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10" />
                <path d="M15 11l-3 3-3-3" />
              </svg>
            </div>
            <p>{{ platformInfo.scanTip }}</p>
          </div>
        </div>

        <div class="platform-info">
          <div class="info-item">
            <span class="info-label">登录方式</span>
            <span class="info-value">{{ platformInfo.loginType }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">安全保障</span>
            <span class="info-value">官方认证 · 加密传输</span>
          </div>
          <div class="info-item">
            <span class="info-label">有效期</span>
            <span class="info-value">5分钟</span>
          </div>
        </div>

        <div class="countdown">
          <span class="countdown-text">二维码将在 </span>
          <span class="countdown-num" :style="{ color: platformInfo.color }">{{ countdown }}</span>
          <span class="countdown-text"> 秒后刷新</span>
        </div>

        <button class="refresh-btn" @click="refreshQR"
          :style="{ borderColor: platformInfo.color, color: platformInfo.color }">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10" />
            <polyline points="1 20 1 14 7 14" />
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15" />
          </svg>
          <span>刷新二维码</span>
        </button>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  platform: {
    type: String,
    default: 'wechat'
  }
})

const emit = defineEmits(['close'])

const countdown = ref(300)
let countdownTimer = null

const platformConfigs = {
  wechat: {
    title: '微信登录',
    subtitle: '使用微信扫码登录',
    bgColor: '#28C445',
    color: '#28C445',
    qrText: '微信扫码',
    scanTip: '请打开微信扫一扫，扫描上方二维码',
    loginType: '微信公众号登录'
  },
  qq: {
    title: 'QQ登录',
    subtitle: '使用QQ扫码登录',
    bgColor: '#30A5DD',
    color: '#30A5DD',
    qrText: 'QQ扫码',
    scanTip: '请打开QQ扫一扫，扫描上方二维码',
    loginType: 'QQ账号登录'
  },
  weibo: {
    title: '微博登录',
    subtitle: '使用微博扫码登录',
    bgColor: '#EA5D5C',
    color: '#EA5D5C',
    qrText: '微博扫码',
    scanTip: '请打开微博扫一扫，扫描上方二维码',
    loginType: '微博账号登录'
  }
}

const platformInfo = computed(() => {
  return platformConfigs[props.platform] || platformConfigs.wechat
})

const platformIcon = computed(() => {
  const icons = {
    wechat: {
      template: `
        <svg t="1737552806004" class="action-btn-iocn-size" viewBox="0 0 1024 1024" version="1.1"
          xmlns="http://www.w3.org/2000/svg" p-id="6242">
          <path
            d="M337.387283 341.82659c-17.757225 0-35.514451 11.83815-35.514451 29.595375s17.757225 29.595376 35.514451 29.595376 29.595376-11.83815 29.595376-29.595376c0-18.49711-11.83815-29.595376-29.595376-29.595375zM577.849711 513.479769c-11.83815 0-22.936416 12.578035-22.936416 23.6763 0 12.578035 11.83815 23.676301 22.936416 23.676301 17.757225 0 29.595376-11.83815 29.595376-23.676301s-11.83815-23.676301-29.595376-23.6763zM501.641618 401.017341c17.757225 0 29.595376-12.578035 29.595376-29.595376 0-17.757225-11.83815-29.595376-29.595376-29.595375s-35.514451 11.83815-35.51445 29.595375 17.757225 29.595376 35.51445 29.595376zM706.589595 513.479769c-11.83815 0-22.936416 12.578035-22.936416 23.6763 0 12.578035 11.83815 23.676301 22.936416 23.676301 17.757225 0 29.595376-11.83815 29.595376-23.676301s-11.83815-23.676301-29.595376-23.6763z"
            fill="#28C445" p-id="6243"></path>
          <path
            d="M510.520231 2.959538C228.624277 2.959538 0 231.583815 0 513.479769s228.624277 510.520231 510.520231 510.520231 510.520231-228.624277 510.520231-510.520231-228.624277-510.520231-510.520231-510.520231zM413.595376 644.439306c-29.595376 0-53.271676-5.919075-81.387284-12.578034l-81.387283 41.433526 22.936416-71.768786c-58.450867-41.433526-93.965318-95.445087-93.965317-159.815029 0-113.202312 105.803468-201.988439 233.803468-201.98844 114.682081 0 216.046243 71.028902 236.023121 166.473989-7.398844-0.739884-14.797688-1.479769-22.196532-1.479769-110.982659 1.479769-198.289017 85.086705-198.289017 188.67052 0 17.017341 2.959538 33.294798 7.398844 49.572255-7.398844 0.739884-15.537572 1.479769-22.936416 1.479768z m346.265896 82.867052l17.757225 59.190752-63.630058-35.514451c-22.936416 5.919075-46.612717 11.83815-70.289017 11.83815-111.722543 0-199.768786-76.947977-199.768786-172.393063-0.739884-94.705202 87.306358-171.653179 198.289017-171.65318 105.803468 0 199.028902 77.687861 199.028902 172.393064 0 53.271676-34.774566 100.624277-81.387283 136.138728z"
            fill="#28C445" p-id="6244">
          </path>
        </svg>
      `
    },
    qq: {
      template: `
        <svg t="1737552871738" class="action-btn-iocn-size" viewBox="0 0 1024 1024" version="1.1"
          xmlns="http://www.w3.org/2000/svg" p-id="7246">
          <path
            d="M512 0C229.003636 0 0 229.003636 0 512s229.003636 512 512 512 512-229.003636 512-512S794.996364 0 512 0z m210.385455 641.396364c-7.447273 9.309091-26.996364-1.861818-41.89091-32.581819-3.723636 13.963636-13.032727 36.305455-34.443636 64.232728 35.374545 8.378182 44.683636 42.821818 33.512727 61.44-8.378182 13.032727-26.996364 24.203636-59.578181 24.203636-58.647273 0-83.781818-15.825455-95.883637-26.996364-1.861818-2.792727-5.585455-3.723636-10.24-3.723636-4.654545 0-7.447273 0.930909-10.24 3.723636-11.170909 11.170909-37.236364 26.996364-95.883636 26.996364-32.581818 0-52.130909-11.170909-59.578182-24.203636-12.101818-18.618182-1.861818-53.061818 33.512727-61.44-20.48-27.927273-29.789091-50.269091-34.443636-64.232728-13.963636 30.72-34.443636 42.821818-41.890909 32.581819-5.585455-8.378182-8.378182-26.065455-7.447273-38.167273 3.723636-46.545455 34.443636-85.643636 53.061818-106.123636-2.792727-5.585455-8.378182-40.029091 14.894546-63.301819v-1.861818c0-92.16 65.163636-158.254545 148.014545-158.254545 81.92 0 148.014545 66.094545 148.014546 158.254545v1.861818c23.272727 23.272727 17.687273 57.716364 14.894545 63.301819 17.687273 20.48 49.338182 59.578182 53.061818 106.123636 0.930909 12.101818-0.930909 29.789091-7.447272 38.167273z"
            fill="#30A5DD" p-id="7247">
        </svg>
      `
    },
    weibo: {
      template: `
        <svg t="1737552907646" class="action-btn-iocn-size" viewBox="0 0 1024 1024" version="1.1"
          xmlns="http://www.w3.org/2000/svg" p-id="8263">
          <path
            d="M448.698182 482.210909c-96.814545 4.654545-175.010909 56.785455-175.010909 121.949091s78.196364 114.501818 175.010909 109.847273S623.709091 647.912727 623.709091 582.749091c-0.930909-64.232727-79.127273-105.192727-175.010909-100.538182z m65.163636 164.770909c-29.789091 39.098182-88.436364 57.716364-145.221818 26.065455-26.996364-14.894545-26.065455-43.752727-26.065455-43.752728s-11.170909-92.16 85.643637-103.330909c97.745455-12.101818 115.432727 81.92 85.643636 121.018182z"
            fill="#EA5D5C" p-id="8264"></path>
          <path
            d="M448.698182 584.610909c-6.516364 4.654545-7.447273 13.032727-3.723637 18.618182 2.792727 5.585455 11.170909 6.516364 16.756364 1.861818 5.585455-4.654545 8.378182-13.032727 4.654546-18.618182-2.792727-5.585455-10.24-6.516364-17.687273-1.861818zM403.083636 597.643636c-18.618182 1.861818-30.72 17.687273-30.72 33.512728 0 14.894545 14.894545 26.065455 32.581819 24.203636 17.687273-1.861818 32.581818-15.825455 32.581818-31.650909s-13.963636-27.927273-34.443637-26.065455z"
            fill="#EA5D5C" p-id="8265"></path>
          <path
            d="M512 0C229.003636 0 0 229.003636 0 512s229.003636 512 512 512 512-229.003636 512-512S794.996364 0 512 0z m197.352727 626.501818C669.323636 712.145455 538.065455 754.036364 441.250909 746.589091c-92.16-7.447273-211.316364-38.167273-223.418182-151.738182 0 0-6.516364-51.2 42.821818-117.294545 0 0 70.749091-99.607273 152.669091-128.465455 82.850909-27.927273 92.16 19.549091 92.16 48.407273-4.654545 24.203636-12.101818 38.167273 18.618182 28.858182 0 0 80.989091-38.167273 114.501818-4.654546 26.996364 26.996364 4.654545 65.163636 4.654546 65.163637s-11.170909 12.101818 12.101818 16.756363c21.410909 3.723636 94.021818 37.236364 53.992727 122.88z m-80.058182-236.450909c-8.378182 0-15.825455-7.447273-15.825454-15.825454 0-9.309091 7.447273-15.825455 15.825454-15.825455 0 0 99.607273-18.618182 87.505455 89.367273v1.861818c-0.930909 7.447273-7.447273 13.963636-15.825455 13.963636-9.309091 0-15.825455-7.447273-15.825454-15.825454 0-1.861818 15.825455-73.541818-55.854546-57.716364zM797.789091 493.381818c-2.792727 18.618182-12.101818 11.170909-22.341818 11.170909-13.032727 0-23.272727-16.756364-23.272728-29.789091 0-11.170909 4.654545-22.341818 4.654546-22.341818 0.930909-4.654545 12.101818-34.443636-7.447273-78.196363-35.374545-60.509091-106.123636-60.509091-114.501818-57.716364-8.378182 3.723636-21.410909 5.585455-21.410909 5.585454-13.032727 0-23.272727-10.24-23.272727-23.272727 0-11.170909 7.447273-19.549091 16.756363-22.341818 0 0 0 0.930909 0.930909 0.930909s1.861818 0.930909 1.861819 0.930909c10.24-1.861818 45.614545-4.654545 79.127272 3.723637 62.370909 14.894545 146.152727 83.781818 108.916364 211.316363z"
            fill="#EA5D5C" p-id="8266"></path>
        </svg>
      `
    }
  }

  const iconComponent = {
    template: icons[props.platform]?.template || icons.wechat.template
  }

  return iconComponent
})

const startCountdown = () => {
  countdown.value = 300
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      refreshQR()
    }
  }, 1000)
}

const handleClose = () => {
  emit('close')
}

const refreshQR = () => {
  countdown.value = 300
}

watch(() => props.visible, (newVal) => {
  if (newVal) {
    startCountdown()
  } else {
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
  }
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.social-login-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 30px;
  width: 90%;
  max-width: 400px;
  position: relative;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 36px;
  height: 36px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #eee;
  transform: rotate(90deg);
}

.close-btn svg {
  width: 18px;
  height: 18px;
}

.modal-header {
  text-align: center;
  margin-bottom: 25px;
}

.platform-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
}

.platform-icon svg {
  width: 32px;
  height: 32px;
}

.modal-header h3 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
  color: #333;
}

.modal-header p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.qr-container {
  background: #fafafa;
  border-radius: 16px;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.qr-wrapper {
  position: relative;
  width: 180px;
  height: 180px;
  margin: 0 auto;
}

.qr-code {
  width: 100%;
  height: 100%;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.qr-svg {
  width: 160px;
  height: 160px;
}

.qr-placeholder {
  position: absolute;
  bottom: 10px;
  text-align: center;
}

.qr-text {
  font-size: 12px;
  color: #999;
}

.qr-scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(79, 172, 254, 0.5), transparent);
  animation: scan 2s linear infinite;
}

@keyframes scan {
  0% {
    top: 0;
    opacity: 1;
  }

  100% {
    top: 100%;
    opacity: 0;
  }
}

.qr-tips {
  text-align: center;
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tip-icon {
  width: 20px;
  height: 20px;
  color: #4facfe;
}

.tip-icon svg {
  width: 100%;
  height: 100%;
}

.qr-tips p {
  margin: 0;
  font-size: 13px;
  color: #666;
}

.platform-info {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding: 15px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item {
  text-align: center;
}

.info-label {
  display: block;
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.info-value {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.countdown {
  text-align: center;
  margin-top: 20px;
  font-size: 13px;
  color: #999;
}

.countdown-num {
  font-weight: 600;
  font-size: 16px;
}

.refresh-btn {
  width: 100%;
  margin-top: 20px;
  padding: 12px;
  border: 1px solid;
  border-radius: 25px;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: rgba(79, 172, 254, 0.05);
}

.refresh-btn svg {
  width: 16px;
  height: 16px;
}
</style>