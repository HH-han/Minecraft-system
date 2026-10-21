<template>
  <div class="error-page" :class="`tone-${tone}`">
    <!-- 背景装饰 -->
    <div class="backdrop" aria-hidden="true">
      <div class="aurora aurora--accent"></div>
      <div class="aurora aurora--primary"></div>
      <div class="grid"></div>
    </div>

    <div class="shell">
      <!-- 顶部：品牌 + 状态 -->
      <header class="topbar">
        <div class="brand">
          <span class="brand__mark" aria-hidden="true">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 2.5 3.5 7v10L12 21.5 20.5 17V7Z" />
              <path d="M3.5 7 12 12l8.5-5" />
              <path d="M12 12v9.5" />
            </svg>
          </span>
          <span class="brand__name">{{ $t('common.brand') }}</span>
        </div>

        <span class="status">
          <span class="status__dot" aria-hidden="true"></span>
          <span class="status__text">{{ badge }}</span>
        </span>
      </header>

      <div class="layout">
        <!-- 左侧：错误信息与操作 -->
        <section class="info">
          <p class="code" aria-hidden="true">{{ code }}</p>
          <h1 class="headline">{{ title }}</h1>
          <p class="desc">{{ desc }}</p>

          <div class="actions">
            <button type="button" class="btn btn--primary" @click="$emit('primary')">
              {{ primaryLabel }}
            </button>
            <button type="button" class="btn btn--ghost" @click="$emit('secondary')">
              {{ secondaryLabel }}
            </button>
          </div>

          <p v-if="footnote" class="footnote">{{ footnote }}</p>
        </section>

        <!-- 右侧：状态可视区 -->
        <aside class="stage" aria-hidden="true">
          <div class="stage__pulse">
            <div class="stage__halo"></div>
            <div class="stage__ring stage__ring--outer"></div>
            <div class="stage__ring stage__ring--mid"></div>
            <div class="stage__ring stage__ring--inner"></div>
            <div class="stage__icon">
              <slot name="visual" />
            </div>
          </div>

          <div class="stage__caption">
            <span class="stage__caption-code">HTTP {{ code }}</span>
            <span class="stage__caption-sep" aria-hidden="true"></span>
            <span class="stage__caption-text">{{ caption }}</span>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  /** HTTP 状态码 */
  code: { type: [String, Number], required: true },
  /** 视觉主色调：warning / danger / info / accent */
  tone: { type: String, default: 'info' },
  /** 顶部状态徽标文案 */
  badge: { type: String, default: '' },
  /** 图标下方的补充说明 */
  caption: { type: String, default: '' },
  title: { type: String, default: '' },
  desc: { type: String, default: '' },
  primaryLabel: { type: String, default: '' },
  secondaryLabel: { type: String, default: '' },
  footnote: { type: String, default: '' }
})

defineEmits(['primary', 'secondary'])
</script>

<style scoped>
/* ============================================================
   主题色映射：所有配色统一走 --acc，随主题切换自动变化
   ============================================================ */
.error-page {
  --acc: var(--theme-info);
  --acc-soft: var(--theme-info-soft);
  position: relative;
  min-height: 100vh;
  min-height: 100dvh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(24px, 5vw, 64px);
  overflow: hidden;
  background: var(--theme-bg-app);
  color: var(--theme-text-1);
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Segoe UI', system-ui, sans-serif;
}

.tone-warning {
  --acc: var(--theme-warning);
  --acc-soft: var(--theme-warning-soft);
}

.tone-danger {
  --acc: var(--theme-danger);
  --acc-soft: var(--theme-danger-soft);
}

.tone-info {
  --acc: var(--theme-info);
  --acc-soft: var(--theme-info-soft);
}

.tone-accent {
  --acc: var(--theme-accent-2);
  --acc-soft: color-mix(in srgb, var(--theme-accent-2) 14%, transparent);
}

/* ============================================================
   背景：光晕 + 网格
   ============================================================ */
