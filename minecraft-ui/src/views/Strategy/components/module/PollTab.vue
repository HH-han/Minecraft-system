<template>
  <div class="poll-tab">
    <div class="tab-actions">
      <button class="btn-small" @click="$emit('create')">发起投票</button>
    </div>
    <div class="poll-list">
      <div v-for="poll in polls" :key="poll.id" class="poll-item">
        <div class="poll-header">
          <span class="poll-title">{{ poll.title }}</span>
          <span v-if="poll.status === 0" class="poll-status active">进行中</span>
          <span v-else class="poll-status ended">已结束</span>
        </div>
        <div class="poll-options">
          <div
            v-for="(option, idx) in poll.options"
            :key="idx"
            :class="['poll-option', { selected: poll.votedOption === idx, 'winner': poll.winningOption === idx }]"
            @click="$emit('vote', poll.id, idx)"
          >
            <span class="option-text">{{ option }}</span>
            <span class="option-count">{{ poll.votes?.[idx] || 0 }}票</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  polls: {
    type: Array,
    default: () => []
  }
})

defineEmits(['create', 'vote'])
</script>

<style scoped>
.poll-tab {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.tab-actions {
  display: flex;
  gap: var(--spacing-12);
  margin-bottom: var(--spacing-20);
}

.tab-actions button {
  border: none;
  padding: var(--spacing-8) var(--spacing-16);
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-4);
  background: var(--color-primary);
  color: #fff;
}

.tab-actions button:hover {
  background: var(--color-primary-hover);
}

.tab-actions button:active {
  transform: scale(0.98);
}

.poll-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-20);
}

.poll-item {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: var(--spacing-20);
}

.poll-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-16);
}

.poll-title {
  font-weight: 600;
  font-size: 17px;
  line-height: 1.3;
}

.poll-status {
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.poll-status.active {
  background: var(--color-success);
  color: #fff;
}

.poll-status.ended {
  background: var(--color-text-secondary);
  color: #fff;
}

.poll-options {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-8);
}

.poll-option {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-12) var(--spacing-16);
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 2px solid transparent;
}

.poll-option:hover {
  border-color: var(--color-primary);
  background: rgba(41, 151, 255, 0.03);
}

.poll-option.selected {
  border-color: var(--color-primary);
  background: rgba(41, 151, 255, 0.1);
}

.poll-option.winner {
  background: rgba(52, 199, 89, 0.1);
  border-color: var(--color-success);
}

.poll-option.winner.selected {
  border-color: var(--color-success);
}

.option-text {
  font-size: 15px;
  font-weight: 400;
}

.option-count {
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 500;
}
</style>