<template>
  <div class="diary-tab">
    <div class="tab-actions">
      <button class="btn-small btn-secondary" @click="$emit('generate')">生成旅行日记</button>
      <button v-if="diary" class="btn-small" @click="$emit('export')">导出日记</button>
    </div>
    <div v-if="diary" class="diary-content">
      <h3>{{ diary.title }}</h3>
      <div class="diary-sections">
        <div v-for="section in diary.sections" :key="section.date" class="diary-section">
          <h4>{{ section.date }}</h4>
          <p>{{ section.content }}</p>
          <div v-if="section.photos" class="diary-photos">
            <img v-for="photo in section.photos" :key="photo" :src="photo" />
          </div>
        </div>
      </div>
    </div>
    <div v-else class="empty-state small">
      <p>还没有旅行日记，点击上方按钮生成</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  diary: {
    type: Object,
    default: null
  }
})

defineEmits(['generate', 'export'])
</script>

<style scoped>
.diary-tab {
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

.diary-content h3 {
  font-size: 24px;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: var(--spacing-20);
}

.diary-sections {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-24);
}

.diary-section {
  padding: var(--spacing-20);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
}

.diary-section h4 {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: var(--spacing-8);
}

.diary-section p {
  line-height: 1.8;
  color: var(--color-text);
  font-size: 15px;
}

.diary-photos {
  display: flex;
  gap: var(--spacing-8);
  margin-top: var(--spacing-12);
  flex-wrap: wrap;
}

.diary-photos img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  transition: transform var(--transition-normal);
}

.diary-photos img:hover {
  transform: scale(1.03);
}

.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: var(--color-bg-secondary);
  border-radius: 28px;
}

.empty-state.small {
  padding: 40px;
}

.empty-state p {
  color: var(--color-text-secondary);
}

@media (max-width: 767px) {
  .diary-photos img {
    width: 80px;
    height: 80px;
  }
}
</style>