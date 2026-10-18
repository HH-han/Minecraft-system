<template>
  <div class="diary-tab">
    <div class="tab-toolbar">
      <span class="toolbar-title">
        {{ t('strategy.detailTabs.diary') }}
      </span>
      <div class="toolbar-actions">
        <button v-if="diary" class="btn-secondary" type="button" @click="$emit('export')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 4v12M7 11l5 5 5-5" />
            <path d="M5 20h14" />
          </svg>
          {{ t('strategy.diary.export') }}
        </button>
        <button class="btn-primary" type="button" @click="$emit('generate')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 3l1.8 4.7L18.5 9l-4.7 1.8L12 15.5l-1.8-4.7L5.5 9l4.7-1.3L12 3Z" />
          </svg>
          {{ t('strategy.diary.generate') }}
        </button>
      </div>
    </div>

    <div v-if="!diary" class="tab-empty">
      <p>{{ t('strategy.diary.empty') }}</p>
    </div>

    <article v-else class="diary-content">
      <h3 class="diary-title">{{ diary.title }}</h3>

      <div class="diary-sections">
        <section v-for="section in diary.sections" :key="section.date" class="diary-section">
          <h4>
            <span class="section-dot"></span>
            {{ section.date }}
          </h4>
          <p>{{ section.content }}</p>
          <div v-if="section.photos?.length" class="diary-photos">
            <img v-for="photo in section.photos" :key="photo" :src="photo" alt="" loading="lazy" />
          </div>
        </section>
      </div>
    </article>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

defineProps({
  diary: {
    type: Object,
    default: null
  }
})

defineEmits(['generate', 'export'])

const { t } = useI18n()
</script>

<style scoped>
.diary-tab {
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.tab-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.toolbar-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.toolbar-actions button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid transparent;
  border-radius: var(--radius-full);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), border-color var(--transition-fast),
    transform var(--transition-fast);
}

.toolbar-actions svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.btn-primary {
  background: var(--color-primary);
  color: var(--theme-primary-text);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-secondary {
  background: var(--theme-bg-surface);
  border-color: var(--color-border);
  color: var(--color-text);
}

.btn-secondary:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.toolbar-actions button:active {
  transform: scale(0.97);
}

.diary-content {
  padding: 26px 28px 30px;
  background:
    radial-gradient(90% 120% at 100% 0%, var(--theme-primary-soft) 0%, transparent 55%),
    var(--theme-bg-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.diary-title {
  font-size: 22px;
  font-weight: 750;
  letter-spacing: -0.02em;
  line-height: 1.3;
  color: var(--color-text);
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.diary-sections {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.diary-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 650;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.section-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-primary);
}

.diary-section p {
  font-size: 14px;
  line-height: 1.85;
  color: var(--color-text);
  white-space: pre-wrap;
}

.diary-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.diary-photos img {
  width: 96px;
  height: 96px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  transition: transform var(--transition-normal);
}

.diary-photos img:hover {
  transform: scale(1.04);
}

.tab-empty {
  padding: 44px;
  text-align: center;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 767px) {
  .tab-toolbar {
    align-items: flex-start;
    flex-direction: column;
    gap: 12px;
  }

  .toolbar-actions {
    width: 100%;
  }

  .toolbar-actions button {
    flex: 1;
    justify-content: center;
  }

  .diary-content {
    padding: 18px;
  }

  .diary-title {
    font-size: 18px;
  }

  .diary-photos img {
    width: 76px;
    height: 76px;
  }
}
</style>
