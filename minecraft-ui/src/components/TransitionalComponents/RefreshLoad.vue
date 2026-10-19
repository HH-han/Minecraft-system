<template>
    <div class="refresh-load" role="status" aria-live="polite">
        <!-- 氛围层：随主题色变化的极光渐变 -->
        <div class="refresh-load__aura" aria-hidden="true"></div>

        <main class="refresh-load__stage">
            <!-- 旅行罗盘：刻度环 + 双向弧线 + 轨道光点 + 球体核心 -->
            <div class="rl-loader" aria-hidden="true">
                <span class="rl-ticks"></span>
                <span class="rl-arc rl-arc--outer"></span>
                <span class="rl-arc rl-arc--inner"></span>
                <span class="rl-orbit"><i class="rl-orbit__dot"></i></span>
                <span class="rl-core">
                    <svg class="rl-core__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                        stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="9" />
                        <path d="M3 12h18" />
                        <path d="M12 3c2.6 2.5 4 5.6 4 9s-1.4 6.5-4 9c-2.6-2.5-4-5.6-4-9s1.4-6.5 4-9Z" />
                    </svg>
                </span>
            </div>

            <h1 class="rl-brand">{{ $t('common.brand') }}</h1>
            <p class="rl-tagline">{{ $t('common.tagline') }}</p>

            <div class="rl-progress" aria-hidden="true">
                <span class="rl-progress__bar"></span>
            </div>
            <p class="rl-status">{{ $t('common.loading') }}</p>
        </main>
    </div>
</template>

<style scoped>
/* ============================================================
   全屏容器
   ============================================================ */
.refresh-load {
    position: fixed;
    inset: 0;
    z-index: 3000;
    display: grid;
    place-items: center;
    overflow: hidden;
    isolation: isolate;
    background: var(--theme-bg-app);
    color: var(--theme-text-1);
}

/* 极光渐变氛围：主色 / 辅助色随主题切换 */
.refresh-load__aura {
    position: absolute;
    inset: -25%;
    background:
        radial-gradient(38% 40% at 20% 18%, var(--theme-primary-soft), transparent 68%),
        radial-gradient(34% 36% at 82% 24%, color-mix(in srgb, var(--theme-accent-2) 26%, transparent), transparent 70%),
        radial-gradient(42% 44% at 52% 96%, color-mix(in srgb, var(--theme-accent-3) 20%, transparent), transparent 72%);
    filter: blur(24px);
    animation: rl-aura 14s ease-in-out infinite alternate;
}

