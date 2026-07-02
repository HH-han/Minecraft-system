<template>
  <div class="tab-nav">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      :class="['tab-item', { active: activeTab === tab.id }]"
      @click="$emit('update:activeTab', tab.id)"
    >
      <img class="tab-icon" :src="tab.icon" :alt="tab.name" />
      <span class="tab-text">{{ tab.name }}</span>
    </button>
  </div>
</template>

<script setup>
defineProps({
  tabs: {
    type: Array,
    required: true
  },
  activeTab: {
    type: String,
    required: true
  }
})

defineEmits(['update:activeTab'])
</script>

<style scoped>
.tab-nav {
  display: flex;
  gap: var(--spacing-8);
  margin-bottom: var(--spacing-32);
  border-bottom: 1px solid var(--color-border);
  padding-bottom: 1px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-8);
  padding: var(--spacing-12) var(--spacing-20);
  background: transparent;
  border: none;
  font-size: 17px;
  font-weight: 400;
  color: var(--color-text-secondary);
  cursor: pointer;
  position: relative;
  transition: all var(--transition-normal);
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}

.tab-item:hover {
  color: var(--color-text);
  background: rgba(0, 0, 0, 0.03);
}

.tab-item.active {
  color: var(--color-text);
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: var(--spacing-8);
  right: var(--spacing-8);
  height: 3px;
  background: var(--color-primary);
  border-radius: 2px;
}

.tab-icon {
  width: 20px;
  height: 20px;
}

@media (max-width: 767px) {
  .tab-nav {
    overflow-x: auto;
    gap: var(--spacing-4);
    padding-bottom: var(--spacing-8);
  }
  .tab-item {
    flex-shrink: 0;
    padding: var(--spacing-12) var(--spacing-16);
    font-size: 15px;
  }
}
</style>