.backdrop {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.aurora {
  position: absolute;
  width: min(760px, 95vw);
  aspect-ratio: 1;
  border-radius: 50%;
  filter: blur(140px);
}

.aurora--accent {
  top: -34%;
  right: -16%;
  background: var(--acc);
  opacity: 0.26;
  animation: drift 18s ease-in-out infinite alternate;
}

.aurora--primary {
  bottom: -42%;
  left: -20%;
  background: var(--theme-primary);
  opacity: 0.16;
  animation: drift 24s ease-in-out infinite alternate-reverse;
}

@keyframes drift {
  from {
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    transform: translate3d(50px, 40px, 0) scale(1.12);
  }
}

.grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--theme-hairline) 1px, transparent 1px),
    linear-gradient(90deg, var(--theme-hairline) 1px, transparent 1px);
  background-size: 56px 56px;
  -webkit-mask-image: radial-gradient(ellipse at 50% 45%, #000 18%, transparent 72%);
  mask-image: radial-gradient(ellipse at 50% 45%, #000 18%, transparent 72%);
  opacity: 0.6;
}

/* ============================================================
   顶部栏
   ============================================================ */
.shell {
  position: relative;
  z-index: 1;
  width: min(1080px, 100%);
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: clamp(32px, 7vh, 72px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.brand__mark {
  width: 34px;
  height: 34px;
  display: grid;
  place-items: center;
  border-radius: 11px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  box-shadow: var(--theme-shadow-sm);
  flex-shrink: 0;
}

.brand__mark svg {
  width: 18px;
  height: 18px;
}

.brand__name {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 7px 14px;
  border-radius: var(--theme-radius-pill);
  background: var(--acc-soft);
  border: 1px solid color-mix(in srgb, var(--acc) 30%, transparent);
  color: var(--acc);
  flex-shrink: 0;
}

.status__dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: currentColor;
  animation: dot-pulse 2.4s ease-out infinite;
}

@keyframes dot-pulse {
  0% {
    box-shadow: 0 0 0 0 color-mix(in srgb, var(--acc) 45%, transparent);
  }
  70% {
    box-shadow: 0 0 0 7px transparent;
  }
  100% {
    box-shadow: 0 0 0 0 transparent;
  }
}

.status__text {
  font-size: 11.5px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

/* ============================================================
   主体布局
   ============================================================ */
.layout {
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(0, 0.95fr);
  gap: clamp(32px, 6vw, 80px);
  align-items: center;
}

.info {
  min-width: 0;
}

.info > * {
  animation: rise 0.7s var(--theme-ease) both;
}

.code {
  margin: 0 0 18px;
  font-size: clamp(88px, 11vw, 152px);
  font-weight: 300;
  line-height: 0.86;
  letter-spacing: -0.045em;
  font-variant-numeric: tabular-nums;
  background: linear-gradient(
    150deg,
    var(--theme-text-1) 22%,
    color-mix(in srgb, var(--acc) 62%, var(--theme-text-1)) 100%
  );
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  user-select: none;
  animation-delay: 0.04s;
}

.headline {
  margin: 0 0 14px;
  font-size: clamp(24px, 2.6vw, 32px);
  font-weight: 600;
  letter-spacing: 0.004em;
  color: var(--theme-text-1);
  animation-delay: 0.1s;
}

.desc {
  margin: 0;
  max-width: 42ch;
  font-size: 15.5px;
  line-height: 1.8;
  color: var(--theme-text-2);
  animation-delay: 0.16s;
}

/* 操作按钮 */
.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 30px;
  animation-delay: 0.22s;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 46px;
  padding: 0 26px;
  border-radius: var(--theme-radius-pill);
  border: 1px solid transparent;
  font-size: 14.5px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition:
    transform var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease),
    background var(--theme-duration-fast) var(--theme-ease),
    border-color var(--theme-duration-fast) var(--theme-ease);
}

.btn:focus-visible {
  outline: none;
  box-shadow: var(--theme-ring);
}

.btn--primary {
  background: var(--theme-text-1);
  color: var(--theme-text-inverse);
}

.btn--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 30px -14px color-mix(in srgb, var(--theme-text-1) 55%, transparent);
}

