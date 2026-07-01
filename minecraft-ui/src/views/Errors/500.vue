<template>
<div class="scale-wrapper">
  <div class="wrapper">
    <div class="left">
      <h1>500 — 服务器内部错误</h1>
      <div style="display:flex;align-items:center;gap:14px;margin-bottom:8px;flex-wrap:nowrap">
        <div class="error-num" id="err">500</div>
        <div>
          <p class="lead" id="msg">服务器正在休息 ☕ — 请稍后重试。</p>
          <div class="actions">
            <button class="primary" id="retry">重试</button>
            <button class="ghost" id="home">返回首页</button>
          </div>
          <div class="hint" id="hint">提示：这很可能不是你的问题。错误代码 <code>500</code></div>
        </div>
      </div>
    </div>

    <div class="server">
      <div class="rack" id="rack">
        <div class="drive" id="drive">
          <div class="panel">DB</div>
          <div class="light"></div>
        </div>
        <div class="robot" id="robot">🤖<div style="font-size:11px;margin-top:4px">服务器机器人</div>
        </div>
        <div style="display:flex;flex-direction:column;gap:8px;align-items:flex-end">
          <div class="panel" style="width:44px;height:44px;">API</div>
          <div class="panel" style="width:44px;height:44px;">CDN</div>
        </div>
      </div>
    </div>

    <div class="foot">调试状态: <span id="debug">空闲</span></div>
  </div>
</div>

<!-- Popup -->
<div class="popup" id="popup">
  <div>💡 多点击几次“重试”按钮 — 也许会有好运！</div>
  <button id="closePopup" aria-label="关闭弹窗">✕</button>
</div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted, onUnmounted } from 'vue'

const router = useRouter()

const refreshPage = () => {
  window.location.reload()
}

const goHome = () => {
  router.push('/')
}

let scaleToFitHandler = null

onMounted(() => {
  const retry = document.getElementById("retry");
  const home = document.getElementById("home");
  const err = document.getElementById("err");
  const msg = document.getElementById("msg");
  const hint = document.getElementById("hint");
  const drive = document.getElementById("drive");
  const debug = document.getElementById("debug");
  const wrapper = document.querySelector(".wrapper");
  const scaleWrapper = document.querySelector(".scale-wrapper");

  if (!wrapper || !scaleWrapper) {
    console.warn("[500.vue] DOM elements not found");
    return;
  }

  function scaleToFit() {
    const parentWidth = window.innerWidth;
    const parentHeight = window.innerHeight;
    const scaleX = parentWidth / wrapper.offsetWidth;
    const scaleY = parentHeight / wrapper.offsetHeight;
    const scale = Math.min(scaleX, scaleY, 1);
    scaleWrapper.style.transform = `translate(-50%, -50%) scale(${scale})`;
  }

  scaleToFitHandler = scaleToFit;
  window.addEventListener("resize", scaleToFit);
  scaleToFit();

  function popConfetti(x, y) {
    const c = document.createElement("canvas");
    c.width = innerWidth;
    c.height = innerHeight;
    c.style.position = "fixed";
    c.style.left = 0;
    c.style.top = 0;
    c.style.pointerEvents = "none";
    document.body.appendChild(c);
    const ctx = c.getContext("2d");
    const parts = [];
    for (let i = 0; i < 36; i++) {
      parts.push({
        x: x || innerWidth / 2,
        y: y || innerHeight / 2,
        vx: (Math.random() - 0.5) * 8,
        vy: Math.random() * -10 - 2,
        r: Math.random() * 6 + 3,
        life: Math.random() * 60 + 40,
        color: ["#ff6b6b", "#ffd166", "#6bcBFF", "#9be7a9", "#c792ff"][
          Math.floor(Math.random() * 5)
        ]
      });
    }
    (function frame() {
      ctx.clearRect(0, 0, c.width, c.height);
      parts.forEach((p) => {
        p.x += p.vx;
        p.y += p.vy;
        p.vy += 0.35;
        p.life--;
        ctx.beginPath();
        ctx.fillStyle = p.color;
        ctx.ellipse(p.x, p.y, p.r, p.r * 0.7, 0, 0, Math.PI * 2);
        ctx.fill();
      });
      if (parts.some((p) => p.life > 0)) requestAnimationFrame(frame);
      else c.remove();
    })();
  }

  function successChance() {
    return Math.random() > 0.5;
  }

  retry.addEventListener("click", async (e) => {
    retry.disabled = true;
    retry.textContent = "检查中...";
    debug.textContent = "重试中";
    await new Promise((r) => setTimeout(r, 900));
    if (successChance()) {
      err.textContent = "200";
      err.style.color = "#9be7a9";
      msg.textContent = "问题已修复！服务器已恢复正常。";
      hint.textContent = "正在为您重定向...";
      popConfetti(e.clientX, e.clientY);
      debug.textContent = "成功";
      retry.textContent = "成功！";
    } else {
      msg.textContent = "还是不行... 也许服务器打翻了咖啡。";
      hint.textContent = "稍后再试。";
      drive.querySelector(".panel").textContent = ["数据库", "¯\\_(ツ)_/¯", "错误"][
        Math.floor(Math.random() * 3)
      ];
      debug.textContent = "错误持续";
      retry.textContent = "重试";
    }
    retry.disabled = false;
  });

  home.addEventListener("click", () => {
    window.location.href = "/";
  });

  const popup = document.getElementById("popup");
  const closeBtn = document.getElementById("closePopup");
  setTimeout(() => popup.classList.add("show"), 2000);
  const hidePopup = () => popup.classList.remove("show");
  closeBtn.addEventListener("click", hidePopup);
  setTimeout(hidePopup, 15000);
})