/* 细网格纹理：中心清晰、四周淡出 */
.refresh-load::after {
    content: '';
    position: absolute;
    inset: 0;
    pointer-events: none;
    background-image:
        linear-gradient(var(--theme-hairline) 1px, transparent 1px),
        linear-gradient(90deg, var(--theme-hairline) 1px, transparent 1px);
    background-size: 64px 64px;
    -webkit-mask-image: radial-gradient(circle at 50% 46%, #000 0%, transparent 66%);
    mask-image: radial-gradient(circle at 50% 46%, #000 0%, transparent 66%);
    opacity: 0.55;
}

/* ============================================================
   主内容：单列居中，节奏统一
   ============================================================ */
.refresh-load__stage {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px;
    text-align: center;
}

/* ============================================================
   加载动画主体
   ============================================================ */
.rl-loader {
    position: relative;
    display: grid;
    place-items: center;
    width: clamp(116px, 24vmin, 172px);
    aspect-ratio: 1;
    margin-bottom: clamp(26px, 4.5vh, 42px);
    animation: rl-rise 0.9s var(--theme-ease) both;
}

/* 刻度环 */
.rl-ticks {
    position: absolute;
    inset: 2%;
    border-radius: 50%;
    background: repeating-conic-gradient(from 0deg,
            var(--theme-border-strong) 0deg 1.2deg,
            transparent 1.2deg 15deg);
    -webkit-mask: radial-gradient(farthest-side, transparent calc(100% - 5px), #000 calc(100% - 4px));
    mask: radial-gradient(farthest-side, transparent calc(100% - 5px), #000 calc(100% - 4px));
    opacity: 0.5;
    animation: rl-spin 48s linear infinite;
}

/* 弧线环：外圈主色，内圈辅助色反向旋转 */
.rl-arc {
    position: absolute;
    border-radius: 50%;
    -webkit-mask: radial-gradient(farthest-side, transparent calc(100% - 2.4px), #000 calc(100% - 1.4px));
    mask: radial-gradient(farthest-side, transparent calc(100% - 2.4px), #000 calc(100% - 1.4px));
}

.rl-arc--outer {
    inset: 0;
    background: conic-gradient(from 90deg,
            transparent 0deg,
            transparent 205deg,
            var(--theme-primary) 330deg,
            transparent 360deg);
    animation: rl-spin 1.6s cubic-bezier(0.6, 0.15, 0.35, 0.9) infinite;
}

.rl-arc--inner {
    inset: 15%;
    background: conic-gradient(from 270deg,
            transparent 0deg,
            transparent 250deg,
            var(--theme-accent-2) 345deg,
            transparent 360deg);
    opacity: 0.75;
    animation: rl-spin 2.6s linear infinite reverse;
}

/* 轨道光点 */
.rl-orbit {
    position: absolute;
    inset: 0;
    animation: rl-spin 4.5s linear infinite;
}

.rl-orbit__dot {
    position: absolute;
    top: -4px;
    left: 50%;
    width: 8px;
    height: 8px;
    margin-left: -4px;
    border-radius: 50%;
    background: var(--theme-primary);
    box-shadow: 0 0 0 4px var(--theme-primary-soft), 0 0 12px var(--theme-primary);
}

/* 球体核心 */
.rl-core {
    position: relative;
    display: grid;
    place-items: center;
    width: 46%;
    aspect-ratio: 1;
    border-radius: 50%;
    color: var(--theme-primary);
    background: var(--theme-bg-surface);
    border: 1px solid var(--theme-border);
    box-shadow: var(--theme-shadow-md), inset 0 1px 0 rgba(255, 255, 255, 0.35);
    backdrop-filter: blur(8px);
    animation: rl-pulse 2.8s ease-in-out infinite;
}

.rl-core__icon {
    width: 52%;
    height: 52%;
}

/* ============================================================
   文案与进度
   ============================================================ */
.rl-brand {
    margin: 0;
    font-size: clamp(26px, 4.4vw, 40px);
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: 0.18em;
    background: var(--theme-gradient);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    animation: rl-rise 0.9s 0.12s var(--theme-ease) both;
}

.rl-tagline {
    display: flex;
    align-items: center;
    gap: 14px;
    margin: clamp(10px, 1.6vh, 14px) 0 0;
    font-size: clamp(12px, 1.4vw, 14px);
    letter-spacing: 0.24em;
    color: var(--theme-text-3);
    animation: rl-rise 0.9s 0.2s var(--theme-ease) both;
}

.rl-tagline::before,
.rl-tagline::after {
    content: '';
    width: clamp(18px, 4vw, 34px);
    height: 1px;
}

.rl-tagline::before {
    background: linear-gradient(90deg, transparent, var(--theme-border-strong));
}

.rl-tagline::after {
    background: linear-gradient(270deg, transparent, var(--theme-border-strong));
}

.rl-progress {
    position: relative;
    overflow: hidden;
    width: clamp(160px, 24vw, 220px);
    height: 3px;
    margin-top: clamp(26px, 4vh, 38px);
    border-radius: 999px;
    background: var(--theme-bg-muted);
    animation: rl-rise 0.9s 0.28s var(--theme-ease) both;
}

.rl-progress__bar {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    width: 42%;
    border-radius: inherit;
    background: var(--theme-gradient);
    animation: rl-slide 1.5s var(--theme-ease) infinite;
}

.rl-status {
    margin: clamp(12px, 1.8vh, 16px) 0 0;
    font-size: 11px;
    letter-spacing: 0.28em;
    text-transform: uppercase;
    color: var(--theme-text-3);
    animation: rl-rise 0.9s 0.36s var(--theme-ease) both;
}

/* ============================================================
   动画
   ============================================================ */
@keyframes rl-spin {
    to {
        transform: rotate(360deg);
    }
}

@keyframes rl-aura {
    from {
        transform: translate3d(-2%, -1%, 0) scale(1);
    }

    to {
        transform: translate3d(2%, 2%, 0) scale(1.08);
    }
}

@keyframes rl-pulse {

    0%,
    100% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.045);
    }
}

@keyframes rl-rise {
    from {
        opacity: 0;
        transform: translateY(14px);
    }

    to {
        opacity: 1;
        transform: none;
    }
}

@keyframes rl-slide {
    from {
        transform: translateX(-110%);
    }

    to {
        transform: translateX(260%);
    }
}

/* 尊重系统减弱动效设置 */
@media (prefers-reduced-motion: reduce) {
    .refresh-load *,
    .refresh-load::after {
        animation-duration: 0.01ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.01ms !important;
    }
}
</style>
