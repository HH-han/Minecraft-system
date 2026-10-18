<template>
  <div class="poll-tab">
    <div class="tab-toolbar">
      <span class="toolbar-title">
        {{ t('strategy.detailTabs.poll') }}
        <em>{{ polls.length }}</em>
      </span>
      <button class="btn-primary" type="button" @click="$emit('create')">
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="M12 5v14M5 12h14" />
        </svg>
        {{ t('strategy.poll.create') }}
      </button>
    </div>

    <div v-if="polls.length === 0" class="tab-empty">
      <p>{{ t('strategy.poll.empty') }}</p>
    </div>

    <div v-else class="poll-list">
      <article v-for="poll in polls" :key="poll.id" class="poll-item">
        <header class="poll-header">
          <span class="poll-title">{{ poll.title }}</span>
          <span :class="['poll-status', poll.status === 0 ? 'active' : 'ended']">
            {{ poll.status === 0 ? t('strategy.poll.active') : t('strategy.poll.ended') }}
          </span>
        </header>

        <div class="poll-options">
          <button
            v-for="(option, idx) in poll.options"
            :key="idx"
            type="button"
            :class="['poll-option', {
              selected: poll.votedOption === idx,
              winner: poll.winningOption === idx
            }]"
            @click="$emit('vote', poll.id, idx)"
          >
            <span class="option-row">
              <span class="option-text">{{ option }}</span>
              <span class="option-count">
                {{ t('strategy.poll.votes', { count: poll.votes?.[idx] || 0 }) }}
              </span>
            </span>
            <span class="option-bar">
              <span class="option-fill" :style="{ width: optionPercent(poll, idx) }"></span>
            </span>
          </button>
        </div>

        <footer class="poll-footer">
          {{ t('strategy.poll.votes', { count: totalVotes(poll) }) }}
        </footer>
      </article>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

defineProps({
  polls: {
    type: Array,
    default: () => []
  }
})

defineEmits(['create', 'vote'])

const { t } = useI18n()

function totalVotes(poll) {
  return (poll.votes || []).reduce((sum, value) => sum + Number(value || 0), 0)
}

function optionPercent(poll, index) {
  const total = totalVotes(poll)
  if (!total) return '0%'
  return `${Math.round(((poll.votes?.[index] || 0) / total) * 100)}%`
}
</script>

<style scoped>
.poll-tab {
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

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast), transform var(--transition-fast);
}

.btn-primary svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-primary:active {
  transform: scale(0.97);
}

.poll-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.poll-item {
  padding: 18px 20px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
}

.poll-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.poll-title {
  font-size: 15px;
  font-weight: 650;
  line-height: 1.4;
  color: var(--color-text);
}

.poll-status {
  flex-shrink: 0;
  padding: 3px 10px;
  border-radius: var(--radius-full);
  font-size: 11px;
  font-weight: 600;
}

.poll-status.active {
  background: var(--theme-success-soft);
  color: var(--color-success);
}

.poll-status.ended {
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
}

.poll-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.poll-option {
  display: block;
  width: 100%;
  padding: 11px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-bg-secondary);
  font-family: inherit;
  text-align: left;
  cursor: pointer;
  transition: border-color var(--transition-fast), background var(--transition-fast);
}

.poll-option:hover {
  border-color: var(--color-primary);
}

.poll-option.selected {
  border-color: var(--color-primary);
  background: var(--theme-primary-soft);
}

.poll-option.winner {
  border-color: var(--color-success);
  background: var(--theme-success-soft);
}

.option-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.option-text {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text);
}

.option-count {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.option-bar {
  display: block;
  height: 4px;
  border-radius: 4px;
  background: var(--theme-bg-active);
  overflow: hidden;
}

.option-fill {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: var(--color-primary);
  transition: width var(--transition-slow);
}

.poll-option.winner .option-fill {
  background: var(--color-success);
}

.poll-footer {
  margin-top: 12px;
  text-align: right;
  font-size: 11px;
  color: var(--color-text-secondary);
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
  .poll-item {
    padding: 14px;
  }
}
</style>
