<template>
  <div class="error-page">
    <!-- 背景装饰 -->
    <div class="bg-grid" aria-hidden="true"></div>
    <div class="bg-glow bg-glow--red" aria-hidden="true"></div>
    <div class="bg-glow bg-glow--amber" aria-hidden="true"></div>

    <main class="container">
      <!-- 左侧：错误信息 -->
      <section class="hero">
        <div class="badge">SERVER ERROR · 服务器内部错误</div>

        <div class="code" data-text="500">500</div>

        <h1 class="title">服务器开小差了</h1>
        <p class="desc">
          服务器在处理请求时遇到意外状况，无法完成本次操作。<br />
          这通常不是你的问题 —— 维护机器人已经出发赶往现场。
        </p>

        <div class="meta">
          <div class="meta-item">
            <span class="meta-label">错误代码</span>
            <span class="meta-value">HTTP 500</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">请求路径</span>
            <span class="meta-value mono">{{ fromPath }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">发生时间</span>
            <span class="meta-value mono">{{ occurredAt }}</span>
          </div>
        </div>

        <div class="actions">
          <button class="btn btn--primary" @click="reload">
            <span class="btn-icon">↻</span>刷新页面
          </button>
          <button class="btn btn--ghost" @click="goBack">返回上一页</button>
          <button class="btn btn--ghost" @click="goHome">返回首页</button>
        </div>
      </section>

      <!-- 右侧：服务器机架 + 终端日志 -->
      <section class="panel">
        <div class="rack" aria-hidden="true">
          <div
            v-for="n in 4"
            :key="n"
            class="unit"
            :class="{ 'unit--down': n === 2 }"
          >
            <span class="unit-name">NODE-0{{ n }}</span>
            <div class="unit-slots">
              <span v-for="s in 6" :key="s" class="slot"></span>
            </div>
            <span class="unit-led"></span>

            <template v-if="n === 2">
              <span class="smoke smoke--1"></span>
              <span class="smoke smoke--2"></span>
              <span class="smoke smoke--3"></span>
            </template>
          </div>

          <div class="robot">🤖</div>
        </div>

        <div class="terminal">
          <div class="terminal-bar">
            <span class="dot dot--red"></span>
            <span class="dot dot--yellow"></span>
            <span class="dot dot--green"></span>
            <span class="terminal-title">server-diagnostics</span>
          </div>
          <div class="terminal-body">
            <div
              v-for="(line, i) in visibleLogs"
              :key="i"
              class="log-line"
              :class="`log-line--${line.type}`"
            >
              <span class="log-time">{{ line.time }}</span>
              <span class="log-text">{{ line.text }}</span>
            </div>
            <span v-if="!logsDone" class="caret"></span>
          </div>
        </div>
      </section>
    </main>

    <footer class="foot">
      若问题持续存在，请联系管理员并附上错误标识：
      <code class="mono">{{ errorId }}</code>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

/* 基本信息 */
const fromPath = computed(() => {
  const q = route.query?.from
  if (typeof q === 'string' && q) return q
  return route.redirectedFrom?.fullPath || window.location.pathname || '/'
})

const occurredAt = ref('')
const errorId = ref('')

const formatTime = (d) =>
  [d.getHours(), d.getMinutes(), d.getSeconds()]
    .map((v) => String(v).padStart(2, '0'))
    .join(':')

const now = new Date()
const stamp = (offsetSec = 0) =>
  formatTime(new Date(now.getTime() - offsetSec * 1000))

/* 终端诊断日志（逐条输出） */
const allLogs = [
  { type: 'cmd', time: stamp(12), text: '$ systemctl status minecraft-server' },
  { type: 'error', time: stamp(11), text: '[ERROR] java.lang.IllegalStateException: 主循环崩溃' },
  { type: 'warn', time: stamp(9), text: '[WARN]  at net.minecraft.server.ServerTick.run(ServerTick.java:512)' },
  { type: 'info', time: stamp(7), text: '[INFO]  正在收集崩溃报告 → crash-reports/crash-500.txt' },
  { type: 'info', time: stamp(5), text: '[INFO]  已派出服务器维护机器人前往现场 🤖' },
  { type: 'error', time: stamp(3), text: '[ERROR] 无法自动恢复，需要人工介入' }
]

const visibleLogs = ref([])
const logsDone = computed(() => visibleLogs.value.length >= allLogs.length)
let logTimer = null

/* 操作 */
const reload = () => window.location.reload()

const goBack = () => {
  if (window.history.length > 1) router.back()
  else router.push('/')
}

const goHome = () => router.push('/')

onMounted(() => {
  occurredAt.value = new Date().toLocaleString('zh-CN', { hour12: false })
  errorId.value =
    'ERR-' +
    Date.now().toString(36).toUpperCase() +
    '-' +
    Math.random().toString(36).slice(2, 6).toUpperCase()

  let index = 0
  logTimer = setInterval(() => {
    if (index < allLogs.length) {
      visibleLogs.value.push(allLogs[index++])
    } else {
      clearInterval(logTimer)
      logTimer = null
    }
  }, 550)
})

onUnmounted(() => {
  if (logTimer) clearInterval(logTimer)
})
</script>

<style scoped>
/* ========== 页面骨架 ========== */
.error-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #0b1220;
  color: #e6edf5;
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Segoe UI', system-ui, sans-serif;
}

