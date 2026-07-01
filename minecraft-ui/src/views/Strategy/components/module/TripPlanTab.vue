<template>
  <div class="trip-tab">
    <div class="tab-actions">
      <button class="btn-small" @click="$emit('add')">添加行程</button>
      <button class="btn-small btn-secondary" @click="$emit('generate')">AI智能规划</button>
    </div>
    <div class="trip-timeline">
      <div v-for="(day, index) in tripPlan" :key="index" class="trip-day">
        <div class="day-header">
          <span class="day-number">Day {{ index + 1 }}</span>
          <span class="day-date">{{ day.date }}</span>
        </div>
        <div class="day-items">
          <div v-for="item in day.items" :key="item.id" class="trip-item">
            <span class="time">{{ item.time }}</span>
            <span class="activity">{{ item.activity }}</span>
            <span v-if="item.location" class="location">{{ item.location }}</span>
            <button v-if="isAdmin" @click="$emit('delete', item.id)" class="delete-btn">×</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
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
</script>

<style scoped>
.trip-tab {
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

.tab-actions .btn-secondary {
  background: var(--color-bg-secondary);
  color: var(--color-text);
}

.tab-actions .btn-secondary:hover {
  background: rgba(0, 0, 0, 0.04);
}

.trip-timeline {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-24);
}

.trip-day {
  position: relative;
  padding-left: 80px;
}

.trip-day::before {
  content: '';
  position: absolute;
  left: 32px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--color-border);
}

.day-header {
  position: absolute;
  left: 0;
  width: 60px;
  text-align: right;
}

.day-number {
  display: block;
  font-weight: 700;
  color: var(--color-primary);
  font-size: 20px;
  line-height: 1.2;
}

.day-date {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.day-items {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-12);
}

.trip-item {
  background: var(--color-bg-secondary);
  padding: var(--spacing-12) var(--spacing-16);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-12);
  position: relative;
  transition: all var(--transition-normal);
}

.trip-item:hover {
  background: rgba(0, 0, 0, 0.04);
}

.trip-item::before {
  content: '';
  position: absolute;
  left: -56px;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  background: var(--color-bg);
  border: 2px solid var(--color-primary);
  border-radius: 50%;
}

.trip-item .time {
  font-size: 13px;
  color: var(--color-text-secondary);
  min-width: 50px;
  font-weight: 500;
}

.trip-item .activity {
  flex: 1;
  font-weight: 500;
  font-size: 15px;
}

.trip-item .location {
  font-size: 13px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.trip-item .delete-btn {
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  font-size: 18px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all var(--transition-fast);
}

.trip-item .delete-btn:hover {
  background: rgba(255, 59, 48, 0.1);
  color: var(--color-danger);
}

@media (max-width: 767px) {
  .trip-day {
    padding-left: 50px;
  }
  .trip-day::before {
    left: 16px;
  }
  .day-header {
    width: 40px;
  }
  .day-number {
    font-size: 16px;
  }
  .trip-item::before {
    left: -36px;
  }
}
</style>