<template>
  <div class="members-tab">
    <div class="tab-toolbar">
      <span class="toolbar-title">
        {{ t('strategy.detailTabs.members') }}
        <em>{{ members.length }}</em>
      </span>
      <button v-if="isAdmin" class="btn-primary btn-sm" type="button" @click="$emit('invite')">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M12 5v14M5 12h14" />
        </svg>
        {{ t('strategy.members.invite') }}
      </button>
    </div>

    <div v-if="members.length === 0" class="tab-empty">
      <p>{{ t('strategy.members.empty') }}</p>
    </div>

    <div v-else class="member-list">
      <div v-for="member in members" :key="member.id" class="member-item">
        <img :src="member.avatar || defaultAvatar" :alt="member.nickname || ''" class="avatar" />
        <div class="member-info">
          <span class="name">{{ member.nickname || '—' }}</span>
          <span class="join-time">{{ t('strategy.members.joinedAt', { date: formatTime(member.joinTime) }) }}</span>
        </div>
        <span :class="['role-badge', member.role]">
          {{ getRoleName(member.role) }}
        </span>
        <button
          v-if="isAdmin && member.role !== 'owner'"
          class="remove-btn"
          type="button"
          @click="$emit('remove', member.userId)"
        >
          {{ t('strategy.members.remove') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

defineProps({
  members: {
    type: Array,
    default: () => []
  },
  isAdmin: {
    type: Boolean,
    default: false
  },
  defaultAvatar: {
    type: String,
    default: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100'
  }
})

defineEmits(['invite', 'remove'])

const { t, locale } = useI18n()

function formatTime(time) {
  if (!time) return '—'
  return new Date(time).toLocaleDateString(locale.value)
}

function getRoleName(role) {
  return t(`strategy.roles.${role}`)
}
</script>

<style scoped>
.members-tab {
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

.btn-sm {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-full);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.btn-sm svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
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

.btn-primary:active {
  transform: scale(0.97);
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: var(--color-bg-secondary);
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  transition: background var(--transition-normal), border-color var(--transition-normal);
}

.member-item:hover {
  background: var(--theme-bg-surface);
  border-color: var(--color-border);
}

.avatar {
  width: 46px;
  height: 46px;
  flex-shrink: 0;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--theme-hairline);
}

.member-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.member-info .name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.join-time {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.role-badge {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: 600;
}

.role-badge.owner {
  background: var(--theme-warning-soft);
  color: var(--color-warning);
}

.role-badge.admin {
  background: var(--theme-primary-soft);
  color: var(--color-primary);
}

.role-badge.member {
  background: var(--theme-bg-active);
  color: var(--color-text-secondary);
}

.remove-btn {
  flex-shrink: 0;
  padding: 6px 12px;
  border: none;
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--color-text-secondary);
  font-family: inherit;
  font-size: 12px;
  cursor: pointer;
  transition: color var(--transition-fast), background var(--transition-fast);
}

.remove-btn:hover {
  background: var(--theme-danger-soft);
  color: var(--color-danger);
}

.tab-empty {
  padding: 40px;
  text-align: center;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-text-secondary);
  font-size: 13px;
}

@media (max-width: 767px) {
  .member-item {
    flex-wrap: wrap;
    gap: 10px;
    padding: 12px;
  }

  .avatar {
    width: 40px;
    height: 40px;
  }
}
</style>
