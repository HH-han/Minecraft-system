<template>
  <div class="tab-nav" role="tablist">
    <button
      v-for="tab in tabs"
      :key="tab.id"
      :class="['tab-item', { active: activeTab === tab.id }]"
      type="button"
      role="tab"
      :aria-selected="activeTab === tab.id"
      @click="$emit('update:activeTab', tab.id)"
    >
      <span class="tab-icon" aria-hidden="true">
        <svg v-if="tab.id === 'my-groups'" viewBox="0 0 24 24">
          <circle cx="9" cy="8" r="3.2" />
          <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
          <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
        </svg>
        <svg v-else-if="tab.id === 'explore'" viewBox="0 0 24 24">
          <circle cx="11" cy="11" r="7" />
          <path d="M20 20l-3.6-3.6" />
        </svg>
        <svg v-else viewBox="0 0 24 24">
          <path d="M18 8a6 6 0 1 0-12 0c0 6-2.5 7-2.5 7h17S18 14 18 8" />
          <path d="M10.3 20a2 2 0 0 0 3.4 0" />
        </svg>
      </span>
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
  display: inline-flex;
  align-self: flex-start;
  gap: 4px;
  padding: 5px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
}

.tab-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
  border: none;
  border-radius: var(--radius-full);
  background: transparent;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  white-space: nowrap;
  transition: color var(--transition-fast), background var(--transition-normal),
    box-shadow var(--transition-normal);
}

.tab-item:hover {
  color: var(--color-text);
}

.tab-item.active {
  background: var(--theme-bg-surface);
  color: var(--color-text);
  font-weight: 600;
  box-shadow: var(--shadow-sm);
}

.tab-icon {
  display: inline-flex;
}

.tab-icon svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
  opacity: 0.75;
  transition: opacity var(--transition-fast), stroke var(--transition-fast);
}

.tab-item.active .tab-icon svg {
  stroke: var(--color-primary);
  opacity: 1;
}

@media (max-width: 767px) {
  .tab-nav {
    display: flex;
    align-self: stretch;
    overflow-x: auto;
    scrollbar-width: none;
  }

  .tab-nav::-webkit-scrollbar {
    display: none;
  }

  .tab-item {
    flex: 1;
    justify-content: center;
    padding: 10px 14px;
    font-size: 13px;
  }
}
</style>
