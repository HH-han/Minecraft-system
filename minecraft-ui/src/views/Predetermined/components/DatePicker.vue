<template>
  <div class="date-picker-component">
    <div
      class="date-picker"
      v-for="(dateField, index) in dateFields"
      :key="dateField.name || index"
    >
      <label :for="'date-' + (dateField.name || index)">{{ dateField.label }}</label>
      <input
        :id="'date-' + (dateField.name || index)"
        type="date"
        v-model="dateField.value"
        :min="dateField.min"
        :max="dateField.max"
        :aria-label="dateField.label"
        @change="handleDateChange(dateField)"
      >
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  dateFields: {
    type: Array,
    default: () => [
      {
        name: 'checkInDate',
        label: '入住日期',
        value: '',
        min: new Date().toISOString().split('T')[0]
      },
      {
        name: 'checkOutDate',
        label: '离店日期',
        value: '',
        min: new Date().toISOString().split('T')[0]
      }
    ]
  }
})

const emit = defineEmits(['dateChange'])

const handleDateChange = (dateField) => {
  emit('dateChange', { [dateField.name]: dateField.value })
}
</script>

<style scoped>
.date-picker-component {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  box-sizing: border-box;
}

.date-picker {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-picker label {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.date-picker input {
  width: 100%;
  max-width: 100%;
  height: 40px;
  padding: 0 16px;
  box-sizing: border-box;
  border: 1px solid #d2d2d6;
  border-radius: 12px;
  background: #ffffff;
  color: #1d1d1f;
  font-size: 16px;
  line-height: 1.2;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  color-scheme: light;
}

.date-picker input:hover {
  border-color: #c7c7cc;
}

.date-picker input:focus {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
  border-color: #2997ff;
}

.date-picker input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式：手机单列 */
@media (max-width: 767px) {
  .date-picker-component {
    flex-direction: column;
    gap: 20px;
    margin-bottom: 24px;
  }
}
</style>