onUnmounted(() => {
  if (scaleToFitHandler) {
    window.removeEventListener("resize", scaleToFitHandler);
  }
})

</script>

<style scoped>
.scale-wrapper {
  width: 100%;
  height: 100%;
  position: fixed;
  top: 50%;
  left: 50%;
  transform-origin: center center;
  z-index: 1;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scale-wrapper::before {
  content: '';
  position: fixed;
  inset: 0;
  background: #0f1724;
  z-index: -1;
}

/* Hoofdcontainer */
.wrapper {
  width: 900px;
  max-width: 100%;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.02),
    rgba(255, 255, 255, 0.01)
  );
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(6, 10, 15, 0.6);
  padding: 36px;
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
  align-items: center;
  overflow: hidden;
  border-radius: 24px;
}

/* Left content */
.left h1 {
  margin: 0 0 12px;
  font-size: 48px;
  letter-spacing: -1px;
  color: white; /* H1 wit */
}

.error-num {
  display: inline-block;
  font-weight: 800;
  font-size: 120px;
  color: #ff6b6b;
  line-height: 1;
  animation: bounce 2s infinite cubic-bezier(0.28, 0.84, 0.42, 1);
}

@keyframes bounce {
  0% {
    transform: translateY(0) rotate(-2deg) scale(1);
  }
  18% {
    transform: translateY(-18px) rotate(6deg) scale(1.02);
  }
  40% {
    transform: translateY(0) rotate(-4deg) scale(1);
  }
  100% {
    transform: translateY(0);
  }
}

p.lead {
  color: #9aa6b2;
  margin: 8px 0 18px;
  font-size: 16px;
}

.actions {
  display: flex;
  gap: 12px;
}

button {
  border: 0;
  padding: 12px 18px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.14s ease, box-shadow 0.14s ease;
}

.primary {
  background: linear-gradient(90deg, #ff8a8a, #ff6b6b);
  color: white;
  box-shadow: 0 6px 18px rgba(2, 6, 23, 0.5);
}

.ghost {
  background: transparent;
  color: #9aa6b2;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 30px rgba(255, 107, 107, 0.2);
}

.ghost:hover {
  transform: translateY(-3px);
}

.hint {
  margin-top: 12px;
  color: #9aa6b2;
  font-size: 13px;
}

/* Right illustration */

.drive {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background: linear-gradient(180deg, #0f2433, #071724);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.light {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #5ee3b4;
  position: absolute;
  top: 10px;
  right: 10px;
  box-shadow: 0 4px 12px rgba(94, 227, 180, 0.15);
  animation: blink 1.6s infinite;
}

@keyframes blink {
  0%,
  60% {
    opacity: 1;
  }
  70% {
    opacity: 0.4;
  }
  100% {
    opacity: 1;
  }
}

.panel {
  width: 50px;
  height: 40px;
  border-radius: 6px;
  background: #081a24;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #90aab6;
  font-weight: 700;
  font-size: 14px;
}

.server {
  display: flex;
  align-items: center;
  justify-content: space-between; /* verdeel ruimte gelijk */
  position: relative;
  min-height: 260px;
  flex-wrap: nowrap; /* voorkom stapeling */
  width: 100%; /* zodat schaal-wrapper correct past */
}

.rack {
  width: 300px;
  height: 200px;
  border-radius: 14px;
  background: linear-gradient(180deg, #071025, #0b1624);
  border: 1px solid rgba(255, 255, 255, 0.05);
  padding: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between; /* Drive, Robot, Panels */
}

.robot {
  width: 110px;
  height: 110px;
  border-radius: 14px;
  background: linear-gradient(180deg, #021424, #052432);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9aa6b2;
  box-shadow: 0 18px 40px rgba(2, 6, 15, 0.6);
  animation: tilt 3.4s infinite ease-in-out;
}

.rack > div:last-child {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

@keyframes tilt {
  0% {
    transform: rotate(-2deg);
  }
  50% {
    transform: rotate(4deg);
  }
  100% {
    transform: rotate(-2deg);
  }
}

.foot {
  position: absolute;
  left: 18px;
  bottom: 14px;
  color: #9aa6b2;
  font-size: 12px;
}

/* Popup notification */
.popup {
  position: fixed;
  right: 20px;
  bottom: 20px;
  background: rgba(255, 255, 255, 0.07);
  color: #e6eef6;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.4);
  max-width: 260px;
  opacity: 0;
  transform: translateY(40px);
  transition: opacity 0.6s ease, transform 0.6s ease;
  display: flex;
  align-items: flex-start;
  gap: 10px;
  backdrop-filter: blur(6px);
  z-index: 99;
}
.popup.show {
  opacity: 1;
  transform: translateY(0);
}
.popup button {
  background: none;
  border: 0;
  color: #9aa6b2;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  margin-left: auto;
  transition: color 0.2s;
}
.popup button:hover {
  color: #fff;
}

</style>