.container {
  position: relative;
  z-index: 1;
  flex: 1;
  width: min(1080px, 92vw);
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 48px;
  align-items: center;
  padding: 48px 0;
}

.mono {
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
}

/* ========== 背景装饰 ========== */
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.06) 1px, transparent 1px);
  background-size: 44px 44px;
  -webkit-mask-image: radial-gradient(ellipse at 50% 40%, #000 30%, transparent 75%);
  mask-image: radial-gradient(ellipse at 50% 40%, #000 30%, transparent 75%);
}

.bg-glow {
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.22;
  pointer-events: none;
}

.bg-glow--red {
  background: #f43f5e;
  top: -180px;
  left: -140px;
  animation: glow-float 14s ease-in-out infinite alternate;
}

.bg-glow--amber {
  background: #f59e0b;
  bottom: -220px;
  right: -160px;
  animation: glow-float 18s ease-in-out infinite alternate-reverse;
}

@keyframes glow-float {
  from { transform: translate(0, 0) scale(1); }
  to { transform: translate(60px, 40px) scale(1.15); }
}

/* ========== 左侧内容 ========== */
.badge {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.12em;
  color: #fda4af;
  background: rgba(244, 63, 94, 0.12);
  border: 1px solid rgba(244, 63, 94, 0.35);
  margin-bottom: 18px;
}

.code {
  position: relative;
  font-size: clamp(96px, 14vw, 168px);
  font-weight: 900;
  line-height: 0.9;
  letter-spacing: 0.04em;
  background: linear-gradient(180deg, #ffffff 20%, #94a3b8);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  user-select: none;
}

.code::before,
.code::after {
  content: attr(data-text);
  position: absolute;
  inset: 0;
  background: none;
  -webkit-background-clip: initial;
  background-clip: initial;
  pointer-events: none;
  mix-blend-mode: screen;
}

.code::before {
  color: #ff4d6d;
  -webkit-text-fill-color: #ff4d6d;
  animation: glitch-a 2.4s infinite steps(1);
}

.code::after {
  color: #38bdf8;
  -webkit-text-fill-color: #38bdf8;
  animation: glitch-b 3.1s infinite steps(1);
}

@keyframes glitch-a {
  0%, 86%, 100% { clip-path: inset(0 0 100% 0); transform: translate(0); }
  20% { clip-path: inset(18% 0 58% 0); transform: translate(-6px, 2px); }
  40% { clip-path: inset(60% 0 12% 0); transform: translate(5px, -2px); }
  60% { clip-path: inset(34% 0 44% 0); transform: translate(-4px, 1px); }
  80% { clip-path: inset(72% 0 6% 0); transform: translate(6px, -1px); }
}

@keyframes glitch-b {
  0%, 82%, 100% { clip-path: inset(0 0 100% 0); transform: translate(0); }
  25% { clip-path: inset(8% 0 74% 0); transform: translate(5px, -1px); }
  50% { clip-path: inset(52% 0 26% 0); transform: translate(-5px, 2px); }
  75% { clip-path: inset(80% 0 4% 0); transform: translate(4px, 1px); }
}

.title {
  margin: 14px 0 10px;
  font-size: 26px;
  font-weight: 700;
  color: #f1f5f9;
}

.desc {
  margin: 0 0 22px;
  font-size: 15px;
  line-height: 1.8;
  color: #94a3b8;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 32px;
  padding: 14px 18px;
  border-radius: 12px;
  background: rgba(148, 163, 184, 0.06);
  border: 1px solid rgba(148, 163, 184, 0.12);
  margin-bottom: 26px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #64748b;
  letter-spacing: 0.06em;
}

.meta-value {
  font-size: 14px;
  font-weight: 600;
  color: #cbd5e1;
  word-break: break-all;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  border: 0;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.16s ease, box-shadow 0.16s ease, background 0.16s ease;
}

.btn-icon {
  display: inline-block;
  animation: spin 3.2s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn--primary {
  color: #fff;
  background: linear-gradient(90deg, #f87171, #f43f5e);
  box-shadow: 0 8px 22px rgba(244, 63, 94, 0.28);
}

.btn--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(244, 63, 94, 0.38);
}

.btn--ghost {
  color: #cbd5e1;
  background: transparent;
  border: 1px solid rgba(148, 163, 184, 0.28);
}

.btn--ghost:hover {
  transform: translateY(-2px);
  color: #fff;
  border-color: rgba(203, 213, 225, 0.5);
  background: rgba(148, 163, 184, 0.08);
}

/* ========== 右侧面板 ========== */
.panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* --- 服务器机架 --- */
.rack {
  position: relative;
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(180deg, #0d1a2d, #0a1424);
  border: 1px solid rgba(148, 163, 184, 0.14);
  box-shadow: 0 18px 44px rgba(2, 6, 15, 0.55);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.unit {
  position: relative;
  height: 44px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 12px;
  border-radius: 8px;
  background: linear-gradient(180deg, #13233a, #0f1c30);
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.unit--down {
  border-color: rgba(244, 63, 94, 0.45);
  background: linear-gradient(180deg, #2b1524, #20101d);
  animation: unit-shake 4s ease-in-out infinite;
}

@keyframes unit-shake {
  0%, 88%, 100% { transform: translateX(0); }
  90% { transform: translateX(-2px); }
  92% { transform: translateX(2px); }
  94% { transform: translateX(-1px); }
  96% { transform: translateX(1px); }
}

.unit-name {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #64748b;
}

.unit--down .unit-name {
  color: #fb7185;
}

.unit-slots {
  display: flex;
  gap: 5px;
  flex: 1;
}

.slot {
  width: 14px;
  height: 5px;
  border-radius: 2px;
  background: rgba(148, 163, 184, 0.18);
}

.unit-led {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 8px rgba(52, 211, 153, 0.7);
  animation: led-blink 1.8s infinite;
}

.unit--down .unit-led {
  background: #f43f5e;
  box-shadow: 0 0 10px rgba(244, 63, 94, 0.8);
  animation: led-blink 0.7s infinite;
}

@keyframes led-blink {
  0%, 60%, 100% { opacity: 1; }
  30% { opacity: 0.25; }
}

/* 冒烟 */
.smoke {
  position: absolute;
  bottom: 100%;
  left: 70%;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(148, 163, 184, 0.35);
  filter: blur(3px);
  animation: smoke-rise 2.6s ease-out infinite;
  pointer-events: none;
}

.smoke--2 {
  left: 58%;
  animation-delay: 0.9s;
}

.smoke--3 {
  left: 82%;
  animation-delay: 1.7s;
}

@keyframes smoke-rise {
  0% {
    transform: translate(0, 0) scale(0.6);
    opacity: 0.7;
  }
  100% {
    transform: translate(10px, -34px) scale(2.2);
    opacity: 0;
  }
}

/* 巡逻机器人 */
.robot {
  position: absolute;
  bottom: -14px;
  left: 0;
  font-size: 24px;
  animation: robot-patrol 8s ease-in-out infinite alternate;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
}

@keyframes robot-patrol {
  from { transform: translateX(8px); }
  to { transform: translateX(calc(100% + 240px)); }
}

/* --- 终端 --- */
.terminal {
  border-radius: 14px;
  overflow: hidden;
  background: #0a111e;
  border: 1px solid rgba(148, 163, 184, 0.14);
  box-shadow: 0 18px 44px rgba(2, 6, 15, 0.55);
}

.terminal-bar {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 10px 14px;
  background: rgba(148, 163, 184, 0.07);
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot--red { background: #ff5f57; }
.dot--yellow { background: #febc2e; }
.dot--green { background: #28c840; }

.terminal-title {
  margin-left: 8px;
  font-size: 12px;
  color: #64748b;
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
}

.terminal-body {
  padding: 14px 16px;
  min-height: 168px;
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
  font-size: 12.5px;
  line-height: 1.9;
}

.log-line {
  display: flex;
  gap: 10px;
  animation: log-in 0.3s ease both;
}

@keyframes log-in {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}

.log-time {
  color: #475569;
  flex-shrink: 0;
}

.log-line--cmd .log-text { color: #a5f3b0; }
.log-line--info .log-text { color: #7dd3fc; }
.log-line--warn .log-text { color: #fbbf24; }
.log-line--error .log-text { color: #ff6b6b; font-weight: 600; }

.caret {
  display: inline-block;
  width: 8px;
  height: 15px;
  margin-top: 4px;
  background: #a5f3b0;
  animation: caret-blink 0.9s steps(1) infinite;
}

@keyframes caret-blink {
  50% { opacity: 0; }
}

/* ========== 页脚 ========== */
.foot {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 16px;
  font-size: 13px;
  color: #64748b;
}

.foot .mono {
  padding: 2px 8px;
  margin-left: 4px;
  border-radius: 6px;
  background: rgba(148, 163, 184, 0.1);
  color: #94a3b8;
}

/* ========== 响应式 ========== */
@media (max-width: 880px) {
  .container {
    grid-template-columns: 1fr;
    gap: 36px;
    padding: 40px 0;
  }

  .panel {
    max-width: 460px;
  }

  .robot {
    display: none;
  }
}

/* 减少动态效果（无障碍） */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation: none !important;
    transition: none !important;
  }
}
</style>
