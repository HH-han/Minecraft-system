<template>
  <section class="group-hero">
    <div class="hero-art" aria-hidden="true">
      <span class="orb orb-a"></span>
      <span class="orb orb-b"></span>
      <span class="hero-grid"></span>
    </div>

    <div class="hero-content">
      <span class="hero-eyebrow">
        <i class="eyebrow-line"></i>
        {{ t('strategy.hero.eyebrow') }}
      </span>
      <h1>{{ t('strategy.hero.title') }}</h1>
      <p class="hero-subtitle">{{ t('strategy.hero.subtitle') }}</p>

      <div class="hero-actions">
        <button class="btn-primary" type="button" @click="$emit('create')">
          <span>{{ t('strategy.hero.create') }}</span>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M5 12h14M13 6l6 6-6 6" />
          </svg>
        </button>
        <button class="btn-secondary" type="button" @click="$emit('join')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="11" cy="11" r="7" />
            <path d="M20 20l-3.6-3.6" />
          </svg>
          <span>{{ t('strategy.hero.join') }}</span>
        </button>
      </div>

      <ul class="hero-features">
        <li>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M4 7h16M4 12h10M4 17h7" />
          </svg>
          {{ t('strategy.hero.featureTrip') }}
        </li>
        <li>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 3v18M7 7h7a3 3 0 0 1 0 6H7" />
          </svg>
          {{ t('strategy.hero.featureBill') }}
        </li>
        <li>
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <rect x="3" y="5" width="18" height="14" rx="3" />
            <circle cx="12" cy="12" r="3" />
          </svg>
          {{ t('strategy.hero.featureAlbum') }}
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

defineEmits(['create', 'join'])

const { t } = useI18n()
</script>

<style scoped>
.group-hero {
  position: relative;
  overflow: hidden;
  padding: 72px 56px;
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  background:
    radial-gradient(120% 140% at 0% 0%, var(--theme-primary-soft) 0%, transparent 55%),
    linear-gradient(145deg, var(--theme-bg-surface-2) 0%, var(--theme-bg-surface) 100%);
  isolation: isolate;
}

.hero-art {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.55;
}

.orb-a {
  width: 420px;
  height: 420px;
  top: -180px;
  right: -80px;
  background: var(--theme-primary);
  opacity: 0.16;
}

.orb-b {
  width: 300px;
  height: 300px;
  bottom: -160px;
  left: 12%;
  background: var(--theme-accent-2);
  opacity: 0.12;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(var(--theme-hairline) 1px, transparent 1px),
    linear-gradient(90deg, var(--theme-hairline) 1px, transparent 1px);
  background-size: 64px 64px;
  mask-image: radial-gradient(90% 80% at 70% 20%, #000 0%, transparent 75%);
  opacity: 0.5;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 720px;
  animation: hero-rise 0.7s var(--theme-ease) both;
}

@keyframes hero-rise {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hero-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: var(--color-primary);
}

.eyebrow-line {
  width: 28px;
  height: 1px;
  background: var(--color-primary);
  opacity: 0.6;
}

.hero-content h1 {
  margin: 18px 0 16px;
  font-size: clamp(38px, 5.4vw, 68px);
  font-weight: 800;
  line-height: 1.04;
  letter-spacing: -0.035em;
  color: var(--color-text);
}

.hero-subtitle {
  max-width: 520px;
  font-size: 16px;
  line-height: 1.7;
  color: var(--color-text-secondary);
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 14px;
  margin-top: 32px;
}

.hero-actions button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 13px 26px;
  border: 1px solid transparent;
  border-radius: var(--radius-full);
  font-size: 15px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: transform var(--transition-fast), background var(--transition-normal),
    box-shadow var(--transition-normal), border-color var(--transition-normal);
}

.hero-actions button svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: transform var(--transition-fast);
}

.hero-actions .btn-primary {
  background: var(--color-primary);
  color: var(--theme-primary-text);
  box-shadow: 0 12px 28px -14px var(--color-primary);
}

.hero-actions .btn-primary:hover {
  background: var(--color-primary-hover);
  transform: translateY(-2px);
}

.hero-actions .btn-primary:hover svg {
  transform: translateX(3px);
}

.hero-actions .btn-secondary {
  background: var(--theme-bg-surface);
  color: var(--color-text);
  border-color: var(--color-border);
}

.hero-actions .btn-secondary:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  transform: translateY(-2px);
}

.hero-actions button:active {
  transform: scale(0.97);
}

.hero-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 24px;
  margin: 36px 0 0;
  padding: 0;
  list-style: none;
}

.hero-features li {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-secondary);
}

.hero-features svg {
  width: 16px;
  height: 16px;
  fill: none;
  stroke: var(--color-primary);
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

@media (max-width: 1199px) {
  .group-hero {
    padding: 56px 40px;
  }
}

@media (max-width: 767px) {
  .group-hero {
    padding: 40px 24px;
    border-radius: var(--radius-lg);
  }

  .hero-content h1 {
    margin: 14px 0 12px;
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 15px;
  }

  .hero-actions {
    margin-top: 24px;
    gap: 10px;
  }

  .hero-actions button {
    flex: 1;
    justify-content: center;
    padding: 12px 18px;
  }

  .hero-features {
    margin-top: 24px;
    gap: 8px 16px;
  }
}
</style>
