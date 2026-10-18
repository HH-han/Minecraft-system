<template>
  <div class="tab-content">
    <div class="section-head">
      <div class="head-copy">
        <span class="eyebrow">{{ t('strategy.notifications.eyebrow') }}</span>
        <h2>{{ t('strategy.notifications.title') }}</h2>
        <p class="head-sub">{{ t('strategy.notifications.subtitle') }}</p>
      </div>
      <button v-if="notifications.length > 0" class="mark-all-btn" type="button" @click="$emit('markAllRead')">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M4 13l4 4L19 7" />
        </svg>
        {{ t('strategy.notifications.markAll') }}
      </button>
    </div>

    <div v-if="notifications.length === 0" class="empty-state">
      <div class="empty-art">
        <svg viewBox="0 0 120 120" aria-hidden="true">
          <circle cx="60" cy="60" r="54" />
          <path d="M78 52a18 18 0 1 0-36 0c0 18-7 21-7 21h50s-7-3-7-21" />
          <path d="M53 86a8 8 0 0 0 14 0" />
        </svg>
      </div>
      <h3>{{ t('strategy.notifications.emptyTitle') }}</h3>
      <p>{{ t('strategy.notifications.emptyDesc') }}</p>
    </div>

    <div v-else class="notification-list">
      <div
        v-for="item in notifications"
        :key="item.id"
        :class="['notification-item', { unread: !item.isRead }]"
        @click="$emit('handle', item)"
      >
        <div :class="['notification-icon', item.type]">
          <svg v-if="item.type === 'group_invite'" viewBox="0 0 24 24">
            <circle cx="9" cy="8" r="3.2" />
            <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
            <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
          </svg>
          <svg v-else-if="item.type === 'join_request'" viewBox="0 0 24 24">
            <path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h9a2 2 0 0 0 2-2v-7" />
            <path d="M18 2v6M15 5h6" />
          </svg>
          <svg v-else-if="item.type === 'post_like'" viewBox="0 0 24 24">
            <path d="M12 20s-7-4.6-7-9.6A4.4 4.4 0 0 1 12 7a4.4 4.4 0 0 1 7 3.4c0 5-7 9.6-7 9.6Z" />
          </svg>
          <svg v-else-if="item.type === 'post_comment'" viewBox="0 0 24 24">
            <path d="M21 12a8 8 0 0 1-8 8H7l-4 3V12a8 8 0 0 1 8-8h2a8 8 0 0 1 8 8Z" />
          </svg>
          <svg v-else viewBox="0 0 24 24">
            <path d="M18 8a6 6 0 1 0-12 0c0 6-2.5 7-2.5 7h17S18 14 18 8" />
            <path d="M10.3 20a2 2 0 0 0 3.4 0" />
          </svg>
        </div>

        <div class="notification-content">
          <div class="notification-top">
            <p class="notification-title">{{ item.title }}</p>
            <span class="notification-time">{{ formatTime(item.createdAt) }}</span>
          </div>
          <p class="notification-message">{{ item.message }}</p>
        </div>

        <button
          v-if="!item.isRead"
          class="read-btn"
          type="button"
          :title="t('strategy.notifications.markRead')"
          :aria-label="t('strategy.notifications.markRead')"
          @click.stop="$emit('markAsRead', item.id)"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M5 13l4 4L19 7" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

defineProps({
  notifications: {
    type: Array,
    default: () => []
  }
})

defineEmits(['markAllRead', 'handle', 'markAsRead'])

const { t, locale } = useI18n()

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return t('strategy.notifications.justNow')
  if (diff < 3600000) return t('strategy.notifications.minutesAgo', { count: Math.floor(diff / 60000) })
  if (diff < 86400000) return t('strategy.notifications.hoursAgo', { count: Math.floor(diff / 3600000) })
  return date.toLocaleDateString(locale.value)
}
</script>

<style scoped>
.tab-content {
  animation: fade-in var(--transition-normal);
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 30px;
}

.eyebrow {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.24em;
  color: var(--color-primary);
  margin-bottom: 8px;
}

.head-copy h2 {
  font-size: clamp(24px, 2.6vw, 32px);
  font-weight: 750;
  line-height: 1.15;
  letter-spacing: -0.02em;
  color: var(--color-text);
}

.head-sub {
  margin-top: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.mark-all-btn {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 9px 18px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  background: var(--theme-bg-surface);
  color: var(--color-text-secondary);
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: color var(--transition-fast), border-color var(--transition-fast),
    background var(--transition-fast);
}

.mark-all-btn svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.mark-all-btn:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
  background: var(--theme-primary-soft);
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification-item {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 18px 20px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  overflow: hidden;
  transition: border-color var(--transition-normal), background var(--transition-normal),
    transform var(--transition-normal);
}

.notification-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: var(--color-primary);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.notification-item.unread::before {
  opacity: 1;
}

.notification-item.unread {
  background: var(--theme-primary-soft);
  border-color: var(--color-primary);
}

.notification-item:hover {
  transform: translateX(3px);
  border-color: var(--theme-border-strong);
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 14px;
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
}

.notification-icon svg {
  width: 20px;
  height: 20px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.notification-icon.group_invite {
  background: var(--theme-primary-soft);
  color: var(--color-primary);
}

.notification-icon.join_request {
  background: var(--theme-warning-soft);
  color: var(--color-warning);
}

.notification-icon.post_like {
  background: var(--theme-danger-soft);
  color: var(--color-danger);
}

.notification-icon.post_comment {
  background: var(--theme-success-soft);
  color: var(--color-success);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-top {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 4px;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
}

.notification-message {
  font-size: 13px;
  line-height: 1.6;
  color: var(--color-text-secondary);
}

.notification-time {
  flex-shrink: 0;
  font-size: 11px;
  color: var(--color-text-secondary);
  opacity: 0.85;
}

.read-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  background: var(--color-primary);
  color: var(--theme-primary-text);
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.read-btn svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.4;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.read-btn:hover {
  background: var(--color-primary-hover);
}

.read-btn:active {
  transform: scale(0.92);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 64px 32px;
  background: var(--color-bg-secondary);
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-xl);
}

.empty-art svg {
  width: 96px;
  height: 96px;
  margin-bottom: 20px;
  fill: none;
  stroke: var(--color-primary);
  stroke-width: 1.6;
  stroke-linecap: round;
  stroke-linejoin: round;
  opacity: 0.7;
}

.empty-state h3 {
  font-size: 19px;
  font-weight: 650;
  color: var(--color-text);
  margin-bottom: 6px;
}

.empty-state p {
  font-size: 14px;
  color: var(--color-text-secondary);
}

@media (max-width: 767px) {
  .section-head {
    align-items: stretch;
    flex-direction: column;
    gap: 14px;
    margin-bottom: 20px;
  }

  .mark-all-btn {
    align-self: flex-start;
  }

  .notification-item {
    padding: 14px;
    gap: 12px;
  }

  .notification-icon {
    width: 38px;
    height: 38px;
    border-radius: 12px;
  }

  .notification-top {
    flex-direction: column;
    gap: 2px;
  }

  .empty-state {
    padding: 44px 22px;
  }
}
</style>
