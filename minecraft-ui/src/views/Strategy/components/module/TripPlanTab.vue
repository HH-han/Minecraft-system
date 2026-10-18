<template>
  <div class="trip-tab">
    <div class="tab-toolbar">
      <span class="toolbar-title">
        {{ t('strategy.detailTabs.trip') }}
        <em>{{ tripPlan.length }}</em>
      </span>
      <div class="toolbar-actions">
        <button class="btn-secondary" type="button" @click="$emit('generate')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 3l1.8 4.7L18.5 9l-4.7 1.8L12 15.5l-1.8-4.7L5.5 9l4.7-1.3L12 3Z" />
            <path d="M18 15l.9 2.2L21 18l-2.1.9L18 21l-.9-2.1L15 18l2.1-.8L18 15Z" />
          </svg>
          {{ t('strategy.trip.generate') }}
        </button>
        <button class="btn-primary" type="button" @click="$emit('add')">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 5v14M5 12h14" />
          </svg>
          {{ t('strategy.trip.add') }}
        </button>
      </div>
    </div>

    <div v-if="tripPlan.length === 0" class="tab-empty">
      <p>{{ t('strategy.trip.empty') }}</p>
    </div>

    <div v-else class="trip-timeline">
      <div v-for="(day, index) in tripPlan" :key="index" class="trip-day">
        <div class="day-marker">
          <span class="day-number">{{ index + 1 }}</span>
        </div>

        <div class="day-body">
          <div class="day-header">
            <span class="day-label">{{ t('strategy.trip.day', { count: index + 1 }) }}</span>
            <span class="day-date">{{ day.date }}</span>
          </div>

          <div class="day-items">
            <div v-for="item in day.items" :key="item.id" class="trip-item">
              <span class="time">{{ item.time }}</span>
              <div class="item-main">
                <span class="activity">{{ item.activity }}</span>
                <span v-if="item.location" class="location">
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M12 21s7-5.5 7-11a7 7 0 1 0-14 0c0 5.5 7 11 7 11Z" />
                    <circle cx="12" cy="10" r="2.5" />
                  </svg>
                  {{ item.location }}
                </span>
              </div>
              <button v-if="isAdmin" class="delete-btn" type="button" @click="$emit('delete', item.id)">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M6 6l12 12M18 6L6 18" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

defineProps({
  tripPlan: {
    type: Array,
    default: () => []
  },
  isAdmin: {
    type: Boolean,
    default: false
  }
})

defineEmits(['add', 'generate', 'delete'])

const { t } = useI18n()
</script>

<style scoped>
.trip-tab {
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
  margin-bottom: 22px;
  flex-wrap: wrap;
}

.toolbar-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.toolbar-title em {
  padding: 1px 8px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  font-size: 11px;
  font-style: normal;
  font-weight: 600;
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

.trip-timeline {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.trip-day {
  position: relative;
  display: grid;
  grid-template-columns: 40px 1fr;
  gap: 16px;
}

.trip-day::before {
  content: '';
  position: absolute;
  left: 19px;
  top: 40px;
  bottom: -22px;
  width: 2px;
  background: var(--color-border);
}

.trip-day:last-child::before {
  display: none;
}

.day-marker {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--theme-primary-soft);
  border: 2px solid var(--color-primary);
  color: var(--color-primary);
  font-size: 15px;
  font-weight: 700;
}

.day-body {
  min-width: 0;
}

.day-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 12px;
  padding-top: 8px;
}

.day-label {
  font-size: 15px;
  font-weight: 650;
  color: var(--color-text);
}

.day-date {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.day-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.trip-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  background: var(--color-bg-secondary);
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  transition: background var(--transition-normal), border-color var(--transition-normal);
}

.trip-item:hover {
  background: var(--theme-bg-surface);
  border-color: var(--color-border);
}

.trip-item .time {
  min-width: 56px;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
}

.item-main {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: baseline;
  flex-wrap: wrap;
  gap: 4px 12px;
}

.activity {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.location {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.location svg {
  width: 12px;
  height: 12px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.delete-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 26px;
  height: 26px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: background var(--transition-fast), color var(--transition-fast);
}

.delete-btn svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
}

.delete-btn:hover {
  background: var(--theme-danger-soft);
  color: var(--color-danger);
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

  .trip-day {
    grid-template-columns: 32px 1fr;
    gap: 12px;
  }

  .day-marker {
    width: 32px;
    height: 32px;
    font-size: 13px;
  }

  .trip-day::before {
    left: 15px;
    top: 32px;
  }

  .trip-item {
    padding: 10px 12px;
    gap: 10px;
  }

  .trip-item .time {
    min-width: 44px;
  }
}
</style>
