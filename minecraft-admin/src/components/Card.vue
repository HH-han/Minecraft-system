<template>
  <div class="card">
    <div class="card-header" v-if="title || $slots.header">
      <slot name="header">
        <h3 class="card-title">{{ title }}</h3>
      </slot>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
    <div class="card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  variant: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'elevated', 'outlined'].includes(value)
  }
})
</script>

<style scoped>
.card {
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.card-header {
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--bg-primary);
  transition: all var(--transition-normal);
}

.card-title {
  color: var(--text-primary);
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0;
  transition: color var(--transition-normal);
}

.card-body {
  padding: var(--space-6);
  transition: background-color var(--transition-normal);
}

.card-footer {
  padding: var(--space-4) var(--space-6);
  border-top: 1px solid var(--border-light);
  background-color: var(--bg-primary);
  display: flex;
  justify-content: flex-end;
  gap: var(--space-4);
  transition: all var(--transition-normal);
}

/* 变体样式 */
.card:deep(.variant-outlined) {
  box-shadow: none;
  border: 1px solid var(--border-medium);
}

.card:deep(.variant-outlined):hover {
  border-color: var(--primary-500);
  box-shadow: var(--shadow-sm);
}

.card:deep(.variant-elevated) {
  box-shadow: var(--shadow-lg);
}

.card:deep(.variant-elevated):hover {
  box-shadow: var(--shadow-xl);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    padding: var(--space-3) var(--space-4);
  }
  
  .card-body {
    padding: var(--space-4);
  }
  
  .card-footer {
    padding: var(--space-3) var(--space-4);
    flex-direction: column;
    align-items: stretch;
  }
  
  .card-title {
    font-size: 1rem;
  }
}
</style>
