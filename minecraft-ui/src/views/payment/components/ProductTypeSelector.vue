<template>
  <div class="product-type-selector">
    <div class="type-item" v-for="(type, index) in types" :key="index">
      <div class="type-label">{{ type.label }}：</div>
      <div class="type-options">
        <div 
          v-for="(option, optionIndex) in type.options" 
          :key="optionIndex"
          class="option-item"
          :class="{ active: selectedOptions[type.label] === option.value }"
          @click="selectOption(type.label, option.value)"
        >
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
  margin-bottom: 20px;
}

.type-item {
  margin-bottom: 15px;
}

.type-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.type-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.option-item:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.option-item.active {
  border-color: #ff4d4f;
  color: #ff4d4f;
  background: #fff1f0;
}

@media (max-width: 768px) {
  .type-options {
    gap: 8px;
  }
  
  .option-item {
    padding: 6px 12px;
    font-size: 13px;
  }
}
</style>