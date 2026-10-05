<template>
  <div class="product-type-selector">
    <div class="type-item" v-for="(type, index) in types" :key="index">
      <div class="type-label">
        <span class="label-dot"></span>
        {{ type.label }}
      </div>
      <div class="type-options">
        <div
          v-for="(option, optionIndex) in type.options"
          :key="optionIndex"
          class="option-item"
          :class="{ active: selectedOptions[type.label] === option.value }"
          @click="selectOption(type.label, option.value)"
        >
          <span class="option-check" v-if="selectedOptions[type.label] === option.value">
            <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="20 6 9 17 4 12"></polyline>
            </svg>
          </span>
          {{ option.label }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const props = defineProps({
  types: {
    type: Array,
    default: () => [
      {
        label: '版本',
        options: [
          { label: 'Java版', value: 'java' },
          { label: '基岩版', value: 'bedrock' },
          { label: '教育版', value: 'education' }
        ]
      },
      {
        label: '规格',
        options: [
          { label: '标准版', value: 'standard' },
          { label: '豪华版', value: 'deluxe' },
          { label: '终极版', value: 'ultimate' }
        ]
      }
    ]
  }
})

const emit = defineEmits(['optionChange'])

const selectedOptions = reactive({})

// 初始化默认选中第一个选项
props.types.forEach(type => {
  if (type.options && type.options.length > 0) {
    selectedOptions[type.label] = type.options[0].value
  }
})

const selectOption = (typeLabel, optionValue) => {
  selectedOptions[typeLabel] = optionValue
  emit('optionChange', selectedOptions)
}
</script>

<style scoped>
.product-type-selector {
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1d1d1f;
}

.type-item {
  margin-bottom: 26px;
}

.type-item:last-child {
  margin-bottom: 0;
}

.type-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 14px;
  letter-spacing: -0.01em;
}

.label-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #2997ff;
  flex-shrink: 0;
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border: 1.5px solid #e5e5ea;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.22s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fafafa;
  color: #1d1d1f;
  user-select: none;
}

.option-item:hover {
  border-color: #2997ff;
  background: rgba(41, 151, 255, 0.04);
  transform: translateY(-1px);
}

.option-item.active {
  border-color: #2997ff;
  color: #2997ff;
  background: rgba(41, 151, 255, 0.08);
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(41, 151, 255, 0.18);
}

.option-check {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #2997ff;
  animation: checkPop 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes checkPop {
  from { transform: scale(0.4); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

@media (max-width: 767px) {
  .type-item { margin-bottom: 22px; }
  .type-label { font-size: 14px; margin-bottom: 12px; }
  .type-options { gap: 8px; }
  .option-item { padding: 9px 15px; font-size: 13px; border-radius: 10px; }
}

@media (max-width: 480px) {
  .option-item { padding: 8px 13px; font-size: 12px; }
}
</style>