.btn--primary:active {
  transform: translateY(0);
}

.btn--ghost {
  background: transparent;
  border-color: var(--theme-border);
  color: var(--theme-text-1);
}

.btn--ghost:hover {
  background: var(--theme-bg-hover);
  border-color: var(--theme-border-strong);
  transform: translateY(-2px);
}

.btn--ghost:active {
  transform: translateY(0);
}

.footnote {
  margin: 22px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--theme-text-3);
  animation-delay: 0.28s;
}

@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============================================================
   右侧：状态可视区
   ============================================================ */
.stage {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 26px;
  justify-self: center;
  animation: rise 0.8s var(--theme-ease) 0.12s both;
}

.stage__pulse {
  position: relative;
  width: min(360px, 66vw);
  aspect-ratio: 1;
  display: grid;
  place-items: center;
}

.stage__halo {
  position: absolute;
  inset: 14%;
  border-radius: 50%;
  background: radial-gradient(circle, var(--acc-soft) 0%, transparent 70%);
  animation: halo-breathe 5s ease-in-out infinite;
}

@keyframes halo-breathe {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.9;
  }
  50% {
    transform: scale(1.08);
    opacity: 1;
  }
}

.stage__ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid color-mix(in srgb, var(--acc) 26%, transparent);
}

.stage__ring--outer {
  inset: 0;
  border-style: dashed;
  border-color: color-mix(in srgb, var(--acc) 30%, transparent);
  animation: spin 32s linear infinite;
}

.stage__ring--outer::after {
  content: '';
  position: absolute;
  top: -3px;
  left: 50%;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--acc);
  box-shadow: 0 0 12px var(--acc);
}

.stage__ring--mid {
  inset: 13%;
  opacity: 0.75;
}

.stage__ring--inner {
  inset: 26%;
  opacity: 0.5;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.stage__icon {
  position: relative;
  z-index: 1;
  width: 148px;
  height: 148px;
  display: grid;
  place-items: center;
  border-radius: 36px;
  background: var(--theme-bg-elevated);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-lg);
  color: var(--acc);
  animation: float 6s ease-in-out infinite;
}

.stage__icon :deep(svg) {
  width: 68px;
  height: 68px;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.stage__caption {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 7px 16px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-muted);
  border: 1px solid var(--theme-hairline);
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
  font-size: 11.5px;
  letter-spacing: 0.06em;
  color: var(--theme-text-3);
  max-width: 100%;
}

.stage__caption-code {
  color: var(--acc);
  font-weight: 700;
}

.stage__caption-sep {
  width: 1px;
  height: 11px;
  background: var(--theme-border);
  flex-shrink: 0;
}

.stage__caption-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ============================================================
   响应式
   ============================================================ */
@media (max-width: 900px) {
  .layout {
    grid-template-columns: 1fr;
    gap: 32px;
    text-align: center;
  }

  .stage {
    order: -1;
    gap: 18px;
  }

  .stage__pulse {
    width: min(240px, 58vw);
  }

  .stage__icon {
    width: 108px;
    height: 108px;
    border-radius: 28px;
  }

  .stage__icon :deep(svg) {
    width: 50px;
    height: 50px;
  }

  .desc {
    margin-inline: auto;
  }

  .actions {
    justify-content: center;
  }

  .topbar {
    margin-bottom: 28px;
  }
}

@media (max-width: 520px) {
  .error-page {
    padding: 20px 18px 32px;
  }

  .code {
    margin-bottom: 12px;
  }

  .actions {
    gap: 10px;
  }

  .btn {
    flex: 1 1 100%;
  }

  .status {
    padding: 6px 11px;
  }

  .status__text {
    font-size: 10.5px;
    letter-spacing: 0.08em;
  }

  .brand__name {
    font-size: 14px;
  }
}

/* 无障碍：减少动态效果 */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation: none !important;
    transition: none !important;
  }
}
</style>
